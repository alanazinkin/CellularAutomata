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
 * Abstract base class for self-replicating loop cellular automata simulations.
 * Provides common functionality for both Langton's Loop and Tempesti's Loop simulations.
 * <p>
 * This class handles the common state management, grid operations, and neighborhood
 * calculations while leaving specific rule implementation to subclasses.
 * </p>
 *
 * @author [Your Name]
 */
public abstract class AbstractLoopSimulation extends Simulation {

  protected static final LangtonState DEFAULT_STATE = LangtonState.EMPTY;
  protected static final int MINIMUM_GRID_SIZE = 5;

  /**
   * Von Neumann neighborhood offsets (North, East, South, West)
   */
  protected static final int[][] VON_NEUMANN_OFFSETS = {
      {-1, 0},  // North
      {0, 1},   // East
      {1, 0},   // South
      {0, -1}   // West
  };

  /**
   * Constructs a new loop simulation with specified configuration.
   *
   * @param simulationConfig Contains initial simulation parameters and grid dimensions
   * @param grid The grid to run the simulation on
   */
  public AbstractLoopSimulation(SimulationConfig simulationConfig, Grid grid) {
    super(simulationConfig, grid);
  }

  /**
   * Initializes a mapping of states to their corresponding color representations.
   * This map is used for visualization purposes, assigning a unique color to each state.
   *
   * @return A map where keys are {@code StateInterface} values and values are CSS class names
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
   * Initializes state counts for all states to 0.0
   */
  @Override
  public void initializeStateCounts() {
    Map<StateInterface, Double> stateCounts = getStateCounts();
    stateCounts.put(LangtonState.EMPTY, 0.0);
    stateCounts.put(LangtonState.SHEATH, 0.0);
    stateCounts.put(LangtonState.CORE, 0.0);
    stateCounts.put(LangtonState.TEMP, 0.0);
    stateCounts.put(LangtonState.TURN, 0.0);
    stateCounts.put(LangtonState.EXTEND, 0.0);
    stateCounts.put(LangtonState.INIT, 0.0);
    stateCounts.put(LangtonState.ADVANCE, 0.0);
    setStateCounts(stateCounts);
  }

  /**
   * Initializes a mapping of integer values to their corresponding states.
   *
   * @return A map where keys are integer values and values are {@code StateInterface} objects
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
   * Applies the rules of the simulation to update the states of cells in the grid.
   */
  @Override
  protected void applyRules() {
    Grid grid = getGrid();
    int rows = grid.getRows();
    int cols = grid.getCols();

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        Cell cell = grid.getCell(row, col);
        if (cell != null) {
          updateCellState(row, col);
        }
      }
    }

    grid.applyNextStates();
  }

  /**
   * Updates the state of a single cell based on its neighborhood configuration and the
   * transition rules.
   *
   * @param row The row index of the cell
   * @param col The column index of the cell
   */
  protected void updateCellState(int row, int col) {
    Cell currentCell = getGrid().getCell(row, col);
    if (currentCell == null) {
      return;
    }

    StateInterface stateInterface = currentCell.getCurrentState();
    if (stateInterface == null) {
      currentCell.setNextState(DEFAULT_STATE);
      return;
    }

    if (!(stateInterface instanceof LangtonState)) {
      currentCell.setNextState(DEFAULT_STATE);
      return;
    }

    LangtonState currentState = (LangtonState) stateInterface;
    LangtonState[] neighbors = getVonNeumannNeighborStates(row, col);

    LangtonState newState = determineNextState(currentState, neighbors);
    currentCell.setNextState(newState);
  }

  /**
   * Retrieves the states of the von Neumann neighborhood (4 adjacent cells).
   *
   * @param row The row index of the center cell
   * @param col The column index of the center cell
   * @return Array of states in order: North, East, South, West
   */
  protected LangtonState[] getVonNeumannNeighborStates(int row, int col) {
    LangtonState[] neighbors = new LangtonState[4];
    Grid grid = getGrid();

    for (int i = 0; i < VON_NEUMANN_OFFSETS.length; i++) {
      int newRow = row + VON_NEUMANN_OFFSETS[i][0];
      int newCol = col + VON_NEUMANN_OFFSETS[i][1];

      if (grid.isValidPosition(newRow, newCol)) {
        neighbors[i] = (LangtonState) grid.getCell(newRow, newCol).getCurrentState();
      } else {
        neighbors[i] = DEFAULT_STATE;
      }
    }

    return neighbors;
  }

  /**
   * Determines the next state of a cell based on specific simulation rules.
   * This method must be implemented by subclasses to define their specific rules.
   *
   * @param currentState The current state of the cell
   * @param neighbors Array of neighbor states in von Neumann neighborhood
   * @return The next state for the cell
   */
  protected abstract LangtonState determineNextState(LangtonState currentState, LangtonState[] neighbors);

  /**
   * Counts how many cells in the neighborhood match a specific state.
   *
   * @param neighbors Array of neighbor states to check
   * @param state The state to count occurrences of
   * @return The number of neighboring cells in the specified state
   */
  protected static int countNeighborType(LangtonState[] neighbors, LangtonState state) {
    int count = 0;
    for (LangtonState neighbor : neighbors) {
      if (neighbor == state) {
        count++;
      }
    }
    return count;
  }

  /**
   * Sets the state of a cell at the specified coordinates.
   *
   * @param x The x-coordinate of the cell
   * @param y The y-coordinate of the cell
   * @param state The state to set
   */
  protected void setState(int x, int y, LangtonState state) {
    Grid grid = getGrid();
    if (grid.isValidPosition(x, y)) {
      Cell cell = grid.getCell(x, y);
      if (cell != null) {
        cell.setCurrentState(state);
        cell.setNextState(state);
      }
    }
  }

  /**
   * Returns the default state used by this simulation.
   *
   * @return The default state
   */
  public StateInterface getDefaultState() {
    return DEFAULT_STATE;
  }
}
