package cellsociety.Model.Simulations;

import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.State.FireState;
import cellsociety.Model.StateInterface;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javafx.scene.paint.Color;

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
   * Constructs a Fire simulation with the specified grid and parameters.
   *
   * @param grid the grid on which the simulation is run
   * @param p    the probability that an empty cell grows a tree
   * @param f    the probability that a tree spontaneously catches fire
   */
  public Fire(Grid grid, double p, double f) {
    super(grid);
    this.p = p;
    this.f = f;
    this.random = new Random();
  }

  @Override
  public Map<StateInterface, Color> initializeStateMap() {
    stateMap = new HashMap<>();
    stateMap.put(FireState.TREE, TREE_COLOR);
    stateMap.put(FireState.BURNING, BURNING_COLOR);
    stateMap.put(FireState.BURNT, BURNT_COLOR);
    stateMap.put(FireState.EMPTY, EMPTY_COLOR);
    return stateMap;
  }

  /**
   * Applies the rules of the fire simulation to each cell in the grid.
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
    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        Cell cell = grid.getCell(row, col);
        FireState currentState = (FireState) cell.getState();

        switch (currentState) {
          case BURNING:
            cell.setNextState(FireState.BURNT);
            break;
          case TREE:
            cell.setNextState(getNextTreeState(row, col));
            break;
          case EMPTY:
          case BURNT:
            cell.setNextState(probabilityEvent(p) ? FireState.TREE : currentState);
            break;
        }
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
   * This method assumes that grid.getNeighbors(row, col) returns a collection of neighboring
   * cells.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return true if at least one neighbor is in the BURNING state; false otherwise
   */
  private boolean hasBurningNeighbor(int row, int col) {
    Collection<Cell> neighbors = grid.getNeighbors(row, col);
    for (Cell neighbor : neighbors) {
      FireState neighborState = (FireState) neighbor.getState();
      if (neighborState == FireState.BURNING) {
        return true;
      }
    }
    return false;
  }
}
