package cellsociety.view.shapefactory;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

/**
 * concrete TriangleCell class for creating a cell in the shape of a Triangle
 */
public class TriangleCell implements CellShape {

  private Polygon triangle;

  /**
   * constructor for making a new TriangleCell, which initializes a new Triangle object
   *
   * @param width  base of the triangle
   * @param height of the triangle
   */
  public TriangleCell(double width, double height) {
    triangle = new Polygon();
    setDimensions(width, height);
  }

  /**
   * creates a new TriangleCell instance
   *
   * @param width  width of the triangle
   * @param height height of the triangle
   * @return new TriangleCell instance
   */
  @Override
  public CellShape createShape(double width, double height) {
    return new TriangleCell(width, height);
  }

  /**
   * @return underlying Triangle object
   */
  @Override
  public Shape getShape() {
    return triangle;
  }

  /**
   * sets the dimensions of the underlying Triangle object
   *
   * @param width  width of the triangle
   * @param height height of the triangle
   */
  @Override
  public void setDimensions(double width, double height) {
    // Set points for an equilateral triangle
    triangle.getPoints().setAll(
        0.0, height,           // Bottom left
        width, height,         // Bottom right
        width / 2, 0.0         // Top middle
    );
  }
}

