package cellsociety.View.GridViews;

import cellsociety.Model.Grid;
import javafx.scene.layout.BorderPane;

public abstract class GridView {
  protected Grid myGrid;

  public GridView() {
  }

  public abstract void createGridDisplay(BorderPane myRoot);
}
