package cellsociety.view.shapefactory;

import javafx.scene.shape.Shape;

public interface CellShape {
  CellShape createShape(double width, double height);

  Shape getShape();

  void setDimensions(double width, double height);
}
