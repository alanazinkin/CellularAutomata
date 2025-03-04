package cellsociety.model;


import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link MooreNeighborhood} implementation.
 * <p>
 * This class verifies that the Moore neighborhood strategy correctly identifies
 * all 8 surrounding cells as neighbors.
 * </p>
 *
 * @author Tatum McKinnis
 */
class MooreNeighborhoodTest {

  /**
   * Tests neighbor coordinates for an interior cell.
   * <p>
   * Verifies that all 8 surrounding cells are identified as neighbors for an interior cell.
   * </p>
   */
  @Test
  void getNeighborCoordinates_InteriorCell_ReturnsEightNeighbors() {
    NeighborhoodStrategy moore = new MooreNeighborhood();
    List<int[]> coords = moore.getNeighborCoordinates(3, 3);

    assertEquals(8, coords.size());

    assertCoordinateExists(coords, 2, 2);
    assertCoordinateExists(coords, 2, 3);
    assertCoordinateExists(coords, 2, 4);
    assertCoordinateExists(coords, 3, 2);
    assertCoordinateExists(coords, 3, 4);
    assertCoordinateExists(coords, 4, 2);
    assertCoordinateExists(coords, 4, 3);
    assertCoordinateExists(coords, 4, 4);
  }

  /**
   * Tests neighbor coordinates for a corner cell.
   * <p>
   * Verifies that all 8 surrounding positions are returned, even if some are outside the grid.
   * </p>
   */
  @Test
  void getNeighborCoordinates_CornerCell_ReturnsEightPositions() {
    NeighborhoodStrategy moore = new MooreNeighborhood();
    List<int[]> coords = moore.getNeighborCoordinates(0, 0);

    assertEquals(8, coords.size());

    assertCoordinateExists(coords, -1, -1);
    assertCoordinateExists(coords, -1, 0);
    assertCoordinateExists(coords, -1, 1);
    assertCoordinateExists(coords, 0, -1);
    assertCoordinateExists(coords, 0, 1);
    assertCoordinateExists(coords, 1, -1);
    assertCoordinateExists(coords, 1, 0);
    assertCoordinateExists(coords, 1, 1);
  }

  /**
   * Tests the strategy type identifier.
   * <p>
   * Verifies that the strategy returns the correct type string.
   * </p>
   */
  @Test
  void getType_NoConditions_ReturnsMooreType() {
    NeighborhoodStrategy moore = new MooreNeighborhood();
    assertEquals("MOORE", moore.getType());
  }

  /**
   * Helper method to check if a specific coordinate exists in the list.
   */
  private void assertCoordinateExists(List<int[]> coords, int row, int col) {
    boolean found = false;
    for (int[] coord : coords) {
      if (coord[0] == row && coord[1] == col) {
        found = true;
        break;
      }
    }
    assertTrue(found, "Coordinate [" + row + "," + col + "] should exist in the list");
  }
}
