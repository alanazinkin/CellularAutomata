package cellsociety.view.gridview;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.StateInterface;
import cellsociety.view.shapestrategy.DefaultStrategy;
import cellsociety.view.shapestrategy.HexagonStrategy;
import cellsociety.view.shapestrategy.ShapeStrategyContext;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 * Default Grid view used for the standard simulation
 * @author Alana Zinkin
 */
public class DefaultGridView extends GridView {

  private Grid myGrid;
  private int numRows;
  private int numCols;
  private Map<String, String> myConfigResourceMap;
  private ShapeStrategyContext addStrategy = new ShapeStrategyContext(new DefaultStrategy());

  /**
   * construct a new instance of GameOfLifeGridView
   */
  public DefaultGridView(SimulationController simulationController,
      SimulationConfig simulationConfig, Grid grid) {
    super(simulationController, simulationConfig, grid);
    myConfigResourceMap = SimulationController.retrieveImmutableConfigResourceBundle();
    setGridPane(new GridPane());
    myGrid = grid;
    numRows = grid.getRows();
    numCols = grid.getCols();
    setCellWidth(
        parseDouble(
            myConfigResourceMap.getOrDefault("grid.width", "400"))
            / numCols);
    setCellHeight(
        parseDouble(
            myConfigResourceMap.getOrDefault("grid.height", "400"))
            / numRows);
    setAddStrategy(new DefaultStrategy());
  }

  @Override
  public void renderGrid(Map<StateInterface, String> colorMap, SimulationConfig simulationConfig)
      throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    getGridPane().getChildren().clear();
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        Cell cell = myGrid.getCell(i, j);
        int col = j;
        int row = i;
        if (getFlipped()) {
          col = j;
          row = numRows - i - 1;
        }
        addCellShapeToGridView(colorMap, simulationConfig, cell, col, row, true);
      }
    }
  }


}
