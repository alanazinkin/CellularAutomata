package cellsociety.model;

import cellsociety.model.state.LangtonState;
import java.util.List;

/**
 * Implementation of rule strategy for Langton's Loop. Defines the transition rules using a
 * collection of TransitionRule records.
 *
 * @author Tatum McKinnis
 */
public class LangtonLoopRules implements RuleStrategy {

  /**
   * Represents a single transition rule for the Langton Loop automaton.
   */
  private record TransitionRule(
      LangtonState currentState,
      LangtonState nextState,
      NeighborPredicate neighborTest
  ) {

    public LangtonState apply(LangtonState[] neighbors) {
      if (neighborTest.test(neighbors)) {
        if (Math.random() < 0.005) {
          LangtonState[] states = LangtonState.values();
          return states[(int) (Math.random() * states.length)];
        }
        return nextState;
      }
      return null;
    }
  }

  /**
   * Functional interface for testing if a cell's neighbors meet specific conditions.
   */
  @FunctionalInterface
  private interface NeighborPredicate {

    boolean test(LangtonState[] neighbors);
  }

  /**
   * Defines all possible state transitions in the Langton's Loop automaton.
   */
  private static final List<TransitionRule> TRANSITION_RULES = List.of(
      // Empty cell rules - allow growth
      new TransitionRule(LangtonState.EMPTY, LangtonState.SHEATH,
          neighbors -> countNeighborType(neighbors, LangtonState.EXTEND) >= 1 ||
              countNeighborType(neighbors, LangtonState.CORE) >= 2),

      // Propagation rules for signal
      new TransitionRule(LangtonState.INIT, LangtonState.ADVANCE,
          neighbors -> neighbors[0] == LangtonState.CORE ||
              neighbors[2] == LangtonState.CORE ||
              countNeighborType(neighbors, LangtonState.EXTEND) >= 1),

      new TransitionRule(LangtonState.ADVANCE, LangtonState.EXTEND,
          neighbors -> countNeighborType(neighbors, LangtonState.SHEATH) >= 1 ||
              countNeighborType(neighbors, LangtonState.CORE) >= 1 ||
              countNeighborType(neighbors, LangtonState.INIT) >= 1),

      // Extend transformation rules
      new TransitionRule(LangtonState.EXTEND, LangtonState.TEMP,
          neighbors -> countNeighborType(neighbors, LangtonState.EMPTY) >= 2 ||
              countNeighborType(neighbors, LangtonState.ADVANCE) >= 1),

      new TransitionRule(LangtonState.EXTEND, LangtonState.CORE,
          neighbors -> countNeighborType(neighbors, LangtonState.SHEATH) >= 2 ||
              countNeighborType(neighbors, LangtonState.TEMP) >= 1),

      // Temp to Core transformation
      new TransitionRule(LangtonState.TEMP, LangtonState.CORE,
          neighbors -> countNeighborType(neighbors, LangtonState.CORE) >= 1 ||
              countNeighborType(neighbors, LangtonState.SHEATH) >= 2 ||
              countNeighborType(neighbors, LangtonState.EXTEND) >= 2),

      // Core transformation rules - enable more activity
      new TransitionRule(LangtonState.CORE, LangtonState.TURN,
          neighbors -> countNeighborType(neighbors, LangtonState.SHEATH) >= 3 ||
              (countNeighborType(neighbors, LangtonState.EXTEND) >= 2 &&
                  countNeighborType(neighbors, LangtonState.SHEATH) >= 1)),

      // Turn transformation rules
      new TransitionRule(LangtonState.TURN, LangtonState.INIT,
          neighbors -> (neighbors[0] == LangtonState.CORE && neighbors[3] == LangtonState.SHEATH) ||
              (neighbors[2] == LangtonState.CORE && neighbors[1] == LangtonState.SHEATH) ||
              countNeighborType(neighbors, LangtonState.EXTEND) >= 2),

      // Sheath transformation rules
      new TransitionRule(LangtonState.SHEATH, LangtonState.CORE,
          neighbors -> countNeighborType(neighbors, LangtonState.CORE) >= 2 ||
              (countNeighborType(neighbors, LangtonState.TURN) >= 1 &&
                  countNeighborType(neighbors, LangtonState.CORE) >= 1)),

      new TransitionRule(LangtonState.SHEATH, LangtonState.EXTEND,
          neighbors -> countNeighborType(neighbors, LangtonState.EXTEND) >= 1 ||
              countNeighborType(neighbors, LangtonState.ADVANCE) >= 1),

      // Additional rules to break stasis
      new TransitionRule(LangtonState.CORE, LangtonState.ADVANCE,
          neighbors -> countNeighborType(neighbors, LangtonState.INIT) >= 1 &&
              countNeighborType(neighbors, LangtonState.EMPTY) >= 2),

      new TransitionRule(LangtonState.SHEATH, LangtonState.INIT,
          neighbors -> countNeighborType(neighbors, LangtonState.EMPTY) >= 3 &&
              countNeighborType(neighbors, LangtonState.CORE) >= 1)
  );

  /**
   * Implements the rule strategy for Langton's Loop.
   */
  @Override
  public LangtonState determineNextState(LangtonState currentState, LangtonState[] neighbors) {
    if (Math.random() < 0.001) {
      if (currentState == LangtonState.EMPTY) {
        return LangtonState.INIT;
      } else if (currentState == LangtonState.SHEATH) {
        return LangtonState.EXTEND;
      }
    }

    for (TransitionRule rule : TRANSITION_RULES) {
      if (rule.currentState() == currentState) {
        try {
          LangtonState result = rule.apply(neighbors);
          if (result != null) {
            return result;
          }
        } catch (Exception e) {
          System.out.println("Exception while testing rule " + rule + ": " + e.getMessage());
        }
      }
    }

    if (Math.random() < 0.0005) {
      if (currentState == LangtonState.EMPTY &&
          countNeighborType(neighbors, LangtonState.SHEATH) > 0) {
        return LangtonState.INIT;
      }
    }

    return currentState;
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