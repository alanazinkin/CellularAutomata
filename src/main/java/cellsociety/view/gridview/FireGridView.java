package cellsociety.view.gridview;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Grid;

public class FireGridView extends GridView {

  /**
   * initialized a grid view for the Spreading Fire simulation
   * @param simulationController controls the initialization and the starting/stopping of the simulation
   * @param simulationConfig the configuration information for the given simulation
   * @param grid the grid object
   */
  public FireGridView(SimulationController simulationController, SimulationConfig simulationConfig,
      Grid grid) {
    super(simulationController, simulationConfig, grid);
  }


}
