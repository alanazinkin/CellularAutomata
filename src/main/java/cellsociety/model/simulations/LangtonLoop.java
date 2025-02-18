package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.state.LangtonState;
import cellsociety.model.StateInterface;
import java.util.HashMap;
import java.util.Map;

/**
 * Simulates Langton's Loop cellular automaton, a self-replicating pattern that demonstrates
 * emergent behavior in cellular automata. The simulation uses 8 states and operates on a
 * von Neumann neighborhood (4 adjacent cells).
 * <p>
 * The automaton demonstrates self-replication through a complex interaction of states that
 * form a loop structure capable of creating copies of itself. The replication process uses
 * a "genome" encoded as state sequences, with signals traveling along sheathed pathways.
 * </p>
 * @author Tatum McKinnis
 */
public class LangtonLoop extends Simulation {
  private static final LangtonState DEFAULT_STATE = LangtonState.EMPTY;

  /**
   * Von Neumann neighborhood offsets (North, East, South, West)
   */
  private static final int[][] VON_NEUMANN_OFFSETS = {
      {-1, 0},  // North
      {0, 1},   // East
      {1, 0},   // South
      {0, -1}   // West
  };

  /**
   * Constructs a new Langton's Loop simulation with specified configuration.
   *
   * @param simulationConfig Contains initial simulation parameters and grid dimensions
   * @throws IllegalArgumentException if grid dimensions are invalid or initial states array is empty
   */
  public LangtonLoop(SimulationConfig simulationConfig) {
    super(simulationConfig, new Grid(simulationConfig.getHeight(), simulationConfig.getWidth(), DEFAULT_STATE));
  }

  /**
   * Initializes a mapping of Langton's Ant states to their corresponding color representations.
   * This map is used for visualization purposes, assigning a unique color to each state.
   *
   * @return A map where keys are {@code StateInterface} values representing different states
   *         and values are hexadecimal color codes as strings.
   */
  @Override
  protected Map<StateInterface, String> initializeColorMap() {
    Map<StateInterface, String> colorMap = new HashMap<>();
    colorMap.put(LangtonState.EMPTY, "langton-state-empty");
    colorMap.put(LangtonState.SHEATH, "langton-state-sheath");
    colorMap.put(LangtonState.CORE, "langton-state-core");
    colorMap.put(LangtonState.TEMP, "langton-state-temp");
    colorMap.put(LangtonState.TURN, "langton-state-turn");
    colorMap.put(LangtonState.EXTEND, "langton-state-extend");
    colorMap.put(LangtonState.INIT, "langton-state-init");
    colorMap.put(LangtonState.ADVANCE, "langton-state-advance");
    return colorMap;
  }

  /**
   * Initializes a mapping of integer values to their corresponding Langton's Ant states.
   * This map is used to associate numerical representations with specific states,
   * which can be useful for grid-based simulations.
   *
   * @return A map where keys are integer values representing different states
   *         and values are {@code StateInterface} objects defining those states.
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    Map<Integer, StateInterface> stateMap = new HashMap<>();
    stateMap.put(0, LangtonState.EMPTY);
    stateMap.put(1, LangtonState.SHEATH);
    stateMap.put(2, LangtonState.CORE);
    stateMap.put(3, LangtonState.TEMP);
    stateMap.put(4, LangtonState.TURN);
    stateMap.put(5, LangtonState.EXTEND);
    stateMap.put(6, LangtonState.INIT);
    stateMap.put(7, LangtonState.ADVANCE);
    return stateMap;
  }

  /**
   * Applies the rules of the simulation to update the states of cells in the grid.
   * Iterates through each cell and updates its state based on predefined rules.
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
   * Updates the state of a single cell based on its von Neumann neighborhood configuration
   * and the Langton's Loop transition rules.
   *
   * @param row The row index of the cell
   * @param col The column index of the cell
   */
  private void updateCellState(int row, int col) {
    Cell currentCell = getGrid().getCell(row, col);
    LangtonState currentState = (LangtonState) currentCell.getCurrentState();
    LangtonState[] neighbors = getVonNeumannNeighborStates(row, col);

    LangtonState newState = applyTransitionRules(currentState, neighbors);
    currentCell.setNextState(newState);
  }

  /**
   * Retrieves the states of the von Neumann neighborhood (4 adjacent cells).
   *
   * @param row The row index of the center cell
   * @param col The column index of the center cell
   * @return Array of states in order: North, East, South, West
   */
  private LangtonState[] getVonNeumannNeighborStates(int row, int col) {
    LangtonState[] neighbors = new LangtonState[4];
    Grid grid = getGrid();

    for (int i = 0; i < VON_NEUMANN_OFFSETS.length; i++) {
      int newRow = row + VON_NEUMANN_OFFSETS[i][0];
      int newCol = col + VON_NEUMANN_OFFSETS[i][1];

      if (grid.isValidPosition(newRow, newCol)) {
        neighbors[i] = (LangtonState) grid.getCell(newRow, newCol).getCurrentState();
      } else {
        neighbors[i] = LangtonState.EMPTY;
      }
    }

    return neighbors;
  }

  /**
   * Applies Langton's Loop transition rules to determine the next state of a cell.
   * The rules are based on the current state and the configuration of neighboring states.
   *
   * @param currentState The current state of the cell
   * @param neighbors Array of neighbor states in von Neumann neighborhood
   * @return The next state for the cell
   */
  private LangtonState applyTransitionRules(LangtonState currentState, LangtonState[] neighbors) {
    if (currentState == LangtonState.EMPTY) {
      // Check for loop extension pattern
      if (countNeighborType(neighbors, LangtonState.EXTEND) >= 1) {
        return LangtonState.SHEATH;
      }
    } else if (currentState == LangtonState.ADVANCE) {
      // Signal propagation
      if (countNeighborType(neighbors, LangtonState.SHEATH) >= 2) {
        return LangtonState.EXTEND;
      }
    }

    return currentState;
  }

  /**
   * Counts the number of neighbors that match a specific state.
   *
   * @param neighbors Array of neighbor states
   * @param state State to count
   * @return Number of neighbors matching the specified state
   */
  private int countNeighborType(LangtonState[] neighbors, LangtonState state) {
    int count = 0;
    for (LangtonState neighbor : neighbors) {
      if (neighbor == state) count++;
    }
    return count;
  }
}
