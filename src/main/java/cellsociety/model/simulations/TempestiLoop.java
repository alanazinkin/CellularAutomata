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
 * Simulates Tempesti's Loop cellular automaton, a self-replicating structure with distinct
 * transition rules from Langton's Loop. Utilizes 8 states and a von Neumann neighborhood.
 * <p>
 * The Tempesti Loop focuses on controlled growth through sheath extension guided by core-generated
 * signals, which propagate and create new loop structures.
 * </p>
 * <p>
 * Each state in the Tempesti Loop has specific transition rules that dictate how cells evolve based
 * on their neighbors.
 * </p>
 *
 * @author Tatum McKinnis
 */
public class TempestiLoop extends Simulation {

  private static final LangtonState DEFAULT_STATE = LangtonState.EMPTY;
  private static final int[][] VON_NEUMANN_OFFSETS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

  /**
   * Constructs the TempestiLoop simulation with a given configuration. Initializes the grid with
   * the specified dimensions and default state.
   *
   * @param simulationConfig Configuration object containing simulation parameters.
   */
  public TempestiLoop(SimulationConfig simulationConfig) {
    super(simulationConfig,
        new Grid(simulationConfig.getHeight(), simulationConfig.getWidth(), DEFAULT_STATE));
  }

  /**
   * Initializes the color map for the simulation, mapping each LangtonState to a corresponding CSS
   * class.
   *
   * @return A map of LangtonState values to CSS class names representing different states.
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
   * Initializes the state map, associating integer values to specific LangtonState.
   *
   * @return A map of integer values to LangtonState (0 -> EMPTY, 1 -> SHEATH, etc.).
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
   * Applies the rules of the Tempesti Loop simulation to all cells in the grid. For each cell, the
   * next state is determined based on the current state and the von Neumann neighbors.
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
   * Updates the state of a specific cell at the given row and column based on its current state and
   * neighbors.
   *
   * @param row The row index of the cell to update.
   * @param col The column index of the cell to update.
   */
  private void updateCellState(int row, int col) {
    Cell currentCell = getGrid().getCell(row, col);
    LangtonState currentState = (LangtonState) currentCell.getCurrentState();
    LangtonState[] neighbors = getVonNeumannNeighborStates(row, col);

    LangtonState newState = applyTempestiRules(currentState, neighbors);
    currentCell.setNextState(newState);
  }

  /**
   * Retrieves the states of the von Neumann neighbors of a given cell. The von Neumann neighborhood
   * includes the cells directly above, below, left, and right of the given cell.
   *
   * @param row The row index of the cell.
   * @param col The column index of the cell.
   * @return An array containing the states of the four von Neumann neighbors.
   */
  private LangtonState[] getVonNeumannNeighborStates(int row, int col) {
    LangtonState[] neighbors = new LangtonState[4];
    Grid grid = getGrid();

    for (int i = 0; i < VON_NEUMANN_OFFSETS.length; i++) {
      int newRow = row + VON_NEUMANN_OFFSETS[i][0];
      int newCol = col + VON_NEUMANN_OFFSETS[i][1];
      neighbors[i] = grid.isValidPosition(newRow, newCol) ?
          (LangtonState) grid.getCell(newRow, newCol).getCurrentState() :
          LangtonState.EMPTY;
    }

    return neighbors;
  }

  /**
   * Applies the specific transition rules for the Tempesti Loop based on the current state and the
   * neighboring states.
   *
   * @param currentState The current state of the cell.
   * @param neighbors    An array of the states of the von Neumann neighbors.
   * @return The new state of the cell after applying the rules.
   */
  private LangtonState applyTempestiRules(LangtonState currentState, LangtonState[] neighbors) {
    switch (currentState) {
      case EMPTY:
        return handleEmptyState(neighbors);
      case SHEATH:
        return handleSheathState(neighbors);
      case CORE:
        return handleCoreState(neighbors);
      case INIT:
        return handleInitState(neighbors);
      case ADVANCE:
        return handleAdvanceState(neighbors);
      case TEMP:
        return LangtonState.SHEATH; // Revert TEMP to SHEATH after one step
      case TURN:
        return handleTurnState(neighbors);
      case EXTEND:
        return handleExtendState(neighbors);
      default:
        return currentState;
    }
  }

  /**
   * Handles the state transition for an EMPTY cell based on its neighbors.
   *
   * @param neighbors The von Neumann neighbors of the cell.
   * @return The new state for the cell (either SHEATH or EMPTY).
   */
  private LangtonState handleEmptyState(LangtonState[] neighbors) {
    // Empty cells become SHEATH if adjacent to ADVANCE or two SHEATH/TEMP
    if (countNeighborType(neighbors, LangtonState.ADVANCE) >= 1) {
      return LangtonState.SHEATH;
    } else if (countNeighborType(neighbors, LangtonState.SHEATH) >= 2 ||
        countNeighborType(neighbors, LangtonState.TEMP) >= 2) {
      return LangtonState.SHEATH;
    }
    return LangtonState.EMPTY;
  }

  /**
   * Handles the state transition for a SHEATH cell based on its neighbors.
   *
   * @param neighbors The von Neumann neighbors of the cell.
   * @return The new state for the cell (either SHEATH or TEMP).
   */
  private LangtonState handleSheathState(LangtonState[] neighbors) {
    // Sheath becomes TEMP if near INIT/ADVANCE to propagate signal
    if (countNeighborType(neighbors, LangtonState.INIT) >= 1 ||
        countNeighborType(neighbors, LangtonState.ADVANCE) >= 1) {
      return LangtonState.TEMP;
    }
    return LangtonState.SHEATH;
  }

  /**
   * Handles the state transition for a CORE cell based on its neighbors.
   *
   * @param neighbors The von Neumann neighbors of the cell.
   * @return The new state for the cell (either CORE or INIT).
   */
  private LangtonState handleCoreState(LangtonState[] neighbors) {
    // Core emits INIT if surrounded by at least two SHEATH
    if (countNeighborType(neighbors, LangtonState.SHEATH) >= 2) {
      return LangtonState.INIT;
    }
    return LangtonState.CORE;
  }

  /**
   * Handles the state transition for an INIT cell based on its neighbors.
   *
   * @param neighbors The von Neumann neighbors of the cell.
   * @return The new state for the cell (either INIT or ADVANCE).
   */
  private LangtonState handleInitState(LangtonState[] neighbors) {
    // INIT propagates to adjacent SHEATH and converts to ADVANCE
    if (countNeighborType(neighbors, LangtonState.SHEATH) >= 1) {
      return LangtonState.ADVANCE;
    }
    return LangtonState.INIT;
  }

  /**
   * Handles the state transition for an ADVANCE cell based on its neighbors.
   *
   * @param neighbors The von Neumann neighbors of the cell.
   * @return The new state for the cell (either ADVANCE or EXTEND).
   */
  private LangtonState handleAdvanceState(LangtonState[] neighbors) {
    // ADVANCE moves to empty cells, extending the sheath
    if (countNeighborType(neighbors, LangtonState.EMPTY) >= 1) {
      return LangtonState.EXTEND;
    }
    return LangtonState.ADVANCE;
  }

  /**
   * Handles the state transition for a TURN cell based on its neighbors.
   *
   * @param neighbors The von Neumann neighbors of the cell.
   * @return The new state for the cell (either SHEATH or TURN).
   */
  private LangtonState handleTurnState(LangtonState[] neighbors) {
    // TURN state facilitates direction change, reverts after extension
    if (countNeighborType(neighbors, LangtonState.EXTEND) >= 1) {
      return LangtonState.SHEATH;
    }
    return LangtonState.TURN;
  }

  /**
   * Handles the state transition for an EXTEND cell based on its neighbors.
   *
   * @param neighbors The von Neumann neighbors of the cell.
   * @return The new state for the cell (always SHEATH).
   */
  private LangtonState handleExtendState(LangtonState[] neighbors) {
    // EXTEND converts to SHEATH and creates new ADVANCE signal
    return LangtonState.SHEATH;
  }

  /**
   * Counts the number of neighbors of a specific type.
   *
   * @param neighbors The array of neighboring cell states.
   * @param state     The state to count.
   * @return The number of neighbors that match the specified state.
   */
  private int countNeighborType(LangtonState[] neighbors, LangtonState state) {
    int count = 0;
    for (LangtonState neighbor : neighbors) {
      if (neighbor == state) {
        count++;
      }
    }
    return count;
  }
}
