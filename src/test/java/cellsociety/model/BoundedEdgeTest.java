package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;
import cellsociety.model.state.MockState;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link BoundedEdge} implementation.
 * <p>
 * This class verifies the behavior of the bounded edge strategy, which treats cells outside the grid
 * bounds as invalid and inaccessible.
 * </p>
 *
 * @author Tatum McKinnis
 */
class BoundedEdgeTest {

  /**
   * Tests validation of positions within grid bounds.
   * <p>
   * Verifies that positions within grid bounds are considered valid.
   * </p>
   */
  @Test
  void isValidPosition_PositionWithinBounds_ReturnsTrue() {
    EdgeStrategy boundedEdge = new BoundedEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    assertTrue(boundedEdge.isValidPosition(grid, 0, 0));
    assertTrue(boundedEdge.isValidPosition(grid, 4, 4));
    assertTrue(boundedEdge.isValidPosition(grid, 2, 3));
  }

  /**
   * Tests validation of positions outside grid bounds.
   * <p>
   * Verifies that positions outside grid bounds are considered invalid.
   * </p>
   */
  @Test
  void isValidPosition_PositionOutsideBounds_ReturnsFalse() {
    EdgeStrategy boundedEdge = new BoundedEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    assertFalse(boundedEdge.isValidPosition(grid, -1, 0));
    assertFalse(boundedEdge.isValidPosition(grid, 0, -1));
    assertFalse(boundedEdge.isValidPosition(grid, 5, 0));
    assertFalse(boundedEdge.isValidPosition(grid, 0, 5));
  }

  /**
   * Tests cell retrieval for positions within grid bounds.
   * <p>
   * Verifies that cells within grid bounds can be successfully retrieved.
   * </p>
   */
  @Test
  void getCell_PositionWithinBounds_ReturnsCell() {
    EdgeStrategy boundedEdge = new BoundedEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    Cell cell = boundedEdge.getCell(grid, 2, 3);
    assertNotNull(cell);
    assertEquals(MockState.STATE_ONE, cell.getCurrentState());
  }

  /**
   * Tests cell retrieval for positions outside grid bounds.
   * <p>
   * Verifies that attempting to retrieve cells outside grid bounds returns null.
   * </p>
   */
  @Test
  void getCell_PositionOutsideBounds_ReturnsNull() {
    EdgeStrategy boundedEdge = new BoundedEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    assertNull(boundedEdge.getCell(grid, -1, 0));
    assertNull(boundedEdge.getCell(grid, 0, -1));
    assertNull(boundedEdge.getCell(grid, 5, 0));
    assertNull(boundedEdge.getCell(grid, 0, 5));
  }

  /**
   * Tests the strategy type identifier.
   * <p>
   * Verifies that the strategy returns the correct type string.
   * </p>
   */
  @Test
  void getType_NoConditions_ReturnsBoundedType() {
    EdgeStrategy boundedEdge = new BoundedEdge();
    assertEquals("BOUNDED", boundedEdge.getType());
  }
}