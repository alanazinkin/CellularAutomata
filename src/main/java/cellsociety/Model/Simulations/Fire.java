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
 *
 * This simulation models a forest fire according to the following rules:
 * <ul>
 *   <li>A burning cell turns into a burnt cell.</li>
 *   <li>A tree will catch fire if at least one neighbor is burning.</li>
 *   <li>A tree ignites spontaneously with probability f even if no neighbor is burning.</li>
 *   <li>An empty or burnt cell fills with a tree with probability p.</li>
 * </ul>
 *
 * The parameters p and f control tree growth and lightning strikes, respectively.
 */
public class Fire extends Simulation {

  // Probability that an empty cell regrows a tree.
  private double p;
  // Probability that a tree spontaneously ignites.
  private double f;
  private Random random;

  /**
   * Constructs a Fire simulation with the specified grid and parameters.
   *
   * @param grid the grid on which the simulation is run
   * @param p the probability that an empty cell grows a tree
   * @param f the probability that a tree spontaneously catches fire
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
    stateMap.put(FireState.TREE, Color.GREEN);
    stateMap.put(FireState.BURNING, Color.RED);
    stateMap.put(FireState.BURNT, Color.BROWN);
    stateMap.put(FireState.EMPTY, Color.WHITE);
    return stateMap;
  }

  /**
   * Applies the rules of the fire simulation to each cell in the grid.
   *
   * For each cell:
   * <ul>
   *   <li>If the cell is BURNING, it becomes BURNT.</li>
   *   <li>If the cell is a TREE:
   *     <ul>
   *       <li>If at least one neighbor is BURNING, the tree will burn.</li>
   *       <li>Otherwise, it may spontaneously catch fire with probability f.</li>
   *       <li>If neither condition holds, it remains a TREE.</li>
   *     </ul>
   *   </li>
   *   <li>If the cell is EMPTY or BURNT, it may regrow a TREE with probability p.</li>
   * </ul>
   */
  @Override
  public void applyRules() {
    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        Cell cell = grid.getCell(row, col);
        // Cast the cell's current state to a FireState.
        FireState currentState = (FireState) cell.getState();

        switch (currentState) {
          case BURNING:
            // Rule 1: A burning cell turns into a burnt cell.
            cell.setNextState(FireState.BURNT);
            break;

          case TREE:
            // Rule 2: A tree will catch fire if at least one neighbor is burning.
            if (hasBurningNeighbor(row, col)) {
              cell.setNextState(FireState.BURNING);
            }
            // Rule 3: A tree may spontaneously ignite with probability f.
            else if (random.nextDouble() < f) {
              cell.setNextState(FireState.BURNING);
            }
            else {
              cell.setNextState(FireState.TREE);
            }
            break;

          case EMPTY:
          case BURNT:
            // Rule 4: An empty or burnt cell fills with a tree with probability p.
            if (random.nextDouble() < p) {
              cell.setNextState(FireState.TREE);
            }
            else {
              // Remain in the same state.
              cell.setNextState(currentState);
            }
            break;

          default:
            // If an unexpected state is encountered, keep the current state.
            cell.setNextState(currentState);
            break;
        }
      }
    }
  }

  /**
   * Checks whether any of the neighbors of the cell at (row, col) are burning.
   *
   * This method assumes that grid.getNeighbors(row, col) returns a collection
   * of neighboring cells (using, for example, the von Neumann neighborhood).
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
