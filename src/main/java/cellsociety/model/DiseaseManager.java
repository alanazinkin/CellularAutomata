package cellsociety.model;

import cellsociety.model.simulations.SugarScape;
import java.util.List;

/**
 * Manages the spread and effects of diseases within the SugarScape simulation.
 * This includes updating agents' immune systems and transmitting diseases to neighboring agents.
 */
public class DiseaseManager {

  private final SugarScape simulation;

  /**
   * Constructs a DiseaseManager associated with a specific SugarScape simulation.
   *
   * @param simulation the SugarScape simulation instance this manager operates on
   */
  public DiseaseManager(SugarScape simulation) {
    this.simulation = simulation;
  }

  /**
   * Applies disease rules to a list of agents. This includes updating their immune systems
   * if they have diseases and transmitting diseases to neighboring agents.
   *
   * @param agents the list of agents in the simulation to apply disease rules to
   */
  public void applyDiseaseRules(List<Agent> agents) {
    for (Agent agent : agents) {
      if (!agent.getDiseases().isEmpty()) {
        Disease randomDisease = selectRandomDisease(agent);
        if (randomDisease != null) {
          agent.updateImmuneSystem(randomDisease);
          agent.checkAndRemoveDiseases();
        }
      }
    }
    for (Agent agent : agents) {
      if (!agent.getDiseases().isEmpty()) {
        List<Agent> neighbors = GridOperations.getAgentNeighbors(agent, simulation);
        for (Agent neighbor : neighbors) {
          Disease disease = agent.getRandomDisease();
          if (disease != null) {
            neighbor.addDisease(disease.clone());
          }
        }
      }
    }
  }

  /**
   * Selects a random disease from the given agent's list of diseases.
   *
   * @param agent the agent whose diseases will be considered
   * @return a randomly selected Disease from the agent's disease list, or null if none exist
   */
  private Disease selectRandomDisease(Agent agent) {
    List<Disease> diseases = agent.getDiseases();
    if (diseases.isEmpty()) return null;
    return diseases.get(simulation.getRandom().nextInt(diseases.size()));
  }
}
