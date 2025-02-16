package cellsociety.view.gridview;

import static java.lang.Integer.parseInt;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.StateInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public abstract class GridView {

  private static final int SLIDER_BAR_HEIGHT = 150;

  private Grid myGrid;
  private List<Shape> myCells;
  private GridPane gridPane;
  private SimulationController myController;
  private Map<String, String> myConfigResourceMap;

  int numRows;
  int numCols;
  int cellWidth;
  int cellHeight;

  /**
   * Constructor for creating a GridView object, which is responsible for creating and updating the
   * visuals of the grid of cells.
   *
   * @param simulationController simulation controller responsible for managing the simulation and
   *                             merging the back-end and front-end
   * @param simulationConfig     configuration object containing relevant simulation details such as
   *                             type, title, description etc.
   * @param grid                 the grid of cells of the simulation
   */
  public GridView(SimulationController simulationController, SimulationConfig simulationConfig,
      Grid grid) {
    myController = simulationController;
    myConfigResourceMap = SimulationController.retrieveImmutableConfigResourceBundle();
    myGrid = grid;
    numRows = simulationConfig.getWidth();
    numCols = simulationConfig.getHeight();
    cellWidth = parseInt(myConfigResourceMap.getOrDefault("window.width", "1000")) / numCols;
    cellHeight =
        (parseInt(myConfigResourceMap.getOrDefault("window.height", "800")) - SLIDER_BAR_HEIGHT)
            / numRows;
    gridPane = new GridPane();
  }

  /**
   * creates a new pane to hold the grid and instantiates myCells and myGrid
   *
   * @param myRoot
   * @param colorMap
   */
  public void createGridDisplay(BorderPane myRoot, Map<StateInterface, String> colorMap) {
    myRoot.setCenter(gridPane);
    gridPane.setMaxWidth(parseInt(myConfigResourceMap.getOrDefault("window.width", "1000")));
    gridPane.setMaxHeight(
        parseInt(myConfigResourceMap.getOrDefault("window.height", "1000")) - SLIDER_BAR_HEIGHT);
    gridPane.setGridLinesVisible(true);
    myCells = new ArrayList<>();
    renderGrid(colorMap);
  }

  /**
   * Creates the visual display of grid cells based on the Grid object and organizes them in the
   * view
   *
   * @param colorMap mapping of state interfaces to colors to visually render each cell according to
   *                 its state value
   */
  public void renderGrid(Map<StateInterface, String> colorMap) {
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        Cell cell = myGrid.getCell(i, j);
        StateInterface cellState = cell.getCurrentState();
        Rectangle rectCell = new Rectangle(cellWidth, cellHeight);
        rectCell.setId(colorMap.get(cellState));
        rectCell.setStroke(Color.BLACK);
        rectCell.setStrokeWidth(1);
        myCells.add(rectCell);
        gridPane.add(rectCell, i * cellWidth, j * cellHeight);
      }
    }
  }

  /**
   * updates the colors of all the cells in the grid according to all cells' current state
   *
   * @param colorMap data structure mapping cell states to visual colors in the simulation grid
   */
  public void updateCellColors(Map<StateInterface, String> colorMap) {
    int cellCount = 0;
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        Cell cell = myGrid.getCell(i, j);
        StateInterface state = cell.getCurrentState();
        Shape myCell = myCells.get(cellCount);
        myCell.setId(colorMap.get(state));
        cellCount++;
      }
    }
  }

  /**
   * visually removes the grid lines y setting stroke outline of cells to 0 pixels
   */
  public void removeGridLines() {
    for (Shape shape : myCells) {
      shape.setStrokeWidth(0);
    }
  }

  /**
   * visually adds the grid lines by setting stroke outline of cells to 1 pixels
   */
  public void addGridLines() {
    for (Shape shape : myCells) {
      shape.setStrokeWidth(1);
    }
  }

}
