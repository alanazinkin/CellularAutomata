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
    Cell center = grid.getCell(1, 1);
    center.setState(FireState.BURNING);

    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        if (!(row == 1 && col == 1)) {
          grid.getCell(row, col).setState(FireState.TREE);
        }
      }
    }

    Fire fireSim = new Fire(grid, 0.0, 0.0);
    fireSim.applyRules();

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
    Cell center = grid.getCell(1, 1);
    center.setState(FireState.TREE);

    Cell leftNeighbor = grid.getCell(1, 0);
    leftNeighbor.setState(FireState.BURNING);

    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        if (!((row == 1 && col == 1) || (row == 1 && col == 0))) {
          grid.getCell(row, col).setState(FireState.TREE);
        }
      }
    }

    Fire fireSim = new Fire(grid, 0.0, 0.0);
    fireSim.applyRules();

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
    Cell center = grid.getCell(1, 1);
    center.setState(FireState.TREE);

    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        grid.getCell(row, col).setState(FireState.TREE);
      }
    }

    Fire fireSim = new Fire(grid, 0.0, 1.0);
    fireSim.applyRules();

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
    Cell center = grid.getCell(1, 1);
    center.setState(FireState.EMPTY);

    Fire fireSim = new Fire(grid, 1.0, 0.0);
    fireSim.applyRules();

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
    Cell center = grid.getCell(1, 1);
    center.setState(FireState.TREE);

    Cell topNeighbor = grid.getCell(0, 1);
    topNeighbor.setState(FireState.BURNING);

    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        if (!((row == 1 && col == 1) || (row == 0 && col == 1))) {
          grid.getCell(row, col).setState(FireState.TREE);
        }
      }
    }

    Fire fireSim = new Fire(grid, 0.0, 0.0);
    fireSim.step();

    assertEquals(FireState.BURNT, topNeighbor.getState(), "A burning neighbor did not become burnt after step().");
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
    Cell edgeCell = grid.getCell(0, 0);
    edgeCell.setState(FireState.BURNING);

    Cell neighborCell = grid.getCell(0, 1);
    neighborCell.setState(FireState.TREE);

    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        if (!((row == 0 && col == 0) || (row == 0 && col == 1))) {
          grid.getCell(row, col).setState(FireState.TREE);
        }
      }
    }

    Fire fireSim = new Fire(grid, 0.0, 0.0);
    fireSim.applyRules();

    assertEquals(FireState.BURNING, neighborCell.getNextState(), "The edge cell's neighbor did not ignite.");
  }

  /////////////////
  // NEGATIVE TESTS
  /////////////////

  /**
   * Negative test to verify that constructing a Fire simulation with a null grid
   * throws an IllegalArgumentException.
   */
  @Test
  void testFireConstructorNullGrid() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Fire(null, 0.5, 0.5);
    }, "Constructing a Fire simulation with a null grid should throw an IllegalArgumentException.");
  }

  /**
   * Negative test to verify that constructing a Fire simulation with an invalid regrowth probability (p)
   * (e.g., negative or greater than 1) throws an IllegalArgumentException.
   */
  @Test
  void testFireConstructorInvalidRegrowthProbability() {
    assertAll("Invalid regrowth probabilities",
        () -> assertThrows(IllegalArgumentException.class, () -> new Fire(grid, -0.1, 0.5),
            "A negative regrowth probability should throw an IllegalArgumentException."),
        () -> assertThrows(IllegalArgumentException.class, () -> new Fire(grid, 1.1, 0.5),
            "A regrowth probability greater than 1 should throw an IllegalArgumentException.")
    );
  }

  /**
   * Negative test to verify that constructing a Fire simulation with an invalid ignition probability (f)
   * (e.g., negative or greater than 1) throws an IllegalArgumentException.
   */
  @Test
  void testFireConstructorInvalidIgnitionProbability() {
    assertAll("Invalid ignition probabilities",
        () -> assertThrows(IllegalArgumentException.class, () -> new Fire(grid, 0.5, -0.1),
            "A negative ignition probability should throw an IllegalArgumentException."),
        () -> assertThrows(IllegalArgumentException.class, () -> new Fire(grid, 0.5, 1.1),
            "An ignition probability greater than 1 should throw an IllegalArgumentException.")
    );
  }
}


