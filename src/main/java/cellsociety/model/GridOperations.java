package cellsociety.model;

import cellsociety.model.simulations.SugarScape;
import cellsociety.model.state.SugarScapeState;
import java.util.List;
import java.util.stream.Collectors;

public class GridOperations {

  /**
   * Returns a list of valid cells that the agent can move to.
   * Valid moves are those that are either EMPTY or contain SUGAR.
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
   * If two moves have the same sugar, the closer one (by Manhattan distance) is chosen.
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
   */
  public static int calculateDistance(Cell cell1, Cell cell2, SugarScape simulation) {
    Grid grid = simulation.getGrid();
    int[] pos1 = RulesHelper.getCellCoordinates(cell1, grid);
    int[] pos2 = RulesHelper.getCellCoordinates(cell2, grid);
    return Math.abs(pos1[0] - pos2[0]) + Math.abs(pos1[1] - pos2[1]);
  }

  /**
   * Returns a list of agents that are adjacent (Manhattan distance 1) to the given agent.
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
   * Finds an adjacent cell (from the agent's current position) that is in the EMPTY state.
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
