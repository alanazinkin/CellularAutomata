package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.state.WaTorWorldState;
import cellsociety.model.StateInterface;
import java.util.Collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Implements the Wa-Tor World simulation rules for a predator–prey ecosystem.
 * <p>
 * In this simulation, fish and sharks move, breed, and (in the case of sharks) lose or gain energy.
 * The rules for movement and reproduction are encapsulated in helper methods to reduce
 * duplication.
 * </p>
 * @author Tatum McKinnis
 */
public class WaTorWorld extends Simulation {

  private static final int DIRECTIONS_COUNT = 4;
  private static final int[] DIRECTION_ROW_OFFSETS = {-1, 0, 1, 0};
  private static final int[] DIRECTION_COL_OFFSETS = {0, 1, 0, -1};
  private static final int SHARK_ENERGY_DECAY = 1;
  private static final Random RANDOM = new Random();

  private final int fishBreedTime;
  private final int sharkBreedTime;
  private final int sharkInitialEnergy;
  private final int sharkEnergyGain;

  private final int rows;
  private final int cols;

  // --- State Arrays (indexed by grid row and col) ---
  // breedCounters holds the number of steps since last reproduction for both fish and sharks.
  private final int[][] breedCounters;
  // sharkEnergies holds the current energy for sharks. (Unused for fish.)
  private final int[][] sharkEnergies;

  /**
   * Constructs a Wa-Tor World simulation with the specified configuration.
   *
   * @param simulationConfig   configuration settings for the simulation
   * @param grid               the grid on which the simulation runs
   * @param fishBreedTime      number of chronons a fish must survive before reproducing (must be >
   *                           0)
   * @param sharkBreedTime     number of chronons a shark must survive before reproducing (must be >
   *                           0)
   * @param sharkInitialEnergy initial energy for a shark when created
   * @param sharkEnergyGain    energy a shark gains by eating a fish
   * @throws IllegalArgumentException if fishBreedTime or sharkBreedTime is less than or equal to 0
   * @author Tatum McKinnis
   */
  public WaTorWorld(SimulationConfig simulationConfig, Grid grid, int fishBreedTime,
      int sharkBreedTime,
      int sharkInitialEnergy, int sharkEnergyGain) {
    super(simulationConfig, grid);

    if (fishBreedTime <= 0) {
      throw new IllegalArgumentException("fishBreedTime must be greater than 0.");
    }
    if (sharkBreedTime <= 0) {
      throw new IllegalArgumentException("sharkBreedTime must be greater than 0.");
    }

    this.fishBreedTime = fishBreedTime;
    this.sharkBreedTime = sharkBreedTime;
    this.sharkInitialEnergy = sharkInitialEnergy;
    this.sharkEnergyGain = sharkEnergyGain;

    this.rows = grid.getRows();
    this.cols = grid.getCols();

    this.breedCounters = new int[rows][cols];
    this.sharkEnergies = new int[rows][cols];

    // Initialize counters for cells that already contain fish or sharks.
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        Cell cell = grid.getCell(r, c);
        WaTorWorldState state = (WaTorWorldState) cell.getCurrentState();
        if (state == WaTorWorldState.FISH) {
          breedCounters[r][c] = 0;
        } else if (state == WaTorWorldState.SHARK) {
          breedCounters[r][c] = 0;
          sharkEnergies[r][c] = sharkInitialEnergy;
        }
      }
    }
  }

  /**
   * Initializes the color mapping for the simulation.
   *
   * @return a map from simulation state to color.
   */
  @Override
  public Map<StateInterface, String> initializeColorMap() {
    return Map.of(
        WaTorWorldState.FISH, "watorworld-state-fish",
        WaTorWorldState.SHARK, "watorworld-state-shark",
        WaTorWorldState.EMPTY, "watorworld-state-empty"
    );
  }

  /**
   * Initializes the state mapping for the simulation.
   *
   * @return a map from integer state codes to simulation states.
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    Map<Integer, StateInterface> stateMap = new HashMap<>();
    stateMap.put(0, WaTorWorldState.EMPTY);
    stateMap.put(1, WaTorWorldState.FISH);
    stateMap.put(2, WaTorWorldState.SHARK);
    return stateMap;
  }

  /**
   * Applies one chronon (time step) of simulation rules.
   * <p>
   * Iterates over the grid; each creature (fish or shark) takes its turn exactly once.
   * </p>
   */
  @Override
  public void applyRules() {
    boolean[][] moved = new boolean[rows][cols];
    List<int[]> agentCoords = new ArrayList<>();

    // Collect all fish and shark coordinates
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        WaTorWorldState state = (WaTorWorldState) getGrid().getCell(r, c).getCurrentState();
        if (state == WaTorWorldState.FISH || state == WaTorWorldState.SHARK) {
          agentCoords.add(new int[]{r, c});
        }
      }
    }

    // Shuffle to randomize processing order
    Collections.shuffle(agentCoords, RANDOM);

    // Process agents in shuffled order
    for (int[] coord : agentCoords) {
      int r = coord[0], c = coord[1];
      if (moved[r][c]) {
        continue;
      }

      Cell cell = getGrid().getCell(r, c);
      WaTorWorldState state = (WaTorWorldState) cell.getCurrentState();
      switch (state) {
        case FISH -> processFish(r, c, moved);
        case SHARK -> processShark(r, c, moved);
        default -> moved[r][c] = true;
      }
    }

    // Process empty cells (if needed)
    // ...
  }

  // ===================== Fish Behavior =====================

  /**
   * Processes the fish at position (r, c).
   *
   * @param r     the row index of the fish
   * @param c     the column index of the fish
   * @param moved a grid that tracks cells already updated in this chronon
   */
  private void processFish(int r, int c, boolean[][] moved) {
    int newBreedCount = breedCounters[r][c] + 1;
    List<int[]> emptyNeighbors = findNeighborsByState(r, c, WaTorWorldState.EMPTY, moved);

    if (!emptyNeighbors.isEmpty()) {
      int[] newPos = selectRandomNeighbor(emptyNeighbors);
      int newR = newPos[0];
      int newC = newPos[1];
      moveFish(r, c, newR, newC, newBreedCount, moved);
    } else {
      // No move available; simply update breeding counter.
      setFish(r, c, newBreedCount);
      moved[r][c] = true;
    }
  }

  /**
   * Moves or reproduces a fish.
   * <p>
   * If the fish has reached its breeding threshold, it reproduces by leaving a new fish in the
   * target cell while resetting its own counter; otherwise, it simply moves.
   * </p>
   *
   * @param currentR    current row index
   * @param currentC    current column index
   * @param targetR     target row index for movement
   * @param targetC     target column index for movement
   * @param newBreedVal new breeding counter value (after increment)
   * @param moved       grid marking moved cells
   */
  private void moveFish(int currentR, int currentC, int targetR, int targetC, int newBreedVal,
      boolean[][] moved) {
    if (newBreedVal >= fishBreedTime) {
      // Parent moves to target, child remains in current cell
      setFish(targetR, targetC, 0); // Parent (moved) resets counter
      setFish(currentR, currentC, 0); // Child (original cell)
    } else {
      // Regular move
      setFish(targetR, targetC, newBreedVal);
      setEmpty(currentR, currentC);
    }
    markMoved(moved, currentR, currentC, targetR, targetC);
  }

  // ===================== Shark Behavior =====================

  /**
   * Processes the shark at position (r, c) by checking for death conditions, attempting to move to prey,
   * attempting to move to empty cells, or updating in-place if no movement is possible.
   *
   * @param r     the row index of the shark
   * @param c     the column index of the shark
   * @param moved a grid that tracks cells already updated in this chronon
   */
  private void processShark(int r, int c, boolean[][] moved) {
    int newBreedCount = breedCounters[r][c] + 1;
    int currentEnergy = sharkEnergies[r][c];
    int newEnergy = currentEnergy - SHARK_ENERGY_DECAY;

    if (handleSharkDeath(r, c, newEnergy, moved)) {
      return;
    }

    // Use array to allow energy modification in helper methods
    int[] energyHolder = {newEnergy};
    if (tryMoveToPrey(r, c, newBreedCount, energyHolder, moved)) {
      newEnergy = energyHolder[0];
      return;
    }

    if (tryMoveToEmpty(r, c, newBreedCount, newEnergy, moved)) {
      return;
    }

    updateSharkInPlace(r, c, newBreedCount, newEnergy, moved);
  }


  /**
   * Handles shark death due to energy depletion.
   *
   * @param r          the row index of the shark
   * @param c          the column index of the shark
   * @param newEnergy  the shark's energy after decay
   * @param moved      a grid that tracks cells already updated in this chronon
   * @return true if the shark died and was removed, false otherwise
   */
  private boolean handleSharkDeath(int r, int c, int newEnergy, boolean[][] moved) {
    if (newEnergy > 0) {
      return false;
    }
    setEmpty(r, c);
    resetCellAttributes(r, c);
    moved[r][c] = true;
    return true;
  }

  /**
   * Attempts to move shark to a cell containing prey (fish) and updates energy.
   *
   * @param r              the row index of the shark
   * @param c              the column index of the shark
   * @param newBreedCount  the updated breed counter after increment
   * @param energyHolder   array holding current energy value (modified during prey consumption)
   * @param moved          a grid that tracks cells already updated in this chronon
   * @return true if movement to prey occurred, false otherwise
   */
  private boolean tryMoveToPrey(int r, int c, int newBreedCount, int[] energyHolder, boolean[][] moved) {
    List<int[]> fishNeighbors = findNeighborsByState(r, c, WaTorWorldState.FISH, moved);
    if (fishNeighbors.isEmpty()) {
      return false;
    }

    int[] targetPos = selectRandomNeighbor(fishNeighbors);
    int targetR = targetPos[0];
    int targetC = targetPos[1];

    // Update energy and pass to movement
    energyHolder[0] += sharkEnergyGain;
    moveShark(r, c, targetR, targetC, newBreedCount, energyHolder[0], moved);
    return true;
  }

  /**
   * Attempts to move shark to an empty neighboring cell.
   *
   * @param r              the row index of the shark
   * @param c              the column index of the shark
   * @param newBreedCount  the updated breed counter after increment
   * @param newEnergy      the updated energy value after decay
   * @param moved          a grid that tracks cells already updated in this chronon
   * @return true if movement to empty cell occurred, false otherwise
   */
  private boolean tryMoveToEmpty(int r, int c, int newBreedCount, int newEnergy, boolean[][] moved) {
    List<int[]> emptyNeighbors = findNeighborsByState(r, c, WaTorWorldState.EMPTY, moved);
    if (emptyNeighbors.isEmpty()) {
      return false;
    }
    int[] targetPos = selectRandomNeighbor(emptyNeighbors);
    int targetR = targetPos[0];
    int targetC = targetPos[1];
    moveShark(r, c, targetR, targetC, newBreedCount, newEnergy, moved);
    return true;
  }

  /**
   * Updates shark's state in its current position when no movement is possible.
   *
   * @param r              the row index of the shark
   * @param c              the column index of the shark
   * @param newBreedCount  the updated breed counter after increment
   * @param newEnergy      the updated energy value after decay
   * @param moved          a grid that tracks cells already updated in this chronon
   */
  private void updateSharkInPlace(int r, int c, int newBreedCount, int newEnergy, boolean[][] moved) {
    setShark(r, c, newBreedCount, newEnergy);
    moved[r][c] = true;
  }
  /**
   * Moves or reproduces a shark.
   * <p>
   * If the shark’s breeding counter exceeds its threshold, it reproduces: a new shark is left in
   * the original cell (with initial energy) and the moving shark resets its counter. Otherwise, it
   * simply moves.
   * </p>
   *
   * @param currentR current row index
   * @param currentC current column index
   * @param targetR  target row index
   * @param targetC  target column index
   * @param breedVal new breeding counter value (after increment)
   * @param energy   updated energy value for the shark
   * @param moved    grid marking moved cells
   */
  private void moveShark(int currentR, int currentC, int targetR, int targetC, int breedVal,
      int energy, boolean[][] moved) {
    if (breedVal >= sharkBreedTime) {
      // Parent moves to target, child remains in current cell
      setShark(targetR, targetC, 0, energy); // Parent (moved)
      setShark(currentR, currentC, 0, sharkInitialEnergy); // Child (original cell)
    } else {
      // Regular move
      setShark(targetR, targetC, breedVal, energy);
      setEmpty(currentR, currentC);
    }
    markMoved(moved, currentR, currentC, targetR, targetC);
  }
  // ===================== Helper Methods =====================

  /**
   * Finds all neighbor positions (with toroidal wrap–around) of the cell at (r, c) that are in the
   * specified target state and have not yet been updated.
   *
   * @param r           the row index of the current cell
   * @param c           the column index of the current cell
   * @param targetState the desired state of the neighbor cells
   * @param moved       grid marking cells updated in this chronon
   * @return a list of {row, col} pairs for matching neighbors
   */
  private List<int[]> findNeighborsByState(int r, int c, WaTorWorldState targetState,
      boolean[][] moved) {
    List<int[]> neighbors = new ArrayList<>();
    for (int i = 0; i < DIRECTIONS_COUNT; i++) {
      int neighborRow = (r + DIRECTION_ROW_OFFSETS[i] + rows) % rows;
      int neighborCol = (c + DIRECTION_COL_OFFSETS[i] + cols) % cols;
      if (!moved[neighborRow][neighborCol] &&
          getGrid().getCell(neighborRow, neighborCol).getCurrentState() == targetState) {
        neighbors.add(new int[]{neighborRow, neighborCol});
      }
    }
    return neighbors;
  }

  /**
   * Selects and returns a random neighbor position from a non-empty list.
   *
   * @param positions a non-empty list of {row, col} pairs
   * @return a randomly selected position
   * @throws IllegalArgumentException if the provided list is empty
   */
  private int[] selectRandomNeighbor(List<int[]> positions) {
    if (positions.isEmpty()) {
      throw new IllegalArgumentException("No available neighbor positions.");
    }
    int index = RANDOM.nextInt(positions.size());
    return positions.get(index);
  }

  /**
   * Marks two cells as updated in the moved grid.
   *
   * @param moved the moved grid
   * @param r1    row index of the first cell
   * @param c1    column index of the first cell
   * @param r2    row index of the second cell
   * @param c2    column index of the second cell
   */
  private void markMoved(boolean[][] moved, int r1, int c1, int r2, int c2) {
    moved[r1][c1] = true;
    moved[r2][c2] = true;
  }

  /**
   * Resets the additional state attributes for a cell.
   *
   * @param r the row index
   * @param c the column index
   */
  private void resetCellAttributes(int r, int c) {
    breedCounters[r][c] = 0;
    sharkEnergies[r][c] = 0;
  }

  /**
   * Sets the cell at (r, c) to the FISH state and updates its breeding counter.
   *
   * @param r          row index
   * @param c          column index
   * @param breedValue breeding counter value to assign
   */
  private void setFish(int r, int c, int breedValue) {
    getGrid().getCell(r, c).setNextState(WaTorWorldState.FISH);
    breedCounters[r][c] = breedValue;
  }

  /**
   * Sets the cell at (r, c) to the SHARK state and updates its breeding counter and energy.
   *
   * @param r          row index
   * @param c          column index
   * @param breedValue breeding counter value to assign
   * @param energy     energy value to assign
   */
  private void setShark(int r, int c, int breedValue, int energy) {
    getGrid().getCell(r, c).setNextState(WaTorWorldState.SHARK);
    breedCounters[r][c] = breedValue;
    sharkEnergies[r][c] = energy;
  }

  /**
   * Sets the cell at (r, c) to the EMPTY state.
   *
   * @param r row index
   * @param c column index
   */
  private void setEmpty(int r, int c) {
    getGrid().getCell(r, c).setNextState(WaTorWorldState.EMPTY);
  }
}



