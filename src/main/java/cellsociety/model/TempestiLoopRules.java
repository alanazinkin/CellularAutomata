package cellsociety.model;

import cellsociety.model.state.LangtonState;

/**
 * Implementation of rule strategy for Tempesti's Loop.
 * Defines the transition rules using state-specific handler methods.
 * @author Tatum McKinnis
 */
public class TempestiLoopRules implements RuleStrategy {

  private static final LangtonState DEFAULT_STATE = LangtonState.EMPTY;
  private static final double SPONTANEOUS_CHANGE_PROBABILITY = 0.002;

  /**
   * Implements the rule strategy for Tempesti's Loop.
   */
  @Override
  public LangtonState determineNextState(LangtonState currentState, LangtonState[] neighbors) {
    if (Math.random() < SPONTANEOUS_CHANGE_PROBABILITY) {
      if (currentState == LangtonState.EMPTY) {
        if (countNeighborType(neighbors, LangtonState.EMPTY) >= 2) {
          return LangtonState.INIT;
        }
      } else if (currentState == LangtonState.SHEATH) {
        if (Math.random() < 0.3) {
          return LangtonState.INIT;
        } else {
          return LangtonState.EXTEND;
        }
      } else if (currentState == LangtonState.CORE) {
        return LangtonState.INIT;
      }
    }

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
        return Math.random() < 0.2 ? LangtonState.EXTEND : LangtonState.SHEATH;
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
   */
  private LangtonState handleEmptyState(LangtonState[] neighbors) {
    if (countNeighborType(neighbors, LangtonState.ADVANCE) >= 1) {
      return LangtonState.SHEATH;
    } else if (countNeighborType(neighbors, LangtonState.SHEATH) >= 2 ||
        countNeighborType(neighbors, LangtonState.TEMP) >= 2) {
      return LangtonState.SHEATH;
    } else if (countNeighborType(neighbors, LangtonState.INIT) >= 1 ||
        countNeighborType(neighbors, LangtonState.EXTEND) >= 1) {
      return Math.random() < 0.7 ? LangtonState.SHEATH : LangtonState.INIT;
    } else if (countNeighborType(neighbors, LangtonState.CORE) >= 1 && Math.random() < 0.1) {
      return LangtonState.INIT;
    }
    return DEFAULT_STATE;
  }

  /**
   * Handles the state transition for a SHEATH cell based on its neighbors.
   */
  private LangtonState handleSheathState(LangtonState[] neighbors) {
    if (countNeighborType(neighbors, LangtonState.INIT) >= 1 ||
        countNeighborType(neighbors, LangtonState.ADVANCE) >= 1) {
      return LangtonState.TEMP;
    }
    return LangtonState.SHEATH;
  }

  /**
   * Handles the state transition for a CORE cell based on its neighbors.
   */
  private LangtonState handleCoreState(LangtonState[] neighbors) {
    if (countNeighborType(neighbors, LangtonState.SHEATH) >= 2) {
      return LangtonState.INIT;
    }
    return LangtonState.CORE;
  }

  /**
   * Handles the state transition for an INIT cell based on its neighbors.
   */
  private LangtonState handleInitState(LangtonState[] neighbors) {
    if (countNeighborType(neighbors, LangtonState.SHEATH) >= 1) {
      return LangtonState.ADVANCE;
    }
    return LangtonState.INIT;
  }

  /**
   * Handles the state transition for an ADVANCE cell based on its neighbors.
   */
  private LangtonState handleAdvanceState(LangtonState[] neighbors) {
    if (countNeighborType(neighbors, LangtonState.EMPTY) >= 1) {
      return LangtonState.EXTEND;
    }
    return LangtonState.ADVANCE;
  }

  /**
   * Handles the state transition for a TURN cell based on its neighbors.
   */
  private LangtonState handleTurnState(LangtonState[] neighbors) {
    if (countNeighborType(neighbors, LangtonState.EXTEND) >= 1) {
      return LangtonState.SHEATH;
    }
    return LangtonState.TURN;
  }

  /**
   * Handles the state transition for an EXTEND cell based on its neighbors.
   */
  private LangtonState handleExtendState(LangtonState[] neighbors) {
    return LangtonState.SHEATH;
  }

  /**
   * Helper method to count occurrences of a state in the neighborhood.
   */
  private static int countNeighborType(LangtonState[] neighbors, LangtonState state) {
    int count = 0;
    for (LangtonState neighbor : neighbors) {
      if (neighbor == state) {
        count++;
      }
    }
    return count;
  }
}