package cellsociety.view.gridview;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Grid;

public class GameOfLifeGridView extends GridView {

  /**
   * construct a new instance of GameOfLifeGridView
   */
  public GameOfLifeGridView(SimulationController simulationController, SimulationConfig simulationConfig, Grid grid) {
    super(simulationController, simulationConfig, grid);
  }

}
