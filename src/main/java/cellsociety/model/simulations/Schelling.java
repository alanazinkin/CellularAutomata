package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.AgentCell;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.state.SchellingState;
import cellsociety.model.StateInterface;
import java.util.Collections;
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

  public Schelling(SimulationConfig simulationConfig, Grid grid, double tolerance) {
    super(simulationConfig, grid);
    validateTolerance(tolerance);
    this.tolerance = tolerance;
    this.randomNumGenerator = new Random();
  }

  private void validateTolerance(double tolerance) {
    if (tolerance < 0.0 || tolerance > 1.0) {
      throw new IllegalArgumentException("Tolerance must be between 0.0 and 1.0.");
    }
  }

  @Override
  protected Map<StateInterface, String> initializeColorMap() {
    return Map.of(
        SchellingState.AGENT, "schelling-state-agent",
        SchellingState.EMPTY_CELL, "schelling-state-empty"
    );
  }

  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    return Map.of(
        EMPTY_STATE_KEY, SchellingState.EMPTY_CELL,
        AGENT_STATE_KEY, SchellingState.AGENT,
        2, SchellingState.AGENT
    );
  }

  @Override
  public void applyRules() {
    List<Coordinate> emptyCells = collectEmptyCells();
    List<Move> moves = collectMoves(emptyCells);
    executeMoves(moves);
  }

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

  private List<Move> collectMoves(List<Coordinate> emptyCells) {
    List<Move> moves = new ArrayList<>();
    List<Coordinate> unsatisfiedAgents = new ArrayList<>();

    // First pass: find all unsatisfied agents
    for (int row = 0; row < getGrid().getRows(); row++) {
      for (int col = 0; col < getGrid().getCols(); col++) {
        AgentCell cell = getAgentCell(row, col);
        if (cell.getCurrentState() == SchellingState.AGENT &&
            !isAgentSatisfied(row, col, cell.getAgentGroup())) {
          unsatisfiedAgents.add(new Coordinate(row, col));
        }
      }
    }

    // Shuffle for random processing
    Collections.shuffle(unsatisfiedAgents, randomNumGenerator);
    Set<Coordinate> availableEmpties = new HashSet<>(emptyCells);

    // Find moves for unsatisfied agents
    for (Coordinate agentCoord : unsatisfiedAgents) {
      AgentCell cell = getAgentCell(agentCoord.row, agentCoord.col);
      // Always try to move unsatisfied agent to first available empty cell
      if (!availableEmpties.isEmpty()) {
        Coordinate destination = availableEmpties.iterator().next();
        moves.add(new Move(agentCoord, destination, cell.getAgentGroup()));
        availableEmpties.remove(destination);
      }
    }

    return moves;
  }

  boolean isAgentSatisfied(int row, int col, int agentGroup) {
    List<AgentCell> neighbors = getAgentNeighbors(row, col);
    int totalAgents = countAgentNeighbors(neighbors);

    // If no neighbors, agent is satisfied
    if (totalAgents == NO_NEIGHBORS) {
      return true;
    }

    // Calculate satisfaction based on number of same-group neighbors
    int sameGroup = countSameGroupNeighbors(neighbors, agentGroup);
    double ratio = (double) sameGroup / totalAgents;
    return ratio >= tolerance;
  }

  private void executeMoves(List<Move> moves) {
    // First mark all source cells as empty in their next state
    for (Move move : moves) {
      AgentCell sourceCell = getAgentCell(move.source.row, move.source.col);
      sourceCell.setNextState(SchellingState.EMPTY_CELL);
      sourceCell.setAgentGroup(EMPTY_AGENT_GROUP);
    }

    // Then mark all destination cells with the moving agents
    for (Move move : moves) {
      AgentCell destCell = getAgentCell(move.destination.row, move.destination.col);
      destCell.setNextState(SchellingState.AGENT);
      destCell.setAgentGroup(move.agentGroup);
    }
  }

  private AgentCell getAgentCell(int row, int col) {
    Cell cell = getGrid().getCell(row, col);
    if (!(cell instanceof AgentCell)) {
      throw new ClassCastException(
          String.format("Cell at (%d,%d) is not an AgentCell", row, col));
    }
    return (AgentCell) cell;
  }

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

  private int countAgentNeighbors(List<AgentCell> neighbors) {
    int count = 0;
    for (AgentCell neighbor : neighbors) {
      if (neighbor.getCurrentState() == SchellingState.AGENT) {
        count++;
      }
    }
    return count;
  }

  private static class Coordinate {
    final int row;
    final int col;

    Coordinate(int row, int col) {
      this.row = row;
      this.col = col;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Coordinate)) return false;
      Coordinate that = (Coordinate) o;
      return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
      return Objects.hash(row, col);
    }
  }

  private static class Move {
    final Coordinate source;
    final Coordinate destination;
    final int agentGroup;

    Move(Coordinate source, Coordinate destination, int agentGroup) {
      this.source = source;
      this.destination = destination;
      this.agentGroup = agentGroup;
    }
  }
}