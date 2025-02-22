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
    for (Agent agent : agents) {
      List<Cell> validMoves = GridOperations.findValidMoves(agent, simulation);
      if (!validMoves.isEmpty()) {
        Cell bestMove = GridOperations.findBestMove(agent, validMoves, simulation);
        if (bestMove != null) {
          // Clear the agent's old cell.
          Cell oldCell = agent.getPosition();
          oldCell.setNextState(SugarScapeState.EMPTY);

          // If moving onto a sugar cell, collect its sugar.
          if (bestMove instanceof SugarCell sugarCell) {
            agent.addSugar(sugarCell.getSugar());
            sugarCell.setSugar(0);
          }
          agent.setPosition(bestMove);
          bestMove.setNextState(SugarScapeState.AGENT);
        }
      }
      agent.metabolize();
    }
    simulation.getGrid().applyNextStates();
  }
}

