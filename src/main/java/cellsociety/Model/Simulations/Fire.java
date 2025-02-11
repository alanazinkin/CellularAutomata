package cellsociety.Model.Simulations;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.State.FireState;
import cellsociety.Model.StateInterface;
import java.util.Map;
import java.util.Random;
import javafx.scene.paint.Color;

/**
 * Represents a forest fire spread simulation based on the following rules:
 * <ul>
 *   <li>Burning cells become burnt in the next state</li>
 *   <li>Tree cells catch fire if neighbors are burning or through spontaneous combustion</li>
 *   <li>Empty/burnt cells may regrow trees based on probability</li>
 * </ul>
 */
public class Fire extends Simulation {

  private static final Color TREE_COLOR = Color.GREEN;
  private static final Color BURNING_COLOR = Color.RED;
  private static final Color BURNT_COLOR = Color.BROWN;
  private static final Color EMPTY_COLOR = Color.WHITE;

  private static final int EMPTY_STATE_KEY = 0;
  private static final int TREE_STATE_KEY = 1;
  private static final int BURNING_STATE_KEY = 2;
  private static final int BURNT_STATE_KEY = 3;

  /**
   * Probability of empty/burnt cell regrowing a tree
   */
  private final double regrowthProbability;
  /**
   * Probability of spontaneous tree combustion
   */
  private final double ignitionProbability;
  /**
   * Random number generator for probability checks
   */
  private final Random randomNumGenerator;

  /**
   * Constructs a Fire simulation with specified parameters
   *
   * @param simulationConfig Configuration object containing simulation settings
   * @param grid             The grid of cells to simulate
   * @param p                Tree regrowth probability (0 ≤ p ≤ 1)
   * @param f                Spontaneous ignition probability (0 ≤ f ≤ 1)
   * @throws IllegalArgumentException If probabilities are out of valid range
   */
  public Fire(SimulationConfig simulationConfig, Grid grid, double p, double f) {
    super(simulationConfig, grid);
    validateProbability(p, "Regrowth probability");
    validateProbability(f, "Ignition probability");
    this.regrowthProbability = p;
    this.ignitionProbability = f;
    this.randomNumGenerator = new Random();
  }

  /**
   * Validates that a probability value is within [0,1] range
   *
   * @param probability The probability value to validate
   * @param name        Descriptive name for error messages
   * @throws IllegalArgumentException If probability is outside valid range
   */
  private void validateProbability(double probability, String name) {
    if (probability < 0 || probability > 1) {
      throw new IllegalArgumentException(name + " must be between 0 and 1.");
    }
  }

  /**
   * {@inheritDoc} Creates color mapping for fire states: - Tree: Green - Burning: Red - Burnt:
   * Brown - Empty: White
   */
  @Override
  public Map<StateInterface, Color> initializeColorMap() {
    return Map.of(
        FireState.TREE, TREE_COLOR,
        FireState.BURNING, BURNING_COLOR,
        FireState.BURNT, BURNT_COLOR,
        FireState.EMPTY, EMPTY_COLOR
    );
  }

  /**
   * {@inheritDoc} Creates state mapping for fire simulation: - 0: Empty - 1: Tree - 2: Burning - 3:
   * Burnt
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    return Map.of(
        EMPTY_STATE_KEY, FireState.EMPTY,
        TREE_STATE_KEY, FireState.TREE,
        BURNING_STATE_KEY, FireState.BURNING,
        BURNT_STATE_KEY, FireState.BURNT
    );
  }

  /**
   * {@inheritDoc} Applies fire spread rules in two phases: 1. Calculate next states for all cells
   * 2. Update all cells' next states simultaneously
   */
  @Override
  public void applyRules() {
    Grid grid = getGrid();
    StateInterface[][] nextStates = new StateInterface[grid.getRows()][grid.getCols()];

    // Phase 1: Calculate next states
    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        Cell cell = grid.getCell(row, col);
        FireState currentState = validateAndGetState(cell);
        nextStates[row][col] = determineNextState(currentState, row, col);
      }
    }

    // Phase 2: Update next states
    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        grid.getCell(row, col).setNextState(nextStates[row][col]);
      }
    }
  }

  /**
   * Validates and retrieves the current fire state of a cell
   *
   * @param cell The cell to check
   * @return Valid FireState of the cell
   * @throws IllegalStateException If cell has unexpected state type
   */
  private FireState validateAndGetState(Cell cell) {
    StateInterface state = cell.getCurrentState();
    if (!(state instanceof FireState)) {
      throw new IllegalStateException("Invalid state type: " + state.getClass().getSimpleName());
    }
    return (FireState) state;
  }

  /**
   * Determines next state based on current state and simulation rules
   *
   * @param currentState The cell's current fire state
   * @param row          Grid row index
   * @param col          Grid column index
   * @return Next state for the cell
   */
  private StateInterface determineNextState(FireState currentState, int row, int col) {
    return switch (currentState) {
      case BURNING -> FireState.BURNT;
      case TREE -> getNextTreeState(row, col);
      case EMPTY, BURNT -> handleEmptyOrBurntState(currentState);
    };
  }

  /**
   * Calculates next state for tree cells
   *
   * @param row Grid row index
   * @param col Grid column index
   * @return BURNING if neighbors are burning or spontaneous ignition occurs, otherwise remains TREE
   */
  private FireState getNextTreeState(int row, int col) {
    return hasBurningNeighbor(row, col) || probabilityEvent(ignitionProbability) ?
        FireState.BURNING : FireState.TREE;
  }

  /**
   * Handles state transitions for empty/burnt cells
   *
   * @param currentState Either EMPTY or BURNT state
   * @return TREE if regrowth occurs, otherwise original state
   */
  private StateInterface handleEmptyOrBurntState(FireState currentState) {
    return probabilityEvent(regrowthProbability) ? FireState.TREE : currentState;
  }

  /**
   * Tests if a random event occurs based on given probability
   *
   * @param probability Chance of event occurring (0 ≤ probability ≤ 1)
   * @return true if event occurs, false otherwise
   */
  private boolean probabilityEvent(double probability) {
    return randomNumGenerator.nextDouble() < probability;
  }

  /**
   * Checks if any neighbors are burning
   *
   * @param row Grid row index
   * @param col Grid column index
   * @return true if at least one neighboring cell is burning
   */
  private boolean hasBurningNeighbor(int row, int col) {
    return getGrid().getNeighbors(row, col).stream()
        .anyMatch(neighbor -> neighbor.getCurrentState() == FireState.BURNING);
  }

  /**
   * {@inheritDoc} Executes one simulation step: 1. Applies rules to calculate next states 2.
   * Commits all state changes simultaneously
   */
  @Override
  public void step() {
    applyRules();
    commitNextStates();
  }

  /**
   * Applies calculated next states to all cells and resets state buffers
   */
  private void commitNextStates() {
    Grid grid = getGrid();
    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        Cell cell = grid.getCell(row, col);
        cell.applyNextState();
        cell.resetNextState();
      }
    }
  }
}




