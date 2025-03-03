package cellsociety.view.shapestrategy;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

/**
 * The {@code HexagonStrategy} class implements the {@link ShapeStrategy} interface and defines a
 * strategy for adding hexagon-shaped cells to a grid. This strategy calculates the appropriate
 * positioning for hexagons based on their width, ensuring a proper hexagonal tiling pattern.
 *
 * @author Alana Zinkin
 */
public class HexagonStrategy implements ShapeStrategy {

  /**
   * Adds a hexagon shape to the specified grid pane, adjusting its position based on hexagonal
   * tiling rules.
   *
   * @param pane   the {@link Pane} representing the grid where the shape should be added
   * @param j      the column index where the shape should be placed
   * @param i      the row index where the shape should be placed
   * @param shape  the {@link Shape} object representing the hexagon to be added
   * @param width  the width of the hexagon cell
   * @param height the height of the hexagon cell (not explicitly used in calculations)
   */
  @Override
  public void addShapeToGrid(Pane pane, double j, double i, Shape shape, double width,
      double height) {
    shape.setTranslateY(i * width * (Math.sqrt(3) / 2)); // Adjust Y position for hexagonal layout
    shape.setTranslateX(j * width * 0.75); // Adjust X position for hexagonal tiling
    pane.getChildren().add(shape);
  }
}

