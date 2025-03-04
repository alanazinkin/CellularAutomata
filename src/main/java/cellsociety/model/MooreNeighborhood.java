package cellsociety.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Moore neighborhood - considers all 8 surrounding cells in a square pattern. It provides direct
 * access to adjacent cells, including diagonals.
 * <p>
 * This is the original behavior from the Grid class.
 * <p>
 * Example: Given a cell (C) at (row, col): [X] [X] [X] [X] [C] [X] [X] [X] [X]
 * <p>
 * The Moore neighborhood includes all X cells.
 *
 * @author Tatum McKinnis
 */
public class MooreNeighborhood implements NeighborhoodStrategy {

  // Moore neighborhood offsets - all 8 surrounding cells
  private static final int[][] NEIGHBOR_OFFSETS = {
      {-1, -1}, {-1, 0}, {-1, 1},
      {0, -1}, {0, 1},
      {1, -1}, {1, 0}, {1, 1}
  };

  /**
   * Retrieves the coordinates of all neighboring cells in a Moore neighborhood pattern.
   *
   * @param row The row index of the central cell.
   * @param col The column index of the central cell.
   * @return A list of coordinate pairs representing the neighboring cells.
   */
  @Override
  public List<int[]> getNeighborCoordinates(int row, int col) {
    List<int[]> neighbors = new ArrayList<>(NEIGHBOR_OFFSETS.length);

    for (int[] offset : NEIGHBOR_OFFSETS) {
      int neighborRow = row + offset[0];
      int neighborCol = col + offset[1];
      neighbors.add(new int[]{neighborRow, neighborCol});
    }

    return neighbors;
  }

  /**
   * Returns the type identifier for this neighborhood strategy.
   *
   * @return A string representing the neighborhood type, "MOORE".
   */
  @Override
  public String getType() {
    return "MOORE";
  }
}
