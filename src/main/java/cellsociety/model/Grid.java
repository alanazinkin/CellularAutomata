package cellsociety.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a 2D grid of {@link Cell} objects used in a simulation. The grid maintains a fixed
 * number of rows and columns and provides methods for accessing cells, retrieving neighbors, and
 * updating states.
 * @author Tatum McKinnis
 */
public class Grid {

  // Named constant for neighbor offsets (row, column)
  private static final int[][] NEIGHBOR_OFFSETS = {
      {-1, -1}, {-1, 0}, {-1, 1},
      {0, -1}, {0, 1},
      {1, -1}, {1, 0}, {1, 1}
  };

  private final Cell[][] cells;
  private final int rows;
  private final int cols;
  private final StateInterface defaultState;

  /**
   * Constructs a new {@code Grid} with the specified dimensions and initializes all cells to the
   * given default state.
   *
   * @param rows         the number of rows in the grid
   * @param cols         the number of columns in the grid
   * @param defaultState the initial state assigned to all cells
   * @throws IllegalArgumentException if either {@code rows} or {@code cols} is negative
   * @throws NullPointerException     if {@code defaultState} is {@code null}
   */
  public Grid(int rows, int cols, StateInterface defaultState) {
    if (rows < 0 || cols < 0) {
      throw new IllegalArgumentException(
          String.format("Grid dimensions cannot be negative: %dx%d", rows, cols));
    }
    this.rows = rows;
    this.cols = cols;
    this.defaultState = Objects.requireNonNull(defaultState, "defaultState cannot be null");
    cells = new Cell[rows][cols];
    initializeCells(defaultState);
  }

  /**
   * Initializes all cells in the grid with the given state.
   *
   * @param state the state to assign to each cell.
   */
  private void initializeCells(StateInterface state) {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        cells[r][c] = new Cell(state);
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
   * Checks if the specified row and column indices represent a valid position within the grid.
   * <p>
   * A valid position is one where:
   * <ul>
   *   <li>{@code row} is between 0 (inclusive) and the grid's row count (exclusive)</li>
   *   <li>{@code col} is between 0 (inclusive) and the grid's column count (exclusive)</li>
   * </ul>
   * This method provides a public interface to the private {@code isInBounds} check while maintaining
   * encapsulation of the grid's internal implementation details.
   * </p>
   *
   * @param row the row index to check
   * @param col the column index to check
   * @return {@code true} if the position is within grid bounds, {@code false} otherwise
   */
  public boolean isValidPosition(int row, int col) {
    return isInBounds(row, col);
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
    if (!isInBounds(row, col)) {
      throw new IndexOutOfBoundsException(
          String.format("Invalid cell indices: row=%d, col=%d", row, col));
    }
    return cells[row][col];
  }

  /**
   * Checks if the specified row and column indices are within the bounds of the grid.
   *
   * @param row the row index
   * @param col the column index
   * @return {@code true} if the indices are in bounds; {@code false} otherwise
   */
  private boolean isInBounds(int row, int col) {
    return row >= 0 && row < rows && col >= 0 && col < cols;
  }

  /**
   * Retrieves the neighboring cells of the specified cell at (row, col). Neighbors are determined
   * using the eight surrounding positions in the grid.
   *
   * @param row the row index of the target cell
   * @param col the column index of the target cell
   * @return an unmodifiable list of neighboring {@code Cell} objects
   * @throws IndexOutOfBoundsException if {@code row} or {@code col} is out of bounds
   */
  public List<Cell> getNeighbors(int row, int col) {
    if (!isInBounds(row, col)) {
      throw new IndexOutOfBoundsException(
          String.format("Indices (%d,%d) out of bounds", row, col));
    }

    List<Cell> neighbors = new ArrayList<>(NEIGHBOR_OFFSETS.length);
    for (int[] offset : NEIGHBOR_OFFSETS) {
      int neighborRow = row + offset[0];
      int neighborCol = col + offset[1];
      if (isInBounds(neighborRow, neighborCol)) {
        neighbors.add(getCell(neighborRow, neighborCol));
      }
    }
    return Collections.unmodifiableList(neighbors);
  }

  /**
   * Applies the next state to all cells in the grid. This method iterates through all cells and
   * updates their state based on the precomputed next state.
   *
   * @throws IllegalStateException if any cell is {@code null} or if any cell's next state is
   *                               {@code null}
   */
  public void applyNextStates() {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        Cell cell = cells[r][c];
        if (cell == null) {
          throw new IllegalStateException(
              String.format("Cell at (%d,%d) is null", r, c));
        }
        cell.applyNextState();
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
    Objects.requireNonNull(newState, "newState cannot be null");
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        cells[r][c].setCurrentState(newState);
        cells[r][c].resetNextState();
      }
    }
  }

  /**
   * Replaces the cell at the specified row and column with the provided cell. This method allows
   * controlled modification of individual cells without exposing the entire internal cell array.
   *
   * @param row  the row index of the cell to replace
   * @param col  the column index of the cell to replace
   * @param cell the new {@code Cell} to be placed at the specified location
   * @throws IllegalArgumentException if the specified row or column is out of bounds or if cell is
   *                                  null
   */
  public void setCellAt(int row, int col, Cell cell) {
    if (!isInBounds(row, col)) {
      throw new IllegalArgumentException(
          String.format("Invalid row or column: (%d, %d)", row, col));
    }
    Objects.requireNonNull(cell, "Cell cannot be null");
    cells[row][col] = cell;
  }

  /**
   * Prints the grid to the console. This is useful for debugging and visualization.
   */
  public void printGrid() {
    StringBuilder sb = new StringBuilder();
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        // For visualization, we print the first character of the state's string representation.
        sb.append(cells[r][c].getCurrentState().toString().charAt(0)).append(" ");
      }
      sb.append(System.lineSeparator());
    }
    System.out.print(sb);
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



