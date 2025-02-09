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
 */
public class Schelling extends Simulation {

  private static final Color AGENT_COLOR = Color.RED;
  private static final Color EMPTY_CELL_COLOR = Color.BLUE;
  private static final int EMPTY_AGENT_GROUP = 0;
  private static final int NO_NEIGHBORS = 0;
  private static final int EMPTY_STATE_KEY = 0;
  private static final int AGENT_STATE_KEY = 1;

  /** Minimum fraction of similar neighbors required for satisfaction (0.0-1.0) */
  private final double tolerance;

  /** Random number generator for stochastic operations */
  private final Random randomNumGenerator;

  /**
   * Constructs a new Schelling segregation model simulation.
   *
   * @param simulationConfig Configuration parameters for simulation setup
   * @param grid The cellular grid structure for the simulation
   * @param tolerance Satisfaction threshold ratio (0.0-1.0 inclusive)
   * @throws IllegalArgumentException if tolerance is outside valid range
   * @throws NullPointerException if simulationConfig or grid are null
   */
  public Schelling(SimulationConfig simulationConfig, Grid grid, double tolerance) {
    super(simulationConfig, grid);
    validateTolerance(tolerance);
    this.tolerance = tolerance;
    this.randomNumGenerator = new Random();
  }

  /**
   * Validates that the tolerance value is within acceptable bounds.
   *
   * @param tolerance The tolerance value to validate
   * @throws IllegalArgumentException if tolerance is not between 0.0 and 1.0
   */
  private void validateTolerance(double tolerance) {
    if (tolerance < 0.0 || tolerance > 1.0) {
      throw new IllegalArgumentException("Tolerance must be between 0.0 and 1.0.");
    }
  }

  /**
   * Immutable representation of grid coordinates.
   */
  private static class Coordinate {
    private final int row;
    private final int col;

    /**
     * Creates a new Coordinate pair.
     *
     * @param row Grid row index
     * @param col Grid column index
     */
    public Coordinate(int row, int col) {
      this.row = row;
      this.col = col;
    }

    public int getRow() { return row; }

    public int getCol() { return col; }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Coordinate that = (Coordinate) o;
      return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
      return Objects.hash(row, col);
    }
  }

  /**
   * Represents a pending agent relocation operation.
   */
  private static class Move {
    /** Original location of the agent */
    private final Coordinate source;

    /** New destination for the agent */
    private final Coordinate destination;

    /** Social group identifier for the agent */
    private final int agentGroup;

    /**
     * Creates a relocation record.
     *
     * @param source Original agent location
     * @param destination Target location for relocation
     * @param agentGroup Social group identifier
     */
    private Move(Coordinate source, Coordinate destination, int agentGroup) {
      this.source = source;
      this.destination = destination;
      this.agentGroup = agentGroup;
    }

    /**
     * @return Source coordinate of the move
     */
    public Coordinate getSource() { return source; }

    /**
     * @return Destination coordinate of the move
     */
    public Coordinate getDestination() { return destination; }

    /**
     * @return Agent's social group identifier
     */
    public int getAgentGroup() { return agentGroup; }
  }

  /**
   * {@inheritDoc}
   *
   * @return Color mapping specific to Schelling simulation states
   */
  @Override
  public Map<StateInterface, Color> initializeColorMap() {
    return Map.of(
        SchellingState.AGENT, AGENT_COLOR,
        SchellingState.EMPTY_CELL, EMPTY_CELL_COLOR
    );
  }

  /**
   * {@inheritDoc}
   *
   * @return State mapping for simulation configuration
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    return Map.of(
        EMPTY_STATE_KEY, SchellingState.EMPTY_CELL,
        AGENT_STATE_KEY, SchellingState.AGENT
    );
  }

  /**
   * {@inheritDoc}
   *
   * <p>Executes one iteration of the Schelling simulation:
   * 1. Identify unsatisfied agents
   * 2. Find suitable relocation targets
   * 3. Process all moves simultaneously
   * </p>
   */
  @Override
  public void applyRules() {
    List<Coordinate> emptyCells = collectEmptyCells();
    List<Move> moves = collectMoves(emptyCells);
    executeMoves(moves);
    getGrid().applyNextStates();
  }

  /**
   * Compiles a list of all currently empty cell coordinates.
   *
   * @return List of empty cell locations
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
   * Collects all valid relocation moves for unsatisfied agents.
   *
   * @param emptyCells List of currently available empty cells
   * @return List of pending relocation operations
   */
  private List<Move> collectMoves(List<Coordinate> emptyCells) {
    List<Move> moves = new ArrayList<>();
    for (int row = 0; row < getGrid().getRows(); row++) {
      for (int col = 0; col < getGrid().getCols(); col++) {
        processCellForMovement(row, col, emptyCells, moves);
      }
    }
    return moves;
  }

  /**
   * Evaluates and processes relocation possibilities for a single cell.
   *
   * @param row Row index of cell to evaluate
   * @param col Column index of cell to evaluate
   * @param emptyCells List of available empty cells
   * @param moves Collection to record valid moves
   */
  private void processCellForMovement(int row, int col, List<Coordinate> emptyCells, List<Move> moves) {
    AgentCell cell = getAgentCell(row, col);
    if (cell.getCurrentState() == SchellingState.AGENT) {
      attemptAgentRelocation(row, col, cell.getAgentGroup(), emptyCells, moves);
    }
  }

  /**
   * Attempts to relocate an unsatisfied agent.
   *
   * @param row Current row of the agent
   * @param col Current column of the agent
   * @param agentGroup Social group identifier
   * @param emptyCells List of available empty cells
   * @param moves Collection to record successful moves
   */
  private void attemptAgentRelocation(int row, int col, int agentGroup, List<Coordinate> emptyCells,
      List<Move> moves) {
    if (!isAgentSatisfied(row, col, agentGroup)) {
      List<Coordinate> candidates = findSatisfyingCandidates(emptyCells, agentGroup);
      if (!candidates.isEmpty()) {
        Coordinate destination = selectRandomCandidate(candidates);
        recordMove(row, col, agentGroup, destination, emptyCells, moves);
      }
    }
  }

  /**
   * Identifies empty cells that would satisfy the agent's tolerance requirement.
   *
   * @param emptyCells List of potential relocation targets
   * @param agentGroup Social group identifier for the agent
   * @return List of suitable candidate locations
   */
  private List<Coordinate> findSatisfyingCandidates(List<Coordinate> emptyCells, int agentGroup) {
    List<Coordinate> candidates = new ArrayList<>();
    for (Coordinate coord : emptyCells) {
      if (isAgentSatisfied(coord.getRow(), coord.getCol(), agentGroup)) {
        candidates.add(coord);
      }
    }
    return candidates;
  }

  /**
   * Randomly selects a candidate from valid relocation targets.
   *
   * @param candidates List of suitable empty cells
   * @return Randomly chosen destination coordinate
   */
  private Coordinate selectRandomCandidate(List<Coordinate> candidates) {
    return candidates.get(randomNumGenerator.nextInt(candidates.size()));
  }

  /**
   * Records a relocation operation and updates available empty cells.
   *
   * @param row Original row of agent
   * @param col Original column of agent
   * @param agentGroup Social group identifier
   * @param destination Chosen relocation target
   * @param emptyCells List of available empty cells (will be modified)
   * @param moves Collection of pending moves (will be modified)
   */
  private void recordMove(int row, int col, int agentGroup, Coordinate destination,
      List<Coordinate> emptyCells, List<Move> moves) {
    moves.add(new Move(new Coordinate(row, col), destination, agentGroup));
    emptyCells.remove(destination);
  }

  /**
   * Executes all pending relocation moves in two phases:
   * 1. Clear original agent locations
   * 2. Populate new agent locations
   *
   * @param moves List of relocation operations to execute
   */
  private void executeMoves(List<Move> moves) {
    clearSourceCells(moves);
    populateDestinationCells(moves);
  }

  /**
   * Converts source cells to empty after agent departure.
   *
   * @param moves List of relocation operations
   */
  private void clearSourceCells(List<Move> moves) {
    moves.forEach(move -> {
      AgentCell sourceCell = getAgentCell(move.getSource().getRow(), move.getSource().getCol());
      sourceCell.setNextState(SchellingState.EMPTY_CELL);
      sourceCell.setAgentGroup(EMPTY_AGENT_GROUP);
    });
  }

  /**
   * Places agents in their new locations.
   *
   * @param moves List of relocation operations
   */
  private void populateDestinationCells(List<Move> moves) {
    moves.forEach(move -> {
      AgentCell destCell = getAgentCell(move.getDestination().getRow(), move.getDestination().getCol());
      destCell.setNextState(SchellingState.AGENT);
      destCell.setAgentGroup(move.getAgentGroup());
    });
  }

  /**
   * Determines if an agent is satisfied with their current neighborhood.
   *
   * @param row Agent's current row
   * @param col Agent's current column
   * @param agentGroup Social group identifier
   * @return true if agent is satisfied, false otherwise
   */
  private boolean isAgentSatisfied(int row, int col, int agentGroup) {
    List<AgentCell> neighbors = getAgentNeighbors(row, col);
    int sameGroup = countSameGroupNeighbors(neighbors, agentGroup);
    int totalAgents = countAgentNeighbors(neighbors);

    return totalAgents == NO_NEIGHBORS || (double) sameGroup / totalAgents >= tolerance;
  }

  /**
   * Counts neighbors belonging to the same social group.
   *
   * @param neighbors List of neighboring cells
   * @param agentGroup Social group identifier to match
   * @return Number of neighbors in the same group
   */
  private int countSameGroupNeighbors(List<AgentCell> neighbors, int agentGroup) {
    return (int) neighbors.stream()
        .filter(n -> n.getCurrentState() == SchellingState.AGENT && n.getAgentGroup() == agentGroup)
        .count();
  }

  /**
   * Counts total number of agent neighbors (excluding empty cells).
   *
   * @param neighbors List of neighboring cells
   * @return Total number of neighboring agents
   */
  private int countAgentNeighbors(List<AgentCell> neighbors) {
    return (int) neighbors.stream()
        .filter(n -> n.getCurrentState() == SchellingState.AGENT)
        .count();
  }


  /**
   * Retrieves an AgentCell from the specified grid location with type safety.
   *
   * <p>This method performs an explicit cast to AgentCell and throws a detailed exception
   * if the grid contains incompatible cell types, ensuring the simulation maintains
   * type consistency.</p>
   *
   * @param row the row index of the target cell
   * @param col the column index of the target cell
   * @return AgentCell at specified grid coordinates
   * @throws ClassCastException if the grid contains a non-AgentCell at the specified
   *         location, with error message containing the problematic coordinates
   */
  private AgentCell getAgentCell(int row, int col) {
    Cell cell = getGrid().getCell(row, col);
    try {
      return (AgentCell) cell;
    } catch (ClassCastException e) {
      throw new ClassCastException("Non-AgentCell at (" + row + "," + col + "): " + e.getMessage());
    }
  }

  /**
   * Retrieves neighboring cells as AgentCell instances for segregation analysis.
   *
   * <p>This method relies on Java's native casting mechanism to ensure all neighbors
   * are valid AgentCells. Any non-AgentCell neighbors will trigger an exception
   * during the casting process.</p>
   *
   * @param row center cell's row position
   * @param col center cell's column position
   * @return List of neighboring AgentCells required for satisfaction calculation
   * @throws ClassCastException if any neighboring cell cannot be cast to AgentCell,
   *         indicating invalid cell types in the simulation grid
   */
  private List<AgentCell> getAgentNeighbors(int row, int col) {
    List<AgentCell> neighbors = new ArrayList<>();
    for (Cell cell : getGrid().getNeighbors(row, col)) {
      neighbors.add((AgentCell) cell);
    }
    return neighbors;
  }
}