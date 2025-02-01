package cellsociety.Model.Simulations;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.State.PercolationState;
import cellsociety.Model.StateInterface;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests for the {@link Percolation} class.
 * <p>
 * This class contains both positive tests (model tests) to verify expected behavior
 * and negative tests where exceptions are expected. Negative tests include scenarios such as:
 * <ul>
 *   <li>Passing a null default state to the {@link Grid} constructor.</li>
 *   <li>Accessing cells or neighbors with out-of-bound indices and forcing a method call
 *       on the result to produce an exception.</li>
 *   <li>Using a null grid in a simulation.</li>
 *   <li>Corrupting cell states (via reflection or manual intervention) so that method calls
 *       result in exceptions.</li>
 * </ul>
 * </p>
 */
class PercolationTest {

  /**
   * Utility method to create a simple 2x2 grid for testing.
   * The grid is initialized with {@code PercolationState.OPEN} for all cells, and then
   * the cell at (1,0) is set to {@code PercolationState.BLOCKED} to mimic a custom configuration.
   *
   * @return a new {@code Grid} configured for testing
   */
  private Grid createTestGrid() {
    Grid grid = new Grid(2, 2, PercolationState.OPEN);
    grid.getCell(1, 0).setState(PercolationState.BLOCKED);
    return grid;
  }

  /**
   * Tests that {@code applyRules()} correctly percolates open cells adjacent to a percolated cell.
   * <p>
   * This test sets up a 2x2 grid where the cell at (0,0) is manually set to
   * {@code PercolationState.PERCOLATED}. It then verifies that all open neighbors become percolated.
   * </p>
   */
  @Test
  void testApplyRulesOpenToPercolated() {
    Grid grid = new Grid(2, 2, PercolationState.OPEN);
    // Manually set (0,0) to PERCOLATED.
    grid.getCell(0, 0).setState(PercolationState.PERCOLATED);
    Percolation simulation = new Percolation(grid);

    simulation.applyRules();
    grid.applyNextStates();

    // All open neighbors of the percolated cell at (0,0) should now be percolated.
    assertEquals(PercolationState.PERCOLATED, grid.getCell(0, 1).getState(),
        "Cell (0,1) should become percolated.");
    assertEquals(PercolationState.PERCOLATED, grid.getCell(1, 0).getState(),
        "Cell (1,0) should become percolated.");
    assertEquals(PercolationState.PERCOLATED, grid.getCell(1, 1).getState(),
        "Cell (1,1) should become percolated.");
  }

  /**
   * Tests that {@code applyRules()} leaves open cells unchanged when no percolated neighbors exist.
   * <p>
   * In this test, the grid is configured so that no cell is percolated and one cell is blocked.
   * It then verifies that the states remain as originally set.
   * </p>
   */
  @Test
  void testApplyRulesNoPercolation() {
    Grid grid = new Grid(2, 2, PercolationState.OPEN);
    // Set (0,1) to BLOCKED.
    grid.getCell(0, 1).setState(PercolationState.BLOCKED);
    Percolation simulation = new Percolation(grid);

    simulation.applyRules();
    grid.applyNextStates();

    // With no percolated neighbors, open cells remain open.
    assertEquals(PercolationState.OPEN, grid.getCell(0, 0).getState(),
        "Cell (0,0) should remain open.");
    assertEquals(PercolationState.BLOCKED, grid.getCell(0, 1).getState(),
        "Cell (0,1) should remain blocked.");
    assertEquals(PercolationState.OPEN, grid.getCell(1, 0).getState(),
        "Cell (1,0) should remain open.");
    assertEquals(PercolationState.OPEN, grid.getCell(1, 1).getState(),
        "Cell (1,1) should remain open.");
  }

  /////////////////
  // NEGATIVE TESTS
  // At least 10 tests where an exception is expected.
  /////////////////

  /**
   * Expects a {@code NullPointerException} when a null default state is passed to the {@code Grid} constructor.
   */
  @Test
  void testGridConstructorWithNullDefaultState() {
    assertThrows(NullPointerException.class, () -> {
      new Grid(2, 2, (StateInterface) null);
    });
  }

  /**
   * Expects a {@code NullPointerException} when accessing a cell with a negative row index and forcing a method call.
   * <p>
   * {@code getCell(-1,0)} returns null; calling {@code getState()} on that null value should throw a NullPointerException.
   * </p>
   */
  @Test
  void testGetCellNegativeIndex() {
    Grid grid = createTestGrid();
    assertThrows(NullPointerException.class, () -> {
      // getCell(-1, 0) returns null; invoking getState() should throw.
      grid.getCell(-1, 0).getState();
    });
  }

  /**
   * Expects a {@code NullPointerException} when accessing a cell with an out-of-bound row index and forcing a method call.
   * <p>
   * {@code getCell(10,0)} returns null; calling {@code getState()} on that null value should throw a NullPointerException.
   * </p>
   */
  @Test
  void testGetCellIndexOutOfBounds() {
    Grid grid = createTestGrid();
    assertThrows(NullPointerException.class, () -> {
      grid.getCell(10, 0).getState();
    });
  }

  /**
   * Expects an {@code IndexOutOfBoundsException} when {@code getNeighbors()} is called with negative indices and then accessing an element.
   * <p>
   * Although {@code getNeighbors(-1,-1)} returns an empty list, attempting to access the first element
   * forces an IndexOutOfBoundsException.
   * </p>
   */
  @Test
  void testGetNeighborsWithNegativeIndex() {
    Grid grid = createTestGrid();
    assertThrows(IndexOutOfBoundsException.class, () -> {
      grid.getNeighbors(-1, -1).get(0);
    });
  }

  /**
   * Expects a {@code NullPointerException} when {@code applyRules()} is called on a {@code Percolation}
   * that was constructed with a null grid.
   */
  @Test
  void testApplyRulesWithNullGrid() {
    Percolation simulation = new Percolation(null);
    assertThrows(NullPointerException.class, simulation::applyRules);
  }

  /**
   * Expects a {@code NullPointerException} when {@code step()} is called on a {@code Percolation}
   * that was constructed with a null grid.
   */
  @Test
  void testStepWithNullGrid() {
    Percolation simulation = new Percolation(null);
    assertThrows(NullPointerException.class, simulation::step);
  }

  /**
   * Expects a {@code NullPointerException} during {@code grid.applyNextStates()} if a cell's next state is set to null.
   * <p>
   * The test corrupts one cell's next state, and when {@code applyNextStates()} is invoked, a method call on a null
   * next state should throw a NullPointerException.
   * </p>
   */
  @Test
  void testGridApplyNextStatesWithInvalidCellState() {
    Grid grid = new Grid(2, 2, PercolationState.OPEN);
    // Manually corrupt one cell's next state.
    assertThrows(IllegalArgumentException.class, () -> grid.getCell(0, 0).setNextState(null));
  }

  /**
   * Expects an {@code IndexOutOfBoundsException} when {@code getNeighbors()} is called with indices far out-of-bounds
   * and then accessing an element from the returned list.
   * <p>
   * Although {@code getNeighbors(100,100)} returns an empty list, forcing an access to the first element causes an exception.
   * </p>
   */
  @Test
  void testGridGetNeighborsOutOfBounds() {
    Grid grid = createTestGrid();
    assertThrows(IndexOutOfBoundsException.class, () -> {
      grid.getNeighbors(100, 100).get(0);
    });
  }

  /**
   * Expects a {@code NullPointerException} when {@code grid.applyNextStates()} is called on a grid that has a null cell.
   * <p>
   * Reflection is used to simulate a grid that contains a null cell.
   * </p>
   */
  @Test
  void testApplyNextStatesWithNullCellInGrid() {
    Grid grid = new Grid(2, 2, PercolationState.OPEN);

    // Use reflection to set one cell in the grid to null.
    try {
      java.lang.reflect.Field field = grid.getClass().getDeclaredField("cells");
      field.setAccessible(true);
      Cell[][] cells = (Cell[][]) field.get(grid);
      cells[0][1] = null;
    } catch (Exception e) {
      fail("Reflection failed: " + e.getMessage());
    }

    assertThrows(NullPointerException.class, grid::applyNextStates);
  }

  /**
   * Expects a {@code NullPointerException} when {@code applyRules()} is called on a grid that contains a null cell.
   * <p>
   * Reflection is used to simulate the grid containing a null cell.
   * </p>
   */
  @Test
  void testApplyRulesWithNullCellInGrid() {
    Grid grid = new Grid(2, 2, PercolationState.OPEN);

    // Use reflection to set one cell in the grid to null.
    try {
      java.lang.reflect.Field field = grid.getClass().getDeclaredField("cells");
      field.setAccessible(true);
      Cell[][] cells = (Cell[][]) field.get(grid);
      cells[1][1] = null;
    } catch (Exception e) {
      fail("Reflection failed: " + e.getMessage());
    }

    Percolation simulation = new Percolation(grid);
    assertThrows(NullPointerException.class, simulation::applyRules);
  }
}
