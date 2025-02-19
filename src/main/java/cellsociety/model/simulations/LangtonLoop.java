package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.state.LangtonState;
import cellsociety.model.StateInterface;
import java.util.HashMap;
import java.util.Map;

/**
 * Simulates Langton's Loop cellular automaton, a self-replicating pattern that demonstrates
 * emergent behavior in cellular automata. The simulation uses 8 states and operates on a von
 * Neumann neighborhood (4 adjacent cells).
 * <p>
 * The automaton demonstrates self-replication through a complex interaction of states that form a
 * loop structure capable of creating copies of itself. The replication process uses a "genome"
 * encoded as state sequences, with signals traveling along sheathed pathways.
 * </p>
 *
 * @author Tatum McKinnis
 */
public class LangtonLoop extends Simulation {

  private static final LangtonState DEFAULT_STATE = LangtonState.EMPTY;
  private static final int MINIMUM_GRID_SIZE = 5;

  /**
   * Von Neumann neighborhood offsets (North, East, South, West)
   */
  private static final int[][] VON_NEUMANN_OFFSETS = {
      {-1, 0},  // North
      {0, 1},   // East
      {1, 0},   // South
      {0, -1}   // West
  };

  /**
   * Constructs a new Langton's Loop simulation with specified configuration.
   *
   * @param simulationConfig Contains initial simulation parameters and grid dimensions
   * @throws IllegalArgumentException if grid dimensions are invalid or initial states array is
   *                                  empty
   */
  public LangtonLoop(SimulationConfig simulationConfig, Grid grid) {
    super(simulationConfig, grid);
    if (grid.getRows() < MINIMUM_GRID_SIZE || grid.getCols() < MINIMUM_GRID_SIZE) {
      throw new IllegalArgumentException(
          String.format("Grid must be at least %dx%d to contain a Langton's Loop",
              MINIMUM_GRID_SIZE, MINIMUM_GRID_SIZE)
      );
    }
    initializeLoop(grid.getRows() / 2, grid.getCols() / 2);
  }

  /**
   * Creates the initial Langton's Loop pattern in the center of the grid.
   * This is the minimal pattern that can self-replicate.
   */
  private void initializeLoop(int centerX, int centerY) {
    // Create core loop structure
    setState(centerX, centerY, LangtonState.CORE);
    setState(centerX-1, centerY, LangtonState.CORE);
    setState(centerX-1, centerY+1, LangtonState.CORE);
    setState(centerX, centerY+1, LangtonState.CORE);

    // Create protective sheath
    for (int dx = -2; dx <= 1; dx++) {
      setState(centerX + dx, centerY - 1, LangtonState.SHEATH);  // Top row
      setState(centerX + dx, centerY + 2, LangtonState.SHEATH);  // Bottom row
    }
    for (int dy = -1; dy <= 2; dy++) {
      setState(centerX - 2, centerY + dy, LangtonState.SHEATH);  // Left column
      setState(centerX + 1, centerY + dy, LangtonState.SHEATH);  // Right column
    }

    // Add multiple signal initiators
    setState(centerX - 1, centerY - 1, LangtonState.INIT);
    setState(centerX, centerY - 1, LangtonState.ADVANCE);
    setState(centerX + 1, centerY - 1, LangtonState.EXTEND);

    // Add some additional signal points to ensure continuous movement
    setState(centerX - 2, centerY - 2, LangtonState.INIT);
    setState(centerX + 1, centerY - 2, LangtonState.ADVANCE);
  }

  private void setState(int x, int y, LangtonState state) {
    if (getGrid().isValidPosition(x, y)) {
      getGrid().getCell(x, y).setCurrentState(state);
    }
  }

  /**
   * Initializes a mapping of Langton's Ant states to their corresponding color representations.
   * This map is used for visualization purposes, assigning a unique color to each state.
   *
   * @return A map where keys are {@code StateInterface} values representing different states and
   * values are hexadecimal color codes as strings.
   */
  @Override
  protected Map<StateInterface, String> initializeColorMap() {
    Map<StateInterface, String> colorMap = new HashMap<>();
    colorMap.put(LangtonState.EMPTY, "langton-state-empty");
    colorMap.put(LangtonState.SHEATH, "langton-state-sheath");
    colorMap.put(LangtonState.CORE, "langton-state-core");
    colorMap.put(LangtonState.TEMP, "langton-state-temp");
    colorMap.put(LangtonState.TURN, "langton-state-turn");
    colorMap.put(LangtonState.EXTEND, "langton-state-extend");
    colorMap.put(LangtonState.INIT, "langton-state-init");
    colorMap.put(LangtonState.ADVANCE, "langton-state-advance");
    return colorMap;
  }

  /**
   * Initializes a mapping of integer values to their corresponding Langton's Ant states. This map
   * is used to associate numerical representations with specific states, which can be useful for
   * grid-based simulations.
   *
   * @return A map where keys are integer values representing different states and values are
   * {@code StateInterface} objects defining those states.
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    Map<Integer, StateInterface> stateMap = new HashMap<>();
    stateMap.put(0, LangtonState.EMPTY);
    stateMap.put(1, LangtonState.SHEATH);
    stateMap.put(2, LangtonState.CORE);
    stateMap.put(3, LangtonState.TEMP);
    stateMap.put(4, LangtonState.TURN);
    stateMap.put(5, LangtonState.EXTEND);
    stateMap.put(6, LangtonState.INIT);
    stateMap.put(7, LangtonState.ADVANCE);
    return stateMap;
  }

  /**
   * Applies the rules of the simulation to update the states of cells in the grid. Iterates through
   * each cell and updates its state based on predefined rules.
   */
  @Override
  protected void applyRules() {
    Grid grid = getGrid();
    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        updateCellState(row, col);
      }
    }
    grid.applyNextStates();
  }

  /**
   * Updates the state of a single cell based on its von Neumann neighborhood configuration and the
   * Langton's Loop transition rules.
   *
   * @param row The row index of the cell
   * @param col The column index of the cell
   */
  private void updateCellState(int row, int col) {
    Cell currentCell = getGrid().getCell(row, col);
    LangtonState currentState = (LangtonState) currentCell.getCurrentState();
    LangtonState[] neighbors = getVonNeumannNeighborStates(row, col);

    LangtonState newState = applyTransitionRules(currentState, neighbors);
    currentCell.setNextState(newState);
  }

  /**
   * Retrieves the states of the von Neumann neighborhood (4 adjacent cells).
   *
   * @param row The row index of the center cell
   * @param col The column index of the center cell
   * @return Array of states in order: North, East, South, West
   */
  private LangtonState[] getVonNeumannNeighborStates(int row, int col) {
    LangtonState[] neighbors = new LangtonState[4];
    Grid grid = getGrid();

    for (int i = 0; i < VON_NEUMANN_OFFSETS.length; i++) {
      int newRow = row + VON_NEUMANN_OFFSETS[i][0];
      int newCol = col + VON_NEUMANN_OFFSETS[i][1];

      if (grid.isValidPosition(newRow, newCol)) {
        neighbors[i] = (LangtonState) grid.getCell(newRow, newCol).getCurrentState();
      } else {
        neighbors[i] = LangtonState.EMPTY;
      }
    }

    return neighbors;
  }

  /**
   * Applies Langton's Loop transition rules to determine the next state of a cell.
   *
   * @param currentState The current state of the cell
   * @param neighbors    Array of neighbor states in von Neumann neighborhood
   * @return The next state for the cell
   */
  private LangtonState applyTransitionRules(LangtonState currentState, LangtonState[] neighbors) {
    LangtonState N = neighbors[0];
    LangtonState E = neighbors[1];
    LangtonState S = neighbors[2];
    LangtonState W = neighbors[3];

    switch (currentState) {
      case EMPTY:
        // Empty cells become sheath if they have enough signal
        if (countNeighborType(neighbors, LangtonState.EXTEND) >= 1 ||
            countNeighborType(neighbors, LangtonState.CORE) >= 2) {
          return LangtonState.SHEATH;
        }
        break;

      case INIT:
        // Init always tries to propagate signals
        if (N == LangtonState.CORE || S == LangtonState.CORE) {
          return LangtonState.ADVANCE;
        }
        break;

      case ADVANCE:
        // Advance signals move along the sheath
        if (countNeighborType(neighbors, LangtonState.SHEATH) >= 1) {
          return LangtonState.EXTEND;
        }
        // Or can start new paths
        if (countNeighborType(neighbors, LangtonState.CORE) >= 1) {
          return LangtonState.EXTEND;
        }
        break;

      case EXTEND:
        // Extension signals create new structure
        if (countNeighborType(neighbors, LangtonState.EMPTY) >= 2) {
          return LangtonState.TEMP;
        }
        // Or continue existing paths
        if (countNeighborType(neighbors, LangtonState.SHEATH) >= 2) {
          return LangtonState.CORE;
        }
        break;

      case TEMP:
        // Temporary states transition to core when near existing structure
        if (countNeighborType(neighbors, LangtonState.CORE) >= 1 ||
            countNeighborType(neighbors, LangtonState.SHEATH) >= 2) {
          return LangtonState.CORE;
        }
        break;

      case CORE:
        // Core can turn to create corners
        if (countNeighborType(neighbors, LangtonState.SHEATH) >= 3) {
          return LangtonState.TURN;
        }
        break;

      case TURN:
        // Turns initiate new signal paths
        if ((N == LangtonState.CORE && W == LangtonState.SHEATH) ||
            (S == LangtonState.CORE && E == LangtonState.SHEATH)) {
          return LangtonState.INIT;
        }
        break;

      case SHEATH:
        // Sheath can become core if surrounded by the right pattern
        if (countNeighborType(neighbors, LangtonState.CORE) >= 2) {
          return LangtonState.CORE;
        }
        // Or can extend if near signals
        if (countNeighborType(neighbors, LangtonState.EXTEND) >= 1) {
          return LangtonState.EXTEND;
        }
        break;
    }

    return currentState;
  }

  /**
   * Counts the number of neighbors that match a specific state.
   *
   * @param neighbors Array of neighbor states
   * @param state     State to count
   * @return Number of neighbors matching the specified state
   */
  private int countNeighborType(LangtonState[] neighbors, LangtonState state) {
    int count = 0;
    for (LangtonState neighbor : neighbors) {
      if (neighbor == state) {
        count++;
      }
    }
    return count;
  }
}
