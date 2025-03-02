package cellsociety.view.shapefactory;

public class ParallelogramCellFactory extends CellShapeFactory {

  @Override
  public CellShape createCellShape(double width, double height, boolean isUpward) {
    return new ParallelogramCell(width, height, isUpward);
  }
}
