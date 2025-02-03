package cellsociety.Model.Simulations;
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

  /**
   * Constructs a new Game of Life simulation with the specified grid.
   *
   * @param grid the {@code Grid} object representing the simulation space
   */
  public GameOfLife(Grid grid) {
    super(grid);
  }

  /**
   * Applies the Game of Life rules to the grid.
   * This method updates the next states of the cells based on their neighbors.
   */
  @Override
  public void applyRules() {
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        Cell cell = grid.getCell(r, c);
        List<Cell> neighbors = grid.getNeighbors(r, c);
        int liveNeighbors = 0;
        for (Cell neighbor : neighbors) {
          if (neighbor.getState() == GameOfLifeState.ALIVE) {
            liveNeighbors++;
          }
        }
        if (cell.getState() == GameOfLifeState.ALIVE) {
          if (liveNeighbors < 2 || liveNeighbors > 3) {
            cell.setNextState(GameOfLifeState.DEAD);
          } else {
            cell.setNextState(GameOfLifeState.ALIVE);
          }
        } else {
          if (liveNeighbors == 3) {
            cell.setNextState(GameOfLifeState.ALIVE);
          } else {
            cell.setNextState(GameOfLifeState.DEAD);
          }
        }

      }
    }
  }

  /**
   * creates state map and color mappings
   */
  @Override
  public Map<StateInterface, Color> initializeStateMap() {
    stateMap = new HashMap<>();
    stateMap.put(GameOfLifeState.ALIVE, Color.BLACK);
    stateMap.put(GameOfLifeState.DEAD, Color.WHITE);
    return stateMap;
  }
}
