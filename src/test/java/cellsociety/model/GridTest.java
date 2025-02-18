package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.model.state.MockState;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Grid} functionality.
 * <p>
 * This class verifies grid operations such as cell retrieval, neighbor determination, state
 * transitions, grid reset, and grid printing. Naming convention: *
 * [UnitOfWork_StateUnderTest_ExpectedBehavior]
 * </p>
 *
 * @author Tatum McKinnis
 */
class GridTest {

  /**
   * Tests cell retrieval with valid indices.
   * <p>
   * Verifies that {@link Grid#getCell(int, int)} returns a cell with the expected initial state.
   * </p>
   */
  @Test
  void getCell_ValidIndices_ReturnsCorrectCell() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertNotNull(grid.getCell(0, 0));
    assertEquals(MockState.STATE_ONE, grid.getCell(0, 0).getCurrentState());
  }

  /**
   * Tests neighbor retrieval for a corner cell.
   * <p>
   * Verifies that a corner cell has exactly three neighbors.
   * </p>
   */
  @Test
  void getNeighbors_CornerCell_ReturnsThreeNeighbors() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertEquals(3, grid.getNeighbors(0, 0).size());
  }

  /**
   * Tests applying next states for all cells in the grid.
   * <p>
   * Verifies that after applying next states, cells maintain their initial state.
   * </p>
   */
  @Test
  void applyNextStates_AllCells_MaintainInitialState() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    grid.applyNextStates();
    assertEquals(MockState.STATE_ONE, grid.getCell(0, 0).getCurrentState());
  }

  /**
   * Tests grid reset functionality.
   * <p>
   * Verifies that resetting the grid with the same state maintains consistency across all cells.
   * </p>
   */
  @Test
  void resetGrid_SameState_ConsistencyMaintained() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    grid.resetGrid(MockState.STATE_ONE);
    assertEquals(MockState.STATE_ONE, grid.getCell(0, 0).getCurrentState());
  }

  /**
   * Tests grid printing functionality.
   * <p>
   * Verifies that calling {@link Grid#printGrid()} does not throw any exceptions.
   * </p>
   */
  @Test
  void printGrid_NoStateChange_NoExceptionThrown() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertDoesNotThrow(grid::printGrid);
  }

  /**
   * Tests retrieval of the number of rows in the grid.
   * <p>
   * Verifies that {@link Grid#getRows()} returns the correct row count.
   * </p>
   */
  @Test
  void getRows_GridInitialized_ReturnsCorrectRowCount() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertEquals(5, grid.getRows());
  }

  /**
   * Tests retrieval of the number of columns in the grid.
   * <p>
   * Verifies that {@link Grid#getCols()} returns the correct column count.
   * </p>
   */
  @Test
  void getCols_GridInitialized_ReturnsCorrectColumnCount() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertEquals(5, grid.getCols());
  }

  /**
   * Tests cell retrieval with out-of-bounds indices.
   * <p>
   * Verifies that {@link Grid#getCell(int, int)} throws an {@link IndexOutOfBoundsException} for
   * invalid indices.
   * </p>
   */
  @Test
  void getCell_OutOfBoundsIndices_ThrowsException() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertThrows(IndexOutOfBoundsException.class, () -> grid.getCell(5, 5));
    assertThrows(IndexOutOfBoundsException.class, () -> grid.getCell(-1, -1));
  }

  /**
   * Tests neighbor retrieval for an edge cell.
   * <p>
   * Verifies that an edge cell (in this case a corner cell) returns exactly three neighbors.
   * </p>
   */
  @Test
  void getNeighbors_EdgeCell_ReturnsThreeNeighbors() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertEquals(3, grid.getNeighbors(0, 0).size());
  }

  /**
   * Tests applying next states for multiple cells in the grid.
   * <p>
   * Verifies that after applying next states, every cell in the grid maintains the initial state.
   * </p>
   */
  @Test
  void applyNextStates_MultipleCells_AllMaintainInitialState() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    grid.applyNextStates();
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        assertEquals(MockState.STATE_ONE, grid.getCell(r, c).getCurrentState());
      }
    }
  }
}


