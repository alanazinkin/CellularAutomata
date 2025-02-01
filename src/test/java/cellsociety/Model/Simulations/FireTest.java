package cellsociety.Model.Simulations;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.State.FireState;
import cellsociety.Model.StateInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for the Fire simulation.
 * <p>
 * These tests use the actual {@code Grid} and {@code Cell} classes from the project.
 * The tests cover the following rules:
 * <ul>
 *   <li>A burning cell turns into a burnt cell.</li>
 *   <li>A tree with at least one burning neighbor catches fire.</li>
 *   <li>A tree ignites spontaneously (with probability f).</li>
 *   <li>An empty cell (or burnt cell) regrows a tree (with probability p).</li>
 *   <li>The {@code step()} method calls {@code applyRules()} and then updates the grid states.</li>
 *   <li>Neighbors on the grid edge are handled correctly.</li>
 * </ul>
 * </p>
 */
class FireTest {

  // We will use a 3x3 grid for testing.
  private Grid grid;

  /**
   * Sets up a 3x3 grid before each test.
   * <p>
   * Uses the {@code Grid(int rows, int cols, StateInterface defaultState)} constructor
   * to initialize every cell to {@code FireState.EMPTY}.
   * </p>
   */
  @BeforeEach
  void setUp() {
    // Create a 3x3 grid with all cells initialized to EMPTY.
    grid = new Grid(3, 3, FireState.EMPTY);
  }

  /**
   * Tests that a burning cell is scheduled to become burnt.
   * <p>
   * This test sets the center cell to {@code BURNING} and all other cells to {@code TREE}
   * so they do not interfere. With probabilities set to zero (p = 0, f = 0), it then verifies
   * that the burning cell is scheduled to become {@code BURNT} after applying rules.
   * </p>
   */
  @Test
  void testApplyRules_BurningCellBecomesBurnt() {
    // Set the center cell to BURNING.
    Cell center = grid.getCell(1, 1);
    center.setState(FireState.BURNING);

    // Set all other cells to TREE.
    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        if (!(row == 1 && col == 1)) {
          grid.getCell(row, col).setState(FireState.TREE);
        }
      }
    }

    // Use probabilities that disable spontaneous ignition and regrowth.
    Fire fireSim = new Fire(grid, 0.0, 0.0);
    fireSim.applyRules();

    // The burning cell should be scheduled to become BURNT.
    StateInterface nextState = center.getNextState();
    assertEquals(FireState.BURNT, nextState, "A burning cell did not become burnt as expected.");
  }

  /**
   * Tests that a tree with a burning neighbor catches fire.
   * <p>
   * This test sets the center cell to {@code TREE} and one of its neighbors (left cell)
   * to {@code BURNING}. With probabilities set to zero, it verifies that the tree becomes
   * scheduled to catch fire.
   * </p>
   */
  @Test
  void testApplyRules_TreeWithBurningNeighbor() {
    // Set the center cell to TREE.
    Cell center = grid.getCell(1, 1);
    center.setState(FireState.TREE);

    // Set one neighbor (left cell at (1,0)) to BURNING.
    Cell leftNeighbor = grid.getCell(1, 0);
    leftNeighbor.setState(FireState.BURNING);

    // Ensure all other cells are TREE.
    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        if (!((row == 1 && col == 1) || (row == 1 && col == 0))) {
          grid.getCell(row, col).setState(FireState.TREE);
        }
      }
    }

    // Use p = 0 and f = 0 to avoid spontaneous ignition or regrowth.
    Fire fireSim = new Fire(grid, 0.0, 0.0);
    fireSim.applyRules();

    // The tree with a burning neighbor should be scheduled to become BURNING.
    StateInterface nextState = center.getNextState();
    assertEquals(FireState.BURNING, nextState, "A tree with a burning neighbor did not ignite.");
  }

  /**
   * Tests that a tree ignites spontaneously when f = 1.
   * <p>
   * This test sets the center cell to {@code TREE} and all cells to {@code TREE}
   * so that there is no burning neighbor. With f = 1, the tree should ignite spontaneously.
   * </p>
   */
  @Test
  void testApplyRules_TreeSpontaneousIgnition() {
    // Set the center cell to TREE.
    Cell center = grid.getCell(1, 1);
    center.setState(FireState.TREE);

    // Set all cells to TREE.
    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        grid.getCell(row, col).setState(FireState.TREE);
      }
    }

    // Use f = 1 to force spontaneous ignition.
    Fire fireSim = new Fire(grid, 0.0, 1.0);
    fireSim.applyRules();

    // The tree should ignite spontaneously.
    StateInterface nextState = center.getNextState();
    assertEquals(FireState.BURNING, nextState, "A tree did not spontaneously ignite when f = 1.");
  }

  /**
   * Tests that an empty cell regrows a tree when p = 1.
   * <p>
   * This test sets the center cell to {@code EMPTY} and uses p = 1 so that the empty cell
   * always regrows a tree. It then verifies that the cell is scheduled to become {@code TREE}.
   * </p>
   */
  @Test
  void testApplyRules_EmptyCellGrowsTree() {
    // Set the center cell to EMPTY.
    Cell center = grid.getCell(1, 1);
    center.setState(FireState.EMPTY);

    // Use p = 1 so that the empty cell always regrows a tree.
    Fire fireSim = new Fire(grid, 1.0, 0.0);
    fireSim.applyRules();

    // The empty cell should be scheduled to become TREE.
    StateInterface nextState = center.getNextState();
    assertEquals(FireState.TREE, nextState, "An empty cell did not grow a tree when p = 1.");
  }

  /**
   * Tests that the {@code step()} method applies the rules and updates the grid.
   * <p>
   * This test sets up a burning neighbor to cause a tree to ignite, then calls {@code step()}
   * to update the cell states. It verifies that the burning neighbor becomes {@code BURNT}
   * and that the tree becomes {@code BURNING} as expected.
   * </p>
   */
  @Test
  void testStep_UpdatesCurrentStates() {
    // Set the center cell to TREE.
    Cell center = grid.getCell(1, 1);
    center.setState(FireState.TREE);

    // Set a neighbor (top cell at (0,1)) to BURNING.
    Cell topNeighbor = grid.getCell(0, 1);
    topNeighbor.setState(FireState.BURNING);

    // Ensure all other cells are TREE.
    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        if (!((row == 1 && col == 1) || (row == 0 && col == 1))) {
          grid.getCell(row, col).setState(FireState.TREE);
        }
      }
    }

    // Use probabilities that disable spontaneous ignition or regrowth.
    Fire fireSim = new Fire(grid, 0.0, 0.0);
    // Call step(), which internally calls applyRules() and then grid.applyNextStates().
    fireSim.step();

    // After stepping, the burning neighbor should have become BURNT.
    assertEquals(FireState.BURNT, topNeighbor.getState(), "A burning neighbor did not become burnt after step().");
    // And the tree with a burning neighbor should have become BURNING.
    assertEquals(FireState.BURNING, center.getState(), "A tree with a burning neighbor did not ignite after step().");
  }

  /**
   * Tests that a cell on the edge correctly detects a burning neighbor.
   * <p>
   * In this test, a burning cell is placed on the edge (0,0) and its adjacent cell (0,1)
   * is set as {@code TREE}. After applying rules, the adjacent cell should be scheduled to
   * ignite (i.e., become {@code BURNING}).
   * </p>
   */
  @Test
  void testApplyRules_EdgeCellNeighborDetection() {
    // Set an edge cell (0,0) to BURNING.
    Cell edgeCell = grid.getCell(0, 0);
    edgeCell.setState(FireState.BURNING);

    // Set the adjacent cell (0,1) to TREE.
    Cell neighborCell = grid.getCell(0, 1);
    neighborCell.setState(FireState.TREE);

    // Set all other cells to TREE.
    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        if (!((row == 0 && col == 0) || (row == 0 && col == 1))) {
          grid.getCell(row, col).setState(FireState.TREE);
        }
      }
    }

    // Use p = 0 and f = 0 to avoid unintended state changes.
    Fire fireSim = new Fire(grid, 0.0, 0.0);
    fireSim.applyRules();

    // The neighbor cell should be scheduled to become BURNING.
    assertEquals(FireState.BURNING, neighborCell.getNextState(), "The edge cell's neighbor did not ignite.");
  }
}

