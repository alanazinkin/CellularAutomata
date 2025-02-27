package cellsociety.view.gridview;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Grid;

/**
 * Default Grid view used for the standard simulation
 */
public class DefaultGridView extends GridView {

  /**
   * construct a new instance of GameOfLifeGridView
   */
  public DefaultGridView(SimulationController simulationController,
      SimulationConfig simulationConfig, Grid grid) {
  