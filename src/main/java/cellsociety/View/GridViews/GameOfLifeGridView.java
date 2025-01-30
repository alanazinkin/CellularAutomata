package cellsociety.View.GridViews;

import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.State.GameOfLifeState;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class GameOfLifeGridView extends GridView {
  private static final double CELL_WIDTH = 10;
  private static final double CELL_HEIGHT = 10;
  private static final int NUM_ROWS = 10;
  private static final int NUM_COLUMNS = 10;

  private Map<String, Color> myStateMap;
  private List<Shape> myCells;
  

  public GameOfLifeGridView(Grid myGrid) {
    super(myGrid);
  }

  @Override
  public void createGridDisplay(BorderPane root) {
    initializeStateMap();
    Grid myGrid = new Grid(NUM_ROWS, NUM_COLUMNS, GameOfLifeState.ALIVE);
    for(int i = 0; i < myGrid.getRows(); i ++) {
      for (int j = 0; j < myGrid.getCols(); j ++) {
        Cell cell = myGrid.getCell(i, j);
        GameOfLifeState state = cell.getState();
        Rectangle rectCell = new Rectangle(CELL_WIDTH, CELL_HEIGHT, myStateMap.get(state));
        myCells.add(rectCell);
        root.getChildren().add(rectCell);
      }
    }
  }

  public void initializeStateMap() {
    myStateMap = new HashMap<>();
    myStateMap.put("alive", Color.BLACK);
    myStateMap.put("dead", Color.WHITE);
  }

  public void updateCellColors(BorderPane root) {
    int cellCount = 0;
    for(int i = 0; i < myGrid.getRows(); i ++) {
      for (int j = 0; j < myGrid.getCols(); j ++) {
        Cell cell = myGrid.getCell(i, j);
        GameOfLifeState state = cell.getState();
        Shape myCell = myCells.get(cellCount);
        myCell.setFill(myStateMap.get(state));
        cellCount++;
      }
    }
  }
}
