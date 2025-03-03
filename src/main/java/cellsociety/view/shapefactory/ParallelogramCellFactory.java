package cellsociety.view.shapefactory;

/**
 * Factory class for creating instances of {@link ParallelogramCell}.
 * This class extends {@link CellShapeFactory} and implements the factory method
 * for creating parallelogram-shaped cells.
 * @author Alana Zinkin
 */
public class ParallelogramCellFactory extends CellShapeFactory {

  /**
   * Creates a new instance of {@link ParallelogramCell} with the specified dimensions
   * and orientation.
   *
   * @param width    the width of the parallelogram cell
   * @param height   the height of the parallelogram cell
   * @param isUpward a boolean indicating the orientation of the parallelogram;
   *                 true if upward-facing, false otherwise
   * @return a new {@link ParallelogramCell} instance
   */
  @Override
  public CellShape createCellShape(double width, double height, boolean isUpward) {
    return new ParallelogramCell(width, height, isUpward);
  }
}
