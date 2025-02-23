package cellsociety.model;

import cellsociety.model.simulations.SugarScape;
import cellsociety.model.state.SugarScapeState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages reproduction within the SugarScape simulation.
 * This includes determining which agents can reproduce,
 * selecting suitable mates, and placing offspring in empty grid cells.
 */
public class ReproductionManager {

  private final SugarScape simulation;

  /**
   * Constructs a ReproductionManager associated with a specific SugarScape simulation.
   *
   * @param simulation the SugarScape simulation instance this manager operates on
   */
  public ReproductionManager(SugarScape simulation) {
    this.simulation = simulation;
  }

  /**
   * Applies reproduction rules to a list of agents. Fertile agents seek mates among their neighbors,
   * and if conditions allow, they reproduce and place offspring in an adjacent empty cell.
   *
   * @param agents the list of agents in the simulation to apply reproduction rules to
   */
  public void applyReproduction(List<Agent> agents) {
    List<Agent> newAgents = new ArrayList<>();

    for (Agent agent : agents) {
      if (!agent.isFertile()) continue;

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
    simulation.getAgents().addAll(newAgents);
    simulation.getGrid().applyNextStates();
  }
}
