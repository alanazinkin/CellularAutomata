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
  public CellShape createShape(d