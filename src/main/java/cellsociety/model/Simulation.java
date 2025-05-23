package cellsociety.model;

import cellsociety.controller.SimulationConfig;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an abstract base class for cellular automata simulations operating on a {@link Grid}.
 * <p>
 * This class provides core functionality for initializing simulations, managing state transitions,
 * and enforcing common simulation constraints. Subclasses must implement simulation-specific rules
 * through the abstract methods while benefiting from shared error handling and state management.
 * </p>
 *
 * @author Tatum McKinnis
 */
public abstract class Simulation {

  private static final String ERROR_GRID_NULL = "Grid cannot be null";
  private static final String ERROR_INITIAL_STATES_EMPTY = "Initial states array is empty";
  private static final String ERROR_INITIAL_STATES_SIZE = "Initial states array size does not match grid dimensions";
  private static final String ERROR_CELL_NULL = "Cell at (%d,%d) is null";
  private static final String ERROR_STATE_MAP_NULL = "State map is null";

  private final Grid grid;
  /**
   * Immutable mapping of states to their visual representations
   */
  private Map<StateInterface, String> colorMap;
  /**
   * Immutable mapping of integer values to simulation states
   */
  private final Map<Integer, StateInterface> stateMap;

  private Map<StateInterface, Double> stateCounts;

  private int iterationCount;

  /**
   * Constructs a new Simulation instance with specified configuration and grid.
   *
   * @param simulationConfig Contains initial simulation parameters and state configuration
   * @param grid             The grid structure to use for this simulation
   * @throws IllegalArgumentException If grid is null or initial states are invalid
   * @throws IllegalStateException    If state mapping fails or grid contains null cells
   */
  public Simulation(SimulationConfig simulationConfig, Grid grid) {
    validateGrid(grid);
    this.grid = grid;
    this.colorMap = initializeColorMap();
    this.stateMap = initializeStateMap();
    this.stateCounts = new HashMap<>();
    initializeStateCounts();
    initializeGrid(simulationConfig);
    updateStateCountsMap();
    resetIterationCount();
  }

  /**
   * Validates grid integrity during construction.
   *
   * @param grid The grid to validate
   * @throws IllegalArgumentException If grid is null
   */
  private void validateGrid(Grid grid) {
    if (grid == null) {
      throw new IllegalArgumentException(ERROR_GRID_NULL);
    }
  }

  /**
   * Initializes grid cells with states from simulation configuration.
   *
   * @param simulationConfig Provides initial cell states array
   * @throws IllegalArgumentException If initial states array is empty or size mismatches grid
   *                                  dimensions
   * @throws IllegalStateException    If state map is not initialized or grid contains null cells
   */
  private void initializeGrid(SimulationConfig simulationConfig) {
    int[] initialStates = simulationConfig.getInitialStates();
    validateInitialStates(initialStates);
    int cellCount = 0;
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        validateCell(r, c);
        setCellState(r, c, initialStates[cellCount]);
        cellCount++;
      }
    }
  }

  /**
   * Reinitializes the grid states based on the provided simulation configuration. This method
   * assigns new states to each cell in the grid according to the initial states specified in the
   * {@code simulationConfig}. It ensures that all required data structures (grid cells and state
   * mappings) are properly initialized before proceeding.
   *
   * @param simulationConfig the configuration containing the initial states for the grid
   * @throws NullPointerException if the initial states array is empty, a grid cell is null, or the
   *                              state map is null
   */
  public void reinitializeGridStates(SimulationConfig simulationConfig) {
    int cellCount = 0;
    resetIterationCount();
    initializeStateCounts();
    if (simulationConfig.getInitialStates().length == 0) {
      throw new NullPointerException("Initial states array is empty");
    }
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        if (grid.getCell(r, c) == null) {
          throw new NullPointerException("Cell at (" + r + "," + c + ") is null");
        }
        if (stateMap == null) {
          throw new NullPointerException("State map is null");
        }
        StateInterface newState = stateMap.get(simulationConfig.getInitialStates()[cellCount]);
        grid.getCell(r, c).setCurrentState(newState);
        cellCount++;
      }
    }
  }

  /**
   * Validates initial states array integrity.
   *
   * @param initialStates The array of initial cell states
   * @throws IllegalArgumentException If array is empty or size mismatches grid
   * @throws IllegalStateException    If state map is not initialized
   */
  private void validateInitialStates(int[] initialStates) {
    if (initialStates.length == 0) {
      throw new IllegalArgumentException(ERROR_INITIAL_STATES_EMPTY);
    }
    if (initialStates.length != grid.getRows() * grid.getCols()) {
      throw new IllegalArgumentException(ERROR_INITIAL_STATES_SIZE);
    }
    if (stateMap == null) {
      throw new IllegalStateException(ERROR_STATE_MAP_NULL);
    }
  }

  /**
   * Validates existence of cell at specified coordinates.
   *
   * @param r Row index
   * @param c Column index
   * @throws IllegalStateException If cell at (r,c) is null
   */
  private void validateCell(int r, int c) {
    if (grid.getCell(r, c) == null) {
      throw new IllegalStateException(String.format(ERROR_CELL_NULL, r, c));
    }
  }

  /**
   * Updates cell state if it matches grid's default state.
   *
   * @param r          Row index of cell to update
   * @param c          Column index of cell to update
   * @param stateValue Integer value mapping to new state via stateMap
   */
  public void setCellState(int r, int c, int stateValue) {
    StateInterface newState = stateMap.get(stateValue);
    if (grid.getCell(r, c).getCurrentState().equals(grid.getDefaultState())) {
      grid.getCell(r, c).setCurrentState(newState);
    }
  }

  /**
   * Executes one simulation step by applying rules and updating grid.
   * <p>
   * Implementation sequence: 1. Apply simulation-specific rules through {@link #applyRules()} 2.
   * Commit calculated next states to the grid 3. Increment iteration count
   * </p>
   */
  public void step() {
    applyRules();
    grid.applyNextStates();
    updateStateCountsMap();
    iterationCount++;
  }

  public boolean stepBackOnce() {
    boolean applied = false;
    if (iterationCount > 0) {
      applied = grid.applyPreviousStates();
      if (applied) {
        iterationCount--;
      }
    }
    return applied;
  }

  /**
   * Template method for initializing state-color mappings.
   *
   * @return Complete mapping of states to their display colors
   */
  protected abstract Map<StateInterface, String> initializeColorMap();

  /**
   * Template method for initializing value-state mappings.
   *
   * @return Complete mapping of integer values to simulation states
   */
  protected abstract Map<Integer, StateInterface> initializeStateMap();

  /**
   * Template method for initializing state-cell count mappings.
   */
  protected abstract void initializeStateCounts();

  /**
   * updates the numerical counts of each state for all cells
   */
  protected void updateStateCountsMap() {
    initializeStateCounts();
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        StateInterface currentState = grid.getCell(r, c).getCurrentState();
        updateStateCountValue(currentState);
      }
    }
  }

  /**
   * updates the state count for a given state value by 1 (for a specific cell)
   * <p>
   * method is called for each cell
   * </p>
   *
   * @param state the state to be updated by 1
   */
  public void updateStateCountValue(StateInterface state) {
    if (stateCounts.containsKey(state)) {
      stateCounts.put(state, stateCounts.get(state) + 1);
    } else {
      return;
    }
  }

  /**
   * Applies simulation-specific rules to calculate next cell states.
   */
  protected abstract void applyRules();

  /**
   * Provides controlled access to simulation grid for subclasses.
   *
   * @return The simulation grid instance
   */
  public Grid getGrid() {
    return grid;
  }

  /**
   * Retrieves immutable view of state-color mappings.
   *
   * @return Unmodifiable map of states to colors
   */
  public Map<StateInterface, String> getColorMap() {
    return Collections.unmodifiableMap(colorMap);
  }

  /**
   * Retrieves immutable view of value-state mappings.
   *
   * @return Unmodifiable map of integer values to states
   */
  public Map<Integer, StateInterface> getStateMap() {
    return Collections.unmodifiableMap(stateMap);
  }

  /**
   * Retrieves state-cell count mappings.
   *
   * @return map of state values to numerical cell counts
   */
  public Map<StateInterface, Double> getStateCounts() {
    return stateCounts;
  }

  /**
   * retrieves the iterationCount variables representing the number of iterations of the simulation
   * that have passed
   *
   * @return iterationCount instance variable
   */
  public int retrieveIterationCount() {
    return iterationCount;
  }

  private void resetIterationCount() {
    iterationCount = 0;
  }

  /**
   * sets the stateCounts instance variable to the parameter
   *
   * @param stateCounts map of state interface values to cell counts
   */
  protected void setStateCounts(Map<StateInterface, Double> stateCounts) {
    this.stateCounts = stateCounts;
  }

  /**
   * Sets the color mapping for different cell states in the simulation.
   *
   * @param colorMap a map from state interfaces to color or image path strings
   */
  public void setColorMap(Map<StateInterface, String> colorMap) {
    this.colorMap = new HashMap<>(colorMap);
  }

}


