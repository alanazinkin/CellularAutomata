package cellsociety.view.shapestrategy;

import java.util.ResourceBundle;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

/**
 * The {@code ShapeStrategyContext} class acts as a context for selecting and executing different
 * shape-placing strategies. It allows for dynamic selection of a {@link ShapeStrategy} at runtime
 * and delegates shape placement to the selected strategy.
 *
 * @author Alana Zinkin
 */
public class ShapeStrategyContext {

  private ShapeStrategy myStrategy;
  private ResourceBundle myResources;

  /**
   * Constructs a {@code ShapeStrategyContext} with an initial shape placement strategy.
   *
   * @param strategy the initial {@link ShapeStrategy} to be used
   */
  public ShapeStrategyContext(ShapeStrategy strategy) {
    setStrategy(strategy);
  }

  /**
   * Sets the shape placement strategy for this context.
   *
   * @param strategy the new {@link ShapeStrategy} to be used
   */
  public void setStrategy(ShapeStrategy strategy) {
    myStrategy = strategy;
  }

  /**
   * Executes the currently set shape placement strategy by adding the given shape to the grid.
   *
   * @param pane   the {@link Pane} representing the grid where the shape should be added
   * @param j      the column index where the shape should be placed
   * @param i      the row index where the shape should be placed
   * @param shape  the {@link Shape} object to be added
   * @param width  the width of the shape
   * @param height the height of the shape
   * @throws IllegalStateException if no strategy has been set before execution
   */
  public void executeStrategy(Pane pane, double j, double i, Shape shape, double width,
      double height) {
    if (myStrategy != null) {
      myStrategy.addShapeToGrid(pane, j, i, shape, width, height);
    } else {
      throw new IllegalStateException("Strategy not set");
    }
  }
}
