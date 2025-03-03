package cellsociety.view.gridview;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Grid;

/**
 * class for generating a HexagonalGridView from a Factory class
 * @author Alana Zinkin
 */
public class HexagonGridViewFactory extends GridViewFactory {

  /**
   * initializes a new HexagonalGridView
   *
   * @param simulationController simulation controller responsible for managing simulation
   * @param simulationConfig     the class responsible for representing the simulation
   *                             configuration
   * @param grid                 the grid holding the states of the simulation cells
   * @return a new HexagonalGridView object
   */
  @Override
  public GridView createGridView(SimulationController simulationController,
      SimulationConfig simulationConfig, Grid grid) {
    return new HexagonGridView(simulationController, simulationConfig, grid);
  }
}
