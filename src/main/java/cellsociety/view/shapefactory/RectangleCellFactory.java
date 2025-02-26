package cellsociety.view.shapefactory;

/**
 * concrete RectangleCellFactory class for creating new RectangleCell types
 */
public class RectangleCellFactory extends CellShapeFactory {

  /**
   * @param width  width of underlying rectangle
   * @param height height of underlying rectangle
   * @return a new RectangleCell instance
   */
  @Override
  public CellShape createCellShape(int width, int height) {
    return new RectangleCell(width, height);
  }
}
