package cellsociety.view.shapefactory;


public class TriangleCellFactory extends CellShapeFactory {

  @Override
  public CellShape createCellShape(int width, int height) {
    return new TriangleCell(width, height);

  }
}
