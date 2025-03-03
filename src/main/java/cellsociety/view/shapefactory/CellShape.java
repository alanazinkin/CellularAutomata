package cellsociety.view.shapefactory;

import javafx.scene.shape.Shape;

/**
 * interface for creating the visual display of a cell
 * @author Alana Zinkin
 */
public interface CellShape {

  /**
   * initializes a new cell as a shape
   *
   * @param width  width of the shape
   * @param height height of the shape
   * @return a new CellShape instance
   */
  CellShape createShape(double width, double height, boolean isUpward);

  /**
   * @return the underlying Shape representation of the CellShape (ex: Rectangle, Triangle, Hexagon)
   */
  Shape getShape();

  /**
   * sets the dimensions of the underlying Shape representation
   *
   * @param width    width of the shape
   * @param height   height of the shape
   * @param isUpward
   */
  void setDimensions(double width, double height, boolean isUpward);
}
