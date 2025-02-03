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

  public FireGridView(SimulationConfig simulationConfig) {
    super(simulationConfig);
  }

  @Override
  public void createGridDisplay(BorderPane myRoot, SimulationConfig simulationConfig, Map<StateInterface, Color> stateMap) {
    myRoot.setCenter(gridPane);
    gridPane.setMaxWidth(SIMULATION_WIDTH);
    gridPane.setMaxHeight(SIMULATION_HEIGHT - SLIDER_BAR_HEIGHT);
    myCells = new ArrayList<>();
    myGrid = new Grid(simulationConfig.getWidth(), simulationConfig.getHeight(), FireState.EMPTY);
    renderGrid(stateMap);
  }}
