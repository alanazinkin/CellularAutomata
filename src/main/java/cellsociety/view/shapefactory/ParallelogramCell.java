package cellsociety.view.shapefactory;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class ParallelogramCell implements CellShape{

  private Polygon parallelogram;

  public ParallelogramCell(double width, double height, boolean isUpward) {
    parallelogram = new Polygon();
    setDimensions(width, height, isUpward);
  }

  @Override
  public CellShape createShape(double width, double height, boolean isUpward) {
    return new ParallelogramCell(width, height, isUpward);
  }

  @Override
  public Shape getShape() {
    return parallelogram;
  }

  @Override
  public void setDimensions(double width, double height, boolean isUpward) {
    double skew = height / 2;
    if (isUpward) {
      parallelogram.getPoints().addAll(
          0.0, height, // Bottom-left
          skew, 0.0,             // Top-left (shifted right by skew)
          width + skew, 0.0,     // Top-right
          width, height
      );
    }
    else{
      parallelogram.getPoints().addAll(
          0.0, 0.0,            // Top-left
          width, 0.0,          // Top-right
          width + skew, height, // Bottom-right (shifted right by skew)
          skew, height         // Bottom-left
      );
    }

  }
}
