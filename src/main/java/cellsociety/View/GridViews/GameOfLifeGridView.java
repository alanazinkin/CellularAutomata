package cellsociety.View.GridViews;

import static cellsociety.View.SimulationView.SIMULATION_HEIGHT;
import static cellsociety.View.SimulationView.SIMULATION_WIDTH;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.State.GameOfLifeState;
import cellsociety.Model.StateInterface;
import java.util.ArrayList;
import java.util.Map;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class GameOfLifeGridView extends GridView {

  /**
   * construct a new instance of GameOfLifeGridView
   */
  public GameOfLifeGridView(SimulationConfig simulationConfig, Grid grid) {
    super(simulationConfig, grid);
  }

  /**
   * creates a new pane to hold the grid and instantiates myCells and myGrid
   * @param myRoot
   */
  @Override
  public void createGridDisplay(BorderPane myRoot, SimulationConfig simulationConfig, Map<StateInterface, Color> stateMap) {
    myRoot.setCenter(gridPane);
    gridPane.setMaxWidth(SIMULATION_WIDTH);
    gridPane.setMaxHeight(SIMULATION_HEIGHT - SLIDER_BAR_HEIGHT);
    gridPane.setGridLinesVisible(true);
    myCells = new ArrayList<>();
    renderGrid(stateMap);
  }

  /**
   * updates the colors of all the cells in the grid according to all cells' current state
   * @param stateMap data structure mapping cell states to visual colors in the simulation grid
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
