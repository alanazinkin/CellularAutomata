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
 *
 * @author Tatum McKinnis
 */
public class TempestiLoop extends Simulation {

  private static final LangtonState DEFAULT_STATE = LangtonState.EMPTY;
  private static final int[][] VON_NEUMANN_OFFSETS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

  public TempestiLoop(SimulationConfig simulationConfig) {
    super(simulationConfig,
        new Grid(simulationConfig.getHeight(), simulationConfig.getWidth(), DEFAULT_STATE));
  }

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

  @Override
  protected void applyRules() {
    Grid grid = getGrid();
    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        updateCellState(row, col);
      }
    }
  }

  private void updateCellState(int row, int col) {
    Cell currentCell = getGrid().getCell(row, col);
    LangtonState currentState = (LangtonState) currentCell.getCurrentState();
    LangtonState[] neighbors = getVonNeumannNeighborStates(row, col);

    LangtonState newState = applyTempestiRules(currentState, neighbors);
    currentCell.setNextState(newState);
  }

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

  private LangtonState handleSheathState(LangtonState[] neighbors) {
    // Sheath becomes TEMP if near INIT/ADVANCE to propagate signal
    if (countNeighborType(neighbors, LangtonState.INIT) >= 1 ||
        countNeighborType(neighbors, LangtonState.ADVANCE) >= 1) {
      return LangtonState.TEMP;
    }
    return LangtonState.SHEATH;
  }

  private LangtonState handleCoreState(LangtonState[] neighbors) {
    // Core emits INIT if surrounded by at least two SHEATH
    if (countNeighborType(neighbors, LangtonState.SHEATH) >= 2) {
      return LangtonState.INIT;
    }
    return LangtonState.CORE;
  }

  private LangtonState handleInitState(LangtonState[] neighbors) {
    // INIT propagates to adjacent SHEATH and converts to ADVANCE
    if (countNeighborType(neighbors, LangtonState.SHEATH) >= 1) {
      return LangtonState.ADVANCE;
    }
    return LangtonState.INIT;
  }

  private LangtonState handleAdvanceState(LangtonState[] neighbors) {
    // ADVANCE moves to empty cells, extending the sheath
    if (countNeighborType(neighbors, LangtonState.EMPTY) >= 1) {
      return LangtonState.EXTEND;
    }
    return LangtonState.ADVANCE;
  }

  private LangtonState handleTurnState(LangtonState[] neighbors) {
    // TURN state facilitates direction change, reverts after extension
    if (countNeighborType(neighbors, LangtonState.EXTEND) >= 1) {
      return LangtonState.SHEATH;
    }
    return LangtonState.TURN;
  }

  private LangtonState handleExtendState(LangtonState[] neighbors) {
    // EXTEND converts to SHEATH and creates new ADVANCE signal
    return LangtonState.SHEATH;
  }

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
