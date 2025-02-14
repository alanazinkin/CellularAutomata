package cellsociety.Model.Simulations;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.State.PercolationState;
import cellsociety.Model.StateInterface;
import java.util.List;
import java.util.Map;
import javafx.scene.paint.Color;

/**
 * A probabilistic percolation simulation that models fluid flow through a porous medium.
 * <p>
 * Each cell can be in one of three states:
 * <ul>
 *   <li>{@link PercolationState#BLOCKED} – the cell is blocked and cannot be percolated.</li>
 *   <li>{@link PercolationState#OPEN} – the cell is open but not yet percolated.</li>
 *   <li>{@link PercolationState#PERCOLATED} – the cell is open and has been percolated.</li>
 * </ul>
 * <p>
 * The simulation uses a probabilistic rule: if an open cell has at least one percolated neighbor,
 * then the cell becomes percolated with a probability specified at construction.
 * @author Tatum McKinnis
 */
public class Percolation extends Simulation {

  public static final int OPEN_ID = 1;
  public static final int PERCOLATED_ID = 2;
  public static final int BLOCKED_ID = 0;

  private static final Color OPEN_COLOR = Color.WHITE;
  private static final Color PERCOLATED_COLOR = Color.LIGHTBLUE;
  private static final Color BLOCKED_COLOR = Color.BLACK;

  /**
   * Maximum value for probability calculations
   */
  private static final double MAX_PROBABILITY = 1.0;
  /**
   * Minimum valid probability value
   */
  private static final double MIN_PROBABILITY = 0.0;

  private final double percolationProbability;

  /**
   * Constructs a new Percolation simulation with the specified configuration, grid, and percolation
   * probability.
   *
   * @param simulationConfig       the configuration settings for the simulation
   * @param grid                   the grid on which the percolation simulation is performed
   * @param percolationProbability the probability that an open cell percolates when adjacent to a
   *                               percolated cell; must be between 0 and 1 inclusive.
   * @throws IllegalArgumentException if the grid is null or the percolationProbability is not in
   *                                  [0, 1]
   * @throws NullPointerException     if either simulationConfig or grid parameters are null
   */
  public Percolation(SimulationConfig simulationConfig, Grid grid, double percolationProbability) {
    super(simulationConfig, grid);
    validateProbability(percolationProbability);
    this.percolationProbability = percolationProbability;
  }

  /**
   * Validates that the given probability falls within the acceptable range [0, 1].
   *
   * @param probability the probability value to validate
   * @throws IllegalArgumentException if the probability is outside the valid range
   */
  private void validateProbability(double probability) {
    if (probability < MIN_PROBABILITY || probability > MAX_PROBABILITY) {
      throw new IllegalArgumentException(
          String.format("Percolation probability must be between %.1f and %.1f inclusive.",
              MIN_PROBABILITY, MAX_PROBABILITY));
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * Initializes the color mapping specific to Percolation states:
   * <ul>
   *   <li>OPEN - White</li>
   *   <li>PERCOLATED - Light Blue</li>
   *   <li>BLOCKED - Black</li>
   * </ul>
   */
  @Override
  public Map<StateInterface, String> initializeColorMap() {
    return Map.of(
        PercolationState.OPEN, "percolation-state-open",
        PercolationState.PERCOLATED, "percolation-state-percolated",
        PercolationState.BLOCKED, "percolation-state-blocked"
    );
  }

  /**
   * {@inheritDoc}
   * <p>
   * Initializes the state mapping using the predefined integer identifiers:
   * <ul>
   *   <li>0 - OPEN</li>
   *   <li>1 - PERCOLATED</li>
   *   <li>2 - BLOCKED</li>
   * </ul>
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    return Map.of(OPEN_ID, PercolationState.OPEN, PERCOLATED_ID, PercolationState.PERCOLATED,
        BLOCKED_ID, PercolationState.BLOCKED);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Applies percolation rules to all cells in the grid:
   * <ul>
   *   <li>BLOCKED and PERCOLATED cells remain unchanged</li>
   *   <li>OPEN cells check neighbors for PERCOLATED cells and may percolate probabilistically</li>
   * </ul>
   */
  @Override
  public void applyRules() {
    final int numRows = getGrid().getRows();
    final int numCols = getGrid().getCols();

    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < numCols; col++) {
        Cell cell = getGrid().getCell(row, col);
        PercolationState currentState = validateAndGetState(cell);

        if (isStaticState(currentState)) {
          cell.setNextState(currentState);
        } else {
          cell.setNextState(determineNextStateForOpenCell(row, col));
        }
      }
    }
  }

  /**
   * Validates that a cell's state is compatible with the Percolation simulation.
   *
   * @param cell the cell to validate
   * @return the validated PercolationState of the cell
   * @throws IllegalStateException if the cell contains an invalid state type
   */
  private PercolationState validateAndGetState(Cell cell) {
    StateInterface state = cell.getCurrentState();
    if (!(state instanceof PercolationState)) {
      throw new IllegalStateException(
          "Invalid cell state type: " + state.getClass().getSimpleName());
    }
    return (PercolationState) state;
  }

  /**
   * Determines if a state is immutable between simulation steps.
   *
   * @param state the state to check
   * @return true if the state is BLOCKED or PERCOLATED, false otherwise
   */
  private boolean isStaticState(PercolationState state) {
    return state == PercolationState.BLOCKED || state == PercolationState.PERCOLATED;
  }

  /**
   * Determines the next state for an OPEN cell based on neighbor states and percolation
   * probability.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return PERCOLATED if conditions are met, otherwise remains OPEN
   */
  private PercolationState determineNextStateForOpenCell(int row, int col) {
    List<Cell> neighbors = getGrid().getNeighbors(row, col);
    for (Cell neighbor : neighbors) {
      if (isPercolatedNeighbor(neighbor) && shouldPercolate()) {
        return PercolationState.PERCOLATED;
      }
    }
    return PercolationState.OPEN;
  }

  /**
   * Checks if a neighboring cell is in the PERCOLATED state.
   *
   * @param neighbor the neighboring cell to check
   * @return true if the neighbor is PERCOLATED, false otherwise
   */
  private boolean isPercolatedNeighbor(Cell neighbor) {
    return neighbor.getCurrentState() == PercolationState.PERCOLATED;
  }

  /**
   * Determines if percolation should occur based on configured probability.
   *
   * @return true if generated random value is below percolation probability threshold
   */
  private boolean shouldPercolate() {
    return Math.random() < percolationProbability;
  }
}




