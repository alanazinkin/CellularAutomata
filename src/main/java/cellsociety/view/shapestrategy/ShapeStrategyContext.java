package cellsociety.view.shapestrategy;

import java.util.ResourceBundle;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public class ShapeStrategyContext {

  private ShapeStrategy myStrategy;
  private ResourceBundle myResources;

  public ShapeStrategyContext(ShapeStrategy strategy) {
    setStrategy(strategy);
  }

  public void setStrategy(ShapeStrategy strategy) {
    myStrategy = strategy;
  }

  public void executeStrategy(Pane pane, double j, double i, Shape shape, double width, double height) {
    if (myStrategy != null) {
      myStrategy.addShapeToGrid(pane, j, i, shape, width, height);
    } else {
      throw new IllegalStateException("Strategy not set");
    }
  }
}
