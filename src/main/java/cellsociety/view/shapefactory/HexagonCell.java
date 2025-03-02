package cellsociety.view.shapefactory;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

/**
 * Class for creating a new HexagonCell instance where cells are displayed as Hexagons
 * <p>
 * implements the CellShape class
 * </p>
 *
 * @author Alana Zinkin
 */
public class HexagonCell implements CellShape {

  private Polygon hexagon;

  /**
   * constructor for creating a new RectangleCell object
   *
   * @param width  width of rectangle
   * @param height height of rectangle
   */
  public HexagonCell(double width, double height, boolean isUpward) {
    hexagon = new Polygon();
    double h = (Math.sqrt(3) / 2) * width;
    setDimensions(width, h, isUpward);
  }
  /**
   * concrete method for creating a new CellShape instance
   * @param width  width of the shape
   * @param height height of the shape
   * @return new HexagonCell instance
   */
  @Override
  public CellShape createShape(double width, double height, boolean isUpward) {
    return new HexagonCell(width, height, isUpward);
  }
  /**
   * @return the underlying Hexagon representation of the CellShape
   */
  @Override
  public Shape getShape() {
    return hexagon;
  }
  /**
   * sets the dimensions of the underlying Hexagon
   *
   * @param width    width of the hexagon
   * @param height   height of the hexagon, which is determined by the width in the method that calls it
   * @param isUpward unused variable -- used in other implementations of setDimensions()
   */
  @Override
  public void setDimensions(double width, double height, boolean isUpward) {
    hexagon.getPoints().addAll(
        0.0, height / 2,  // Left-middle
        width / 4, 0.0,        // Top-left
        3 * width / 4, 0.0,    // Top-right
        width, height / 2,          // Right-middle
        3 * width / 4, height,      // Bottom-right
        width / 4, height           // Bottom-left
    );
  }
}
