package cellsociety.model;

import cellsociety.model.state.LangtonState;

/**
 * Strategy interface for cellular automaton rules. Defines the contract for different rule
 * implementations for loop simulations.
 *
 * @author Tatum McKinnis
 */
public interface RuleStrategy {

  /**
   * Determines the next state based on current state and neighbors.
   *
   * @param currentState The current state of the cell
   * @param neighbors    Array of neighbor states in von Neumann neighborhood
   * @return The next state for the cell
   */
  LangtonState determineNextState(LangtonState currentState, LangtonState[] neighbors);
}
