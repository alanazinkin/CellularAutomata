package cellsociety.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of Grid that supports dynamic expansion for infinite edge behavior.
 * This class works with the InfiniteEdge strategy to allow the grid to grow
 * as needed when cells outside the current bounds are accessed.
 *
 * @author Tatum McKinnis
 */
public class InfiniteGrid extends Grid implements DynamicGrid {

  private int rowOffset = 0;
  private int colOffset = 0;

  /**
   * Constructs a new InfiniteGrid with the specified dimensions and state.
   * Uses InfiniteEdge strategy by default.
   *
   * @param rows the initial number of rows
   * @param cols the initial number of columns
   * @param defaultState the initial state for all cells
   */
  public InfiniteGrid(int rows, int cols, StateInterface defaultState) {
    super(rows, cols, defaultState, new InfiniteEdge(), new MooreNeighborhood());
  }

  /**
   * Constructs a new InfiniteGrid with the specified dimensions, state, and neighborhood strategy.
   * Always uses InfiniteEdge strategy for edge handling.
   *
   * @param rows the initial number of rows
   * @param cols the initial number of columns
   * @param defaultState the initial state for all cells
   * @param neighborhoodStrategy the strategy for determining cell neighborhoods
   */
  public InfiniteGrid(int rows, int cols, StateInterface defaultState,
      NeighborhoodStrategy neighborhoodStrategy) {
    super(rows, cols, defaultState, new InfiniteEdge(), neighborhoodStrategy);
  }

  /**
   * Gets the row offset of this grid.
   * The offset is used to map between the original coordinate system and
   * the expanded grid's internal coordinates.
   *
   * @return the current row offset
   */
  public int getRowOffset() {
    return rowOffset;
  }

  /**
   * Gets the column offset of this grid.
   * The offset is used to map between the original coordinate system and
   * the expanded grid's internal coordinates.
   *
   * @return the current column offset
   */
  public int getColOffset() {
    return colOffset;
  }

  /**
   * Gets a cell from the grid, adjusting for offsets.
   * This method overrides the parent class to handle the coordinate mapping.
   *
   * @param row the row in the original coordinate system
   * @param col the column in the original coordinate system
   * @return the cell at the adjusted position
   */
  @Override
  public Cell getCell(int row, int col) {
    // Delegate to the edge strategy, which will handle expansion if needed
    return super.getCell(row, col);
  }

  /**
   * Gets a cell from the grid directly, bypassing edge strategy.
   * This method overrides the parent class to handle the coordinate mapping.
   *
   * @param row the row in the original coordinate system
   * @param col the column in the original coordinate system
   * @return the cell at the adjusted position
   */
  @Override
  Cell getCellDirect(int row, int col) {
    // Adjust for offsets before accessing cell array
    int adjustedRow = row + rowOffset;
    int adjustedCol = col + colOffset;

    if (adjustedRow < 0 || adjustedRow >= getRows() || adjustedCol < 0 || adjustedCol >= getCols()) {
      throw new IndexOutOfBoundsException(
          String.format("Invalid cell indices: row=%d, col=%d", row, col));
    }

    return super.getCellDirect(adjustedRow, adjustedCol);
  }

  /**
   * Expands the grid to include the given range of coordinates.
   * This is called by the InfiniteEdge strategy when accessing cells
   * outside the current grid bounds.
   *
   * @param rowStart the minimum row to include
   * @param rowEnd the maximum row to include
   * @param colStart the minimum column to include
   * @param colEnd the maximum column to include
   */
  @Override
  public void expandToInclude(int rowStart, int rowEnd, int colStart, int colEnd) {
    // Calculate new dimensions needed to include all coordinates
    int newRowStart = Math.min(0, rowStart);
    int newRowEnd = Math.max(getRows() - 1, rowEnd);
    int newColStart = Math.min(0, colStart);
    int newColEnd = Math.max(getCols() - 1, colEnd);

    int newRows = newRowEnd - newRowStart + 1;
    int newCols = newColEnd - newColStart + 1;

    // Create a new larger grid with the expanded dimensions
    Cell[][] newCells = new Cell[newRows][newCols];

    // Track changes in offset to maintain coordinate system
    int newRowOffset = rowOffset - newRowStart;
    int newColOffset = colOffset - newColStart;

    // Initialize the new cells
    for (int r = 0; r < newRows; r++) {
      for (int c = 0; c < newCols; c++) {
        // Calculate the original grid coordinates
        int origRow = r + newRowStart;
        int origCol = c + newColStart;

        // If this cell existed in the original grid, copy it
        if (origRow >= 0 && origRow < getRows() && origCol >= 0 && origCol < getCols()) {
          newCells[r][c] = super.getCellDirect(origRow, origCol);
        } else {
          // Otherwise, create a new cell with the default state
          newCells[r][c] = new Cell(getDefaultState());
        }
      }
    }

    // Update the grid's state
    setCells(newCells);
    setDimensions(newRows, newCols);
    this.rowOffset = newRowOffset;
    this.colOffset = newColOffset;
  }

  /**
   * Checks if a position is valid in this grid.
   * For an infinite grid, all positions are considered valid.
   *
   * @param row the row to check
   * @param col the column to check
   * @return true always, since all positions are valid for an infinite grid
   */
  @Override
  public boolean isValidPosition(int row, int col) {
    return true; // All positions are valid in an infinite grid
  }

  /**
   * Gets neighbors for a cell, accounting for the potential need to expand the grid.
   *
   * @param row the row of the cell
   * @param col the column of the cell
   * @return a list of neighboring cells
   */
  @Override
  public List<Cell> getNeighbors(int row, int col) {
    // For InfiniteGrid, we can't rely on isInBounds check in the parent class,
    // as we want to handle positions outside current bounds by expanding

    // Get neighbor coordinates from the strategy
    List<int[]> neighborCoords = getNeighborhoodStrategy().getNeighborCoordinates(row, col);

    // Find the bounds needed for all neighbors
    int minRow = row;
    int maxRow = row;
    int minCol = col;
    int maxCol = col;

    for (int[] coord : neighborCoords) {
      minRow = Math.min(minRow, coord[0]);
      maxRow = Math.max(maxRow, coord[0]);
      minCol = Math.min(minCol, coord[1]);
      maxCol = Math.max(maxCol, coord[1]);
    }

    // Ensure the grid is expanded to include all potential neighbors
    if (minRow < -rowOffset || maxRow >= getRows() - rowOffset ||
        minCol < -colOffset || maxCol >= getCols() - colOffset) {
      expandToInclude(minRow, maxRow, minCol, maxCol);
    }

    // Collect all valid neighbors
    List<Cell> neighbors = new ArrayList<>();
    for (int[] coord : neighborCoords) {
      Cell cell = getCell(coord[0], coord[1]);
      if (cell != null) {
        neighbors.add(cell);
      }
    }

    return Collections.unmodifiableList(neighbors);
  }

  /**
   * Applies the next state to all cells in the grid.
   * This method overrides the parent class to account for the expanded grid size.
   */
  @Override
  public void applyNextStates() {
    super.applyNextStates();
  }

  /**
   * Applies previous states to all cells in the grid.
   * This method overrides the parent class to account for the expanded grid size.
   *
   * @return true if any cell was reverted to a previous state
   */
  @Override
  public boolean applyPreviousStates() {
    return super.applyPreviousStates();
  }

  /**
   * Resets the grid with a new state.
   * This method overrides the parent class to ensure all cells are reset.
   *
   * @param newState the new state for all cells
   */
  @Override
  public void resetGrid(StateInterface newState) {
    super.resetGrid(newState);
  }

  /**
   * Prints the grid to the console, accounting for the grid offsets.
   * This method overrides the parent class to show the current grid coordinates.
   */
  @Override
  public void printGrid() {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("Grid with offsets: rowOffset=%d, colOffset=%d%n", rowOffset, colOffset));

    // Add column headers showing logical coordinates
    sb.append("    ");
    for (int c = 0; c < getCols(); c++) {
      sb.append(String.format("%3d ", c - colOffset));
    }
    sb.append("\n");

    for (int r = 0; r < getRows(); r++) {
      // Add row headers showing logical coordinates
      sb.append(String.format("%3d ", r - rowOffset));

      for (int c = 0; c < getCols(); c++) {
        // For visualization, we print the first character of the state's string representation.
        sb.append(String.format("%3s ", getCellDirect(r, c).getCurrentState().toString().charAt(0)));
      }
      sb.append(System.lineSeparator());
    }
    System.out.print(sb);
  }
}