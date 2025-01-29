import java.util.ArrayList;
import java.util.List;

/**
 * Represents a 2D grid of {@link Cell} objects used in a simulation.
 * The grid maintains a fixed number of rows and columns and provides
 * methods for accessing cells, retrieving neighbors, and updating states.
 */
public class Grid {

  /** The 2D array of cells representing the grid. */
  private Cell[][] cells;

  /** The number of rows in the grid. */
  private int rows;

  /** The number of columns in the grid. */
  private int cols;

  /**
   * Constructs a new {@code Grid} with the specified dimensions and initializes
   * all cells to the given default state.
   *
   * @param rows         the number of rows in the grid
   * @param cols         the number of columns in the grid
   * @param defaultState the initial state assigned to all cells
   */
  public Grid(int rows, int cols, State defaultState) {
    this.rows = rows;
    this.cols = cols;
    cells = new Cell[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        cells[r][c] = new Cell(defaultState);
      }
    }
  }

  /**
   * Retrieves the cell at the specified row and column.
   * If the requested position is out of bounds, {@code null} is returned.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the {@code Cell} at the specified position, or {@code null} if out of bounds
   */
  public Cell getCell(int row, int col) {
    if (row >= 0 && row < rows && col >= 0 && col < cols) {
      return cells[row][col];
    }
    return null;
  }

  /**
   * Retrieves the neighboring cells of the specified cell at (row, col).
   * Neighbors are determined using the eight surrounding positions in the grid.
   * Out-of-bounds positions are ignored.
   *
   * @param row the row index of the target cell
   * @param col the column index of the target cell
   * @return a list of neighboring {@code Cell} objects
   */
  public List<Cell> getNeighbors(int row, int col) {
    int[] dRow = {-1, -1, -1, 0, 0, 1, 1, 1};
    int[] dCol = {-1, 0, 1, -1, 1, -1, 0, 1};
    List<Cell> neighbors = new ArrayList<>();

    for (int i = 0; i < dRow.length; i++) {
      Cell neighbor = getCell(row + dRow[i], col + dCol[i]);
      if (neighbor != null) {
        neighbors.add(neighbor);
      }
    }
    return neighbors;
  }

  /**
   * Counts the number of active neighbors for a given cell.
   * This can be useful for simulations like the Game of Life.
   *
   * @param row the row index of the target cell
   * @param col the column index of the target cell
   * @return the number of active neighbors
   */
  public int countActiveNeighbors(int row, int col) {
    int activeCount = 0;
    for (Cell neighbor : getNeighbors(row, col)) {
      if (neighbor.getState() == State.ACTIVE) {
        activeCount++;
      }
    }
    return activeCount;
  }

  /**
   * Applies the next state to all cells in the grid.
   * This method iterates through all cells and updates their state
   * based on the precomputed next state.
   */
  public void applyNextStates() {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        cells[r][c].applyNextState();
      }
    }
  }

  /**
   * Resets the grid to a new state, initializing all cells with the specified state.
   *
   * @param newState the state to reset all cells to
   */
  public void resetGrid(State newState) {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        cells[r][c].setState(newState);
        cells[r][c].resetNextState();
      }
    }
  }

  /**
   * Prints the grid to the console. This is useful for debugging and visualization.
   */
  public void printGrid() {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        System.out.print(cells[r][c].getState().toString().charAt(0) + " ");
      }
      System.out.println();
    }
  }

}

