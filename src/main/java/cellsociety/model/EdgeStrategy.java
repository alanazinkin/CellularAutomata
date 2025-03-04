package cellsociety.model;

/**
 * Interface for edge handling strategies in cellular automata grids. Different implementations
 * define how to handle cell access beyond grid boundaries.
 *
 * @author Tatum McKinnis
 */
public interface EdgeStrategy {

  /**
   * Determines if coordinates are valid according to the edge strategy's rules. Some strategies
   * like toroidal might transform coordinates but still consider them valid.
   *
   * @param grid The grid containing the cells
   * @param row  The row coordinate
   * @param col  The column coordinate
   * @return true if the coordinates are valid according to this strategy, false otherwise
   */
  boolean isValidPosition(Grid grid, int row, int col);

  /**
   * Gets the cell at the specified coordinates, applying edge behavior as needed. This method might
   * transform coordinates (for toroidal) or create new cells (for infinite).
   *
   * @param grid The grid containing the cells
   * @param row  The row coordinate
   * @param col  The column coordinate
   * @return The cell at the specified coordinates after applying edge behavior, or null if not
   * valid
   */
  Cell getCell(Grid grid, int row, int col);

  /**
   * Returns a unique identifier for this edge strategy type.
   *
   * @return A string identifier for the strategy type
   */
  String getType();
}
