package cellsociety.View.GridViews;

import cellsociety.Model.Grid;

public abstract class GridView {
  protected Grid myGrid;

  public GridView(Grid grid) {
    myGrid = grid;
  }

  public abstract void createGridDisplay();
}
