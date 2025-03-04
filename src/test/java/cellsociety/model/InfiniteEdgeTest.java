package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.model.state.MockState;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link InfiniteEdge} implementation.
 * <p>
 * This class verifies the behavior of the infinite edge strategy, which expands the grid
 * as needed to accommodate cells outside the original boundaries.
 * </p>
 *
 * @author Tatum McKinnis
 */
class InfiniteEdgeTest {

  /**
   * Tests validation of positions with infinite edge.
   * <p>
   * Verifies that all positions are considered valid with infinite edge, regardless of bounds.
   * </p>
   */
  @Test
  void isValidPosition_AnyPosition_ReturnsTrue() {
    EdgeStrategy infiniteEdge = new InfiniteEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    assertTrue(infiniteEdge.isValidPosition(grid, 0, 0));
    assertTrue(infiniteEdge.isValidPosition(grid, 4, 4));
    assertTrue(infiniteEdge.isValidPosition(grid, -1, 0));
    assertTrue(infiniteEdge.isValidPosition(grid, 0, -1));
    assertTrue(infiniteEdge.isValidPosition(grid, 5, 0));
    assertTrue(infiniteEdge.isValidPosition(grid, 0, 5));
    assertTrue(infiniteEdge.isValidPosition(grid, 100, 100));
  }

  /**
   * Tests cell retrieval for positions within grid bounds.
   * <p>
   * Verifies that cells within grid bounds are retrieved as expected.
   * </p>
   */
  @Test
  void getCell_PositionWithinBounds_ReturnsCell() {
    EdgeStrategy infiniteEdge = new InfiniteEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    Cell cell = infiniteEdge.getCell(grid, 2, 3);
    assertNotNull(cell);
    assertEquals(MockState.STATE_ONE, cell.getCurrentState());
  }

  /**
   * Tests that an exception is thrown when used with a non-DynamicGrid.
   * <p>
   * Verifies that the infinite edge strategy requires a grid that implements DynamicGrid.
   * </p>
   */
  @Test
  void getCell_NonDynamicGrid_ThrowsException() {
    EdgeStrategy infiniteEdge = new InfiniteEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    assertThrows(UnsupportedOperationException.class, () -> infiniteEdge.getCell(grid, 5, 5));
    assertThrows(UnsupportedOperationException.class, () -> infiniteEdge.getCell(grid, -1, 0));
  }

  /**
   * Tests the strategy type identifier.
   * <p>
   * Verifies that the strategy returns the correct type string.
   * </p>
   */
  @Test
  void getType_NoConditions_ReturnsInfiniteType() {
    EdgeStrategy infiniteEdge = new InfiniteEdge();
    assertEquals("INFINITE", infiniteEdge.getType());
  }
}