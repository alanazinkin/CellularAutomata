package cellsociety.view.shapefactory;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class TriangleCell implements CellShape {
  private Polygon triangle;

  public TriangleCell(double width, double height) {
    triangle = new Polygon();
    setDimensions(width, height);
  }

  @Override
  public CellShape createShape(double width, double height) {
    return new TriangleCell(width, height);
  }

  @Override
  public Shape getShape() {
    return triangle;
  }

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

