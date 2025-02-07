package cellsociety.Model.Simulations;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.State.WaTorWorldState;
import cellsociety.Model.StateInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.paint.Color;

/**
 * Implements the Wa-Tor World simulation rules for a predator–prey ecosystem.
 * <p>
 * This simulation tracks extra information (such as breeding timers and shark energy)
 * in parallel arrays. The simulation iterates through each cell on the grid,
 * applying the following rules:
 * </p>
 * <ul>
 *   <li><b>Fish:</b> At each point in time a fish moves randomly to an adjacent empty cell.
 *       If it has survived at least {@code fishBreedTime} steps, it reproduces as it moves,
 *       leaving behind a new fish with its breeding timer reset.</li>
 *   <li><b>Sharks:</b> At each point in time a shark first looks for adjacent fish.
 *       If one is found it moves there, eats the fish (gaining energy),
 *       and otherwise it will move (if possible) to an empty cell.
 *       Every step the shark loses one unit of energy; if its energy reaches zero,
 *       it dies. Similarly, if a shark has survived at least {@code sharkBreedTime}
 *       steps, it reproduces as it moves.</li>
 * </ul>
 */
public class WaTorWorld extends Simulation {

  private static final int DIRECTIONS_COUNT = 4;
  private static final int[] DIRECTION_ROW_OFFSETS = {-1, 0, 1, 0};
  private static final int[] DIRECTION_COL_OFFSETS = {0, 1, 0, -1};

  private static final int SHARK_ENERGY_DECAY = 1;

  private final int fishBreedTime;
  private final int sharkBreedTime;
  private final int sharkInitialEnergy;
  private final int sharkEnergyGain;

  private final int[][] breedCounter;
  private final int[][] sharkEnergy;

  private final int rows;
  private final int cols;

  /**
   * Constructs a Wa-TorWorld simulation with the specified grid and parameters.
   *
   * @param grid                the grid on which the simulation runs
   * @param fishBreedTime       number of chronons a fish must survive before reproducing
   * @param sharkBreedTime      number of chronons a shark must survive before reproducing
   * @param sharkInitialEnergy  the initial energy for a shark when it is created
   * @param sharkEnergyGain     the energy a shark gains by eating a fish
   */
  public WaTorWorld(SimulationConfig simulationConfig, Grid grid, int fishBreedTime, int sharkBreedTime,
      int sharkInitialEnergy, int sharkEnergyGain) {
    super(simulationConfig, grid);
    this.rows = grid.getRows();
    this.cols = grid.getCols();
    this.fishBreedTime = fishBreedTime;
    this.sharkBreedTime = sharkBreedTime;
    this.sharkInitialEnergy = sharkInitialEnergy;
    this.sharkEnergyGain = sharkEnergyGain;

    this.breedCounter = new int[rows][cols];
    this.sharkEnergy = new int[rows][cols];

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        Cell cell = grid.getCell(r, c);
        if (cell.getState() == WaTorWorldState.FISH) {
          breedCounter[r][c] = 0;
        } else if (cell.getState() == WaTorWorldState.SHARK) {
          breedCounter[r][c] = 0;
          sharkEnergy[r][c] = sharkInitialEnergy;
        }
      }
    }
  }

  /**
   * Initializes the color map for Wa-Tor World simulation.
   *
   * @return the map of simulation interface states to colors.
   */
  @Override
  public Map<StateInterface, Color> initializeColorMap() {
    Map<StateInterface, Color> colorMap = new HashMap<>();
    colorMap.put(WaTorWorldState.FISH, Color.ORANGE);
    colorMap.put(WaTorWorldState.SHARK, Color.GRAY);
    colorMap.put(WaTorWorldState.EMPTY, Color.LIGHTBLUE);
    return colorMap;
  }

  /**
   * Initializes the state map for Wa-Tor World simulation.
   *
   * @return the map of integer states to simulation states.
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
   * Applies one chronon (time step) of the Wa-Tor simulation.
   * <p>
   * Loops through every cell and processes fish and shark actions according
   * to the simulation rules. A boolean array is used to ensure that each creature
   * only moves once per chronon.
   * </p>
   */
  @Override
  public void applyRules() {
    final boolean[][] moved = new boolean[rows][cols];

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (moved[r][c]) {
          continue;
        }
        Cell cell = getGrid().getCell(r, c);
        WaTorWorldState state = (WaTorWorldState) cell.getState();
        if (state == WaTorWorldState.FISH) {
          processFish(r, c, moved);
        } else if (state == WaTorWorldState.SHARK) {
          processShark(r, c, moved);
        } else {
          setEmpty(r, c);
          moved[r][c] = true;
        }
      }
    }
  }

  /**
   * Processes the actions of a fish located at (r, c).
   *
   * @param r      row index of the fish
   * @param c      column index of the fish
   * @param moved  boolean grid tracking cells already updated in this chronon
   */
  private void processFish(int r, int c, boolean[][] moved) {
    final int newBreedCount = breedCounter[r][c] + 1;
    final List<int[]> emptyNeighbors = getNeighborPositions(r, c, WaTorWorldState.EMPTY, moved);

    if (!emptyNeighbors.isEmpty()) {
      final int[] newPos = randomChoice(emptyNeighbors);
      final int newR = newPos[0];
      final int newC = newPos[1];

      if (newBreedCount >= fishBreedTime) {
        // Reproduce: leave behind a fish in the current cell and place a new fish in the neighbor.
        setFish(r, c, 0);
        setFish(newR, newC, 0);
      } else {
        // Move: place the fish in the neighbor cell and mark the current cell as empty.
        setFish(newR, newC, newBreedCount);
        setEmpty(r, c);
      }
      moved[r][c] = true;
      moved[newR][newC] = true;
    } else {
      // No available move; update breeding counter.
      setFish(r, c, newBreedCount);
      moved[r][c] = true;
    }
  }

  /**
   * Processes the actions of a shark located at (r, c).
   *
   * @param r      row index of the shark
   * @param c      column index of the shark
   * @param moved  boolean grid tracking cells already updated in this chronon
   */
  private void processShark(int r, int c, boolean[][] moved) {
    final int newBreedCount = breedCounter[r][c] + 1;
    int newEnergy = sharkEnergy[r][c] - SHARK_ENERGY_DECAY;

    if (newEnergy <= 0) {
      setEmpty(r, c);
      breedCounter[r][c] = 0;
      sharkEnergy[r][c] = 0;
      moved[r][c] = true;
      return;
    }

    final List<int[]> fishNeighbors = getNeighborPositions(r, c, WaTorWorldState.FISH, moved);
    if (!fishNeighbors.isEmpty()) {
      final int[] newPos = randomChoice(fishNeighbors);
      final int newR = newPos[0];
      final int newC = newPos[1];
      newEnergy += sharkEnergyGain;

      if (newBreedCount >= sharkBreedTime) {
        // Reproduce: leave a new shark in the current cell with reset energy.
        setShark(r, c, 0, sharkInitialEnergy);
        // Move to the new cell with the current energy.
        setShark(newR, newC, 0, newEnergy);
      } else {
        // Move: place the shark in the neighbor cell.
        setShark(newR, newC, newBreedCount, newEnergy);
        setEmpty(r, c);
      }
      moved[r][c] = true;
      moved[newR][newC] = true;
    } else {
      final List<int[]> emptyNeighbors = getNeighborPositions(r, c, WaTorWorldState.EMPTY, moved);
      if (!emptyNeighbors.isEmpty()) {
        final int[] newPos = randomChoice(emptyNeighbors);
        final int newR = newPos[0];
        final int newC = newPos[1];

        if (newBreedCount >= sharkBreedTime) {
          setShark(r, c, 0, sharkInitialEnergy);
          setShark(newR, newC, 0, newEnergy);
        } else {
          setShark(newR, newC, newBreedCount, newEnergy);
          setEmpty(r, c);
        }
        moved[r][c] = true;
        moved[newR][newC] = true;
      } else {
        // No available move; update the shark's breeding counter and energy.
        setShark(r, c, newBreedCount, newEnergy);
        moved[r][c] = true;
      }
    }
  }

  /**
   * Returns a list of neighbor positions (using toroidal wrap–around) around (r, c)
   * that currently have the specified target state and have not been updated yet.
   *
   * @param r           row index of the current cell
   * @param c           column index of the current cell
   * @param targetState the required state for the neighbor cell
   * @param moved       boolean grid tracking whether a cell has been updated in this chronon
   * @return a list of {row, col} pairs for matching neighbors
   */
  private List<int[]> getNeighborPositions(int r, int c, WaTorWorldState targetState, boolean[][] moved) {
    final List<int[]> neighbors = new ArrayList<>();

    for (int i = 0; i < DIRECTIONS_COUNT; i++) {
      final int nr = (r + DIRECTION_ROW_OFFSETS[i] + rows) % rows;
      final int nc = (c + DIRECTION_COL_OFFSETS[i] + cols) % cols;
      if (!moved[nr][nc] && getGrid().getCell(nr, nc).getState() == targetState) {
        neighbors.add(new int[]{nr, nc});
      }
    }
    return neighbors;
  }

  /**
   * Utility method to randomly choose one element from a non-empty list of positions.
   *
   * @param positions a non-empty list of {row, col} pairs
   * @return a randomly selected position as an int[2] array
   */
  private int[] randomChoice(List<int[]> positions) {
    final int randomIndex = (int) (Math.random() * positions.size());
    return positions.get(randomIndex);
  }

  /**
   * Sets the cell at (r, c) to the FISH state and updates its breeding counter.
   *
   * @param r          row index of the cell
   * @param c          column index of the cell
   * @param breedValue the breeding counter value to set
   */
  private void setFish(int r, int c, int breedValue) {
    getGrid().getCell(r, c).setNextState(WaTorWorldState.FISH);
    breedCounter[r][c] = breedValue;
  }

  /**
   * Sets the cell at (r, c) to the SHARK state and updates its breeding counter and energy.
   *
   * @param r          row index of the cell
   * @param c          column index of the cell
   * @param breedValue the breeding counter value to set
   * @param energy     the shark's energy value to set
   */
  private void setShark(int r, int c, int breedValue, int energy) {
    getGrid().getCell(r, c).setNextState(WaTorWorldState.SHARK);
    breedCounter[r][c] = breedValue;
    sharkEnergy[r][c] = energy;
  }

  /**
   * Sets the cell at (r, c) to the EMPTY state.
   *
   * @param r row index of the cell
   * @param c column index of the cell
   */
  private void setEmpty(int r, int c) {
    getGrid().getCell(r, c).setNextState(WaTorWorldState.EMPTY);
  }
}


