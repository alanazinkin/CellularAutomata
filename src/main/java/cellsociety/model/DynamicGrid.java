package cellsociety.model;

/**
 * Interface that allows grids to dynamically resize for infinite edge support.
 * This is needed for the InfiniteEdge strategy to work properly.
 *
 * <p>Implementations should ensure that expanding the grid maintains the
 * integrity of existing cells and appropriately initializes new cells.
 *
 * @author Tatum McKinnis
 */
public interface DynamicGrid {

  /**
   * Expands the grid to include the specified range of coordinates.
   * Implementation should handle creating new cells with appropriate states.
   *
   * @param rowStart The minimum row coordinate to include (can be negative)
   * @param rowEnd The maximum row coordinate to include
   * @param colStart The minimum column coordinate to include (can be negative)
   * @param colEnd The maximum column coordinate to include
   */
  void expandToInclude(int rowStart, int rowEnd, int colStart, int colEnd);
}
