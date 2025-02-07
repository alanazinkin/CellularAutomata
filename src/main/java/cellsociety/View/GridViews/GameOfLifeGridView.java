package cellsociety.View.GridViews;

import static cellsociety.View.SimulationView.SIMULATION_HEIGHT;
import static cellsociety.View.SimulationView.SIMULATION_WIDTH;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.State.GameOfLifeState;
import cellsociety.Model.StateInterface;
import java.util.ArrayList;
import java.util.Map;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class GameOfLifeGridView extends GridView {

  /**
   * construct a new instance of GameOfLifeGridView
   */
  public GameOfLifeGridView(SimulationConfig simulationConfig, Grid grid) {
    super(simulationConfig, grid);
  }

}
