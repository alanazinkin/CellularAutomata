package cellsociety.view.gridview;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.StateInterface;
import cellsociety.view.shapefactory.CellShape;
import cellsociety.view.shapefactory.CellShapeFactory;
import cellsociety.view.shapefactory.TriangleCellFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 * class for creating a TriangleGridView: a grid with Triangular tiling
 */
public class TriangleGridView extends GridView {

  private Pane myGridPane;
  private Grid myGrid;
  private int numRows;
  private int numCols;
  private Map<String, String> myConfigResourceMap;

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
  public TriangleGridView(SimulationController simulationController,
      SimulationConfig simulationConfig, Grid grid) {
    super(simulationController, simulationConfig, grid);
    myConfigResourceMap = SimulationController.retrieveImmutableConfigResourceBundle();
    myGridPane = getGridPane();
    myGrid = grid;
    numRows = grid.getRows();
    numCols = grid.getCols();
    setCellWidth(parseDouble(myConfigResourceMap.getOrDefault("grid.width", "400"))
        / numCols);
    setCellHeight(parseDouble(myConfigResourceMap.getOrDefault("grid.height", "400"))
        / numRows);
    addColumnConstraints((GridPane) myGridPane);
  }


  /**
   * Creates the visual display of grid cells based on the Grid object and organizes them in the
   * view
   *
   * @param colorMap         mapping of state interfaces to colors to visually render each cell
   *                         according to its state value
   * @param simulationConfig the object representation of the simulation configuration
   * @throws ClassNotFoundException    if there is no factory class for creating the grid view
   * @throws InvocationTargetException if a new grid view cannot be made
   * @throws NoSuchMethodException     if there is no constructor for creating the gridview
   * @throws InstantiationException    if a new gridview cannot be instantiated
   * @throws IllegalAccessException    if user attempts to access a method that should not be
   *                                   accessed
   */
  @Override
  public void renderGrid(Map<StateInterface, String> colorMap, SimulationConfig simulationConfig)
      throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    // Generate a triangular tiling pattern
    getGridPane().getChildren().clear();
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        Cell cell = myGrid.getCell(i, j);
        boolean isUpward = (i + j) % 2 == 0;
        int row = i;
        int col = j;
        if (getFlipped()) {
          col = j;
          row = numRows - i - 1;
        }
        addCellShapeToGridView(colorMap, simulationConfig, cell, col, row, isUpward);
      }
    }
  }

}
