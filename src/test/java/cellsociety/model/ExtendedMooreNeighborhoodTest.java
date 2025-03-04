package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link ExtendedMooreNeighborhood} implementation.
 * <p>
 * This class verifies that the Extended Moore neighborhood strategy correctly identifies all cells
 * within a specified radius as neighbors.
 * </p>
 *
 * @author Tatum McKinnis
 */
class ExtendedMooreNeighborhoodTest {

  /**
   * Tests neighbor coordinates for radius 1.
   * <p>
   * Verifies that with radius 1, the extended Moore neighborhood is identical to standard Moore.
   * </p>
   */
  @Test
  void getNeighborCoordinates_RadiusOne_EquivalentToMoore() {
    NeighborhoodStrategy extendedMoore = new ExtendedMooreNeighborhood(1);
    NeighborhoodStrategy moore = new MooreNeighborhood();

    List<int[]> extendedCoords = extendedMoore.getNeighborCoordinates(3, 3);
    List<int[]> mooreCoords = moore.getNeighborCoordinates(3, 3);

    assertEquals(mooreCoords.size(), extendedCoords.size());
    assertEquals(8, extendedCoords.size());

    assertCoordinateExists(extendedCoords, 2, 2);
    assertCoordinateExists(extendedCoords, 2, 3);
    assertCoordinateExists(extendedCoords, 2, 4);
    assertCoordinateExists(extendedCoords, 3, 2);
    assertCoordinateExists(extendedCoords, 3, 4);
    assertCoordinateExists(extendedCoords, 4, 2);
    assertCoordinateExists(extendedCoords, 4, 3);
    assertCoordinateExists(extendedCoords, 4, 4);
  }

  /**
   * Tests neighbor coordinates for radius 2.
   * <p>
   * Verifies that with radius 2, there are 24 neighbor cells (5x5 square minus the center).
   * </p>
   */
  @Test
  void getNeighborCoordinates_RadiusTwo_ReturnsTwentyFourNeighbors() {
    NeighborhoodStrategy extendedMoore = new ExtendedMooreNeighborhood(2);
    List<int[]> coords = extendedMoore.getNeighborCoordinates(5, 5);

    assertEquals(24, coords.size());

    assertCoordinateExists(coords, 4, 5);
    assertCoordinateExists(coords, 5, 4);
    assertCoordinateExists(coords, 6, 5);
    assertCoordinateExists(coords, 5, 6);

    assertCoordinateExists(coords, 3, 5);
    assertCoordinateExists(coords, 5, 3);
    assertCoordinateExists(coords, 7, 5);
    assertCoordinateExists(coords, 5, 7);
    assertCoordinateExists(coords, 3, 3);
    assertCoordinateExists(coords, 3, 7);
    assertCoordinateExists(coords, 7, 3);
    assertCoordinateExists(coords, 7, 7);
  }

  /**
   * Tests constructor with invalid radius.
   * <p>
   * Verifies that attempting to create a neighborhood with radius <= 0 throws an exception.
   * </p>
   */
  @Test
  void constructor_ZeroRadius_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new ExtendedMooreNeighborhood(0));
    assertThrows(IllegalArgumentException.class, () -> new ExtendedMooreNeighborhood(-1));
  }

  /**
   * Tests the strategy type identifier.
   * <p>
   * Verifies that the strategy returns the correct type string including the radius.
   * </p>
   */
  @Test
  void getType_RadiusTwo_ReturnsExtendedMooreTypeWithRadius() {
    NeighborhoodStrategy extendedMoore = new ExtendedMooreNeighborhood(2);
    assertEquals("EXTENDED_MOORE_2", extendedMoore.getType());

    NeighborhoodStrategy extendedMoore3 = new ExtendedMooreNeighborhood(3);
    assertEquals("EXTENDED_MOORE_3", extendedMoore3.getType());
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
