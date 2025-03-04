package cellsociety.model;

/**
 * Implements an infinite edge strategy where new cells are created beyond the grid boundaries as
 * needed.
 *
 * <p>This strategy assumes that the grid supports dynamic expansion and may require modifications
 * to
 * the Grid implementation to handle infinite growth properly.
 *
 * @author Tatum McKinnis
 */
public class InfiniteEdge implements EdgeStrategy {

  /**
   * Determines whether a position is valid within an infinite grid.
   *
   * @param grid The grid instance
   * @param row  The row index to check
   * @param col  The column index to check
   * @return Always returns true since all positions are conceptually valid in an infinite grid
   */
  @Override
  public boolean isValidPosition(Grid grid, int row, int col) {
    return true;
  }

  /**
   * Retrieves the cell at the specified position, expanding the grid if necessary.
   *
   * @param grid The grid instance
   * @param row  The row index of the desired cell
   * @param col  The column index of the desired cell
   * @return The cell at the specified position, creating new cells if needed
   * @throws UnsupportedOperationException if the grid does not support dynamic expansion
   */
  @Override
  public Cell getCell(Grid grid, int row, int col) {
    if (row >= 0 && row < grid.getRows() && col >= 0 && col < grid.getCols()) {
      return grid.getCellDirect(row, col);
    }
    int expandRowStart = Math.min(0, row);
    int expandRowEnd = Math.max(grid.getRows() - 1, row);
    int expandColStart = Math.min(0, col);
    int expandColEnd = Math.max(grid.getCols() - 1, col);

    if (grid instanceof DynamicGrid) {
      ((DynamicGrid) grid).expandToInclude(expandRowStart, expandRowEnd, expandColStart,
          expandColEnd);
      return grid.getCellDirect(row, col);
    } else {
      throw new UnsupportedOperationException("InfiniteEdge requires a DynamicGrid implementation");
    }
  }

  /**
   * Returns the type of edge strategy as a string.
   *
   * @return "INFINITE"
   */
  @Override
  public String getType() {
    return "INFINITE";
  }
}
