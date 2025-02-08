package cellsociety.View.GridViews;

import static cellsociety.View.SimulationView.SIMULATION_HEIGHT;
import static cellsociety.View.SimulationView.SIMULATION_WIDTH;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.StateInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public abstract class GridView {
  protected static final int SLIDER_BAR_HEIGHT = 150;

  protected Grid myGrid;
  protected List<Shape> myCells;
  protected GridPane gridPane;
  int numRows;
  int numCols;
  int cellWidth;
  int cellHeight;

  /**
   * Constructor for creating a GridView object, which is responsible for creating and updating the visuals
   * of the grid of cells.
   * @param simulationConfig configuration object containing relevant simulation details such as type, title, description etc.
   * @param grid the grid of cells of the simulation
   */
  public GridView(SimulationConfig simulationConfig, Grid grid) {
    myGrid = grid;
    numRows = simulationConfig.getWidth();
    numCols = simulationConfig.getHeight();
    cellWidth = SIMULATION_WIDTH / numCols;
    cellHeight = (SIMULATION_HEIGHT - SLIDER_BAR_HEIGHT) / numRows;
    gridPane = new GridPane();
  }

  /**
   * creates a new pane to hold the grid and instantiates myCells and myGrid
   * @param myRoot
   */
  public void createGridDisplay(BorderPane myRoot, Map<StateInterface, Color> stateMap) {
    myRoot.setCenter(gridPane);
    gridPane.setMaxWidth(SIMULATION_WIDTH);
    gridPane.setMaxHeight(SIMULATION_HEIGHT - SLIDER_BAR_HEIGHT);
    gridPane.setGridLinesVisible(true);
    myCells = new ArrayList<>();
    renderGrid(stateMap);
  }

  /**
   * Creates the visual display of grid cells based on the Grid object and organizes them in the view
   * @param stateMap mapping of state interfaces to colors to visually render each cell according to its state value
   */
  public void renderGrid(Map<StateInterface, Color> stateMap) {
    for(int i = 0; i < numRows; i ++) {
      for (int j = 0; j < numCols; j ++) {
        Cell cell = myGrid.getCell(i, j);
        StateInterface cellState = cell.getState();
        Rectangle rectCell = new Rectangle(cellWidth, cellHeight, stateMap.get(cellState));
        rectCell.setStroke(Color.BLACK);
        rectCell.setStrokeWidth(1);
        myCells.add(rectCell);
        gridPane.add(rectCell, i * cellWidth, j * cellHeight);
      }
    }
  }

  /**
   * updates the colors of all the cells in the grid according to all cells' current state
   * @param stateMap data structure mapping cell states to visual colors in the simulation grid
   */
  public void updateCellColors(Map<StateInterface, Color> stateMap) {
    int cellCount = 0;
    for(int i = 0; i < numRows; i ++) {
      for (int j = 0; j < numCols; j ++) {
        Cell cell = myGrid.getCell(i, j);
        StateInterface state =  cell.getState();
        Shape myCell = myCells.get(cellCount);
        myCell.setFill(stateMap.get(state));
        cellCount++;
      }
    }
  }
}
