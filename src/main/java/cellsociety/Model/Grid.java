package cellsociety.Model;

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

  /** The default state used when constructing the grid. */
  private final StateInterface defaultState;

  /**
   * Constructs a new {@code Grid} with the specified dimensions and initializes
   * all cells to the given default state.
   *
   * @param rows         the number of rows in the grid
   * @param cols         the number of columns in the grid
   * @param defaultState the initial state assigned to all cells
   * @throws IllegalArgumentException if either {@code rows} or {@code cols} is negative
   * @throws NullPointerException if {@code defaultState} is {@code null}
   */
  public Grid(int rows, int cols, StateInterface defaultState) {
    if (rows < 0 || cols < 0) {
      throw new IllegalArgumentException("Grid dimensions cannot be negative: " + rows + "x" + cols);
    }
    if (defaultState == null) {
      throw new NullPointerException("defaultState cannot be null");
    }
    this.rows = rows;
    this.cols = cols;
    this.defaultState = defaultState;
    cells = new Cell[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        cells[r][c] = new Cell(defaultState);
      }
    }
  }
  /**
   * Returns the default state that was used to construct the grid.
   *
   * @return the default state.
   */
  public StateInterface getDefaultState() {
    return defaultState;
  }


  /**
   * Retrieves the cell at the specified row and column in the grid.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the {@code Cell} at the specified row and column
   * @throws IndexOutOfBoundsException if the row or column indices are out of bounds
   */
  public Cell getCell(int row, int col) {
    if (row < 0 || row >= getRows() || col < 0 || col >= getCols()) {
      throw new IndexOutOfBoundsException("Invalid cell indices: row=" + row + ", col=" + col);
    }
    return cells[row][col];
  }


  /**
   * Retrieves the neighboring cells of the specified cell at (row, col).
   * Neighbors are determined using the eight surrounding positions in the grid.
   * If the provided indices are out of bounds, an {@code ArrayIndexOutOfBoundsException} is thrown.
   *
   * @param row the row index of the target cell
   * @param col the column index of the target cell
   * @return a list of neighboring {@code Cell} objects
   * @throws ArrayIndexOutOfBoundsException if {@code row} or {@code col} is out of bounds
   */
  public List<Cell> getNeighbors(int row, int col) {
    if (row < 0 || row >= rows || col < 0 || col >= cols) {
      throw new ArrayIndexOutOfBoundsException("Indices (" + row + "," + col + ") out of bounds");
    }

    int[] dRow = {-1, -1, -1, 0, 0, 1, 1, 1};
    int[] dCol = {-1, 0, 1, -1, 1, -1, 0, 1};
    List<Cell> neighbors = new ArrayList<>();

    for (int i = 0; i < dRow.length; i++) {
      int neighborRow = row + dRow[i];
      int neighborCol = col + dCol[i];
      if (neighborRow >= 0 && neighborRow < rows && neighborCol >= 0 && neighborCol < cols) {
        Cell neighbor = getCell(neighborRow, neighborCol);
        if (neighbor != null) {
          neighbors.add(neighbor);
        }
      }
    }
    return neighbors;
  }

  /**
   * Applies the next state to all cells in the grid.
   * This method iterates through all cells and updates their state
   * based on the precomputed next state.
   *
   * @throws NullPointerException if any cell is {@code null} or if any cell's next state is {@code null}
   */
  public void applyNextStates() {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (cells[r][c] == null) {
          throw new NullPointerException("Cell at (" + r + "," + c + ") is null");
        }
        cells[r][c].applyNextState();
      }
    }
  }

  /**
   * Resets the grid to a new state, initializing all cells with the specified state.
   *
   * @param newState the state to reset all cells to
   * @throws NullPointerException if {@code newState} is {@code null}
   */
  public void resetGrid(StateInterface newState) {
    if (newState == null) {
      throw new NullPointerException("newState cannot be null");
    }
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        cells[r][c].setCurrentState(newState);
        cells[r][c].resetNextState();
      }
    }
  }

  /**
   * Replaces the cell at the specified row and column with the provided cell.
   * This method allows controlled modification of individual cells without exposing
   * the entire internal cell array.
   *
   * @param row  the row index of the cell to replace
   * @param col  the column index of the cell to replace
   * @param cell the new {@code Cell} to be placed at the specified location
   * @throws IllegalArgumentException if the specified row or column is out of bounds or if cell is null
   */
  public void setCellAt(int row, int col, Cell cell) {
    if (row < 0 || row >= rows || col < 0 || col >= cols) {
      throw new IllegalArgumentException("Invalid row or column: (" + row + "," + col + ")");
    }
    if (cell == null) {
      throw new IllegalArgumentException("Cell cannot be null");
    }
    cells[row][col] = cell;
  }


  /**
   * Prints the grid to the console. This is useful for debugging and visualization.
   */
  public void printGrid() {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        System.out.print(cells[r][c].getCurrentState().toString().charAt(0) + " ");
      }
      System.out.println();
    }
  }

  /**
   * Returns the number of rows in the grid.
   *
   * @return the number of rows
   */
  public int getRows() {
    return rows;
  }

  /**
   * Returns the number of columns in the grid.
   *
   * @return the number of columns
   */
  public int getCols() {
    return cols;
  }
}


