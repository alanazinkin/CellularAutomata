package cellsociety.model;

import cellsociety.controller.SimulationConfig;
import java.util.Collections;
import java.util.Map;

/**
 * Represents an abstract base class for cellular automata simulations operating on a {@link Grid}.
 * <p>
 * This class provides core functionality for initializing simulations, managing state transitions,
 * and enforcing common simulation constraints. Subclasses must implement simulation-specific rules
 * through the abstract methods while benefiting from shared error handling and state management.
 * </p>
 */
public abstract class Simulation {

  private static final String ERROR_GRID_NULL = "Grid cannot be null";
  private static final String ERROR_INITIAL_STATES_EMPTY = "Initial states array is empty";
  private static final String ERROR_INITIAL_STATES_SIZE = "Initial states array size does not match grid dimensions";
  private static final String ERROR_CELL_NULL = "Cell at (%d,%d) is null";
  private static final String ERROR_STATE_MAP_NULL = "State map is null";

  private final Grid grid;
  /** Immutable mapping of states to their visual representations */
  private final Map<StateInterface, String> colorMap;
  /** Immutable mapping of integer values to simulation states */
  private final Map<Integer, StateInterface> stateMap;

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
    initializeGrid(simulationConfig);
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

  public void reinitializeGridStates(SimulationConfig simulationConfig) {
    int cellCount = 0;
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
  private void setCellState(int r, int c, int stateValue) {
    StateInterface newState = stateMap.get(stateValue);
    if (grid.getCell(r, c).getCurrentState().equals(grid.getDefaultState())) {
      grid.getCell(r, c).setCurrentState(newState);
    }
  }

  /**
   * Executes one simulation step by applying rules and updating grid.
   * <p>
   * Implementation sequence: 1. Apply simulation-specific rules through {@link #applyRules()} 2.
   * Commit calculated next states to the grid
   * </p>
   */
  public void step() {
    applyRules();
    grid.applyNextStates();
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
   * Applies simulation-specific rules to calculate next cell states.
   */
  protected abstract void applyRules();

  /**
   * Provides controlled access to simulation grid for subclasses.
   *
   * @return The simulation grid instance
   */
  protected Grid getGrid() {
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
}


