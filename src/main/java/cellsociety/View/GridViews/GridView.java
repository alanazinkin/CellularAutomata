package cellsociety.View.GridViews;

import cellsociety.Model.Grid;

public abstract class GridView {
  protected Grid myGrid;

  public GridView() {
  }

  public abstract void createGridDisplay();
}
