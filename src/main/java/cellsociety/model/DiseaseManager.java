package cellsociety.model;

import cellsociety.model.simulations.SugarScape;
import java.util.List;

public class DiseaseManager {

  private final SugarScape simulation;

  public DiseaseManager(SugarScape simulation) {
    this.simulation = simulation;
  }

  public void applyDiseaseRules(List<Agent> agents) {
    // Update immune system for agents with diseases.
    for (Agent agent : agents) {
      if (!agent.getDiseases().isEmpty()) {
        Disease randomDisease = selectRandomDisease(agent);
        if (randomDisease != null) {
          agent.updateImmuneSystem(randomDisease);
          agent.checkAndRemoveDiseases();
        }
      }
    }
    // Transmit diseases to neighboring agents.
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

  private Disease selectRandomDisease(Agent agent) {
    List<Disease> diseases = agent.getDiseases();
    if (diseases.isEmpty()) return null;
    return diseases.get(simulation.getRandom().nextInt(diseases.size()));
  }
}

