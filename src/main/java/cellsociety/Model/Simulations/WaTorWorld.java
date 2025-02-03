package cellsociety.Model.Simulations;

import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.State.WaTorWorldState;
import cellsociety.Model.StateInterface;
import java.util.ArrayList;
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
 *   <li><b>Fish:</b> At each chronon a fish moves randomly to an adjacent empty cell.
 *       If it has survived at least {@code fishBreedTime} steps, it reproduces as it moves,
 *       leaving behind a new fish with its breeding timer reset.</li>
 *   <li><b>Sharks:</b> At each chronon a shark first looks for adjacent fish.
 *       If one is found it moves there, eats the fish (gaining energy),
 *       and otherwise it will move (if possible) to an empty cell.
 *       Every step the shark loses one unit of energy; if its energy reaches zero,
 *       it dies. Similarly, if a shark has survived at least {@code sharkBreedTime}
 *       steps, it reproduces as it moves.</li>
 * </ul>
 */
public class WaTorWorld extends Simulation {

  // Simulation parameters.
  private final int fishBreedTime;
  private final int sharkBreedTime;
  private final int sharkInitialEnergy;
  private final int sharkEnergyGain;

  // Arrays to hold per-cell counters:
  // breedCounter[r][c] holds how many chronons the creature in cell (r,c) has survived.
  private int[][] breedCounter;
  // For cells with a shark, sharkEnergy[r][c] holds its current energy.
  private int[][] sharkEnergy;

  // Number of rows and columns in the grid.
  private final int rows;
  private final int cols;

  /**
   * Constructs a Wa-TorWorldSimulation with the specified grid and simulation parameters.
   *
   * @param grid                the Grid on which the simulation runs
   * @param fishBreedTime       number of chronons a fish must survive before reproducing
   * @param sharkBreedTime      number of chronons a shark must survive before reproducing
   * @param sharkInitialEnergy  the initial energy for a shark when it is created
   * @param sharkEnergyGain     the energy a shark gains by eating a fish
   */
  public WaTorWorld(Grid grid, int fishBreedTime, int sharkBreedTime,
      int sharkInitialEnergy, int sharkEnergyGain) {
    super(grid);
    this.rows = grid.getRows();
    this.cols = grid.getCols();
    this.fishBreedTime = fishBreedTime;
    this.sharkBreedTime = sharkBreedTime;
    this.sharkInitialEnergy = sharkInitialEnergy;
    this.sharkEnergyGain = sharkEnergyGain;

    // Initialize the parallel arrays.
    breedCounter = new int[rows][cols];
    sharkEnergy = new int[rows][cols];

    // Initialize counters based on the initial grid state.
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

  @Override
  public Map<StateInterface, Color> initializeStateMap() {
    return Map.of();
  }

  /**
   * Applies one chronon (time step) of the Wa-Tor simulation.
   * <p>
   * The method loops through every cell and processes fish and shark actions according
   * to the simulation rules. A boolean array is used to ensure that each creature only
   * moves once per chronon.
   * </p>
   */
  @Override
  public void applyRules() {
    // Track which cells have already been processed this step.
    boolean[][] moved = new boolean[rows][cols];

    // Process each cell in the grid.
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        // Skip if this cell has already been updated.
        if (moved[r][c]) {
          continue;
        }
        Cell cell = grid.getCell(r, c);
        WaTorWorldState state = (WaTorWorldState) cell.getState();
        if (state == WaTorWorldState.FISH) {
          processFish(r, c, moved);
        } else if (state == WaTorWorldState.SHARK) {
          processShark(r, c, moved);
        } else {
          // For EMPTY cells, schedule them as empty.
          grid.getCell(r, c).setNextState(WaTorWorldState.EMPTY);
          moved[r][c] = true;
        }
      }
    }
  }

  /**
   * Processes the actions of a fish located at (r, c).
   *
   * @param r     row index of the fish
   * @param c     column index of the fish
   * @param moved boolean grid tracking cells already updated in this chronon
   */
  private void processFish(int r, int c, boolean[][] moved) {
    // Increase the fish's breeding counter.
    int currentBreed = breedCounter[r][c] + 1;
    // Get a list of adjacent empty neighbor positions.
    List<int[]> emptyNeighbors = getNeighborPositions(r, c, WaTorWorldState.EMPTY, moved);
    if (!emptyNeighbors.isEmpty()) {
      // Choose one random empty cell.
      int[] pos = randomChoice(emptyNeighbors);
      int newR = pos[0];
      int newC = pos[1];

      // Fish moves: if its breeding counter reaches the threshold,
      // it reproduces by leaving a new fish behind in its old cell.
      if (currentBreed >= fishBreedTime) {
        // Reproduction: leave fish in both original and new cell.
        grid.getCell(r, c).setNextState(WaTorWorldState.FISH);
        breedCounter[r][c] = 0;
        grid.getCell(newR, newC).setNextState(WaTorWorldState.FISH);
        breedCounter[newR][newC] = 0;
      } else {
        // No reproduction: the fish moves and its counter carries over.
        grid.getCell(newR, newC).setNextState(WaTorWorldState.FISH);
        breedCounter[newR][newC] = currentBreed;
        grid.getCell(r, c).setNextState(WaTorWorldState.EMPTY);
      }
      moved[r][c] = true;
      moved[newR][newC] = true;
    } else {
      // If the fish cannot move, it remains in place.
      grid.getCell(r, c).setNextState(WaTorWorldState.FISH);
      breedCounter[r][c] = currentBreed;
      moved[r][c] = true;
    }
  }

  /**
   * Processes the actions of a shark located at (r, c).
   *
   * @param r     row index of the shark
   * @param c     column index of the shark
   * @param moved boolean grid tracking cells already updated in this chronon
   */
  private void processShark(int r, int c, boolean[][] moved) {
    // Increase the shark's breeding counter and reduce its energy by 1.
    int currentBreed = breedCounter[r][c] + 1;
    int currentEnergy = sharkEnergy[r][c] - 1;

    // If energy has been depleted, the shark dies.
    if (currentEnergy <= 0) {
      grid.getCell(r, c).setNextState(WaTorWorldState.EMPTY);
      sharkEnergy[r][c] = 0;
      breedCounter[r][c] = 0;
      moved[r][c] = true;
      return;
    }

    // First, try to find an adjacent fish to eat.
    List<int[]> fishNeighbors = getNeighborPositions(r, c, WaTorWorldState.FISH, moved);
    if (!fishNeighbors.isEmpty()) {
      int[] pos = randomChoice(fishNeighbors);
      int newR = pos[0];
      int newC = pos[1];
      // Move to the fish cell and eat the fish. Increase energy by the gain amount.
      currentEnergy += sharkEnergyGain;
      if (currentBreed >= sharkBreedTime) {
        // Reproduction: leave a new shark at the original cell.
        grid.getCell(r, c).setNextState(WaTorWorldState.SHARK);
        breedCounter[r][c] = 0;
        sharkEnergy[r][c] = sharkInitialEnergy;
        grid.getCell(newR, newC).setNextState(WaTorWorldState.SHARK);
        breedCounter[newR][newC] = 0;
        sharkEnergy[newR][newC] = currentEnergy;
      } else {
        grid.getCell(newR, newC).setNextState(WaTorWorldState.SHARK);
        breedCounter[newR][newC] = currentBreed;
        sharkEnergy[newR][newC] = currentEnergy;
        grid.getCell(r, c).setNextState(WaTorWorldState.EMPTY);
      }
      moved[r][c] = true;
      moved[newR][newC] = true;
    } else {
      // No adjacent fish: try to move to an empty cell.
      List<int[]> emptyNeighbors = getNeighborPositions(r, c, WaTorWorldState.EMPTY, moved);
      if (!emptyNeighbors.isEmpty()) {
        int[] pos = randomChoice(emptyNeighbors);
        int newR = pos[0];
        int newC = pos[1];
        if (currentBreed >= sharkBreedTime) {
          // Reproduction: spawn a new shark at the original cell.
          grid.getCell(r, c).setNextState(WaTorWorldState.SHARK);
          breedCounter[r][c] = 0;
          sharkEnergy[r][c] = sharkInitialEnergy;
          grid.getCell(newR, newC).setNextState(WaTorWorldState.SHARK);
          breedCounter[newR][newC] = 0;
          sharkEnergy[newR][newC] = currentEnergy;
        } else {
          grid.getCell(newR, newC).setNextState(WaTorWorldState.SHARK);
          breedCounter[newR][newC] = currentBreed;
          sharkEnergy[newR][newC] = currentEnergy;
          grid.getCell(r, c).setNextState(WaTorWorldState.EMPTY);
        }
        moved[r][c] = true;
        moved[newR][newC] = true;
      } else {
        // The shark cannot move: remain in place.
        grid.getCell(r, c).setNextState(WaTorWorldState.SHARK);
        breedCounter[r][c] = currentBreed;
        sharkEnergy[r][c] = currentEnergy;
        moved[r][c] = true;
      }
    }
  }

  /**
   * Returns a list of neighbor positions (using toroidal wrap–around) around (r, c)
   * that currently have the specified target state and have not been moved yet.
   *
   * @param r           row index of the current cell
   * @param c           column index of the current cell
   * @param targetState the state required in the neighbor cell
   * @param moved       boolean grid tracking whether a cell has been updated this step
   * @return a list of {row, col} pairs for matching neighbors
   */
  private List<int[]> getNeighborPositions(int r, int c, WaTorWorldState targetState, boolean[][] moved) {
    List<int[]> neighbors = new ArrayList<>();
    // Define the four cardinal directions: north, east, south, west.
    int[] dr = {-1, 0, 1, 0};
    int[] dc = {0, 1, 0, -1};

    for (int i = 0; i < dr.length; i++) {
      int nr = (r + dr[i] + rows) % rows;  // wrap–around row
      int nc = (c + dc[i] + cols) % cols;    // wrap–around col
      if (!moved[nr][nc] && grid.getCell(nr, nc).getState() == targetState) {
        neighbors.add(new int[]{nr, nc});
      }
    }
    return neighbors;
  }

  /**
   * Utility method to randomly choose one element from a list.
   *
   * @param list a non–empty list of integer arrays (each representing a position)
   * @return a randomly selected position (as an int[2] array)
   */
  private int[] randomChoice(List<int[]> list) {
    int index = (int) (Math.random() * list.size());
    return list.get(index);
  }
}

