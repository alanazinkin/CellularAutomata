package cellsociety.Model.Simulations;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.AgentCell;
import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.State.SchellingState;
import cellsociety.Model.StateInterface;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

  private static final Color AGENT_COLOR = Color.RED;
  private static final Color EMPTY_CELL_COLOR = Color.BLUE;
  private static final int EMPTY_AGENT_GROUP = 0;
  private static final int NO_NEIGHBORS = 0;

  // The satisfaction threshold: an agent is satisfied if at least this fraction of its neighbors
  // belong to the same group.
  private final double tolerance;

  private final Random random;

  /**
   * Constructs a new {@code Schelling} simulation with the specified configuration, grid, and tolerance.
   *
   * The constructor initializes the simulation with the provided {@link SimulationConfig}, {@link Grid},
   * and tolerance value. The tolerance determines the proportion of similar neighbors an agent requires
   * to be content in its location. The tolerance value must be between 0.0 and 1.0, inclusive.
   *
   * @param simulationConfig the configuration for the simulation (must not be {@code null})
   * @param grid the grid for the simulation (must not be {@code null})
   * @param tolerance the tolerance for similarity of neighbors (must be between 0.0 and 1.0)
   * @throws IllegalArgumentException if {@code tolerance} is less than 0.0 or greater than 1.0
   */
  public Schelling(SimulationConfig simulationConfig, Grid grid, double tolerance) {
    super(simulationConfig, grid);
    if (tolerance < 0.0 || tolerance > 1.0) {
      throw new IllegalArgumentException("Tolerance must be between 0.0 and 1.0.");
    }
    this.tolerance = tolerance;
    this.random = new Random();
  }

  /**
   * A helper class to record relocation moves.
   */
  private static class Move {
    private final int sourceRow;
    private final int sourceCol;
    private final int destRow;
    private final int destCol;
    private final int agentGroup;

    private Move(int sourceRow, int sourceCol, int destRow, int destCol, int agentGroup) {
      this.sourceRow = sourceRow;
      this.sourceCol = sourceCol;
      this.destRow = destRow;
      this.destCol = destCol;
      this.agentGroup = agentGroup;
    }
  }

  /**
   * Initializes the color map for Schelling simulation.
   *
   * @return the map of simulation states to colors.
   */
  @Override
  public Map<StateInterface, Color> initializeColorMap() {
    Map<StateInterface, Color> colorMap = new HashMap<>();
    colorMap.put(SchellingState.AGENT, AGENT_COLOR);
    colorMap.put(SchellingState.EMPTY_CELL, EMPTY_CELL_COLOR);
    return colorMap;
  }

  /**
   * Initializes the state map for Schelling simulation.
   *
   * @return the map of simulation states to colors.
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    Map<Integer, StateInterface> stateMap = new HashMap<>();
    stateMap.put(0, SchellingState.EMPTY_CELL);
    stateMap.put(1, SchellingState.AGENT);
    return stateMap;
  }

  /**
   * Applies Schelling's segregation rules.
   *
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
    final int numRows = getGrid().getRows();
    final int numCols = getGrid().getCols();

    List<Move> moves = new ArrayList<>();
    List<int[]> emptyCells = getEmptyCells(numRows, numCols);

    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < numCols; col++) {
        AgentCell cell = (AgentCell) getGrid().getCell(row, col);
        if (cell.getCurrentState() == SchellingState.AGENT) {
          int agentGroup = cell.getAgentGroup();
          if (!isAgentSatisfied(row, col, agentGroup)) {
            List<int[]> candidateCells = getCandidateCells(emptyCells, agentGroup);
            if (!candidateCells.isEmpty()) {
              int[] chosenCell = candidateCells.get(random.nextInt(candidateCells.size()));
              moves.add(new Move(row, col, chosenCell[0], chosenCell[1], agentGroup));
              removeEmptyCell(emptyCells, chosenCell[0], chosenCell[1]);
            }
          }
        }
      }
    }

    executeMoves(moves);
    getGrid().applyNextStates();
  }

  /**
   * Returns a list of coordinates (row, col) for cells that are empty.
   *
   * @param numRows number of rows in the grid.
   * @param numCols number of columns in the grid.
   * @return list of empty cell coordinates.
   */
  private List<int[]> getEmptyCells(int numRows, int numCols) {
    List<int[]> emptyCells = new ArrayList<>();
    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < numCols; col++) {
        AgentCell cell = (AgentCell) getGrid().getCell(row, col);
        if (cell.getCurrentState() == SchellingState.EMPTY_CELL) {
          emptyCells.add(new int[]{row, col});
        }
      }
    }
    return emptyCells;
  }

  /**
   * Returns a list of candidate empty cell coordinates where an agent of the specified group
   * would be satisfied.
   *
   * @param emptyCells the list of current empty cell coordinates.
   * @param agentGroup the agent's group identifier.
   * @return list of candidate cell coordinates.
   */
  private List<int[]> getCandidateCells(List<int[]> emptyCells, int agentGroup) {
    List<int[]> candidateCells = new ArrayList<>();
    for (int[] coord : emptyCells) {
      if (isAgentSatisfied(coord[0], coord[1], agentGroup)) {
        candidateCells.add(coord);
      }
    }
    return candidateCells;
  }

  /**
   * Executes the list of moves by updating the source and destination cells.
   *
   * @param moves list of moves to execute.
   */
  private void executeMoves(List<Move> moves) {
    for (Move move : moves) {
      AgentCell sourceCell = (AgentCell) getGrid().getCell(move.sourceRow, move.sourceCol);
      sourceCell.setNextState(SchellingState.EMPTY_CELL);
      sourceCell.setAgentGroup(EMPTY_AGENT_GROUP);
    }
    for (Move move : moves) {
      AgentCell destCell = (AgentCell) getGrid().getCell(move.destRow, move.destCol);
      destCell.setNextState(SchellingState.AGENT);
      destCell.setAgentGroup(move.agentGroup);
    }
  }

  /**
   * Checks whether an agent at the given position with the specified group would be satisfied
   * with its neighbors. This method is used for both existing agents and hypothetical moves.
   *
   * @param row the row index.
   * @param col the column index.
   * @param agentGroup the group identifier for the agent.
   * @return true if the agent is satisfied; false otherwise.
   */
  private boolean isAgentSatisfied(int row, int col, int agentGroup) {
    List<AgentCell> neighbors = getAgentNeighbors(row, col);
    int sameGroupCount = 0;
    int agentNeighborCount = 0;

    for (AgentCell neighbor : neighbors) {
      if (neighbor.getCurrentState() == SchellingState.AGENT) {
        agentNeighborCount++;
        if (neighbor.getAgentGroup() == agentGroup) {
          sameGroupCount++;
        }
      }
    }

    if (agentNeighborCount == NO_NEIGHBORS) {
      return true;
    }

    double satisfactionFraction = (double) sameGroupCount / agentNeighborCount;
    return satisfactionFraction >= tolerance;
  }

  /**
   * Removes a cell from the list of empty cells.
   *
   * @param emptyCells the list of empty cell coordinates.
   * @param row the row index of the cell to remove.
   * @param col the column index of the cell to remove.
   */
  private void removeEmptyCell(List<int[]> emptyCells, int row, int col) {
    emptyCells.removeIf(coord -> coord[0] == row && coord[1] == col);
  }

  /**
   * Retrieves the list of neighbors for a cell at the specified position and casts them to AgentCell.
   *
   * @param row the row index of the cell.
   * @param col the column index of the cell.
   * @return a list of neighboring AgentCells.
   */
  private List<AgentCell> getAgentNeighbors(int row, int col) {
    List<AgentCell> agentNeighbors = new ArrayList<>();
    List<Cell> neighbors = getGrid().getNeighbors(row, col);
    for (Cell cell : neighbors) {
      agentNeighbors.add((AgentCell) cell);
    }
    return agentNeighbors;
  }
}

