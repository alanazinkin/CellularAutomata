package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link MultipleNeighborhoods} implementation.
 * <p>
 * This class verifies that the multiple neighborhoods strategy correctly combines multiple
 * neighborhood patterns.
 * </p>
 *
 * @author Tatum McKinnis
 */
class MultipleNeighborhoodsTest {

  /**
   * Tests combining Moore and Von Neumann neighborhoods.
   * <p>
   * Verifies that combining Moore and Von Neumann neighborhoods results in the same neighbors as
   * Moore alone (since Moore is a superset of Von Neumann).
   * </p>
   */
  @Test
  void getNeighborCoordinates_MoorePlusVonNeumann_ReturnsMooreNeighbors() {
    NeighborhoodStrategy moore = new MooreNeighborhood();
    NeighborhoodStrategy vonNeumann = new VonNeumannNeighborhood();

    List<NeighborhoodStrategy> strategies = Arrays.asList(moore, vonNeumann);
    NeighborhoodStrategy multiple = new MultipleNeighborhoods(strategies);

    List<int[]> multipleCoords = multiple.getNeighborCoordinates(3, 3);
    List<int[]> mooreCoords = moore.getNeighborCoordinates(3, 3);

    assertEquals(mooreCoords.size(), multipleCoords.size());
    assertEquals(8, multipleCoords.size());

    assertCoordinateExists(multipleCoords, 2, 2);
    assertCoordinateExists(multipleCoords, 2, 3);
    assertCoordinateExists(multipleCoords, 2, 4);
    assertCoordinateExists(multipleCoords, 3, 2);
    assertCoordinateExists(multipleCoords, 3, 4);
    assertCoordinateExists(multipleCoords, 4, 2);
    assertCoordinateExists(multipleCoords, 4, 3);
    assertCoordinateExists(multipleCoords, 4, 4);
  }

  /**
   * Tests combining Moore and Extended Moore neighborhoods.
   * <p>
   * Verifies that combining Moore with Extended Moore neighborhood results in the union of both
   * neighborhoods.
   * </p>
   */
  @Test
  void getNeighborCoordinates_MoorePlusExtendedMoore_ReturnsCombinedNeighbors() {
    NeighborhoodStrategy moore = new MooreNeighborhood();
    NeighborhoodStrategy extendedMoore = new ExtendedMooreNeighborhood(2);

    List<NeighborhoodStrategy> strategies = Arrays.asList(moore, extendedMoore);
    NeighborhoodStrategy multiple = new MultipleNeighborhoods(strategies);

    List<int[]> multipleCoords = multiple.getNeighborCoordinates(3, 3);
    List<int[]> extendedCoords = extendedMoore.getNeighborCoordinates(3, 3);

    assertEquals(extendedCoords.size(), multipleCoords.size());
    assertEquals(24, multipleCoords.size());

    assertCoordinateExists(multipleCoords, 1, 1);
    assertCoordinateExists(multipleCoords, 1, 3);
    assertCoordinateExists(multipleCoords, 5, 5);
  }

  /**
   * Tests the strategy type identifier.
   * <p>
   * Verifies that the strategy returns the correct type string.
   * </p>
   */
  @Test
  void getType_NoConditions_ReturnsMultipleType() {
    List<NeighborhoodStrategy> strategies = Arrays.asList(
        new MooreNeighborhood(), new VonNeumannNeighborhood());
    NeighborhoodStrategy multiple = new MultipleNeighborhoods(strategies);

    assertEquals("MULTIPLE", multiple.getType());
  }

  /**
   * Tests combining standard Moore neighborhood with itself.
   * <p>
   * Verifies that combining a strategy with itself doesn't duplicate neighbors.
   * </p>
   */
  @Test
  void getNeighborCoordinates_DuplicateStrategies_NoDuplicateNeighbors() {
    NeighborhoodStrategy moore1 = new MooreNeighborhood();
    NeighborhoodStrategy moore2 = new MooreNeighborhood();

    List<NeighborhoodStrategy> strategies = Arrays.asList(moore1, moore2);
    NeighborhoodStrategy multiple = new MultipleNeighborhoods(strategies);

    List<int[]> multipleCoords = multiple.getNeighborCoordinates(3, 3);

    assertEquals(8, multipleCoords.size());
  }

  /**
   * Tests an empty MultipleNeighborhoods strategy.
   * <p>
   * Verifies that a MultipleNeighborhoods with no strategies returns no neighbors.
   * </p>
   */
  @Test
  void getNeighborCoordinates_EmptyStrategiesList_ReturnsNoNeighbors() {
    List<NeighborhoodStrategy> strategies = Arrays.asList();
    NeighborhoodStrategy multiple = new MultipleNeighborhoods(strategies);

    List<int[]> coords = multiple.getNeighborCoordinates(3, 3);

    assertEquals(0, coords.size());
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