package cellsociety.View.GridViews;

import Model.Cell;
import cellsociety.Model.Grid;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameOfLifeGridView {
  private static final double CELL_WIDTH = 10;
  private static final double CELL_HEIGHT = 10;
  Grid myGrid;

  public GameOfLifeGridView() {
    super();
  }

  @Override
  public void createGridDisplay(BorderPane root) {
    Grid myGrid = new Grid();
    for(int i = 0; i < myGrid.getRows(); i ++) {
      for (int j = 0; j < myGrid.getCols(); j ++) {
        Cell cell = myGrid.getCell(i, j);
        String state = cell.getState();
        if (state.equals("alive")) {
          Rectangle rectCell = new Rectangle(CELL_WIDTH, CELL_HEIGHT, Color.WHITE);
          root.getChildren().add(rectCell);

        }
        else if (state.equals("dead")) {
          Rectangle rectCell = new Rectangle(CELL_WIDTH, CELL_HEIGHT, Color.BLACK);
          root.getChildren().add(rectCell);
        }
      }
    }
  }
}
