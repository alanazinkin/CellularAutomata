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
   * @param width    base of the triangle
   * @param height   of the triangle
   * @param isUpward the triangle's orientation has its base at the bottom and point at the top
   */
  public TriangleCell(double width, double height, boolean isUpward) {
    triangle = new Polygon();
    setDimensions(width, height, isUpward);
  }

  /**
   * creates a new TriangleCell instance
   *
   * @param width  width of the triangle
   