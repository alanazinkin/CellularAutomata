package cellsociety.model;

/**
 * Implements a bounded edge strategy where coordinates outside the grid are considered invalid.
 * This maintains backward compatibility with the original Grid implementation.
 *
 * @author Tatum McKinnis
 */
public class BoundedEdge implements EdgeStrategy {

  /**
   * Checks if the given position is within the valid bounds of the grid.
   *
   * @param grid the grid in which to check the position
   * @param row  the row index
   * @param col  the column index
   * @return true if the position is within the grid bounds, false otherwise
   */
  @Override
  public boolean isValidPosition(Grid grid, int row, int col) {
    return row >= 0 && row < grid.getRows() && col >= 0 && col < grid.getCols();
  }

  /**
   * Retrieves the cell at the specified position if it is within bounds.
   *
   * @param grid the grid from which to retrieve the cell
   * @param row  the row index
   * @param col  the column index
   * @return the cell at the given position, or null if out of bounds
   */
  @Override
  public Cell getCell(Grid grid, int row, int col) {
    if (!isValidPosition(grid, row, col)) {
      return null;
    }
    return grid.getCellDirect(row, col);
  }

  /**
   * Returns the type of edge strategy implemented.
   *
   * @return a string representing the type of edge strategy
   */
  @Override
  public String getType() {
    return "BOUNDED";
  }
}

