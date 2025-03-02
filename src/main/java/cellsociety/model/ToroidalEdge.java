package cellsociety.model;

/**
 * Toroidal edge strategy - coordinates wrap around the grid horizontally and vertically.
 * This creates a seamless, continuous grid where moving off one edge re-enters from the opposite side.
 *
 * Example behavior:
 * - If a cell moves beyond the right edge, it reappears on the left.
 * - If a cell moves beyond the bottom edge, it reappears at the top.
 *
 * @author Tatum McKinnis
 */
public class ToroidalEdge implements EdgeStrategy {

  /**
   * Determines whether a given position is valid. Since this is a toroidal grid, all positions are valid.
   *
   * @param grid The grid on which to check the position
   * @param row The row index of the position
   * @param col The column index of the position
   * @return Always returns {@code true}, as all positions wrap around
   */
  @Override
  public boolean isValidPosition(Grid grid, int row, int col) {
    return true;
  }

  /**
   * Retrieves the cell at the given coordinates, wrapping them around the grid if they are out of bounds.
   *
   * @param grid The grid from which to retrieve the cell
   * @param row The row index of the desired cell (may be out of bounds)
   * @param col The column index of the desired cell (may be out of bounds)
   * @return The cell at the wrapped-around coordinates
   */
  @Override
  public Cell getCell(Grid grid, int row, int col) {
    int wrappedRow = ((row % grid.getRows()) + grid.getRows()) % grid.getRows();
    int wrappedCol = ((col % grid.getCols()) + grid.getCols()) % grid.getCols();

    return grid.getCellDirect(wrappedRow, wrappedCol);
  }

  /**
   * Returns the type identifier for this edge strategy.
   *
   * @return The string "TOROIDAL"
   */
  @Override
  public String getType() {
    return "TOROIDAL";
  }
}
