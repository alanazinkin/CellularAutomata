package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.List;

/**
 * Test class for {@link VonNeumannNeighborhood} implementation.
 * <p>
 * This class verifies that the Von Neumann neighborhood strategy correctly identifies
 * the 4 orthogonally adjacent cells as neighbors.
 * </p>
 *
 * @author Tatum McKinnis
 */
class VonNeumannNeighborhoodTest {

  /**
   * Tests neighbor coordinates for an interior cell.
   * <p>
   * Verifies that only the 4 orthogonally adjacent cells are identified as neighbors.
   * </p>
   */
  @Test
  void getNeighborCoordinates_InteriorCell_ReturnsFourNeighbors() {
    NeighborhoodStrategy vonNeumann = new VonNeumannNeighborhood();
    List<int[]> coords = vonNeumann.getNeighborCoordinates(3, 3);

    assertEquals(4, coords.size());

    assertCoordinateExists(coords, 2, 3); // Up
    assertCoordinateExists(coords, 3, 2); // Left
    assertCoordinateExists(coords, 3, 4); // Right
    assertCoordinateExists(coords, 4, 3); // Down

    assertCoordinateNotExists(coords, 2, 2);
    assertCoordinateNotExists(coords, 2, 4);
    assertCoordinateNotExists(coords, 4, 2);
    assertCoordinateNotExists(coords, 4, 4);
  }

  /**
   * Tests neighbor coordinates for a corner cell.
   * <p>
   * Verifies that all 4 orthogonal positions are returned, even if some are outside the grid.
   * </p>
   */
  @Test
  void getNeighborCoordinates_CornerCell_ReturnsFourPositions() {
    NeighborhoodStrategy vonNeumann = new VonNeumannNeighborhood();
    List<int[]> coords = vonNeumann.getNeighborCoordinates(0, 0);

    assertEquals(4, coords.size());

    assertCoordinateExists(coords, -1, 0); // Up
    assertCoordinateExists(coords, 0, -1); // Left
    assertCoordinateExists(coords, 0, 1);  // Right
    assertCoordinateExists(coords, 1, 0);  // Down

    assertCoordinateNotExists(coords, -1, -1);
    assertCoordinateNotExists(coords, -1, 1);
    assertCoordinateNotExists(coords, 1, -1);
    assertCoordinateNotExists(coords, 1, 1);
  }

  /**
   * Tests the strategy type identifier.
   * <p>
   * Verifies that the strategy returns the correct type string.
   * </p>
   */
  @Test
  void getType_NoConditions_ReturnsVonNeumannType() {
    NeighborhoodStrategy vonNeumann = new VonNeumannNeighborhood();
    assertEquals("VON_NEUMANN", vonNeumann.getType());
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

  /**
   * Helper method to check if a specific coordinate does not exist in the list.
   */
  private void assertCoordinateNotExists(List<int[]> coords, int row, int col) {
    boolean found = false;
    for (int[] coord : coords) {
      if (coord[0] == row && coord[1] == col) {
        found = true;
        break;
      }
    }
    assertFalse(found, "Coordinate [" + row + "," + col + "] should not exist in the list");
  }
}
