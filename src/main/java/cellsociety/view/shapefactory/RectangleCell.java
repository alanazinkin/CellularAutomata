package cellsociety.view.shapefactory;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class RectangleCell implements CellShape {
  private Rectangle rectangle;

  public RectangleCell(double width, double height) {
    rectangle = new Rectangle(width, height);
  }

  @Override
  public CellShape createShape(double width, double height) {
    return new RectangleCell(width, height);
  }

  @Override
  public Shape getShape() {
    return rectangle;
  }

  @Override
  public void setDimensions(double width, double height) {
    rectangle.setWidth(width);
    rectangle.setHeight(height);
  }

}
