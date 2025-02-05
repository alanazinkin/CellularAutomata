package cellsociety.Model.Simulations;

import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.State.PercolationState;
import cellsociety.Model.StateInterface;
import java.util.HashMap;
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
 */
public class Percolation extends Simulation {

  private static final Color OPEN_COLOR = Color.WHITE;
  private static final Color PERCOLATED_COLOR = Color.LIGHTBLUE;
  private static final Color BLOCKED_COLOR = Color.BLACK;

  private static final double RANDOM_MAX = 1.0;

  private final double percolationProbability;

  /**
   * Constructs a new Percolation simulation on the specified grid with the given percolation probability.
   *
   * @param grid the grid on which the percolation simulation is performed
   * @param percolationProbability the probability that an open cell percolates when adjacent to a percolated cell;
   *                               must be between 0 and 1 inclusive.
   * @throws IllegalArgumentException if the grid is null or the percolationProbability is not in [0, 1]
   */
  public Percolation(Grid grid, double percolationProbability) {
    super(grid);
    // Explicit check to ensure grid is not null, throwing IllegalArgumentException if it is.
    if (grid == null) {
      throw new IllegalArgumentException("Grid cannot be null");
    }
    if (percolationProbability < 0.0 || percolationProbability > RANDOM_MAX) {
      throw new IllegalArgumentException("Percolation probability must be between 0 and 1 inclusive.");
    }
    this.percolationProbability = percolationProbability;
  }

  @Override
  public Map<StateInterface, Color> initializeStateMap() {
    Map<StateInterface, Color> stateMap = new HashMap<>();
    stateMap.put(PercolationState.OPEN, OPEN_COLOR);
    stateMap.put(PercolationState.PERCOLATED, PERCOLATED_COLOR);
    stateMap.put(PercolationState.BLOCKED, BLOCKED_COLOR);
    return stateMap;
  }

  /**
   * Applies the percolation rules with probabilistic behavior to all cells in the grid.
   * <p>
   * - BLOCKED and PERCOLATED cells remain unchanged.
   * - For OPEN cells, if any neighbor is PERCOLATED then with the given probability the cell becomes PERCOLATED.
   * <p>
   * After calling this method, grid.applyNextStates() should be invoked to update the cell states.
   */
  @Override
  public void applyRules() {
    final int numRows = grid.getRows();
    final int numCols = grid.getCols();

    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < numCols; col++) {
        Cell cell = grid.getCell(row, col);
        PercolationState currentState = (PercolationState) cell.getState();

        if (isStaticState(currentState)) {
          cell.setNextState(currentState);
        } else {
          cell.setNextState(determineNextStateForOpenCell(row, col));
        }
      }
    }
  }

  /**
   * Determines if the given state is static (i.e., BLOCKED or PERCOLATED) and should remain unchanged.
   *
   * @param state the current state of the cell
   * @return true if the state is BLOCKED or PERCOLATED; false otherwise
   */
  private boolean isStaticState(PercolationState state) {
    return state == PercolationState.BLOCKED || state == PercolationState.PERCOLATED;
  }

  /**
   * Determines the next state for an OPEN cell based on its neighbors and a probabilistic rule.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return {@link PercolationState#PERCOLATED} if the cell percolates; {@link PercolationState#OPEN} otherwise
   */
  private PercolationState determineNextStateForOpenCell(int row, int col) {
    List<Cell> neighbors = grid.getNeighbors(row, col);
    for (Cell neighbor : neighbors) {
      if (neighbor.getState() == PercolationState.PERCOLATED && shouldPercolate()) {
        return PercolationState.PERCOLATED;
      }
    }
    return PercolationState.OPEN;
  }

  /**
   * Determines whether an open cell should percolate based on the percolation probability.
   *
   * @return true if a generated random number is less than percolationProbability; false otherwise
   */
  private boolean shouldPercolate() {
    final double randomValue = Math.random();  // Generates a value in [0, RANDOM_MAX)
    return randomValue < percolationProbability;
  }
}




