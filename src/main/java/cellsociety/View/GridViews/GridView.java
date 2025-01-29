package cellsociety.View.GridViews;

import Model.Grid;
import javafx.scene.layout.BorderPane;

public abstract class GridView {
  private Grid myGrid;

  public GridView(Grid grid) {
    myGrid = grid;
  }

  public abstract void createGridDisplay();
}
