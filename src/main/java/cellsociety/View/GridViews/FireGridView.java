package cellsociety.View.GridViews;

import static cellsociety.View.SimulationView.SIMULATION_HEIGHT;
import static cellsociety.View.SimulationView.SIMULATION_WIDTH;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.Grid;
import cellsociety.Model.State.FireState;
import cellsociety.Model.StateInterface;
import java.util.ArrayList;
import java.util.Map;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class FireGridView extends GridView {

  public FireGridView(SimulationConfig simulationConfig, Grid grid) {
    super(simulationConfig, grid);
  }


}
