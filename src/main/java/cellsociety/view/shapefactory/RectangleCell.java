package cellsociety.view.shapefactory;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Class for creating a new RectangleCell instance where cells are displayed as rectangles
 * <p>
 *   implements the CellShape class
 * </p>
 * @author Alana Zinkin
 */
public class RectangleCell implements CellShape {
  private Rectangle rectangle;

  /**
   * constructor for creating a new RectangleCell object
   * @param width width of rectangle
   * @param height height of rectangle
   */
  public RectangleCell(double width, double height) {
    rectangle = new Rectangle(width, height);
  }

  /**
   * concrete method for creating a new CellShape instance
   * @param width  width of the shape
   * @param height height of the shape
   * @return new RectangleCell instance
   */
  @Override
  public CellShape createShape(double width, double height, boolean isUpward) {
    return new RectangleCell(width, height);
  }

  /**
   *
   * @return the underlying Rectangle
   */
  @Override
  public Shape getShape() {
    return rectangle;
  }

  /**
   * sets the dimensions of the underlying Rectangle
   *
   * @param width    width of the rectangle
   * @param height   height of the rectangle
   * @param isUpward
   */
  @Override
  public void setDimensions(double width, double height, boolean isUpward) {
    rectangle.setWidth(width);
    rectangle.setHeight(height);
  }

}
