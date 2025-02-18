package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.StateInterface;
import cellsociety.model.state.SandState;
import java.util.HashMap;
import java.util.Map;

/**
 * Simulates granular materials like sand and water, implementing physics-based particle behavior.
 * <p>
 * This simulation allows particles to fall under gravity, spread, and interact with walls and other
 * particles. Particles exhibit realistic behavior such as: - Falling under gravity - Stacking and
 * forming piles - Flowing around obstacles - Different behaviors for sand and water particles
 * </p>
 *
 * @author Tatum McKinnis
 */
public class Sand extends Simulation {

  /**
   * Mapping of states to their visual color representations in hex format.
   */
  private static final Map<StateInterface, String> COLORS = Map.of(
      SandState.EMPTY, "sand-state-empty",
      SandState.SAND, "sand-state-sand",
      SandState.WALL, "sand-state-wall",
      SandState.WATER, "sand-state-water"
  );

  /**
   * Mapping of integer values to their corresponding states.
   */
  private static final Map<Integer, StateInterface> STATES = Map.of(
      0, SandState.EMPTY,
      1, SandState.SAND,
      2, SandState.WALL,
      3, SandState.WATER
  );

  /**
   * Constructs a new Sand with the specified configuration and grid.
   *
   * @param simulationConfig Initial configuration parameters
   * @param grid             The grid structure for the simulation
   * @throws IllegalArgumentException if grid is null or initial states are invalid
   */
  public Sand(SimulationConfig simulationConfig, Grid grid) {
    super(simulationConfig, grid);
  }

  /**
   * Initializes the color mapping for visualizing different states.
   *
   * @return A map associating each state with its display color
   */
  @Override
  protected Map<StateInterface, String> initializeColorMap() {
    return new HashMap<>(COLORS);
  }

  /**
   * Initializes the mapping between integer values and states.
   *
   * @return A map associating integer values with their corresponding states
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    return new HashMap<>(STATES);
  }

  /**
   * Applies the simulation rules to all cells in the grid.
   * <p>
   * The rules are applied from bottom to top to simulate gravity properly. Each row alternates
   * processing direction for more natural particle flow.
   * </p>
   */
  @Override
  protected void applyRules() {
    Grid grid = getGrid();
    for (int r = grid.getRows() - 1; r >= 0; r--) {
      if (r % 2 == 0) {
        for (int c = 0; c < grid.getCols(); c++) {
          processCell(r, c);
        }
      } else {
        for (int c = grid.getCols() - 1; c >= 0; c--) {
          processCell(r, c);
        }
      }
    }
  }

  /**
   * Processes a single cell according to particle physics rules.
   *
   * @param row The row of the cell to process
   * @param col The column of the cell to process
   */
  private void processCell(int row, int col) {
    Cell currentCell = getGrid().getCell(row, col);
    if (isMovableParticle(currentCell.getCurrentState())) {
      applyParticlePhysics(row, col, currentCell);
    }
  }

  /**
   * Checks if the given state represents a movable particle (sand or water).
   *
   * @param state The state to check
   * @return true if the state is sand or water
   */
  private boolean isMovableParticle(StateInterface state) {
    return state == SandState.SAND || state == SandState.WATER;
  }

  /**
   * Applies physics rules to a particle at the specified location.
   * <p>
   * The rules include: - Falling straight down if possible - Special water flow behavior - Diagonal
   * movement for both sand and water
   * </p>
   *
   * @param row  Current row of the particle
   * @param col  Current column of the particle
   * @param cell The cell containing the particle
   */
  private void applyParticlePhysics(int row, int col, Cell cell) {
    Grid grid = getGrid();
    StateInterface currentState = cell.getCurrentState();

    if (canMove(row + 1, col)) {
      moveParticle(cell, grid.getCell(row + 1, col));
      return;
    }

    if (currentState == SandState.WATER) {
      boolean tryLeftFirst = Math.random() < 0.5;
      if (tryLeftFirst) {
        if (tryWaterFlow(row, col, -1, cell) || tryWaterFlow(row, col, 1, cell)) {
          return;
        }
      } else {
        if (tryWaterFlow(row, col, 1, cell) || tryWaterFlow(row, col, -1, cell)) {
          return;
        }
      }
    }

    boolean tryLeftFirst = Math.random() < 0.5;
    if (tryLeftFirst) {
      if (canMove(row + 1, col - 1)) {
        moveParticle(cell, grid.getCell(row + 1, col - 1));
      } else if (canMove(row + 1, col + 1)) {
        moveParticle(cell, grid.getCell(row + 1, col + 1));
      }
    } else {
      if (canMove(row + 1, col + 1)) {
        moveParticle(cell, grid.getCell(row + 1, col + 1));
      } else if (canMove(row + 1, col - 1)) {
        moveParticle(cell, grid.getCell(row + 1, col - 1));
      }
    }
  }

  /**
   * Attempts to flow water particles horizontally in a given direction.
   *
   * @param row       Current row of the water particle
   * @param col       Current column of the water particle
   * @param direction Direction to try (-1 for left, 1 for right)
   * @param cell      The cell containing the water particle
   * @return true if water was able to flow
   */
  private boolean tryWaterFlow(int row, int col, int direction, Cell cell) {
    if (canMove(row, col + direction)) {
      moveParticle(cell, getGrid().getCell(row, col + direction));
      return true;
    }
    return false;
  }

  /**
   * Checks if a particle can move to the specified position.
   *
   * @param row Target row
   * @param col Target column
   * @return true if the position is valid and empty
   */
  private boolean canMove(int row, int col) {
    Grid grid = getGrid();
    return grid.isValidPosition(row, col) &&
        grid.getCell(row, col).getCurrentState() == SandState.EMPTY;
  }

  /**
   * Moves a particle from one cell to another.
   *
   * @param sourceCell The cell containing the particle
   * @param targetCell The cell to move the particle to
   */
  private void moveParticle(Cell sourceCell, Cell targetCell) {
    StateInterface particleState = sourceCell.getCurrentState();
    sourceCell.setNextState(SandState.EMPTY);
    targetCell.setNextState(particleState);
  }
}

