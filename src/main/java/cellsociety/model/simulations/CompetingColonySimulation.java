package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Cell;
import cellsociety.model.ColonyState;
import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.StateInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.paint.Color;

/**
 * Models a cellular automaton where bacterial colonies compete in a circular dominance
 * relationship.
 * <p>
 * Simulation Rules:
 * <ul>
 *   <li>States are arranged in a circular hierarchy (state 0 → 1 → ... → n-1 → 0)</li>
 *   <li>A cell changes state if the percentage of neighbors in the next dominant state exceeds a threshold</li>
 *   <li>Example: With 3 states (0,1,2), state 0 is defeated by state 1, which is defeated by state 2,
 *       which loops back to state 0</li>
 * </ul>
 * Configurable via:
 * <ul>
 *   <li>{@code numStates}: Total distinct colony types (≥2)</li>
 *   <li>{@code threshold}: Percentage of neighbors required for state conversion (0-100)</li>
 * </ul>
 * </p>
 */
public class CompetingColonySimulation extends Simulation {

  private final int numStates;
  private final double thresholdPercentage;
  private Map<StateInterface, Integer> stateToIntMap;

  /**
   * Initializes simulation with configuration parameters and grid structure.
   *
   * @param simulationConfig Contains:
   *                         <ul>
   *                           <li>{@code numStates}: Number of colony types (≥2)</li>
   *                           <li>{@code threshold}: Conversion threshold percentage (0-100)</li>
   *                         </ul>
   * @param grid             Preconfigured grid structure
   * @throws IllegalArgumentException If parameters violate constraints
   */
  public CompetingColonySimulation(SimulationConfig simulationConfig, Grid grid) {
    super(simulationConfig, grid);

    this.numStates = simulationConfig.getParameters().get("numStates").intValue();
    this.thresholdPercentage = simulationConfig.getParameters().get("threshold");

    if (numStates <= 1) {
      throw new IllegalArgumentException("Minimum 2 states required. Received: " + numStates);
    }
    if (thresholdPercentage < 0 || thresholdPercentage > 100) {
      throw new IllegalArgumentException(
          "Threshold must be 0-100. Received: " + thresholdPercentage);
    }

    initializeStateToIntMap();
  }

  /**
   * Creates bidirectional mapping between state objects and their integer identifiers.
   */
  private void initializeStateToIntMap() {
    stateToIntMap = new HashMap<>();
    for (Map.Entry<Integer, StateInterface> entry : getStateMap().entrySet()) {
      stateToIntMap.put(entry.getValue(), entry.getKey());
    }
  }

  /**
   * Generates mapping from integer identifiers to state objects.
   *
   * @return Map with keys 0 to {@code numStates-1} mapped to corresponding {@link ColonyState}
   * instances
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    Map<Integer, StateInterface> stateMap = new HashMap<>();
    for (int i = 0; i < numStates; i++) {
      stateMap.put(i, new ColonyState(i));
    }
    return stateMap;
  }

  /**
   * Assigns unique HSB colors to each state for visualization.
   *
   * @return Map where each state is associated with a distinct color in HSB spectrum
   */
  @Override
  protected Map<StateInterface, String> initializeColorMap() {
    Map<StateInterface, String> colorMap = new HashMap<>();
    for (int i = 0; i < numStates; i++) {
      double hue = (360.0 * i) / numStates;
      colorMap.put(new ColonyState(i), Color.hsb(hue, 1.0, 1.0).toString());
    }
    return colorMap;
  }

  /**
   * Applies state transition rules to all cells in parallel:
   * <ol>
   *   <li>For each cell, identify the next state in the dominance cycle</li>
   *   <li>Count neighbors in that dominant state</li>
   *   <li>Convert cell if dominant neighbors meet/exceed threshold percentage</li>
   * </ol>
   */
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
