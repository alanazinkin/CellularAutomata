package cellsociety.Model.Simulations;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.Simulation;
import cellsociety.Model.Grid;
import cellsociety.Model.Cell;
import cellsociety.Model.State.GameOfLifeState;
import cellsociety.Model.StateInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.paint.Color;

/**
 * Represents the Game of Life simulation.
 * The rules of the Game of Life are applied to each cell in the grid.
 */
public class GameOfLife extends Simulation {

  private static final int MINIMUM_LIVE_NEIGHBORS_FOR_SURVIVAL = 2;
  private static final int MAXIMUM_LIVE_NEIGHBORS_FOR_SURVIVAL = 3;
  private static final int EXACT_LIVE_NEIGHBORS_FOR_BIRTH = 3;

  private static final Color ALIVE_COLOR = Color.BLACK;
  private static final Color DEAD_COLOR = Color.WHITE;

  /**
   * Constructs a new {@code GameOfLife} object with the specified simulation configuration and grid.
   *
   * The constructor initializes the simulation with the provided {@link SimulationConfig} and {@link Grid}.
   * Both parameters must be non-null, as they represent the configuration and the grid for the simulation.
   *
   * @param simulationConfig the configuration for the simulation (must not be {@code null})
   * @param grid the grid for the simulation (must not be {@code null})
   * @throws IllegalArgumentException if either {@code simulationConfig} or {@code grid} is {@code null}
   */
  public GameOfLife(SimulationConfig simulationConfig, Grid grid) {
    super(simulationConfig, grid);
    if (simulationConfig == null) {
      throw new IllegalArgumentException("SimulationConfig cannot be null.");
    }
  }

  /**
   * Applies the Game of Life rules to the grid.
   * This method updates the next states of the cells based on their neighbors.
   */
  @Override
  public void applyRules() {
    for (int r = 0; r < getGrid().getRows(); r++) {
      for (int c = 0; c < getGrid().getCols(); c++) {
        Cell cell = getGrid().getCell(r, c);
        List<Cell> neighbors = getGrid().getNeighbors(r, c);
        int liveNeighbors = countLiveNeighbors(neighbors);
        GameOfLifeState nextState = determineNextState((GameOfLifeState) cell.getCurrentState(), liveNeighbors);
        cell.setNextState(nextState);
      }
    }
  }

  /**
   * Counts the number of live neighbors in the provided list of cells.
   *
   * @param neighbors the list of neighboring cells
   * @return the count of live neighbors
   */
  private int countLiveNeighbors(List<Cell> neighbors) {
    int liveCount = 0;
    for (Cell neighbor : neighbors) {
      if (neighbor.getCurrentState() == GameOfLifeState.ALIVE) {
        liveCount++;
      }
    }
    return liveCount;
  }

  /**
   * Determines the next state of a cell based on its current state and the number of live neighbors.
   *
   * @param currentState the current state of the cell
   * @param liveNeighbors the number of live neighboring cells
   * @return the next state of the cell
   */
  private GameOfLifeState determineNextState(GameOfLifeState currentState, int liveNeighbors) {
    if (currentState == null) {
      throw new IllegalArgumentException("Cell state cannot be null");
    }
    if (currentState == GameOfLifeState.ALIVE) {
      if (liveNeighbors < MINIMUM_LIVE_NEIGHBORS_FOR_SURVIVAL ||
          liveNeighbors > MAXIMUM_LIVE_NEIGHBORS_FOR_SURVIVAL) {
        return GameOfLifeState.DEAD;
      } else {
        return GameOfLifeState.ALIVE;
      }
    } else {
      if (liveNeighbors == EXACT_LIVE_NEIGHBORS_FOR_BIRTH) {
        return GameOfLifeState.ALIVE;
      } else {
        return GameOfLifeState.DEAD;
      }
    }
  }

  /**
   * Creates the state map and color mappings.
   *
   * @return the state-to-color map
   */
  @Override
  public Map<StateInterface, Color> initializeColorMap() {
    Map<StateInterface, Color> colorMap = new HashMap<>();
    colorMap.put(GameOfLifeState.ALIVE, ALIVE_COLOR);
    colorMap.put(GameOfLifeState.DEAD, DEAD_COLOR);
    return colorMap;
  }

  /**
   * Initializes the state map for the Game Of Life simulation.
   *
   * @return the map of integer states to simulation states.
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    Map<Integer, StateInterface> stateMap = new HashMap<>();
    stateMap.put(0, GameOfLifeState.DEAD);
    stateMap.put(1, GameOfLifeState.ALIVE);
    return stateMap;
  }
}


