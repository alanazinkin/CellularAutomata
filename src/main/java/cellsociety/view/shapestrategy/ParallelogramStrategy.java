package cellsociety.view.shapestrategy;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public class ParallelogramStrategy implements ShapeStrategy {

  @Override
  public void addShapeToGrid(Pane pane, double j, double i, Shape shape, double width,
      double height) {
    shape.setTranslateY(i * height);
    shape.setTranslateX(j * width);
    pane.getChildren().add(shape);
  }
}
