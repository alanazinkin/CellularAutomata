package cellsociety.view.shapefactory;

/**
 * Abstract class for creating a CellShapeFactory which can create Cell Shapes
 * @author Alana Zinkin
 */
public abstract class CellShapeFactory {

  /**
   * abstract method for creating a new CellShape instance
   *
   * @param width  width of underlying shape
   * @param height height of underlying shape
   * @return a new CellShape instance
   */
  public abstract CellShape createCellShape(double width, double height, boolean isUpward);

}