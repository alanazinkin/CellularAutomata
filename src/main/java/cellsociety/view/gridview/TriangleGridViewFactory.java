package cellsociety.view.gridview;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Grid;

/**
 * Factory class for creating a TriangleGridView
 * @author Alana Zinkin
 */
public class TriangleGridViewFactory extends GridViewFactory {

  /**
   * returns a new TriangleGridView
   *
   * @param simulationController controller of the simulation
   * @param simulationConfig     simulation configuration object
   * @param grid                 grid object of the simulation
   * @return a new TriangleGridView object
   */
  @Override
  public GridView createGridView(SimulationController simulationController,
      SimulationConfig simulationConfig, Grid grid) {
    return new TriangleGridView(simulationController, simulationConfig, grid);
  }
}
