package cellsociety.view.gridview;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Grid;
import cellsociety.view.shapefactory.CellShape;

/**
 * abstract GridView factory class for creating new grid views
 */
public abstract class GridViewFactory {

  /**
   * abstract method for creating a new GridView instance
   */
  public abstract GridView createGridView(SimulationController simulationController,
      SimulationConfig simulationConfig, Grid grid);
}
