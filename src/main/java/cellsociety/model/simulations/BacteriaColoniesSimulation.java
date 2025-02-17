package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.state.BacteriaState;
import cellsociety.model.StateInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simulates competing bacteria colonies using a rock-paper-scissors model.
 * Each cell in the grid represents a bacteria colony that can be in one of three states:
 * ROCK, PAPER, or SCISSORS. The simulation follows rock-paper-scissors rules where:
 * <ul>
 *   <li>ROCK beats SCISSORS</li>
 *   <li>SCISSORS beats PAPER</li>
 *   <li>PAPER beats ROCK</li>
 * </ul>
 * During each simulation step, cells compete with their Moore neighbors (8 surrounding cells).
 * A cell changes its state to a competing state if the number of neighbors in that state
 * exceeds a configurable threshold.
 *
 * @author Tatum McKinnis
 * @see BacteriaState
 * @see Simulation
 */
public class BacteriaColoniesSimulation extends Simulation {
  /** Default threshold for number of neighbors needed to change a cell's state */
  private static final int DEFAULT_THRESHOLD = 3;
  /** Default state for initializing grid cells */
  private static final BacteriaState DEFAULT_STATE = BacteriaState.ROCK;
  /** Threshold number of neighbors needed to change a cell's state */
  private final double neighborThreshold;

  /**
   * Constructs a new BacteriaColoniesSimulation with specified configuration and grid.
   * Initializes the simulation grid with the default state and sets up the neighbor
   * threshold for state changes. The neighbor threshold determines how many neighbors
   * of a competing state are needed to change a cell's current state.
   *
   * @param simulationConfig Configuration containing grid dimensions, initial states,
   *                        and parameters including the neighbor threshold
   * @throws IllegalArgumentException if grid dimensions are invalid or initial states array is empty
   */
  public BacteriaColoniesSimulation(SimulationConfig simulationConfig) {
    super(simulationConfig, new Grid(simulationConfig.getHeight(), simulationConfig.getWidth(), DEFAULT_STATE));
    this.neighborThreshold = simulationConfig.getParameters().getOrDefault("neighborThreshold", (double) DEFAULT_THRESHOLD);
  }

  /**
   * Initializes the mapping between states and their display colors.
   * ROCK is displayed as red, PAPER as green, and SCISSORS as blue.
   *
   * @return Map associating each bacteria state with its hexadecimal color code
   */
  @Override
  protected Map<StateInterface, String> initializeColorMap() {
    Map<StateInterface, String> colorMap = new HashMap<>();
    colorMap.put(BacteriaState.ROCK, "bacteria-state-rock");
    colorMap.put(BacteriaState.PAPER, "bacteria-state-paper");
    colorMap.put(BacteriaState.SCISSORS, "bacteria-state-scissors");
    return colorMap;
  }

  /**
   * Initializes the mapping between integer values and simulation states.
   * This mapping is used to convert between the numeric representation in the
   * configuration and the actual state objects.
   *
   * @return Map associating integer values with their corresponding bacteria states
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    Map<Integer, StateInterface> stateMap = new HashMap<>();
    stateMap.put(0, BacteriaState.ROCK);
    stateMap.put(1, BacteriaState.PAPER);
    stateMap.put(2, BacteriaState.SCISSORS);
    return stateMap;
  }

  /**
   * Applies the rock-paper-scissors rules to all cells in the grid.
   * For each cell, counts its neighbors' states and determines if the cell
   * should change state based on the neighbor threshold.
   */
  @Override
  protected void applyRules() {
    Grid grid = getGrid();
    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        updateCellState(row, col);
      }
    }
  }

  /**
   * Updates the state of a single cell based on its Moore neighbors.
   * A cell changes its state to a competing state if the number of neighbors
   * in that state exceeds the neighbor threshold. The competing state must be
   * one that beats the current state in rock-paper-scissors rules.
   *
   * @param row The row index of the cell to update
   * @param col The column index of the cell to update
   */
  private void updateCellState(int row, int col) {
    Cell currentCell = getGrid().getCell(row, col);
    BacteriaState currentState = (BacteriaState) currentCell.getCurrentState();

    List<Cell> neighbors = getGrid().getNeighbors(row, col);
    Map<BacteriaState, Integer> neighborCounts = countNeighborStates(neighbors);

    // Check if any competing state has enough neighbors to beat the current state
    for (BacteriaState state : BacteriaState.values()) {
      if (state.beats(currentState)) {
        int count = neighborCounts.getOrDefault(state, 0);
        if (count >= neighborThreshold) {
          currentCell.setNextState(state);
          return;
        }
      }
    }

    // If no state has enough neighbors to win, keep current state
    currentCell.setNextState(currentState);
  }

  /**
   * Counts the number of neighbors in each possible state.
   * Creates a mapping from each bacteria state to the number of neighboring
   * cells currently in that state.
   *
   * @param neighbors List of neighboring cells to count
   * @return Map containing the count of neighbors for each possible state
   */
  private Map<BacteriaState, Integer> countNeighborStates(List<Cell> neighbors) {
    Map<BacteriaState, Integer> counts = new HashMap<>();
    for (Cell neighbor : neighbors) {
      BacteriaState state = (BacteriaState) neighbor.getCurrentState();
      counts.merge(state, 1, Integer::sum);
    }
    return counts;
  }
}
