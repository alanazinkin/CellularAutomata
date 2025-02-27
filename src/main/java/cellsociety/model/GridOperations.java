package cellsociety.model;

import cellsociety.model.simulations.SugarScape;
import cellsociety.model.state.SugarScapeState;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides various grid operations used within the simulation.
 * These include finding valid moves for agents, calculating distances,
 * and identifying neighboring cells and agents.
 * @author Tatum McKinnis
 */
public class GridOperations {

  /**
   * Returns a list of valid cells that the agent can move to.
   * Valid moves are those that are either in the EMPTY state or contain SUGAR.
   *
   * @param agent the agent whose valid moves are to be determined.
   * @param simulation the SugarScape simulation instance containing the grid.
   * @return a list of cells representing valid moves for the agent.
   */
  public static List<Cell> findValidMoves(Agent agent, SugarScape simulation) {
    Grid grid = simulation.getGrid();
    int[] coords = RulesHelper.getCellCoordinates(agent.getPosition(), grid);
    return grid.getNeighbors(coords[0], coords[1]).stream()
        .filter(cell -> cell.getCurrentState() == SugarScapeState.EMPTY ||
            cell.getCurrentState() == SugarScapeState.SUGAR)
        .collect(Collectors.toList());
  }

  /**
   * From the list of valid moves, selects the best move based on sugar availability.
   * If two moves have the same amount of sugar, the one closer to the agent (based on Manhattan distance) is chosen.
   *
   * @param agent the agent for which the best move is being determined.
   * @param validMoves the list of valid cells the agent can move to.
   * @param simulation the SugarScape simulation instance providing grid details.
   * @return the cell representing the best move, or {@code null} if no valid move exists.
   */
  public static Cell findBestMove(Agent agent, List<Cell> validMoves, SugarScape simulation) {
    return validMoves.stream()
        .filter(cell -> cell instanceof SugarCell)
        .max((cell1, cell2) -> {
          SugarCell sugar1 = (SugarCell) cell1;
          SugarCell sugar2 = (SugarCell) cell2;
          int cmp = Integer.compare(sugar1.getSugar(), sugar2.getSugar());
          if (cmp != 0) return cmp;
          return Integer.compare(-calculateDistance(agent.getPosition(), cell1, simulation),
              -calculateDistance(agent.getPosition(), cell2, simulation));
        })
        .orElse(null);
  }

  /**
   * Calculates the Manhattan distance between two cells.
   *
   * @param cell1 the first cell.
   * @param cell2 the second cell.
   * @param simulation the SugarScape simulation instance containing the grid.
   * @return the Manhattan distance between {@code cell1} and {@code cell2}.
   */
  public static int calculateDistance(Cell cell1, Cell cell2, SugarScape simulation) {
    Grid grid = simulation.getGrid();
    int[] pos1 = RulesHelper.getCellCoordinates(cell1, grid);
    int[] pos2 = RulesHelper.getCellCoordinates(cell2, grid);
    return Math.abs(pos1[0] - pos2[0]) + Math.abs(pos1[1] - pos2[1]);
  }

  /**
   * Returns a list of agents that are adjacent (with a Manhattan distance of 1) to the given agent.
   *
   * @param agent the agent whose neighbors are being determined.
   * @param simulation the SugarScape simulation instance containing the grid and agents.
   * @return a list of agents that are directly adjacent to the given agent.
   */
  public static List<Agent> getAgentNeighbors(Agent agent, SugarScape simulation) {
    Grid grid = simulation.getGrid();
    int[] agentPos = RulesHelper.getCellCoordinates(agent.getPosition(), grid);

    return simulation.getAgents().stream()
        .filter(other -> other != agent)
        .filter(other -> {
          int[] otherPos = RulesHelper.getCellCoordinates(other.getPosition(), grid);
          int distance = Math.abs(agentPos[0] - otherPos[0]) + Math.abs(agentPos[1] - otherPos[1]);
          return distance == 1;
        })
        .collect(Collectors.toList());
  }

  /**
   * Finds an adjacent cell (relative to the agent's current position) that is in the EMPTY state.
   *
   * @param agent the agent for which to find an adjacent empty cell.
   * @param simulation the SugarScape simulation instance containing the grid.
   * @return the first adjacent cell in the EMPTY state, or {@code null} if none is found.
   */
  public static Cell findAdjacentEmptyCell(Agent agent, SugarScape simulation) {
    Grid grid = simulation.getGrid();
    int[] coords = RulesHelper.getCellCoordinates(agent.getPosition(), grid);
    return grid.getNeighbors(coords[0], coords[1]).stream()
        .filter(cell -> cell.getCurrentState() == SugarScapeState.EMPTY)
        .findFirst()
        .orElse(null);
  }
}
