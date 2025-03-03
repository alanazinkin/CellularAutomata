package cellsociety.view.shapefactory;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

/**
 * Class for creating a ParallelogramCell, which produces instances of parallelogram-shaped cells.
 * @author Alana Zinkin
 */
public class ParallelogramCell implements CellShape {

  private Polygon parallelogram;

  /**
   * Constructs a ParallelogramCell with the specified width, height, and orientation.
   *
   * @param width    the width of the parallelogram
   * @param height   the height of the parallelogram
   * @param isUpward a boolean indicating the orientation of the parallelogram; true if
   *                 upward-facing, false otherwise
   */
  public ParallelogramCell(double width, double height, boolean isUpward) {
    parallelogram = new Polygon();
    setDimensions(width, height, isUpward);
  }

  /**
   * Creates a new instance of ParallelogramCell with the given dimensions.
   *
   * @param width    the width of the parallelogram
   * @param height   the height of the parallelogram
   * @param isUpward a boolean indicating the orientation of the parallelogram; true if
   *                 upward-facing, false otherwise
   * @return a new instance of ParallelogramCell with the specified properties
   */
  @Override
  public CellShape createShape(double width, double height, boolean isUpward) {
    return new ParallelogramCell(width, height, isUpward);
  }

  /**
   * Retrieves the shape of the parallelogram cell.
   *
   * @return the Shape object representing the parallelogram
   */
  @Override
  public Shape getShape() {
    return parallelogram;
  }

  /**
   * Sets the dimensions of the parallelogram and updates its points.
   *
   * @param width    the width of the parallelogram
   * @param height   the height of the parallelogram
   * @param isUpward a boolean indicating the orientation of the parallelogram; true if
   *                 upward-facing, false otherwise
   */
  @Override
  public void setDimensions(double width, double height, boolean isUpward) {
    double skew = height / 2;
    parallelogram.getPoints().clear();

    if (isUpward) {
      parallelogram.getPoints().addAll(
          0.0, height,        // Bottom-left
          skew, 0.0,          // Top-left (shifted right by skew)
          width + skew, 0.0,  // Top-right
          width, height       // Bottom-right
      );
    } else {
      parallelogram.getPoints().addAll(
          0.0, 0.0,           // Top-left
          width, 0.0,         // Top-right
          width + skew, height, // Bottom-right (shifted right by skew)
          skew, height        // Bottom-left
      );
    }
  }
}
