package cellsociety.model;

import cellsociety.model.state.StepBackState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a 2D grid of {@link Cell} objects used in a simulation. The grid maintains a fixed
 * number of rows and columns and provides methods for accessing cells, retrieving neighbors, and
 * updating states.
 *
 * @author Tatum McKinnis
 */
public class Grid {

  private Cell[][] cells;
  private int rows;
  private int cols;
  private final StateInterface defaultState;

  private EdgeStrategy edgeStrategy;
  private NeighborhoodStrategy neighborhoodStrategy;

  /**
   * Constructs a new {@code Grid} with the specified dimensions and initializes all cells to the
   * given default state. Uses default bounded edge and Moore neighborhood.
   *
   * @param rows         the number of rows in the grid
   * @param cols         the number of columns in the grid
   * @param defaultState the initial state assigned to all cells
   * @throws IllegalArgumentException if either {@code rows} or {@code cols} is negative
   * @throws NullPointerException     if {@code defaultState} is {@code null}
   */
  public Grid(int rows, int cols, StateInterface defaultState) {
    this(rows, cols, defaultState, new BoundedEdge(), new MooreNeighborhood());
  }

  /**
   * Constructs a new {@code Grid} with the specified dimensions, state, and strategies.
   *
   * @param rows                 the number of rows in the grid
   * @param cols                 the number of columns in the grid
   * @param defaultState         the initial state assigned to all cells
   * @param edgeStrategy         the strategy for handling grid edges
   * @param neighborhoodStrategy the strategy for determining cell neighborhoods
   * @throws IllegalArgumentException if either {@code rows} or {@code cols} is negative
   * @throws NullPointerException     if any parameter is {@code null}
   */
  public Grid(int rows, int cols, StateInterface defaultState,
      EdgeStrategy edgeStrategy, NeighborhoodStrategy neighborhoodStrategy) {
    if (rows < 0 || cols < 0) {
      throw new IllegalArgumentException(
          String.format("Grid dimensions cannot be negative: %dx%d", rows, cols));
    }
    this.rows = rows;
    this.cols = cols;
    this.defaultState = Objects.requireNonNull(defaultState, "defaultState cannot be null");
    this.edgeStrategy = Objects.requireNonNull(edgeStrategy, "edgeStrategy cannot be null");
    this.neighborhoodStrategy = Objects.requireNonNull(neighborhoodStrategy,
        "neighborhoodStrategy cannot be null");

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
   * Checks if the specified row and column indices represent a valid position within the grid
   * according to the current edge strategy.
   *
   * @param row the row index to check
   * @param col the column index to check
   * @return {@code true} if the position is valid according to the edge strategy
   */
  public boolean isValidPosition(int row, int col) {
    return edgeStrategy.isValidPosition(this, row, col);
  }

  /**
   * Checks if the specified row and column indices are within the actual bounds of the grid. This
   * method is used internally and by edge strategies.
   *
   * @param row the row index
   * @param col the column index
   * @return {@code true} if the indices are in bounds; {@code false} otherwise
   */
  boolean isInBounds(int row, int col) {
    return row >= 0 && row < rows && col >= 0 && col < cols;
  }

  /**
   * Retrieves the cell at the specified row and column in the grid, applying edge strategy.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the {@code Cell} at the specified row and column or null if not valid
   */
  public Cell getCell(int row, int col) {
    return edgeStrategy.getCell(this, row, col);
  }

  /**
   * Directly accesses a cell without applying edge strategy. This method is used internally by edge
   * strategies to avoid recursive calls.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the {@code Cell} at the specified row and column
   * @throws IndexOutOfBoundsException if the indices are out of bounds
   */
  Cell getCellDirect(int row, int col) {
    if (row < 0 || row >= rows || col < 0 || col >= cols) {
      throw new IndexOutOfBoundsException(
          String.format("Invalid cell indices: row=%d, col=%d", row, col));
    }
    return cells[row][col];
  }

  /**
   * Retrieves the neighboring cells of the specified cell at (row, col). Neighbors are determined
   * using the current neighborhood strategy.
   *
   * @param row the row index of the target cell
   * @param col the column index of the target cell
   * @return an unmodifiable list of neighboring {@code Cell} objects
   * @throws IndexOutOfBoundsException if the central cell is out of bounds
   */
  public List<Cell> getNeighbors(int row, int col) {
    if (!isInBounds(row, col)) {
      throw new IndexOutOfBoundsException(
          String.format("Indices (%d,%d) out of bounds", row, col));
    }

    List<Cell> neighbors = new ArrayList<>();
    List<int[]> neighborCoords = neighborhoodStrategy.getNeighborCoordinates(row, col);

    for (int[] coord : neighborCoords) {
      Cell neighbor = getCell(coord[0], coord[1]);
      if (neighbor != null) {
        neighbors.add(neighbor);
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
   * Applies previous states to all cells in the grid, if available.
   *
   * @return {@code true} if any cell was reverted to a previous state; {@code false} otherwise
   */
  public boolean applyPreviousStates() {
    boolean applied = false;
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        Cell cell = cells[r][c];
        if (cell == null) {
          throw new IllegalStateException(
              String.format("Cell at (%d,%d) is null", r, c));
        }
        if (cell.getPrevState() != StepBackState.STEP_BACK_STATE) {
          cell.applyPrevState();
          applied = true;
        }
      }
    }
    return applied;
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

  /**
   * Gets the current edge strategy.
   *
   * @return the edge strategy
   */
  public EdgeStrategy getEdgeStrategy() {
    return edgeStrategy;
  }

  /**
   * Sets a new edge strategy.
   *
   * @param edgeStrategy the new edge strategy
   * @throws NullPointerException if edgeStrategy is null
   */
  public void setEdgeStrategy(EdgeStrategy edgeStrategy) {
    this.edgeStrategy = Objects.requireNonNull(edgeStrategy, "edgeStrategy cannot be null");
  }

  /**
   * Gets the current neighborhood strategy.
   *
   * @return the neighborhood strategy
   */
  public NeighborhoodStrategy getNeighborhoodStrategy() {
    return neighborhoodStrategy;
  }

  /**
   * Sets a new neighborhood strategy.
   *
   * @param neighborhoodStrategy the new neighborhood strategy
   * @throws NullPointerException if neighborhoodStrategy is null
   */
  public void setNeighborhoodStrategy(NeighborhoodStrategy neighborhoodStrategy) {
    this.neighborhoodStrategy = Objects.requireNonNull(neighborhoodStrategy,
        "neighborhoodStrategy cannot be null");
  }


  /**
   * Updates the cells array with a new array. This method is protected to allow subclasses like
   * InfiniteGrid to modify the grid structure.
   *
   * @param newCells the new cells array to use
   */
  protected void setCells(Cell[][] newCells) {
    this.cells = newCells;
  }

  /**
   * Updates the dimensions of the grid. This method is protected to allow subclasses like
   * InfiniteGrid to modify the grid structure.
   *
   * @param newRows the new number of rows
   * @param newCols the new number of columns
   */
  protected void setDimensions(int newRows, int newCols) {
    this.rows = newRows;
    this.cols = newCols;
  }
}