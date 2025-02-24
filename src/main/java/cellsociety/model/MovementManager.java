package cellsociety.model;

import cellsociety.model.state.SugarScapeState;
import cellsociety.model.simulations.SugarScape;
import java.util.List;

public class MovementManager {
  private final SugarScape simulation;

  public MovementManager(SugarScape simulation) {
    this.simulation = simulation;
  }

  public void applyMovement(List<Agent> agents) {
    if (agents == null) {
      throw new IllegalArgumentException("Agents list cannot be null");
    }

    for (Agent agent : agents) {
      if (agent.getPosition() == null) {
        throw new IllegalArgumentException("Agent position cannot be null");
      }

      Cell currentCell = agent.getPosition();
      List<Cell> validMoves = GridOperations.findValidMoves(agent, simulation);

      if (!validMoves.isEmpty()) {
        Cell bestMove = GridOperations.findBestMove(agent, validMoves, simulation);
        if (bestMove != null) {
          // Clear the agent's old cell
          currentCell.setNextState(SugarScapeState.EMPTY);

          // If moving onto a sugar cell with sugar, collect it
          if (bestMove instanceof SugarCell sugarCell) {
            int sugar = sugarCell.getSugar();
            if (sugar > 0) {
              agent.addSugar(sugar);
              sugarCell.setSugar(0);
            }
          }
          agent.setPosition(bestMove);
          bestMove.setNextState(SugarScapeState.AGENT);
        } else {
          currentCell.resetNextState();
        }
      } else {
        currentCell.resetNextState();
      }
      agent.metabolize();
    }
    simulation.getGrid().applyNextStates();
  }
}
