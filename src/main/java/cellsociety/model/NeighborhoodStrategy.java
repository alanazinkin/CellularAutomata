package cellsociety.model;

import java.util.List;

/**
 * Interface for neighborhood strategies in cellular automata grids. Different implementations
 * define which cells are considered neighbors.
 *
 * @author Tatum McKinnis
 */
public interface NeighborhoodStrategy {

  /**
   * Returns the coordinates of neighboring cells for a given cell position.
   *
   * @param row The row of the cell
   * @param col The column of the cell
   * @return List of int arrays containing [row, col] pairs for all neighbors
   */
  List<int[]> getNeighborCoordinates(int row, int col);

  /**
   * Returns a unique identifier for this neighborhood strategy type.
   *
   * @return A string identifier for the strategy type
   */
  String getType();
}
