package cellsociety.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements an extended Moore neighborhood where all cells within a specified radius around a
 * central cell are considered neighbors.
 *
 * <p>The neighborhood extends in all directions (including diagonals) within the given radius.
 *
 * @author Tatum McKinnis
 */
public class ExtendedMooreNeighborhood implements NeighborhoodStrategy {

  private final int radius;

  /**
   * Creates an extended Moore neighborhood with the specified radius.
   *
   * @param radius The maximum distance from the central cell to be considered a neighbor
   * @throws IllegalArgumentException if the radius is not positive
   */
  public ExtendedMooreNeighborhood(int radius) {
    if (radius <= 0) {
      throw new IllegalArgumentException("Radius must be positive");
    }
    this.radius = radius;
  }

  /**
   * Computes the coordinates of all neighboring cells within the specified radius.
   *
   * @param row The row index of the central cell
   * @param col The column index of the central cell
   * @return A list of integer arrays representing the coordinates of neighboring cells
   */
  @Override
  public List<int[]> getNeighborCoordinates(int row, int col) {
    List<int[]> neighbors = new ArrayList<>();

    for (int dr = -radius; dr <= radius; dr++) {
      for (int dc = -radius; dc <= radius; dc++) {
        if (dr == 0 && dc == 0) {
          continue;
        }

        neighbors.add(new int[]{row + dr, col + dc});
      }
    }

    return neighbors;
  }

  /**
   * Returns a string representing the type of neighborhood strategy used.
   *
   * @return A string in the format "EXTENDED_MOORE_<radius>"
   */
  @Override
  public String getType() {
    return "EXTENDED_MOORE_" + radius;
  }
}
