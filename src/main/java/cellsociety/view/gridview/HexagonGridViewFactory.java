package cellsociety.view.gridview;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Grid;

public class HexagonGridViewFactory extends GridViewFactory{

  @Override
  public GridView createGridView(SimulationController simulationController,
      SimulationConfig simulationConfig, Grid grid) {
    return new HexagonGridView(simulationController, simulationConfig, grid);
  }
}
