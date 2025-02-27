package cellsociety.view.shapefactory;

/**
 * concrete RectangleCellFactory class for creating new RectangleCell types
 */
public class RectangleCellFactory extends CellShapeFactory {

  @Override
  public CellShape createCellShape(double width, double height, boolean isUpward) {
    return new RectangleCell(width, height);
  }
}