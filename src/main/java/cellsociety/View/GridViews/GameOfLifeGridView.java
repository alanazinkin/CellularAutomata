package cellsociety.View.GridViews;

import static cellsociety.View.SimulationView.SIMULATION_HEIGHT;
import static cellsociety.View.SimulationView.SIMULATION_WIDTH;

import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.State.GameOfLifeState;
import cellsociety.Model.StateInterface;
import cellsociety.View.GridViews.GridView;
import cellsociety.View.SimulationView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class GameOfLifeGridView extends GridView {
  private static final int SLIDER_BAR_HEIGHT = 150;
  private static final int NUM_ROWS = 10;
  private static final int NUM_COLUMNS = 10;
  private static final int CELL_WIDTH = SIMULATION_WIDTH / NUM_COLUMNS;
  private static final int CELL_HEIGHT = (SIMULATION_HEIGHT - SLIDER_BAR_HEIGHT) / NUM_ROWS;

  private List<Shape> myCells;


  /**
   * construct a new instance of GameOfLifeGridView
   */
  public GameOfLifeGridView() {
    super();
  }

  /**
   * creates a new pane to hold the grid and instantiates myCells and myGrid
   * @param myRoot
   */
  @Override
  public void createGridDisplay(BorderPane myRoot, Map<StateInterface, Color> stateMap) {
    GridPane gridPane = new GridPane();
    myRoot.setCenter(gridPane);
    myCells = new ArrayList<>();
    myGrid = new Grid(NUM_ROWS, NUM_COLUMNS, GameOfLifeState.ALIVE);
    renderGrid(gridPane, stateMap);
  }

  /**
   * renders the grid by looping through all the cells in teh grid and setting their color based on their state
   * @param gridPane pane that holds all the cell shapes
   */
  private void renderGrid(GridPane gridPane, Map<StateInterface, Color> stateMap) {
    for(int i = 0; i < myGrid.getRows(); i ++) {
      for (int j = 0; j < myGrid.getCols(); j ++) {
        Cell cell = myGrid.getCell(i, j);
        GameOfLifeState cellState = (GameOfLifeState) cell.getState();
        Rectangle rectCell = new Rectangle(CELL_WIDTH, CELL_HEIGHT, stateMap.get(cellState));
        myCells.add(rectCell);
        gridPane.add(rectCell, i * CELL_WIDTH, j * CELL_HEIGHT);
      }
    }
  }

  /**
   * updates the colors of all the cells in the grid according to all cells' current state
   */
  public void updateCellColors(Map<StateInterface, Color> stateMap) {
    int cellCount = 0;
    for(int i = 0; i < myGrid.getRows(); i ++) {
      for (int j = 0; j < myGrid.getCols(); j ++) {
        Cell cell = myGrid.getCell(i, j);
        GameOfLifeState state = (GameOfLifeState) cell.getState();
        Shape myCell = myCells.get(cellCount);
        myCell.setFill(stateMap.get(state));
        cellCount++;
      }
    }
  }
}
