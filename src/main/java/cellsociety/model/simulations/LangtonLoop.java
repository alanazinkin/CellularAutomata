package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Grid;
import cellsociety.model.LangtonLoopRules;
import cellsociety.model.RuleStrategy;
import cellsociety.model.state.LangtonState;

/**
 * Simulates Langton's Loop cellular automaton, a self-replicating pattern that demonstrates
 * emergent behavior in cellular automata. The simulation uses 8 states and operates on a von
 * Neumann neighborhood (4 adjacent cells).
 *
 * @author Tatum McKinnis
 */
public class LangtonLoop extends AbstractLoopSimulation {

  private final RuleStrategy ruleStrategy;

  /**
   * Constructs a new Langton's Loop simulation with specified configuration.
   */
  public LangtonLoop(SimulationConfig simulationConfig, Grid grid) {
    super(simulationConfig, grid);
    this.ruleStrategy = new LangtonLoopRules();
    validateGridSize(grid);
  }

  /**
   * Applies the specific transition rules for Langton's Loop based on the current state and the
   * neighboring states. Delegates to the rule strategy.
   */
  @Override
  protected LangtonState determineNextState(LangtonState currentState, LangtonState[] neighbors) {
    return ruleStrategy.determineNextState(currentState, neighbors);
  }
}