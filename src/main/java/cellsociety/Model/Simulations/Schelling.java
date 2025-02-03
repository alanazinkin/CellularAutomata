package cellsociety.Model.Simulations;

import cellsociety.Model.AgentCell;
import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.State.SchellingState;
import cellsociety.Model.StateInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import cellsociety.Model.Cell;
import javafx.scene.paint.Color;

/**
 * Implementation of Schelling's Model of Segregation using AgentCell.
 *
 * <p>
 * Each simulation step checks every AgentCell to see if the agent is satisfied.
 * If not, the simulation searches for an empty cell where the agent would be satisfied.
 * When a candidate is found, the agent moves to the new location.
 * </p>
 */
public class Schelling extends Simulation {

  /**
   * The satisfaction threshold Bₐ.
   * An agent is satisfied if at least this fraction of its neighbors belong to the same group.
   */
  private final double tolerance;

  private final Random random;

  /**
   * Constructs a new SchellingSimulation with the specified grid and tolerance threshold.
   *
   * @param grid the simulation grid (populated with AgentCell instances)
   * @param tolerance the minimum fraction of same-group neighbors required for satisfaction
   */
  public Schelling(Grid grid, double tolerance) {
    super(grid);
    this.tolerance = tolerance;
    this.random = new Random();
  }

  /**
   * A helper class to record relocation moves.
   */
  private static class Move {
    int sourceRow;
    int sourceCol;
    int destRow;
    int destCol;
    int agentGroup;

    Move(int sourceRow, int sourceCol, int destRow, int destCol, int agentGroup) {
      this.sourceRow = sourceRow;
      this.sourceCol = sourceCol;
      this.destRow = destRow;
      this.destCol = destCol;
      this.agentGroup = agentGroup;
    }
  }

  @Override
  public Map<StateInterface, Color> initializeStateMap() {
    stateMap = new HashMap<>();
    stateMap.put(SchellingState.AGENT, Color.RED);
    stateMap.put(SchellingState.EMPTY_CELL, Color.BLUE);
    return stateMap;
  }

  /**
   * Applies Schelling's segregation rules:
   * <ol>
   *   <li>Examine each cell. For each AgentCell, determine whether the fraction of same-group
   *       neighbors meets the tolerance threshold.</li>
   *   <li>If not, find candidate empty cells (AgentCell with state EMPTY_CELL) where the agent
   *       would be satisfied.</li>
   *   <li>Choose one candidate at random (if available) and record the move.</li>
   *   <li>After processing all cells, update the grid's next states accordingly.</li>
   * </ol>
   */
  @Override
  public void applyRules() {
    List<Move> moves = new ArrayList<>();

    List<int[]> emptyCells = new ArrayList<>();
    int numRows = grid.getRows();
    int numCols = grid.getCols();
    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < numCols; col++) {
        AgentCell cell = (AgentCell) grid.getCell(row, col);
        if (cell.getState() == SchellingState.EMPTY_CELL) {
          emptyCells.add(new int[]{row, col});
        }
      }
    }

    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < numCols; col++) {
        AgentCell cell = (AgentCell) grid.getCell(row, col);
        if (cell.getState() == SchellingState.AGENT) {
          int agentGroup = cell.getAgentGroup();
          if (!isSatisfied(row, col, agentGroup)) {
            List<int[]> candidateCells = new ArrayList<>();
            for (int[] emptyCoord : emptyCells) {
              int emptyRow = emptyCoord[0];
              int emptyCol = emptyCoord[1];
              if (wouldBeSatisfied(emptyRow, emptyCol, agentGroup)) {
                candidateCells.add(emptyCoord);
              }
            }
            if (!candidateCells.isEmpty()) {
              int[] chosen = candidateCells.get(random.nextInt(candidateCells.size()));
              int destRow = chosen[0];
              int destCol = chosen[1];
              moves.add(new Move(row, col, destRow, destCol, agentGroup));
              removeEmptyCell(emptyCells, destRow, destCol);
            }
          }
        }
      }
    }

    for (Move move : moves) {
      AgentCell sourceCell = (AgentCell) grid.getCell(move.sourceRow, move.sourceCol);
      sourceCell.setNextState(SchellingState.EMPTY_CELL);
      sourceCell.setAgentGroup(-1);
    }
    for (Move move : moves) {
      AgentCell destCell = (AgentCell) grid.getCell(move.destRow, move.destCol);
      destCell.setNextState(SchellingState.AGENT);
      destCell.setAgentGroup(move.agentGroup);
    }

    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < numCols; col++) {
        Cell cell = grid.getCell(row, col);
        if (cell.getNextState().equals(cell.getState())) {
          cell.setNextState(cell.getState());
        }
      }
    }

    grid.applyNextStates();
  }



    /**
     * Checks if an agent at the given coordinates is satisfied with its neighborhood.
     *
     * @param row the row index of the agent
     * @param col the column index of the agent
     * @param agentGroup the group identifier for the agent
     * @return true if the fraction of same-group neighbors meets or exceeds the tolerance,
     *         or if there are no neighbors; false otherwise.
     */
  private boolean isSatisfied(int row, int col, int agentGroup) {
    List<AgentCell> neighbors = getAgentNeighbors(row, col);
    int sameGroupCount = 0;
    int agentNeighborCount = 0;
    for (AgentCell neighbor : neighbors) {
      if (neighbor.getState() == SchellingState.AGENT) {
        agentNeighborCount++;
        if (neighbor.getAgentGroup() == agentGroup) {
          sameGroupCount++;
        }
      }
    }
    if (agentNeighborCount == 0) {
      return true;
    }
    double fraction = (double) sameGroupCount / agentNeighborCount;
    return fraction >= tolerance;
  }

  /**
   * Determines if an agent of the specified group would be satisfied if placed at the given cell.
   *
   * @param row the row index of the candidate cell
   * @param col the column index of the candidate cell
   * @param agentGroup the group identifier for the agent
   * @return true if the agent would be satisfied at this cell, false otherwise.
   */
  private boolean wouldBeSatisfied(int row, int col, int agentGroup) {
    List<AgentCell> neighbors = getAgentNeighbors(row, col);
    int sameGroupCount = 0;
    int agentNeighborCount = 0;
    for (AgentCell neighbor : neighbors) {
      if (neighbor.getState() == SchellingState.AGENT) {
        agentNeighborCount++;
        if (neighbor.getAgentGroup() == agentGroup) {
          sameGroupCount++;
        }
      }
    }
    if (agentNeighborCount == 0) {
      return true;
    }
    double fraction = (double) sameGroupCount / agentNeighborCount;
    return fraction >= tolerance;
  }

  /**
   * Helper method to remove a coordinate from the list of empty cells.
   *
   * @param emptyCells the list of empty cell coordinates
   * @param row the row index of the cell to remove
   * @param col the column index of the cell to remove
   */
  private void removeEmptyCell(List<int[]> emptyCells, int row, int col) {
    emptyCells.removeIf(coord -> coord[0] == row && coord[1] == col);
  }

  /**
   * Retrieves the list of neighbors as AgentCell instances for a cell at (row, col).
   *
   * <p>
   * This method assumes that the grid's {@code getNeighbors} method returns a list of
   * generic Cell objects. We cast them to AgentCell. Ensure that the grid indeed contains
   * AgentCells for Schelling's simulation.
   * </p>
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return a list of neighboring AgentCells.
   */
  private List<AgentCell> getAgentNeighbors(int row, int col) {
    List<AgentCell> agentNeighbors = new ArrayList<>();
    List<Cell> neighbors = grid.getNeighbors(row, col);
    for (Cell cell : neighbors) {
      agentNeighbors.add((AgentCell) cell);
    }
    return agentNeighbors;
  }
}

