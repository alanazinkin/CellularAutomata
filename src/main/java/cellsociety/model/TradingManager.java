package cellsociety.model;

import cellsociety.model.simulations.SugarScape;
import java.util.List;/**
 * The {@code TradingManager} class manages trading between agents within the SugarScape simulation.
 * It determines which agents can trade based on predefined rules and facilitates resource exchanges
 * between neighboring agents.
 */
public class TradingManager {

  private final SugarScape simulation;

  /**
   * Constructs a {@code TradingManager} associated with a specific SugarScape simulation.
   *
   * @param simulation the {@code SugarScape} simulation instance this manager operates on
   */
  public TradingManager(SugarScape simulation) {
    this.simulation = simulation;
  }

  /**
   * Applies trading rules to a list of agents. Each agent interacts with its neighbors,
   * and if the predefined trading conditions are met, they exchange resources.
   *
   * @param agents the list of agents in the simulation to apply trading rules to
   */
  public void applyTrading(List<Agent> agents) {
    for (Agent agent : agents) {
      List<Agent> neighbors = GridOperations.getAgentNeighbors(agent, simulation);
      for (Agent neighbor : neighbors) {
        if (RulesOperations.canTrade(agent, neighbor)) {
          RulesOperations.executeTrade(agent, neighbor);
        }
      }
    }
  }
}
