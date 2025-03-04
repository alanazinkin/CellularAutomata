package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.model.state.MockState;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link MirrorEdge} implementation.
 * <p>
 * This class verifies the behavior of the mirror edge strategy, which reflects cells at the grid
 * edges to create a mirrored effect.
 * </p>
 *
 * @author Tatum McKinnis
 */
class MirrorEdgeTest {

  /**
   * Tests validation of positions with mirror edge.
   * <p>
   * Verifies that all positions are considered valid with mirror edge, regardless of bounds.
   * </p>
   */
  @Test
  void isValidPosition_AnyPosition_ReturnsTrue() {
    EdgeStrategy mirrorEdge = new MirrorEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    assertTrue(mirrorEdge.isValidPosition(grid, 0, 0));
    assertTrue(mirrorEdge.isValidPosition(grid, 4, 4));
    assertTrue(mirrorEdge.isValidPosition(grid, -1, 0));
    assertTrue(mirrorEdge.isValidPosition(grid, 0, -1));
    assertTrue(mirrorEdge.isValidPosition(grid, 5, 0));
    assertTrue(mirrorEdge.isValidPosition(grid, 0, 5));
  }

  /**
   * Tests cell retrieval for positions within grid bounds.
   * <p>
   * Verifies that cells within grid bounds are retrieved as expected.
   * </p>
   */
  @Test
  void getCell_PositionWithinBounds_ReturnsCell() {
    EdgeStrategy mirrorEdge = new MirrorEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    Cell cell = mirrorEdge.getCell(grid, 2, 3);
    assertNotNull(cell);
    assertEquals(MockState.STATE_ONE, cell.getCurrentState());
  }

  /**
   * Tests cell mirroring for positions just beyond right edge.
   * <p>
   * Verifies that positions beyond right edge are mirrored correctly.
   * </p>
   */
  @Test
  void getCell_PositionBeyondRightEdge_MirrorsCorrectly() {
    EdgeStrategy mirrorEdge = new MirrorEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    Cell cell = mirrorEdge.getCell(grid, 0, 5);
    assertNotNull(cell);
    assertSame(grid.getCellDirect(0, 4), cell);

    cell = mirrorEdge.getCell(grid, 2, 6);
    assertNotNull(cell);
    assertSame(grid.getCellDirect(2, 3), cell);
  }

  /**
   * Tests cell mirroring for positions just beyond bottom edge.
   * <p>
   * Verifies that positions beyond bottom edge are mirrored correctly.
   * </p>
   */
  @Test
  void getCell_PositionBeyondBottomEdge_MirrorsCorrectly() {
    EdgeStrategy mirrorEdge = new MirrorEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    Cell cell = mirrorEdge.getCell(grid, 5, 0);
    assertNotNull(cell);
    assertSame(grid.getCellDirect(4, 0), cell);

    cell = mirrorEdge.getCell(grid, 6, 3);
    assertNotNull(cell);
    assertSame(grid.getCellDirect(3, 3), cell);
  }

  /**
   * Tests cell mirroring for negative positions.
   * <p>
   * Verifies that negative positions are mirrored correctly.
   * </p>
   */
  @Test
  void getCell_NegativePosition_MirrorsCorrectly() {
    EdgeStrategy mirrorEdge = new MirrorEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    Cell cell = mirrorEdge.getCell(grid, -1, 0);
    assertNotNull(cell);
    assertSame(grid.getCellDirect(0, 0), cell);

    cell = mirrorEdge.getCell(grid, 0, -2);
    assertNotNull(cell);
    assertSame(grid.getCellDirect(0, 1), cell);
  }

  /**
   * Tests cell mirroring for corner positions.
   * <p>
   * Verifies that positions beyond corners are mirrored correctly.
   * </p>
   */
  @Test
  void getCell_CornerPosition_MirrorsCorrectly() {
    EdgeStrategy mirrorEdge = new MirrorEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    Cell cell = mirrorEdge.getCell(grid, -1, -1);
    assertNotNull(cell);
    assertSame(grid.getCellDirect(0, 0), cell);

    cell = mirrorEdge.getCell(grid, 5, 5);
    assertNotNull(cell);
    assertSame(grid.getCellDirect(4, 4), cell);
  }

  /**
   * Tests multiple mirroring for positions far outside the grid.
   * <p>
   * Verifies that positions that would require multiple mirroring are handled correctly.
   * </p>
   */
  @Test
  void getCell_FarOutsidePosition_HandleMultipleMirroring() {
    EdgeStrategy mirrorEdge = new MirrorEdge();
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    Cell cell = mirrorEdge.getCell(grid, 10, 3);
    assertNotNull(cell);

    cell = mirrorEdge.getCell(grid, 3, 15);
    assertNotNull(cell);
  }

  /**
   * Tests the strategy type identifier.
   * <p>
   * Verifies that the strategy returns the correct type string.
   * </p>
   */
  @Test
  void getType_NoConditions_ReturnsMirrorType() {
    EdgeStrategy mirrorEdge = new MirrorEdge();
    assertEquals("MIRROR", mirrorEdge.getType());
  }
}