package cellsociety.Model.Simulations;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.Cell;
import cellsociety.Model.State.ColonyState;
import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.StateInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.paint.Color;

public class CompetingColony extends Simulation {
  private int numStates;
  private double thresholdPercentage;
  private Map<StateInterface, Integer> stateToIntMap;
  private SimulationConfig config;

  public CompetingColony(SimulationConfig simulationConfig, Grid grid) {
    super(simulationConfig, grid);
    this.config = simulationConfig;
    initializeStateToIntMap();
  }

  private void initializeStateToIntMap() {
    stateToIntMap = new HashMap<>();
    for (Map.Entry<Integer, StateInterface> entry : getStateMap().entrySet()) {
      stateToIntMap.put(entry.getValue(), entry.getKey());
    }
  }

  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    // Get and validate parameters here since this is called during super()
    numStates = config.getParameters().get("numStates").intValue();
    thresholdPercentage = config.getParameters().get("threshold");

    if (numStates <= 1) {
      throw new IllegalArgumentException("Minimum 2 states required. Received: " + numStates);
    }
    if (thresholdPercentage < 0 || thresholdPercentage > 100) {
      throw new IllegalArgumentException("Threshold must be 0-100. Received: " + thresholdPercentage);
    }

    // Create and return state map
    Map<Integer, StateInterface> stateMap = new HashMap<>();
    for (int i = 0; i < numStates; i++) {
      stateMap.put(i, new ColonyState(i));
    }
    return stateMap;
  }

  @Override
  protected Map<StateInterface, String> initializeColorMap() {
    Map<StateInterface, String> colorMap = new HashMap<>();
    Map<Integer, StateInterface> stateMap = getStateMap();
    for (int i = 0; i < numStates; i++) {
      double hue = (360.0 * i) / numStates;
      colorMap.put(stateMap.get(i), Color.hsb(hue, 1.0, 1.0).toString());
    }
    return colorMap;
  }

  @Override
  protected void applyRules() {
    Grid grid = getGrid();
    int rows = grid.getRows();
    int cols = grid.getCols();

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        Cell currentCell = grid.getCell(r, c);
        StateInterface currentState = currentCell.getCurrentState();
        int currentStateInt = stateToIntMap.get(currentState);

        int beatingStateInt = (currentStateInt + 1) % numStates;
        StateInterface beatingState = getStateMap().get(beatingStateInt);

        List<Cell> neighbors = grid.getNeighbors(r, c);
        long count = neighbors.stream()
            .filter(neighbor -> neighbor.getCurrentState().equals(beatingState))
            .count();

        double threshold = (thresholdPercentage / 100.0) * neighbors.size();
        if (count >= threshold) {
          currentCell.setNextState(beatingState);
        } else {
          currentCell.setNextState(currentState);
        }
      }
    }
  }
}
