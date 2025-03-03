package cellsociety.view.shapestrategy;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

/**
 * The {@code DefaultStrategy} class implements the {@link ShapeStrategy} interface and provides a
 * basic implementation for adding shapes to a {@link GridPane}. This strategy directly places the
 * shape at the specified row and column index in the grid.
 *
 * @author Alana Zinkin
 */
public class DefaultStrategy implements ShapeStrategy {

  /**
   * Adds a shape to the given {@link GridPane} at the specified row and column indices.
   *
   * @param pane   the {@link Pane} representing the grid where the shape should be added
   * @param j      the column index where the shape should be placed
   * @param i      the row index where the shape should be placed
   * @param shape  the {@link Shape} object to be added to the grid
   * @param width  the width of the shape (not used in this strategy)
   * @param height the height of the shape (not used in this strategy)
   * @throws ClassCastException if the provided {@code Pane} is not an instance of {@code GridPane}
   */
  @Override
  public void addShapeToGrid(Pane pane, double j, double i, Shape shape, double width,
      double height) {
    ((GridPane) pane).add(shape, (int) j, (int) i);
  }
}

