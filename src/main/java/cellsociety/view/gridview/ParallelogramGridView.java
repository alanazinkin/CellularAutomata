package cellsociety.view.gridview;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.StateInterface;
import cellsociety.view.shapestrategy.HexagonStrategy;
import cellsociety.view.shapestrategy.ParallelogramStrategy;
import cellsociety.view.shapestrategy.ShapeStrategyContext;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

/**
 * class for creating a ParallelogramGridView: a grid with Parallelogram tiling
 */
public class ParallelogramGridView extends GridView {

  private static final ResourceBundle myGridViewResourceBundle = ResourceBundle.getBundle(
      "cellsociety.view.GridSettings");
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
  public ParallelogramGridView(SimulationController simulationController,
      SimulationConfig simulationConfig, Grid grid) {
    super(simulationController, simulationConfig, grid);
    myConfigResourceMap = SimulationController.retrieveImmutableConfigResourceBundle();
    setGridPane(new Pane());
    myGrid = grid;
    numRows = grid.getRows();
    numCols = grid.getCols();
    setCellWidth(
        parseDouble(myGridViewResourceBundle.getString("shape.shrinkage.factor")) * parseInt(
            myConfigResourceMap.getOrDefault("grid.width", "400"))
            / numCols);
    setCellHeight(
        parseDouble(myGridViewResourceBundle.getString("shape.shrinkage.factor")) * parseInt(
            myConfigResourceMap.getOrDefault("grid.height", "400"))
            / numRows);
    setAddStrategy(new ParallelogramStrategy());
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
    getGridPane().getChildren().clear();
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        Cell cell = myGrid.getCell(i, j);
        boolean isUpward = i % 2 == 0;
        double row = i;
        double col = j;
        if (getFlipped()) {
          row = numRows - i - 1;
        }
        addCellShapeToGridView(colorMap, simulationConfig, cell, col, row, isUpward);
      }
    }
  }

}
