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
    initializeLoop(grid.getRows() / 2, grid.getCols() / 2); // Place initial loop in center
  }

  /**
   * Creates the initial Langton's Loop pattern in the center of the grid.
   * This is the minimal pattern that can self-replicate.
   */
  private void initializeLoop(int centerX, int centerY) {
    // Core pathway (forms a loop)
    setState(centerX, centerY, LangtonState.CORE);
    setState(centerX-1, centerY, LangtonState.CORE);
    setState(centerX-1, centerY+1, LangtonState.CORE);
    setState(centerX, centerY+1, LangtonState.CORE);

    // Sheath around core (protective layer)
    setState(centerX-2, centerY-1, LangtonState.SHEATH);
    setState(centerX-2, centerY, LangtonState.SHEATH);
    setState(centerX-2, centerY+1, LangtonState.SHEATH);
    setState(centerX-2, centerY+2, LangtonState.SHEATH);
    setState(centerX-1, centerY-1, LangtonState.SHEATH);
    setState(centerX-1, centerY+2, LangtonState.SHEATH);
    setState(centerX, centerY-1, LangtonState.SHEATH);
    setState(centerX, centerY+2, LangtonState.SHEATH);
    setState(centerX+1, centerY-1, LangtonState.SHEATH);
    setState(centerX+1, centerY, LangtonState.SHEATH);
    setState(centerX+1, centerY+1, LangtonState.SHEATH);
    setState(centerX+1, centerY+2, LangtonState.SHEATH);

    // Initial replication signal
    setState(centerX-1, centerY-1, LangtonState.INIT);
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
    // Get states in NESW order for easier pattern matching
    LangtonState N = neighbors[0];
    LangtonState E = neighbors[1];
    LangtonState S = neighbors[2];
    LangtonState W = neighbors[3];

    // Core loop maintenance rules
    if (currentState == LangtonState.CORE) {
      if (N == LangtonState.SHEATH && E == LangtonState.SHEATH &&
          S == LangtonState.CORE && W == LangtonState.SHEATH) {
        return LangtonState.CORE;  // Maintain core pathway
      }
      if (N == LangtonState.CORE && E == LangtonState.SHEATH &&
          S == LangtonState.SHEATH && W == LangtonState.SHEATH) {
        return LangtonState.TURN;  // Turn corner in core pathway
      }
    }

    // Sheath growth and maintenance
    if (currentState == LangtonState.EMPTY) {
      if (countNeighborType(neighbors, LangtonState.CORE) >= 1 &&
          countNeighborType(neighbors, LangtonState.SHEATH) >= 2) {
        return LangtonState.SHEATH;  // Create new sheath around core
      }
    }

    // Signal propagation for replication
    if (currentState == LangtonState.INIT) {
      if (N == LangtonState.CORE && S == LangtonState.SHEATH) {
        return LangtonState.ADVANCE;  // Start replication signal
      }
    }

    if (currentState == LangtonState.ADVANCE) {
      if (countNeighborType(neighbors, LangtonState.CORE) >= 1 &&
          countNeighborType(neighbors, LangtonState.SHEATH) >= 2) {
        return LangtonState.EXTEND;  // Extend replication arm
      }
    }

    // Growth control
    if (currentState == LangtonState.EXTEND) {
      if (countNeighborType(neighbors, LangtonState.EMPTY) >= 3) {
        return LangtonState.TEMP;  // Prepare for new growth
      }
    }

    if (currentState == LangtonState.TEMP) {
      boolean hasCorePath = (N == LangtonState.CORE || S == LangtonState.CORE) &&
          (E == LangtonState.CORE || W == LangtonState.CORE);
      if (hasCorePath) {
        return LangtonState.CORE;  // Complete new segment
      }
    }

    // Turn corners in replication path
    if (currentState == LangtonState.TURN) {
      if (N == LangtonState.CORE && E == LangtonState.SHEATH &&
          W == LangtonState.SHEATH) {
        return LangtonState.INIT;  // Initialize new replication
      }
    }

    // Default: maintain current state if no rules match
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
