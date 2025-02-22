package cellsociety.model;

import cellsociety.model.simulations.SugarScape;

import cellsociety.model.state.SugarScapeState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReproductionManager {

  private final SugarScape simulation;

  public ReproductionManager(SugarScape simulation) {
    this.simulation = simulation;
  }

  public void applyReproduction(List<Agent> agents) {
    List<Agent> newAgents = new ArrayList<>();

    for (Agent agent : agents) {
      if (!agent.isFertile()) continue;

      // Get adjacent agents as potential mates.
      List<Agent> neighbors = GridOperations.getAgentNeighbors(agent, simulation);
      Collections.shuffle(neighbors, simulation.getRandom());

      for (Agent neighbor : neighbors) {
        if (RulesOperations.canReproduce(agent, neighbor)) {
          Cell emptyCell = GridOperations.findAdjacentEmptyCell(agent, simulation);
          if (emptyCell != null) {
            Agent child = RulesOperations.reproduce(agent, neighbor, emptyCell, simulation.getRandom());
            newAgents.add(child);
            emptyCell.setNextState(SugarScapeState.AGENT);
            break;
          }
        }
      }
    }
    // Directly add new agents to the simulation's internal list.
    simulation.getAgents().addAll(newAgents);
    simulation.getGrid().applyNextStates();
  }
}

