package cellsociety.view.shapefactory;

/**
 * class for creating a TriangleCellFactory, which can produce TriangleCell instances
 */
public class TriangleCellFactory extends CellShapeFactory {

  @Override
  public CellShape createCellShape(double width, double height, boolean isUpward) {
    return new TriangleCell(width, height, isUpward);
  }
}