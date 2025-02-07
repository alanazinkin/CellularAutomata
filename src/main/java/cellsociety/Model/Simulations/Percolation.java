package cellsociety.Model.Simulations;

import cellsociety.Controller.SimulationConfig;
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
  public Percolation(SimulationConfig simulationConfig, Grid grid, double percolationProbability) {
    super(simulationConfig, grid);
    if (grid == null) {
      throw new IllegalArgumentException("Grid cannot be null");
    }
    if (percolationProbability < 0.0 || percolationProbability > RANDOM_MAX) {
      throw new IllegalArgumentException("Percolation probability must be between 0 and 1 inclusive.");
    }
    this.percolationProbability = percolationProbability;
  }

  /**
   * Initializes the color map for Percolation simulation.
   *
   * @return the map of integer states to simulation states.
   */
  @Override
  public Map<StateInterface, Color> initializeColorMap() {
    Map<StateInterface, Color> colorMap = new HashMap<>();
    colorMap.put(PercolationState.OPEN, OPEN_COLOR);
    colorMap.put(PercolationState.PERCOLATED, PERCOLATED_COLOR);
    colorMap.put(PercolationState.BLOCKED, BLOCKED_COLOR);
    return colorMap;
  }

  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    Map<Integer, StateInterface> stateMap = new HashMap<>();
    stateMap.put(0, PercolationState.OPEN);
    stateMap.put(1, PercolationState.PERCOLATED);
    stateMap.put(2, PercolationState.BLOCKED);
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
    final int numRows = getGrid().getRows();
    final int numCols = getGrid().getCols();

    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < numCols; col++) {
        Cell cell = getGrid().getCell(row, col);
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
    List<Cell> neighbors = getGrid().getNeighbors(row, col);
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
    final double randomValue = Math.random();
    return randomValue < percolationProbability;
  }
}




