package cellsociety.view.shapestrategy;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public interface ShapeStrategy {
  void addShapeToGrid(Pane pane, double j, double i, Shape shape, double width, double height);
}
