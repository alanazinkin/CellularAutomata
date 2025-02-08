package cellsociety.Model.Simulations;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.State.FireState;
import cellsociety.Model.StateInterface;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javafx.scene.paint.Color;
import java.util.Collection;

/**
 * Simulation class for the Spreading of Fire.
 * <p>
 * This simulation models a forest fire according to the following rules:
 * <ul>
 *   <li>A burning cell turns into a burnt cell.</li>
 *   <li>A tree will catch fire if at least one neighbor is burning.</li>
 *   <li>A tree ignites spontaneously with probability f even if no neighbor is burning.</li>
 *   <li>An empty or burnt cell fills with a tree with probability p.</li>
 * </ul>
 * <p>
 * The parameters p and f control tree growth and lightning strikes, respectively.
 */
public class Fire extends Simulation {

  // Probability that an empty cell regrows a tree.
  private final double p;
  // Probability that a tree spontaneously ignites.
  private final double f;
  private Random random;
  private static final Color TREE_COLOR = Color.GREEN;
  private static final Color BURNING_COLOR = Color.RED;
  private static final Color BURNT_COLOR = Color.BROWN;
  private static final Color EMPTY_COLOR = Color.WHITE;

  /**
   * Constructs a Fire simulation with the specified configuration, grid, and parameters.
   *
   * @param simulationConfig the configuration settings for the simulation
   * @param grid the grid on which the simulation is run
   * @param p    the probability that an empty cell grows a tree (must be between 0 and 1)
   * @param f    the probability that a tree spontaneously catches fire (must be between 0 and 1)
   * @throws IllegalArgumentException if grid is null or if p or f are not in the interval [0, 1]
   */
  public Fire(SimulationConfig simulationConfig, Grid grid, double p, double f) {
    super(simulationConfig, grid);
    if (p < 0 || p > 1) {
      throw new IllegalArgumentException("Regrowth probability must be between 0 and 1.");
    }
    if (f < 0 || f > 1) {
      throw new IllegalArgumentException("Ignition probability must be between 0 and 1.");
    }
    this.p = p;
    this.f = f;
    this.random = new Random();
  }

  @Override
  public Map<StateInterface, Color> initializeColorMap() {
    Map<StateInterface, Color> colorMap = new HashMap<>();
    colorMap.put(FireState.TREE, TREE_COLOR);
    colorMap.put(FireState.BURNING, BURNING_COLOR);
    colorMap.put(FireState.BURNT, BURNT_COLOR);
    colorMap.put(FireState.EMPTY, EMPTY_COLOR);
    return colorMap;
  }

  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    Map<Integer, StateInterface> stateMap = new HashMap<>();
    stateMap.put(0, FireState.EMPTY);
    stateMap.put(1, FireState.TREE);
    stateMap.put(2, FireState.BURNING);
    stateMap.put(3, FireState.BURNT);
    return stateMap;
  }

  /**
   * Computes the next state for every cell without modifying any cell’s current state.
   * <p>
   * The rules are as follows:
   * <ul>
   *   <li>If a cell is BURNING, it becomes BURNT.</li>
   *   <li>If a cell is a TREE:
   *     <ul>
   *       <li>If at least one neighbor is BURNING, it catches fire.</li>
   *       <li>Otherwise, it may spontaneously ignite with probability f.</li>
   *     </ul>
   *   </li>
   *   <li>If a cell is EMPTY or BURNT, it may regrow into a TREE with probability p.</li>
   * </ul>
   */
  @Override
  public void applyRules() {
    int numRows = getGrid().getRows();
    int numCols = getGrid().getCols();
    // Create a temporary array to hold computed next states.
    StateInterface[][] nextStates = new StateInterface[numRows][numCols];

    // First pass: compute next state for each cell using only the current state.
    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < numCols; col++) {
        Cell cell = getGrid().getCell(row, col);
        FireState currentState = (FireState) cell.getState();
        switch (currentState) {
          case BURNING:
            nextStates[row][col] = FireState.BURNT;
            break;
          case TREE:
            nextStates[row][col] = getNextTreeState(row, col);
            break;
          case EMPTY:
          case BURNT:
            nextStates[row][col] = probabilityEvent(p) ? FireState.TREE : currentState;
            break;
          default:
            nextStates[row][col] = currentState;
        }
      }
    }
    // Second pass: write computed next states into each cell.
    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < numCols; col++) {
        getGrid().getCell(row, col).setNextState(nextStates[row][col]);
      }
    }
  }

  /**
   * Determines the next state of a TREE cell based on fire spreading rules.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return FireState.BURNING if the tree catches fire, otherwise FireState.TREE
   */
  private FireState getNextTreeState(int row, int col) {
    if (hasBurningNeighbor(row, col)) {
      return FireState.BURNING;
    }
    if (probabilityEvent(f)) {
      return FireState.BURNING;
    }
    return FireState.TREE;
  }

  /**
   * Determines whether an event occurs based on the given probability.
   *
   * @param probability the probability (between 0 and 1) of the event occurring
   * @return true if the event occurs, false otherwise
   */
  private boolean probabilityEvent(double probability) {
    return random.nextDouble() < probability;
  }

  /**
   * Checks whether any of the neighbors of the cell at (row, col) are burning.
   * <p>
   * This method assumes that getNeighbors(row, col) returns a collection of neighboring cells.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return true if at least one neighbor is in the BURNING state; false otherwise
   */
  private boolean hasBurningNeighbor(int row, int col) {
    Collection<Cell> neighbors = getGrid().getNeighbors(row, col);
    for (Cell neighbor : neighbors) {
      FireState neighborState = (FireState) neighbor.getState();
      if (neighborState == FireState.BURNING) {
        return true;
      }
    }
    return false;
  }

  /**
   * The step method performs a two-phase update:
   * <ol>
   *   <li>Compute next state for each cell using applyRules().</li>
   *   <li>Commit those states to each cell.</li>
   * </ol>
   */
  @Override
  public void step() {
    // First, compute the next state for every cell.
    applyRules();
    // Then, commit the computed next state for each cell and reset its next state.
    for (int row = 0; row < getGrid().getRows(); row++) {
      for (int col = 0; col < getGrid().getCols(); col++) {
        Cell cell = getGrid().getCell(row, col);
        cell.applyNextState();
        cell.resetNextState();
      }
    }
  }
}





