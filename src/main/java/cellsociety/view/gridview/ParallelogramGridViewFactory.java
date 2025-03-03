package cellsociety.view.gridview;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Grid;

/**
 * class for creating a new parallelogram grid view
 * @author Alana Zinkin
 */
public class ParallelogramGridViewFactory extends GridViewFactory {

  /**
   * initializes a new Parallelogram Grid View
   *
   * @param simulationController simulation controller responsible for managing simulation
   * @param simulationConfig     the class responsible for representing the simulation
   *                             configuration
   * @param grid                 the grid holding the states of the simulation cells
   * @return a new ParallelogramGridView object
   */
  @Override
  public GridView createGridView(SimulationController simulationController,
      SimulationConfig simulationConfig, Grid grid) {
    return new ParallelogramGridView(simulationController, simulationConfig, grid);
  }
}
