package cellsociety.view.shapefactory;

/**
 * Factory class for creating instances of {@link RectangleCell}. This class extends
 * {@link CellShapeFactory} and implements the factory method for creating rectangle-shaped cells.
 * @author Alana Zinkin
 */
public class RectangleCellFactory extends CellShapeFactory {

  /**
   * Creates a new instance of {@link RectangleCell} with the specified dimensions. The
   * {@code isUpward} parameter is not used in this implementation since a rectangle does not have
   * an inherent upward or downward orientation.
   *
   * @param width    the width of the rectangle cell
   * @param height   the height of the rectangle cell
   * @param isUpward an unused boolean parameter (included for compatibility with other cell
   *                 shapes)
   * @return a new {@link RectangleCell} instance
   */
  @Override
  public CellShape createCellShape(double width, double height, boolean isUpward) {
    return new RectangleCell(width, height);
  }
}
