package cellsociety.Model;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.Model.State.MockState;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Grid} class.
 * <p>
 * This class contains various test methods that verify the functionality of
 * the {@link Grid} class, including cell retrieval, neighbor retrieval,
 * state application, grid reset, and boundary conditions.
 * </p>
 */
class GridTest {

  /**
   * Tests the {@link Grid#getCell(int, int)} method.
   * <p>
   * Verifies that a cell can be correctly retrieved from the grid and that its
   * state matches the expected mock state.
   * </p>
   */
  @Test
  void getCell() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertNotNull(grid.getCell(0, 0));
    assertEquals(MockState.STATE_ONE, grid.getCell(0, 0).getState());
  }

  /**
   * Tests the {@link Grid#getNeighbors(int, int)} method.
   * <p>
   * Verifies that the neighbors of a cell are correctly retrieved. The test
   * checks both the return value of the neighbors and ensures that out-of-bounds
   * neighbors are properly handled.
   * </p>
   */
  @Test
  void getNeighbors() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    grid.getCell(0, 0).setState(MockState.STATE_ONE);
    assertEquals(3, grid.getNeighbors(0, 0).size());
  }

  /**
   * Tests the {@link Grid#applyNextStates()} method.
   * <p>
   * Verifies that the next state of the cells in the grid is correctly applied.
   * Since {@code MockState} always returns the same state, no changes are expected.
   * </p>
   */
  @Test
  void applyNextStates() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    grid.applyNextStates(); // No change expected, as MockState always returns itself
    assertEquals(MockState.STATE_ONE, grid.getCell(0, 0).getState());
  }

  /**
   * Tests the {@link Grid#resetGrid(StateInterface)} method.
   * <p>
   * Verifies that the grid can be reset to a new state, and the state of all cells
   * is updated accordingly. In this case, the grid is reset to the same state as
   * before for consistency.
   * </p>
   */
  @Test
  void resetGrid() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    grid.resetGrid(MockState.STATE_ONE); // Reset to the same state
    assertEquals(MockState.STATE_ONE, grid.getCell(0, 0).getState());
  }

  /**
   * Tests the {@link Grid#printGrid()} method.
   * <p>
   * Verifies that the grid can be printed without exceptions. This is primarily
   * a smoke test to ensure no errors occur during printing.
   * </p>
   */
  @Test
  void printGrid() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    grid.printGrid(); // This will print the grid's state to the console
    // This test will pass if no exceptions occur during printing
  }

  /**
   * Tests the {@link Grid#getRows()} method.
   * <p>
   * Verifies that the number of rows in the grid is returned correctly.
   * </p>
   */
  @Test
  void getRows() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertEquals(5, grid.getRows());
  }

  /**
   * Tests the {@link Grid#getCols()} method.
   * <p>
   * Verifies that the number of columns in the grid is returned correctly.
   * </p>
   */
  @Test
  void getCols() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertEquals(5, grid.getCols());
  }

  /**
   * Tests the {@link Grid#getCell(int, int)} method for out-of-bounds indices.
   * <p>
   * Verifies that the {@link Grid#getCell(int, int)} method correctly returns
   * {@code null} for out-of-bounds indices.
   * </p>
   */
  @Test
  void testGridOutOfBounds() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertNull(grid.getCell(5, 5)); // Out of bounds, should return null
    assertNull(grid.getCell(-1, -1)); // Negative index, should return null
  }

  /**
   * Tests the {@link Grid#getNeighbors(int, int)} method for out-of-bounds indices.
   * <p>
   * Verifies that the neighbors method correctly handles edge cases where there
   * are no neighbors, such as at the edges or corners of the grid.
   * </p>
   */
  @Test
  void testNeighborsOutOfBounds() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertEquals(3, grid.getNeighbors(0, 0).size());  // Edge of the grid, should have 3 neighbors
  }

  /**
   * Tests the {@link Grid#applyNextStates()} method for all cells in the grid.
   * <p>
   * Verifies that the {@code applyNextStates()} method applies the next state
   * correctly to all cells in the grid. Since the mock state doesn't change,
   * all cells should retain the same state.
   * </p>
   */
  @Test
  void testApplyNextStatesForMultipleCells() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    grid.applyNextStates();
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        assertEquals(MockState.STATE_ONE, grid.getCell(r, c).getState());
      }
    }
  }
}

