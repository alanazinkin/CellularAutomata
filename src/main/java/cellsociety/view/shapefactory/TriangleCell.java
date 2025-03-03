package cellsociety.view.shapefactory;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

/**
 * concrete TriangleCell class for creating a cell in the shape of a Triangle
 * @author Alana Zinkin
 */
public class TriangleCell implements CellShape {

  private Polygon triangle;

  /**
   * constructor for making a new TriangleCell, which initializes a new Triangle object
   *
   * @param width    base of the triangle
   * @param height   of the triangle
   * @param isUpward the triangle's orientation has its base at the bottom and point at the top
   */
  public TriangleCell(double width, double height, boolean isUpward) {
    triangle = new Polygon();
    setDimensions(width, height, isUpward);
  }

  @Override
  public CellShape createShape(double width, double height, boolean isUpward) {
    return new TriangleCell(width, height, isUpward);
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
  public void setDimensions(double width, double height, boolean isUpward) {
    // Set points for an equilateral triangle
    if (isUpward) {
      triangle.getPoints().setAll(
          0.0, height,       // Bottom left
          width, height,     // Bottom right
          width / 2, 0.0     // Top middle
      );
    } else {
      triangle.getPoints().setAll(
          0.0, 0.0,          // Top left
          width, 0.0,        // Top right
          width / 2, height  // Bottom middle
      );
    }

  }
}