package cellsociety.view.gridview;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Grid;

/**
 * Default Grid View Factory creates a DefaultGridView object
 * @author Alana Zinkin
 */
public class DefaultGridViewFactory extends GridViewFactory {

  /**
   * initializes a new DefaultGridView object
   *
   * @param simulationController simulation controller for simulation
   * @param simulationConfig     the information about the simulation from the configuration file
   * @param grid                 the grid representation
   * @return new DefaultGridView object
   */
  @Override
  public GridView createGridView(SimulationController simulationController,
      SimulationConfig simulationConfig, Grid grid) {
    return new DefaultGridView(simulationController, simulationConfig, grid);
  }
}
