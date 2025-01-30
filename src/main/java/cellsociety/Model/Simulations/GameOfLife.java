package Model.Simulations;
import Model.Simulation;
import Model.Grid;
import Model.Cell;
import Model.State.GameOfLifeState;
import java.util.List;

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
          }
        }
      }
    }
  }
}
