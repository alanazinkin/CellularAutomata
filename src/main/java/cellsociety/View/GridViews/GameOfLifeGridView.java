package View.GridViews;

import Model.Cell;
import Model.Grid;
import Model.State.GameOfLifeState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class GameOfLifeGridView extends GridView {
  private static final double CELL_WIDTH = 10;
  private static final double CELL_HEIGHT = 10;
  private static final int NUM_ROWS = 10;
  private static final int NUM_COLUMNS = 10;

  private BorderPane myRoot;
  private Map<String, Color> myStateMap;
  private List<Shape> myCells;


  public GameOfLifeGridView(Grid myGrid) {
    super(myGrid);
  }

  @Override
  public void createGridDisplay() {
    myRoot = new BorderPane();
    HBox hBox = new HBox();
    myRoot.setCenter(hBox);
    initializeStateMap();
    myCells = new ArrayList<>();
    Grid myGrid = new Grid(NUM_ROWS, NUM_COLUMNS, GameOfLifeState.ALIVE);
    for(int i = 0; i < myGrid.getRows(); i ++) {
      for (int j = 0; j < myGrid.getCols(); j ++) {
        Cell cell = myGrid.getCell(i, j);
        GameOfLifeState state = (GameOfLifeState) cell.getState();
        Rectangle rectCell = new Rectangle(CELL_WIDTH, CELL_HEIGHT, myStateMap.get(state));
        myCells.add(rectCell);
        hBox.getChildren().add(rectCell);
      }
    }
  }

  public void initializeStateMap() {
    myStateMap = new HashMap<>();
    myStateMap.put("alive", Color.BLACK);
    myStateMap.put("dead", Color.WHITE);
  }

  public void updateCellColors() {
    int cellCount = 0;
    for(int i = 0; i < myGrid.getRows(); i ++) {
      for (int j = 0; j < myGrid.getCols(); j ++) {
        Cell cell = myGrid.getCell(i, j);
        GameOfLifeState state = (GameOfLifeState) cell.getState();
        Shape myCell = myCells.get(cellCount);
        myCell.setFill(myStateMap.get(state));
        cellCount++;
      }
    }
  }
}
