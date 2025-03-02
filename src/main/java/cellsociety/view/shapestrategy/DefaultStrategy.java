package cellsociety.view.shapestrategy;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public class DefaultStrategy implements ShapeStrategy {

  @Override
  public void addShapeToGrid(Pane pane, double j, double i, Shape shape, double width,
      double height) {
    ((GridPane) pane).add(shape, (int) j, (int) i);
  }
}
