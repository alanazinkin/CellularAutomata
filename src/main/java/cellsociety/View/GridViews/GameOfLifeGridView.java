package cellsociety.View.GridViews;

import static cellsociety.View.SimulationView.SIMULATION_HEIGHT;
import static cellsociety.View.SimulationView.SIMULATION_WIDTH;

import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.State.GameOfLifeState;
import cellsociety.View.GridViews.GridView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class GameOfLifeGridView extends GridView {
  private static final int NUM_ROWS = 10;
  private static final int NUM_COLUMNS = 10;
  private static final int CELL_WIDTH = SIMULATION_WIDTH / NUM_COLUMNS;
  private static final int CELL_HEIGHT = SIMULATION_HEIGHT / NUM_ROWS;

  private BorderPane myRoot;
  private Map<GameOfLifeState, Color> myStateMap;
  private List<Shape> myCells;


  public GameOfLifeGridView() {
    super();
  }

  @Override
  public void createGridDisplay(BorderPane myRoot) {
    GridPane gridPane = new GridPane();
    myRoot.setCenter(gridPane);
    initializeStateMap();
    myCells = new ArrayList<>();
    myGrid = new Grid(NUM_ROWS, NUM_COLUMNS, GameOfLifeState.ALIVE);
    for(int i = 0; i < myGrid.getRows(); i ++) {
      for (int j = 0; j < myGrid.getCols(); j ++) {
        Cell cell = myGrid.getCell(i, j);
        GameOfLifeState cellState = (GameOfLifeState) cell.getState();
        Rectangle rectCell = new Rectangle(CELL_WIDTH, CELL_HEIGHT, myStateMap.get(cellState));
        System.out.println(myStateMap.get(cellState));
        myCells.add(rectCell);
        gridPane.add(rectCell, i * CELL_WIDTH, j * CELL_HEIGHT);
      }
    }
  }

  public void initializeStateMap() {
    myStateMap = new HashMap<>();
    myStateMap.put(GameOfLifeState.ALIVE, Color.BLACK);
    myStateMap.put(GameOfLifeState.DEAD, Color.WHITE);
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
