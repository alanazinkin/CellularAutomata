package cellsociety.model;

/**
 * Implements a mirror edge strategy where coordinates outside the grid are mirrored from the
 * edges.
 *
 * <p>This strategy ensures that out-of-bounds positions reflect back into the grid, effectively
 * creating a continuous mirrored surface at the boundaries.
 *
 * @author Tatum McKinnis
 */
public class MirrorEdge implements EdgeStrategy {

  /**
   * Determines whether a position is valid within a mirrored grid.
   *
   * @param grid The grid instance
   * @param row  The row index to check
   * @param col  The column index to check
   * @return Always returns true since all positions are conceptually valid in a mirrored space
   */
  @Override
  public boolean isValidPosition(Grid grid, int row, int col) {
    return true;
  }

  /**
   * Retrieves the cell at the specified position, mirroring coordinates if out of bounds.
   *
   * @param grid The grid instance
   * @param row  The row index of the desired cell
   * @param col  The column index of the desired cell
   * @return The cell at the mirrored position within the grid
   */
  @Override
  public Cell getCell(Grid grid, int row, int col) {
    int rows = grid.getRows();
    int cols = grid.getCols();

    int mirroredRow = row;
    int mirroredCol = col;

    if (row < 0) {
      mirroredRow = -row - 1;
    } else if (row >= rows) {
      mirroredRow = 2 * rows - row - 1;
    }

    if (col < 0) {
      mirroredCol = -col - 1;
    } else if (col >= cols) {
      mirroredCol = 2 * cols - col - 1;
    }

    mirroredRow = Math.max(0, Math.min(rows - 1, mirroredRow));
    mirroredCol = Math.max(0, Math.min(cols - 1, mirroredCol));

    return grid.getCellDirect(mirroredRow, mirroredCol);
  }

  /**
   * Returns the type of edge strategy as a string.
   *
   * @return "MIRROR"
   */
  @Override
  public String getType() {
    return "MIRROR";
  }
}
