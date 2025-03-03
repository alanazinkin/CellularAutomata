package cellsociety.view.shapestrategy;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

/**
 * The {@code ParallelogramStrategy} class implements the {@link ShapeStrategy} interface and
 * defines a strategy for adding parallelogram-shaped cells to a grid. This strategy positions
 * parallelogram shapes in a uniform grid based on their width and height.
 *
 * @author Alana Zinkin
 */
public class ParallelogramStrategy implements ShapeStrategy {

  /**
   * Adds a parallelogram shape to the specified grid pane, positioning it based on the given row
   * and column indices.
   *
   * @param pane   the {@link Pane} representing the grid where the shape should be added
   * @param j      the column index where the shape should be placed
   * @param i      the row index where the shape should be placed
   * @param shape  the {@link Shape} object representing the parallelogram to be added
   * @param width  the width of the parallelogram cell
   * @param height the height of the parallelogram cell
   */
  @Override
  public void addShapeToGrid(Pane pane, double j, double i, Shape shape, double width,
      double height) {
    shape.setTranslateY(i * height); // Adjust Y position based on height
    shape.setTranslateX(j * width);  // Adjust X position based on width
    pane.getChildren().add(shape);
  }
}

