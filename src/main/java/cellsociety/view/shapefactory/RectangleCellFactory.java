package cellsociety.view.shapefactory;

public class RectangleCellFactory extends CellShapeFactory{

  @Override
  public CellShape createCellShape(int width, int height) {
    return new RectangleCell(width, height);
  }
}
