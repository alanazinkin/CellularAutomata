package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.model.state.MockState;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link ToroidalEdge} implementation.
 * <p>
 * This class verifies the behavior of the toroidal edge strategy, which wraps cells around the grid
 * edges to create a continuous surface with no boundaries.
 * </p>
 *
 * @author Tatum McKinnis
 */
class ToroidalEdgeTest {

  /**
   * Tests validation of positions with toroidal edge.
   * <p>
   * Verifies that all positions are considered valid with toroidal edge, regardless of bounds.
   * </p>
   */
  @Test
  void isValidPosition_AnyPosition_ReturnsTrue() {
    EdgeStrategy toroidalEdge = new ToroidalEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    assertTrue(toroidalEdge.isValidPosition(grid, 0, 0));
    assertTrue(toroidalEdge.isValidPosition(grid, 4, 4));
    assertTrue(toroidalEdge.isValidPosition(grid, -1, 0));
    assertTrue(toroidalEdge.isValidPosition(grid, 0, -1));
    assertTrue(toroidalEdge.isValidPosition(grid, 5, 0));
    assertTrue(toroidalEdge.isValidPosition(grid, 0, 5));
    assertTrue(toroidalEdge.isValidPosition(grid, 100, 100));
  }

  /**
   * Tests cell retrieval for positions within grid bounds.
   * <p>
   * Verifies that cells within grid bounds are retrieved as expected.
   * </p>
   */
  @Test
  void getCell_PositionWithinBounds_ReturnsCell() {
    EdgeStrategy toroidalEdge = new ToroidalEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    Cell cell = toroidalEdge.getCell(grid, 2, 3);
    assertNotNull(cell);
    assertEquals(MockState.STATE_ONE, cell.getCurrentState());
  }

  /**
   * Tests cell wrapping for positions beyond right edge.
   * <p>
   * Verifies that positions beyond right edge wrap around to the left edge.
   * </p>
   */
  @Test
  void getCell_PositionBeyondRightEdge_WrapsToLeftEdge() {
    EdgeStrategy toroidalEdge = new ToroidalEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    Cell cell = toroidalEdge.getCell(grid, 0, 5);
    assertNotNull(cell);
    assertSame(grid.getCellDirect(0, 0), cell);

    cell = toroidalEdge.getCell(grid, 2, 7);
    assertNotNull(cell);
    assertSame(grid.getCellDirect(2, 2), cell);
  }

  /**
   * Tests cell wrapping for positions beyond bottom edge.
   * <p>
   * Verifies that positions beyond bottom edge wrap around to the top edge.
   * </p>
   */
  @Test
  void getCell_PositionBeyondBottomEdge_WrapsToTopEdge() {
    EdgeStrategy toroidalEdge = new ToroidalEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    Cell cell = toroidalEdge.getCell(grid, 5, 0);
    assertNotNull(cell);
    assertSame(grid.getCellDirect(0, 0), cell);

    cell = toroidalEdge.getCell(grid, 8, 3);
    assertNotNull(cell);
    assertSame(grid.getCellDirect(3, 3), cell);
  }

  /**
   * Tests cell wrapping for negative position indices.
   * <p>
   * Verifies that negative positions wrap around from the opposite edge.
   * </p>
   */
  @Test
  void getCell_NegativePosition_WrapsFromOppositeEdge() {
    EdgeStrategy toroidalEdge = new ToroidalEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    Cell cell = toroidalEdge.getCell(grid, -1, 0);
    assertNotNull(cell);
    assertSame(grid.getCellDirect(4, 0), cell);

    cell = toroidalEdge.getCell(grid, 0, -2);
    assertNotNull(cell);
    assertSame(grid.getCellDirect(0, 3), cell);
  }

  /**
   * Tests cell wrapping for corner positions.
   * <p>
   * Verifies that positions beyond corners wrap around correctly.
   * </p>
   */
  @Test
  void getCell_CornerPosition_WrapsCorrectly() {
    EdgeStrategy toroidalEdge = new ToroidalEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    Cell cell = toroidalEdge.getCell(grid, -1, -1);
    assertNotNull(cell);
    assertSame(grid.getCellDirect(4, 4), cell);

    cell = toroidalEdge.getCell(grid, 5, 5);
    assertNotNull(cell);
    assertSame(grid.getCellDirect(0, 0), cell);
  }

  /**
   * Tests the strategy type identifier.
   * <p>
   * Verifies that the strategy returns the correct type string.
   * </p>
   */
  @Test
  void getType_NoConditions_ReturnsToroidalType() {
    EdgeStrategy toroidalEdge = new ToroidalEdge();
    assertEquals("TOROIDAL", toroidalEdge.getType());
  }
}
