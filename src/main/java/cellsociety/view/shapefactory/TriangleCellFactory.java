package cellsociety.view.shapefactory;

/**
 * class for creating a TriangleCellFactory, which can produce TriangleCell instances
 */
public class TriangleCellFactory extends CellShapeFactory {

  /**
   * constructor for creating a new TriangleCell object
   *
   * @param width  width of underlying triangle
   * @param height height of underlying triangle
   * @return new TriangleCell object
   */
  @Override
  public CellShape createCellShape(int width, int height) {
    return new TriangleCell(width, height);

  }
}
