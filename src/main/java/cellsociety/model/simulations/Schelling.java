package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.AgentCell;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.state.SchellingState;
import cellsociety.model.StateInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Objects;

/**
 * Implementation of Schelling's Model of Segregation using AgentCell.
 *
 * <p>The simulation models agents' movement patterns based on their satisfaction with neighborhood
 * composition. Agents will relocate if the fraction of similar neighbors falls below a specified
 * tolerance threshold.</p>
 *
 * <p>Key features:
 * <ul>
 *   <li>Agents belong to different social groups</li>
 *   <li>Empty cells act as available spaces for relocation</li>
 *   <li>Tolerance parameter controls satisfaction threshold</li>
 *   <li>Stochastic selection of relocation targets</li>
 * </ul>
 * </p>
 *
 * @author Tatum McKinnis
 */
public class Schelling extends Simulation {

  private static final int EMPTY_AGENT_GROUP = 0;
  private static final int NO_NEIGHBORS = 0;
  private static final int EMPTY_STATE_KEY = 0;
  private static final int AGENT_STATE_KEY = 1;

  private final double tolerance;
  private final Random randomNumGenerator;

  /**
   * Constructs a Schelling simulation with the specified parameters.
   *
   * @param simulationConfig Configuration settings for the simulation.
   * @param grid             The grid on which the simulation will run.
   * @param tolerance        The tolerance threshold for agent satisfaction (0.0 to 1.0).
   * @throws IllegalArgumentException if tolerance is outside the range [0.0, 1.0].
   */
  public Schelling(SimulationConfig simulationConfig, Grid grid, double tolerance) {
    super(simulationConfig, grid);
    validateTolerance(tolerance);

    convertGridToAgentCells(grid);

    this.tolerance = tolerance;
    this.randomNumGenerator = new Random();
  }

  /**
   * Converts all cells in the grid to AgentCells while preserving their states.
   *
   * @param grid the grid whose cells need to be converted
   */
  private void convertGridToAgentCells(Grid grid) {
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        Cell cell = grid.getCell(r, c);
        if (!(cell instanceof AgentCell) &&
            (cell.getCurrentState() == SchellingState.EMPTY_CELL ||
                cell.getCurrentState() == SchellingState.AGENT)) {
          int agentGroup = 0;
          if (cell.getCurrentState() == SchellingState.AGENT) {
            agentGroup = ((r + c) % 2) + 1;
          }
          AgentCell agentCell = new AgentCell(cell.getCurrentState(), agentGroup);
          grid.setCellAt(r, c, agentCell);
        }
      }
    }
  }
  /**
   * Validates the tolerance value to ensure it is within the acceptable range [0.0, 1.0].
   *
   * @param tolerance The tolerance value to validate.
   * @throws IllegalArgumentException if the tolerance is outside the valid range.
   */
  private void validateTolerance(double tolerance) {
    if (tolerance < 0.0 || tolerance > 1.0) {
      throw new IllegalArgumentException("Tolerance must be between 0.0 and 1.0.");
    }
  }

  /**
   * Initializes the color map for visualizing the Schelling model.
   *
   * @return A map of states to color strings.
   */
  @Override
  protected Map<StateInterface, String> initializeColorMap() {
    return Map.of(
        SchellingState.AGENT, "schelling-state-agent",
        SchellingState.EMPTY_CELL, "schelling-state-empty"
    );
  }

  /**
   * Initializes state counts for all states to 0.0
   */
  @Override
  public void initializeStateCounts() {
    Map<StateInterface, Double> stateCounts = getStateCounts();
    stateCounts.put(SchellingState.AGENT, 0.0);
    stateCounts.put(SchellingState.EMPTY_CELL, 0.0);
    setStateCounts(stateCounts);
  }

  /**
   * Initializes the state map, associating state keys with Schelling state objects.
   *
   * @return A map of state keys to SchellingState objects.
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    return Map.of(
        EMPTY_STATE_KEY, SchellingState.EMPTY_CELL,
        AGENT_STATE_KEY, SchellingState.AGENT,
        2, SchellingState.AGENT
    );
  }

  /**
   * Applies the rules of the Schelling model for one iteration of the simulation. This includes
   * identifying unsatisfied agents and moving them to available empty cells.
   */
  @Override
  public void applyRules() {
    List<Coordinate> emptyCells = collectEmptyCells();
    List<Move> moves = collectMoves(emptyCells);
    executeMoves(moves);
  }

  /**
   * Collects all empty cells in the grid.
   *
   * @return A list of coordinates for all empty cells.
   */
  private List<Coordinate> collectEmptyCells() {
    List<Coordinate> emptyCells = new ArrayList<>();
    for (int row = 0; row < getGrid().getRows(); row++) {
      for (int col = 0; col < getGrid().getCols(); col++) {
        AgentCell cell = getAgentCell(row, col);
        if (cell.getCurrentState() == SchellingState.EMPTY_CELL) {
          emptyCells.add(new Coordinate(row, col));
        }
      }
    }
    return emptyCells;
  }

  /**
   * Collects the moves for unsatisfied agents, randomly selecting empty cells for relocation.
   *
   * @param emptyCells A list of available empty cells.
   * @return A list of moves, each consisting of a source and destination coordinate.
   */
  private List<Move> collectMoves(List<Coordinate> emptyCells) {
    List<Move> moves = new ArrayList<>();
    List<Coordinate> unsatisfiedAgents = new ArrayList<>();

    for (int row = 0; row < getGrid().getRows(); row++) {
      for (int col = 0; col < getGrid().getCols(); col++) {
        AgentCell cell = getAgentCell(row, col);
        if (cell.getCurrentState() == SchellingState.AGENT &&
            !isAgentSatisfied(row, col, cell.getAgentGroup())) {
          unsatisfiedAgents.add(new Coordinate(row, col));
        }
      }
    }

    Collections.shuffle(unsatisfiedAgents, randomNumGenerator);
    Set<Coordinate> availableEmpties = new HashSet<>(emptyCells);

    for (Coordinate agentCoord : unsatisfiedAgents) {
      AgentCell cell = getAgentCell(agentCoord.row, agentCoord.col);
      if (!availableEmpties.isEmpty()) {
        Coordinate destination = availableEmpties.iterator().next();
        moves.add(new Move(agentCoord, destination, cell.getAgentGroup()));
        availableEmpties.remove(destination);
      }
    }

    return moves;
  }

  /**
   * Determines if the agent at the given coordinates is satisfied with its current neighborhood.
   *
   * @param row        The row of the agent's current position.
   * @param col        The column of the agent's current position.
   * @param agentGroup The group to which the agent belongs.
   * @return True if the agent is satisfied, false otherwise.
   */
  boolean isAgentSatisfied(int row, int col, int agentGroup) {
    List<AgentCell> neighbors = getAgentNeighbors(row, col);
    int totalAgents = countAgentNeighbors(neighbors);

    if (totalAgents == NO_NEIGHBORS) {
      return true;
    }

    int sameGroup = countSameGroupNeighbors(neighbors, agentGroup);
    double ratio = (double) sameGroup / totalAgents;
    return ratio >= tolerance;
  }

  /**
   * Executes the moves, updating the grid by marking source cells as empty and destination cells
   * with the relocated agents.
   *
   * @param moves A list of moves to execute.
   */
  private void executeMoves(List<Move> moves) {
    for (Move move : moves) {
      AgentCell sourceCell = getAgentCell(move.source.row, move.source.col);
      sourceCell.setNextState(SchellingState.EMPTY_CELL);
      sourceCell.setAgentGroup(EMPTY_AGENT_GROUP);
    }

    for (Move move : moves) {
      AgentCell destCell = getAgentCell(move.destination.row, move.destination.col);
      destCell.setNextState(SchellingState.AGENT);
      destCell.setAgentGroup(move.agentGroup);
    }

    getGrid().applyNextStates();
  }

  /**
   * Retrieves the agent cell at the specified coordinates.
   *
   * @param row The row of the desired cell.
   * @param col The column of the desired cell.
   * @return The AgentCell at the specified coordinates.
   * @throws ClassCastException if the cell at the specified coordinates is not an AgentCell.
   */
  private AgentCell getAgentCell(int row, int col) {
    Cell cell = getGrid().getCell(row, col);
    if (!(cell instanceof AgentCell)) {
      throw new ClassCastException(
          String.format("Cell at (%d,%d) is not an AgentCell", row, col));
    }
    return (AgentCell) cell;
  }

  /**
   * Retrieves a list of agent neighbors for the specified coordinates.
   *
   * @param row The row of the desired cell.
   * @param col The column of the desired cell.
   * @return A list of AgentCell neighbors.
   * @throws ClassCastException if any neighbor cell is not an AgentCell.
   */
  private List<AgentCell> getAgentNeighbors(int row, int col) {
    List<AgentCell> neighbors = new ArrayList<>();
    for (Cell cell : getGrid().getNeighbors(row, col)) {
      if (!(cell instanceof AgentCell)) {
        throw new ClassCastException("Neighbor cell is not an AgentCell");
      }
      neighbors.add((AgentCell) cell);
    }
    return neighbors;
  }

  /**
   * Counts the number of same-group neighbors for an agent.
   *
   * @param neighbors  A list of agent neighbors.
   * @param agentGroup The group to which the agent belongs.
   * @return The number of same-group neighbors.
   */
  private int countSameGroupNeighbors(List<AgentCell> neighbors, int agentGroup) {
    int count = 0;
    for (AgentCell neighbor : neighbors) {
      if (neighbor.getCurrentState() == SchellingState.AGENT &&
          neighbor.getAgentGroup() == agentGroup) {
        count++;
      }
    }
    return count;
  }

  /**
   * Counts the total number of agent neighbors.
   *
   * @param neighbors A list of agent neighbors.
   * @return The number of agent neighbors.
   */
  private int countAgentNeighbors(List<AgentCell> neighbors) {
    int count = 0;
    for (AgentCell neighbor : neighbors) {
      if (neighbor.getCurrentState() == SchellingState.AGENT) {
        count++;
      }
    }
    return count;
  }

  /**
   * Represents a coordinate in the grid with a row and column.
   */
  private static class Coordinate {

    final int row;
    final int col;

    /**
     * Constructs a coordinate with the specified row and column.
     *
     * @param row The row of the coordinate.
     * @param col The column of the coordinate.
     */
    Coordinate(int row, int col) {
      this.row = row;
      this.col = col;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Coordinate)) {
        return false;
      }
      Coordinate that = (Coordinate) o;
      return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
      return Objects.hash(row, col);
    }
  }

  /**
   * Represents a move from a source coordinate to a destination coordinate, along with the agent's
   * group information.
   */
  private static class Move {

    final Coordinate source;
    final Coordinate destination;
    final int agentGroup;

    /**
     * Constructs a move with the specified source, destination, and agent group.
     *
     * @param source      The source coordinate of the move.
     * @param destination The destination coordinate of the move.
     * @param agentGroup  The group to which the agent belongs.
     */
    Move(Coordinate source, Coordinate destination, int agentGroup) {
      this.source = source;
      this.destination = destination;
      this.agentGroup = agentGroup;
    }
  }
}
