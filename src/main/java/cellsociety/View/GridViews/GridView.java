package cellsociety.View.GridViews;

import cellsociety.Model.Grid;
import cellsociety.Model.StateInterface;
import java.util.Map;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public abstract class GridView {
  protected Grid myGrid;

  public GridView() {
  }

  public abstract void createGridDisplay(BorderPane myRoot, Map<StateInterface, Color> stateMap);
}
