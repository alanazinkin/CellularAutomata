package cellsociety.view.shapefactory;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class HexagonCell implements CellShape {

  private Polygon hexagon;

  public HexagonCell(double width, double height, boolean isUpward) {
    hexagon = new Polygon();
    double h = (Math.sqrt(3) / 2) * width;
    setDimensions(width, h, isUpward);
  }

  @Override
  public CellShape createShape(double width, double height, boolean isUpward) {
    return new HexagonCell(width, height, isUpward);
  }

  @Override
  public Shape getShape() {
    return hexagon;
  }

  @Override
  public void setDimensions(double width, double height, boolean isUpward) {
  // Correct height proportion

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
