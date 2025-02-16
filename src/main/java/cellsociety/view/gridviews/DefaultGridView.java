package cellsociety.view.gridviews;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Grid;

public class DefaultGridView extends GridView {

  /**
   * construct a new instance of GameOfLifeGridView
   */
  public DefaultGridView(SimulationController simulationController, SimulationConfig simulationConfig, Grid grid) {
    super(simulationController, simulationConfig, grid);
  }

}
