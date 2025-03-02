package cellsociety.view.shapefactory;

public class HexagonCellFactory extends CellShapeFactory {

  @Override
  public CellShape createCellShape(double width, double height, boolean isUpward) {
    return new HexagonCell(width, height, isUpward);
  }
}
