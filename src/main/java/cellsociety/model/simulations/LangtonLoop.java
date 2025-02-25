package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.state.LangtonState;
import cellsociety.model.StateInterface;
import java.util.HashMap;
import java.util.List;
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
   * Represents a single transition rule for the Langton Loop automaton.
   * Each rule consists of a current state, the next state it should transition to,
   * and a predicate that tests if the neighboring cells meet the required conditions.
   */
  private record TransitionRule(
      LangtonState currentState,
      LangtonState nextState,
      NeighborPredicate neighborTest
  ) {}

  /**
   * Functional interface for testing if a cell's neighbors meet specific conditions
   * for a state transition. Used by TransitionRule to determine if a transition should occur.
   */
  @FunctionalInterface
  private interface NeighborPredicate {
    boolean test(LangtonState[] neighbors);
  }

  /**
   * Defines all possible state transitions in the Langton's Loop automaton.
   * Each rule specifies the current state, the next state, and the conditions
   * under which the transition should occur based on the neighboring cells.
   *
   * Rules have been modified and enhanced to create more dynamic behavior
   * and resolve stasis issues.
   */
  private static final List<TransitionRule> TRANSITION_RULES = List.of(
      // Empty cell rules - allow growth
      new TransitionRule(LangtonState.EMPTY, LangtonState.SHEATH,
          neighbors -> countNeighborType(neighbors, LangtonState.EXTEND) >= 1 ||
              countNeighborType(neighbors, LangtonState.CORE) >= 2),

      // Propagation rules for signal
      new TransitionRule(LangtonState.INIT, LangtonState.ADVANCE,
          neighbors -> neighbors[0] == LangtonState.CORE ||
              neighbors[2] == LangtonState.CORE ||
              countNeighborType(neighbors, LangtonState.EXTEND) >= 1),

      new TransitionRule(LangtonState.ADVANCE, LangtonState.EXTEND,
          neighbors -> countNeighborType(neighbors, LangtonState.SHEATH) >= 1 ||
              countNeighborType(neighbors, LangtonState.CORE) >= 1 ||
              countNeighborType(neighbors, LangtonState.INIT) >= 1),

      // Extend transformation rules
      new TransitionRule(LangtonState.EXTEND, LangtonState.TEMP,
          neighbors -> countNeighborType(neighbors, LangtonState.EMPTY) >= 2 ||
              countNeighborType(neighbors, LangtonState.ADVANCE) >= 1),

      new TransitionRule(LangtonState.EXTEND, LangtonState.CORE,
          neighbors -> countNeighborType(neighbors, LangtonState.SHEATH) >= 2 ||
              countNeighborType(neighbors, LangtonState.TEMP) >= 1),

      // Temp to Core transformation
      new TransitionRule(LangtonState.TEMP, LangtonState.CORE,
          neighbors -> countNeighborType(neighbors, LangtonState.CORE) >= 1 ||
              countNeighborType(neighbors, LangtonState.SHEATH) >= 2 ||
              countNeighborType(neighbors, LangtonState.EXTEND) >= 2),

      // Core transformation rules - enable more activity
      new TransitionRule(LangtonState.CORE, LangtonState.TURN,
          neighbors -> countNeighborType(neighbors, LangtonState.SHEATH) >= 3 ||
              (countNeighborType(neighbors, LangtonState.EXTEND) >= 2 &&
                  countNeighborType(neighbors, LangtonState.SHEATH) >= 1)),

      // Turn transformation rules
      new TransitionRule(LangtonState.TURN, LangtonState.INIT,
          neighbors -> (neighbors[0] == LangtonState.CORE && neighbors[3] == LangtonState.SHEATH) ||
              (neighbors[2] == LangtonState.CORE && neighbors[1] == LangtonState.SHEATH) ||
              countNeighborType(neighbors, LangtonState.EXTEND) >= 2),

      // Sheath transformation rules
      new TransitionRule(LangtonState.SHEATH, LangtonState.CORE,
          neighbors -> countNeighborType(neighbors, LangtonState.CORE) >= 2 ||
              (countNeighborType(neighbors, LangtonState.TURN) >= 1 &&
                  countNeighborType(neighbors, LangtonState.CORE) >= 1)),

      new TransitionRule(LangtonState.SHEATH, LangtonState.EXTEND,
          neighbors -> countNeighborType(neighbors, LangtonState.EXTEND) >= 1 ||
              countNeighborType(neighbors, LangtonState.ADVANCE) >= 1),

      // Additional rules to break stasis
      new TransitionRule(LangtonState.CORE, LangtonState.ADVANCE,
          neighbors -> countNeighborType(neighbors, LangtonState.INIT) >= 1 &&
              countNeighborType(neighbors, LangtonState.EMPTY) >= 2),

      new TransitionRule(LangtonState.SHEATH, LangtonState.INIT,
          neighbors -> countNeighborType(neighbors, LangtonState.EMPTY) >= 3 &&
              countNeighborType(neighbors, LangtonState.CORE) >= 1)
  );

  /**
   * Constructs a new Langton's Loop simulation with specified configuration.
   *
   * @param simulationConfig Contains initial simulation parameters and grid dimensions
   * @param grid The grid to run the simulation on
   * @throws IllegalArgumentException if grid dimensions are invalid or initial states array is
   *                                  empty
   */
  public LangtonLoop(SimulationConfig simulationConfig, Grid grid) {
    super(simulationConfig, grid);
    System.out.println("Creating LangtonLoop simulation");

    validateGridSize(grid);

    if (grid == null) {
      throw new IllegalArgumentException("Grid cannot be null");
    }

    try {
      initializeWithRandomStates();

      int centerRow = grid.getRows() / 2;
      int centerCol = grid.getCols() / 2;
      initializeLoop(centerRow, centerCol);

      addAdditionalPatterns();

      System.out.println("LangtonLoop simulation initialization complete");

      printGridState();

    } catch (Exception e) {
      System.out.println("Error during initialization: " + e.getMessage());
      e.printStackTrace();
      initializeWithDefaultState();
      initializeLoop(grid.getRows() / 2, grid.getCols() / 2);
    }
  }

  /**
   * Validates that the grid meets the minimum size requirements.
   *
   * @param grid The grid to validate
   * @throws IllegalArgumentException if the grid is too small
   */
  private void validateGridSize(Grid grid) {
    if (grid.getRows() < MINIMUM_GRID_SIZE || grid.getCols() < MINIMUM_GRID_SIZE) {
      throw new IllegalArgumentException(
          String.format("Grid must be at least %dx%d to contain a Langton's Loop",
              MINIMUM_GRID_SIZE, MINIMUM_GRID_SIZE)
      );
    }
  }

  /**
   * Initializes the entire grid with the default state.
   */
  /**
   * Initializes the entire grid with the default state.
   * This ensures that every cell has a valid initial state before the simulation begins.
   */
  private void initializeWithDefaultState() {
    System.out.println("Initializing entire grid with default state");
    Grid grid = getGrid();
    int rows = grid.getRows();
    int cols = grid.getCols();
    System.out.println("Grid size: " + rows + "x" + cols);

    int cellsInitialized = 0;
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        Cell cell = grid.getCell(row, col);
        if (cell != null) {
          cell.setCurrentState(DEFAULT_STATE);
          cellsInitialized++;
        } else {
          System.out.println("Warning: Null cell at position (" + row + ", " + col + ")");
        }
      }
    }
    System.out.println("Initialized " + cellsInitialized + " cells with default state");
  }

  /**
   * Creates the initial Langton's Loop pattern in the center of the grid.
   * This is the minimal pattern that can self-replicate.
   *
   * @param centerX The x-coordinate for the center of the loop
   * @param centerY The y-coordinate for the center of the loop
   */
  private void initializeLoop(int centerX, int centerY) {
    Grid grid = getGrid();

    setState(centerX, centerY, LangtonState.CORE);
    setState(centerX-1, centerY, LangtonState.CORE);
    setState(centerX-1, centerY+1, LangtonState.CORE);
    setState(centerX, centerY+1, LangtonState.CORE);

    for (int dx = -2; dx <= 1; dx++) {
      setState(centerX + dx, centerY - 1, LangtonState.SHEATH);  // Top row
      setState(centerX + dx, centerY + 2, LangtonState.SHEATH);  // Bottom row
    }
    for (int dy = -1; dy <= 2; dy++) {
      setState(centerX - 2, centerY + dy, LangtonState.SHEATH);  // Left column
      setState(centerX + 1, centerY + dy, LangtonState.SHEATH);  // Right column
    }

    setState(centerX - 1, centerY - 1, LangtonState.INIT);
    setState(centerX, centerY - 1, LangtonState.ADVANCE);
    setState(centerX + 1, centerY - 1, LangtonState.EXTEND);

    setState(centerX - 2, centerY - 2, LangtonState.INIT);
    setState(centerX + 1, centerY - 2, LangtonState.ADVANCE);
  }

  /**
   * Sets the state of a cell at the specified coordinates.
   *
   * @param x The x-coordinate of the cell
   * @param y The y-coordinate of the cell
   * @param state The state to set
   */
  private void setState(int x, int y, LangtonState state) {
    Grid grid = getGrid();
    if (grid.isValidPosition(x, y)) {
      Cell cell = grid.getCell(x, y);
      if (cell != null) {
        cell.setCurrentState(state);
        cell.setNextState(state);
      } else {
        System.out.println("Warning: Cannot set state at (" + x + "," + y + ") - cell is null");
      }
    } else {
      System.out.println("Position out of bounds: (" + x + "," + y + ")");
    }
  }

  /**
   * Initializes the grid with random states to create more dynamic behavior.
   * This increases the chance of interesting patterns emerging.
   */
  private void initializeWithRandomStates() {
    System.out.println("Initializing grid with random states");
    Grid grid = getGrid();
    int rows = grid.getRows();
    int cols = grid.getCols();

    // Initialize with mostly empty cells, but some random states
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        Cell cell = grid.getCell(row, col);
        if (cell != null) {
          // 80% chance of empty, 20% chance of random state
          double random = Math.random();
          if (random > 0.8) {
            // Choose a random non-empty state
            LangtonState state = getRandomState();
            cell.setCurrentState(state);
            cell.setNextState(state);
          } else {
            cell.setCurrentState(DEFAULT_STATE);
            cell.setNextState(DEFAULT_STATE);
          }
        }
      }
    }
    System.out.println("Random initialization complete");
  }

  /**
   * Adds additional patterns at different locations in the grid to increase activity.
   */
  private void addAdditionalPatterns() {
    Grid grid = getGrid();
    int rows = grid.getRows();
    int cols = grid.getCols();

    // Add a small pattern in the top-left quadrant
    int x1 = rows / 4;
    int y1 = cols / 4;
    addSmallPattern(x1, y1);

    // Add another small pattern in the bottom-right quadrant
    int x2 = (3 * rows) / 4;
    int y2 = (3 * cols) / 4;
    addSmallPattern(x2, y2);

    System.out.println("Added additional patterns at (" + x1 + "," + y1 + ") and (" + x2 + "," + y2 + ")");
  }

  /**
   * Adds a small pattern at the specified location.
   *
   * @param x The x-coordinate for the pattern center
   * @param y The y-coordinate for the pattern center
   */
  private void addSmallPattern(int x, int y) {
    // Create a small cross pattern
    setState(x, y, LangtonState.CORE);
    setState(x-1, y, LangtonState.SHEATH);
    setState(x+1, y, LangtonState.SHEATH);
    setState(x, y-1, LangtonState.SHEATH);
    setState(x, y+1, LangtonState.SHEATH);

    // Add initiators
    setState(x-1, y-1, LangtonState.INIT);
    setState(x+1, y+1, LangtonState.EXTEND);
  }

  /**
   * Returns a random LangtonState excluding EMPTY.
   *
   * @return A random non-empty LangtonState
   */
  private LangtonState getRandomState() {
    LangtonState[] states = LangtonState.values();
    LangtonState state;
    do {
      int index = (int)(Math.random() * states.length);
      state = states[index];
    } while (state == LangtonState.EMPTY);
    return state;
  }

  /**
   * Prints a text representation of the current grid state for debugging.
   */
  private void printGridState() {
    Grid grid = getGrid();
    int rows = grid.getRows();
    int cols = grid.getCols();

    System.out.println("Current grid state (" + rows + "x" + cols + "):");
    StringBuilder builder = new StringBuilder();

    // Only print a smaller section around the center for readability
    int centerRow = rows / 2;
    int centerCol = cols / 2;
    int range = 5; // Print 5 cells in each direction from center

    int startRow = Math.max(0, centerRow - range);
    int endRow = Math.min(rows - 1, centerRow + range);
    int startCol = Math.max(0, centerCol - range);
    int endCol = Math.min(cols - 1, centerCol + range);

    for (int row = startRow; row <= endRow; row++) {
      for (int col = startCol; col <= endCol; col++) {
        Cell cell = grid.getCell(row, col);
        if (cell != null && cell.getCurrentState() != null) {
          LangtonState state = (LangtonState) cell.getCurrentState();
          char symbol = getSymbolForState(state);
          builder.append(symbol);
        } else {
          builder.append('?');
        }
      }
      builder.append('\n');
    }

    System.out.println(builder.toString());
  }

  /**
   * Returns a character symbol for the given state for grid printing.
   *
   * @param state The state to convert to a symbol
   * @return A character representing the state
   */
  private char getSymbolForState(LangtonState state) {
    switch (state) {
      case EMPTY: return '.';
      case CORE: return 'C';
      case SHEATH: return 'S';
      case INIT: return 'I';
      case EXTEND: return 'E';
      case ADVANCE: return 'A';
      case TEMP: return 'T';
      case TURN: return 'R';
      default: return '?';
    }
  }

  /**
   * Initializes a mapping of Langton's Loop states to their corresponding color representations.
   * This map is used for visualization purposes, assigning a unique color to each state.
   *
   * @return A map where keys are {@code StateInterface} values representing different states and
   * values are CSS class names for styling.
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
   * Initializes a mapping of integer values to their corresponding Langton's Loop states. This map
   * is used to associate numerical representations with specific states, which can be useful for
   * grid-based simulations and file I/O operations.
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
    int rows = grid.getRows();
    int cols = grid.getCols();

    // Debug information to track the simulation
    System.out.println("Applying rules to grid of size: " + rows + "x" + cols);

    // Count state frequencies before update
    Map<LangtonState, Integer> stateCounts = new HashMap<>();
    for (LangtonState state : LangtonState.values()) {
      stateCounts.put(state, 0);
    }

    // First pass: calculate next states for all cells
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        Cell cell = grid.getCell(row, col);
        if (cell != null) {
          // Count current states
          LangtonState currentState = (LangtonState) cell.getCurrentState();
          stateCounts.put(currentState, stateCounts.get(currentState) + 1);

          // Calculate next state
          updateCellState(row, col);
        } else {
          System.out.println("Warning: Null cell at position (" + row + ", " + col + ") during rule application");
        }
      }
    }

    // Log state distribution
    System.out.println("State distribution before update:");
    for (Map.Entry<LangtonState, Integer> entry : stateCounts.entrySet()) {
      System.out.println("  " + entry.getKey() + ": " + entry.getValue());
    }

    // Apply all next states at once
    grid.applyNextStates();
    System.out.println("Applied next states to all cells");
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
    if (currentCell == null) {
      System.out.println("Null cell at (" + row + "," + col + ")");
      return;
    }

    StateInterface stateInterface = currentCell.getCurrentState();
    if (stateInterface == null) {
      System.out.println("Null state at (" + row + "," + col + ")");
      currentCell.setNextState(DEFAULT_STATE);
      return;
    }

    if (!(stateInterface instanceof LangtonState)) {
      System.out.println("Non-Langton state at (" + row + "," + col + "): " + stateInterface.getClass().getName());
      currentCell.setNextState(DEFAULT_STATE);
      return;
    }

    LangtonState currentState = (LangtonState) stateInterface;
    LangtonState[] neighbors = getVonNeumannNeighborStates(row, col);

    LangtonState newState = applyTransitionRules(currentState, neighbors);

    // Debug output for state transitions
    if (newState != currentState) {
      System.out.println("Cell at (" + row + "," + col + ") changing from " + currentState + " to " + newState);
    }

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
        neighbors[i] = DEFAULT_STATE; // Use default state for out-of-bounds cells
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
    // Verify that neighbors array is correctly populated
    if (neighbors == null) {
      System.out.println("Null neighbors array for state " + currentState);
      return currentState;
    }

    // Debug information about the neighborhood
    StringBuilder neighborhood = new StringBuilder();
    neighborhood.append("Neighborhood for ").append(currentState).append(": [");
    for (LangtonState neighbor : neighbors) {
      neighborhood.append(neighbor).append(", ");
    }
    if (neighbors.length > 0) {
      neighborhood.setLength(neighborhood.length() - 2); // Remove trailing comma and space
    }
    neighborhood.append("]");

    // Check if any rules apply
    for (TransitionRule rule : TRANSITION_RULES) {
      if (rule.currentState() == currentState) {
        try {
          if (rule.neighborTest().test(neighbors)) {
            System.out.println(neighborhood + " -> Transition from " + currentState +
                " to " + rule.nextState());
            return rule.nextState();
          }
        } catch (Exception e) {
          System.out.println("Exception while testing rule " + rule + ": " + e.getMessage());
          e.printStackTrace();
        }
      }
    }

    // If no rule applies, keep the current state
    System.out.println(neighborhood + " -> No rule applied, keeping " + currentState);
    return currentState;
  }

  /**
   * Counts how many cells in the neighborhood match a specific state.
   *
   * @param neighbors Array of neighbor states to check
   * @param state The state to count occurrences of
   * @return The number of neighboring cells in the specified state
   */
  private static int countNeighborType(LangtonState[] neighbors, LangtonState state) {
    int count = 0;
    for (LangtonState neighbor : neighbors) {
      if (neighbor == state) {
        count++;
      }
    }
    return count;
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