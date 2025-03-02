package cellsociety.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Von Neumann neighborhood strategy - considers only the 4 orthogonally adjacent cells (up, down, left, right).
 * Example:
 * Given a cell at (r, c), its neighbors are:
 *   (r-1, c)  -> Above
 *   (r+1, c)  -> Below
 *   (r, c-1)  -> Left
 *   (r, c+1)  -> Right
 *
 * @author Tatum McKinnis
 */
public class VonNeumannNeighborhood implements NeighborhoodStrategy {

  private static final int[][] NEIGHBOR_OFFSETS = {
      {-1, 0},  // Above
      {0, -1}, {0, 1},  // Left, Right
      {1, 0}   // Below
  };

  /**
   * Computes the coordinates of neighboring cells using the Von Neumann neighborhood pattern.
   *
   * @param row The row index of the current cell
   * @param col The column index of the current cell
   * @return A list of integer arrays, where each array represents the [row, col] of a neighboring cell
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
   * @return The string "VON_NEUMANN"
   */
  @Override
  public String getType() {
    return "VON_NEUMANN";
  }
}
