package cellsociety.view.shapestrategy;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

/**
 * Interface defining a strategy for adding shapes to a grid. This allows different shape
 * positioning strategies to be implemented and used interchangeably.
 *
 * @author Alana Zinkin
 */
public interface ShapeStrategy {

  /**
   * Adds a shape to the specified grid pane at the given coordinates.
   *
   * @param pane   the {@link Pane} representing the grid where the shape should be added
   * @param j      the column index where the shape should be placed
   * @param i      the row index where the shape should be placed
   * @param shape  the {@link Shape} object to be added to the grid
   * @param width  the width of the shape
   * @param height the height of the shape
   */
  void addShapeToGrid(Pane pane, double j, double i, Shape shape, double width, double height);
}

