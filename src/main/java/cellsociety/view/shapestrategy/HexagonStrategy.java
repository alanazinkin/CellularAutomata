package cellsociety.view.shapestrategy;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public class HexagonStrategy implements ShapeStrategy {

  @Override
  public void addShapeToGrid(Pane pane, double j, double i, Shape shape, double width, double height) {
    shape.setTranslateY(i * width * (Math.sqrt(3) / 2));
    shape.setTranslateX(j * width * .75);
    pane.getChildren().add(shape);
  }
}
