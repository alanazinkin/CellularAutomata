package cellsociety.model;

import cellsociety.model.state.SugarScapeState;
import cellsociety.model.simulations.SugarScape;
import java.util.List;

/**
 * Manages movement operations for agents within the SugarScape simulation.
 * <p>
 * This class is responsible for applying movement logic to agents, including validating positions,
 * selecting valid moves based on available sugar and distance, updating cell states, and triggering
 * agents' metabolism. After processing the agents, pending grid state changes are applied.
 * </p>
 * @author Tatum McKinnis
 */
public class MovementManager {
  private final SugarScape simulation;

  /**
   * Constructs a new MovementManager for the specified SugarScape simulation.
   *
   * @param simulation the SugarScape simulation instance used to retrieve the grid and agents.
   */
  public MovementManager(SugarScape simulation) {
    this.simulation = simulation;
  }

  /**
   * Applies movement logic for each agent in the provided list.
   * <p>
   * For each agent, the following steps are executed:
   * <ul>
   *   <li>Checks that the agents list and the agent's position are not null.</li>
   *   <li>Retrieves the current cell of the agent and determines valid moves (cells that are either EMPTY or contain SUGAR).</li>
   *   <li>Selects the best move based on sugar availability and proximity (using Manhattan distance) from the valid moves.</li>
   *   <li>If a valid best move is found:
   *     <ul>
   *       <li>The current cell's next state is set to EMPTY.</li>
   *       <li>If the destination cell is a SugarCell and contains sugar, the agent collects the sugar and the cell's sugar is reset to 0.</li>
   *       <li>The agent's position is updated to the new cell and the new cell's next state is set to AGENT.</li>
   *     </ul>
   *   </li>
   *   <li>If no valid move is found, the current cell's next state is reset.</li>
   *   <li>The agent metabolizes after attempting movement.</li>
   *   <li>After processing all agents, the grid applies all pending next states.</li>
   * </ul>
   * </p>
   *
   * @param agents the list of agents to move.
   * @throws IllegalArgumentException if the agents list is {@code null} or if any agent's position is {@code null}.
   */
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
          currentCell.setNextState(SugarScapeState.EMPTY);

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
