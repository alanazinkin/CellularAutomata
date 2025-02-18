package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Simulation;
import cellsociety.model.Grid;
import cellsociety.model.Cell;
import cellsociety.model.state.GameOfLifeState;
import cellsociety.model.StateInterface;
import java.util.List;
import java.util.Map;

/**
 * Represents Conway's Game of Life cellular automaton simulation.
 * <p>
 * Implements the following rules:
 * <ul>
 *   <li>Any live cell with 2-3 live neighbors survives</li>
 *   <li>Any dead cell with exactly 3 live neighbors becomes alive</li>
 *   <li>All other cells die or stay dead</li>
 * </ul>
 * Maintains strict encapsulation through private fields and final constants, with validation
 * for cell states and neighbor calculations.
 * </p>
 *
 * @author Tatum McKinnis
 */
public class GameOfLife extends Simulation {

  private static final int MINIMUM_LIVE_NEIGHBORS_FOR_SURVIVAL = 2;
  private static final int MAXIMUM_LIVE_NEIGHBORS_FOR_SURVIVAL = 3;
  private static final int EXACT_LIVE_NEIGHBORS_FOR_BIRTH = 3;

  /**
   * Constructs a Game of Life simulation with specified configuration and grid
   *
   * @param simulationConfig Simulation configuration parameters (parsed from XML)
   * @param grid             Initial grid layout containing cell states
   * @throws NullPointerException if either parameter is null (enforced by superclass)
   */
  public GameOfLife(SimulationConfig simulationConfig, Grid grid) {
    super(simulationConfig, grid);
  }

  /**
   * Applies Conway's Game of Life rules to all cells in the grid
   * <p>
   * Iterates through each cell, calculates its next state based on neighbor counts, and updates the
   * cell's next state. Actual grid update happens after all calculations.
   * </p>
   */
  @Override
  public void applyRules() {
    Grid grid = getGrid();
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        Cell cell = grid.getCell(r, c);
        validateCellState(cell);
        GameOfLifeState currentState = (GameOfLifeState) cell.getCurrentState();
        List<Cell> neighbors = grid.getNeighbors(r, c);
        int liveNeighbors = countLiveNeighbors(neighbors);
        GameOfLifeState nextState = determineNextState(currentState, liveNeighbors);
        cell.setNextState(nextState);
      }
    }
  }

  /**
   * Validates that a cell's current state is compatible with Game of Life rules
   *
   * @param cell Cell to validate
   * @throws IllegalStateException if cell contains an invalid state type
   */
  void validateCellState(Cell cell) {
    StateInterface state = cell.getCurrentState();
    if (!(state instanceof GameOfLifeState)) {
      throw new IllegalStateException("Invalid cell state for Game of Life: " + state.getClass());
    }
  }

  /**
   * Counts the number of alive neighbors using stream operations
   *
   * @param neighbors List of neighboring cells to evaluate
   * @return Number of neighbors in ALIVE state
   */
  int countLiveNeighbors(List<Cell> neighbors) {
    return (int) neighbors.stream()
        .filter(neighbor -> neighbor.getCurrentState() == GameOfLifeState.ALIVE)
        .count();
  }

  /**
   * Determines a cell's next state based on current state and neighbor count
   *
   * @param currentState  The cell's current state (ALIVE/DEAD)
   * @param liveNeighbors Number of alive neighbors
   * @return The calculated next state for the cell
   * @throws IllegalArgumentException if currentState is null
   */
  private static GameOfLifeState determineNextState(GameOfLifeState currentState,
      int liveNeighbors) {
    if (currentState == null) {
      throw new IllegalArgumentException("Cell state cannot be null");
    }
    if (currentState == GameOfLifeState.ALIVE) {
      return (liveNeighbors >= MINIMUM_LIVE_NEIGHBORS_FOR_SURVIVAL &&
          liveNeighbors <= MAXIMUM_LIVE_NEIGHBORS_FOR_SURVIVAL)
          ? GameOfLifeState.ALIVE : GameOfLifeState.DEAD;
    } else {
      return (liveNeighbors == EXACT_LIVE_NEIGHBORS_FOR_BIRTH)
          ? GameOfLifeState.ALIVE : GameOfLifeState.DEAD;
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * Game of Life implementation maps:
   * <ul>
   *   <li>ALIVE → Black</li>
   *   <li>DEAD → White</li>
   * </ul>
   * </p>
   */
  @Override
  public Map<StateInterface, String> initializeColorMap() {
    return Map.of(
        GameOfLifeState.ALIVE, "gameoflife-state-alive",
        GameOfLifeState.DEAD, "gameoflife-state-dead"
    );
  }

  /**
   * {@inheritDoc}
   * <p>
   * Game of Life implementation maps:
   * <ul>
   *   <li>0 → DEAD</li>
   *   <li>1 → ALIVE</li>
   * </ul>
   * </p>
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    return Map.of(
        0, GameOfLifeState.DEAD,
        1, GameOfLifeState.ALIVE
    );
  }
}


