package cellsociety.model;

import cellsociety.model.Agent;
import cellsociety.model.simulations.SugarScape;
import java.util.List;

public class TradingManager {

  private final SugarScape simulation;

  public TradingManager(SugarScape simulation) {
    this.simulation = simulation;
  }

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

