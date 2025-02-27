package cellsociety.view.shapefactory;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Class for creating a new RectangleCell instance where cells are displayed as rectangles
 * <p>
 *   implements the CellShape class
 * </p>
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
   * concrete method for creatin