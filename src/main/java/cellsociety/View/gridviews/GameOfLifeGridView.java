package cellsociety.View.GridViews;

import static cellsociety.View.SimulationView.SIMULATION_HEIGHT;
import static cellsociety.View.SimulationView.SIMULATION_WIDTH;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.Grid;

public class GameOfLifeGridView extends GridView {

  /**
   * construct a new instance of GameOfLifeGridView
   */
  public GameOfLifeGridView(SimulationConfig simulationConfig, Grid grid) {
    super(simulationConfig, grid);
  }

}
