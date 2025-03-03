package cellsociety.view.shapefactory;

/**
 * class for creating a TriangleCellFactory, which can produce TriangleCell instances
 * @author Alana Zinkin
 */
public class TriangleCellFactory extends CellShapeFactory {

  /**
   * Creates a new instance of {@link TriangleCell} with the specified dimensions and orientation.
   *
   * @param width    the width of the triangle cell
   * @param height   the height of the triangle cell
   * @param isUpward a boolean indicating the orientation of the triangle; {@code true} if the
   *                 triangle points upward, {@code false} otherwise
   * @return a new {@link TriangleCell} instance with the given dimensions and orientation
   */
  @Override
  public CellShape createCellShape(double width, double height, boolean isUpward) {
    return new TriangleCell(width, height, isUpward);
  }
}