import cellsociety.Model.Grid;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Unit tests for the {@link Grid} class.
 * This class tests the basic functionality and edge cases for a {@link Grid} of {@link Cell} objects.
 */
public class GridTest {

  /** The grid instance to be tested. */
  private Grid grid;

  /**
   * Sets up a 5x5 grid with initial state {@link State#EMPTY} before each test.
   * This method is annotated with {@code @Before} to ensure it is executed before every test.
   */
  @Before
  public void setUp() {
    grid = new Grid(5, 5, State.EMPTY);
  }

  /**
   * Tests the initialization of the grid.
   * Verifies that the grid has the correct number of rows and columns,
   * and that all cells are initialized to the {@link State#EMPTY} state.
   */
  @Test
  public void testGridInitialization() {
    assertEquals(5, grid.getRowCount());
    assertEquals(5, grid.getColCount());


    for (int r = 0; r < 5; r++) {
      for (int c = 0; c < 5; c++) {
        assertEquals(State.EMPTY, grid.getCell(r, c).getState());
      }
    }
  }

  /**
   * Tests the {@link Grid#getCell(int, int)} method for a valid cell.
   * Verifies that the correct cell is returned when a valid row and column index are provided.
   */
  @Test
  public void testGetCellValid() {
    assertNotNull(grid.getCell(2, 2));
  }

  /**
   * Tests the {@link Grid#getCell(int, int)} method for out-of-bounds cells.
   * Verifies that {@code null} is returned when attempting to access a cell outside the grid boundaries.
   */
  @Test
  public void testGetCellOutOfBounds() {
    assertNull(grid.getCell(5, 5));
    assertNull(grid.getCell(-1, -1));
  }

  /**
   * Tests the {@link Grid#getNeighbors(int, int)} method.
   * Verifies the correct number of neighbors returned for a cell in the middle and at a corner of the grid.
   */
  @Test
  public void testGetNeighbors() {

    List<Cell> neighbors = grid.getNeighbors(2, 2);
    assertEquals(8, neighbors.size());


    neighbors = grid.getNeighbors(0, 0);
    assertEquals(3, neighbors.size());
  }

  /**
   * Tests the functionality for counting active neighbors in the grid.
   * Verifies that the correct number of active neighbors is counted for a given cell.
   */
  @Test
  public void testCountActiveNeighbors() {

    grid.getCell(1, 1).setState(State.ACTIVE);
    grid.getCell(2, 2).setState(State.ACTIVE);


    int activeNeighbors = grid.countActiveNeighbors(2, 1);
    assertEquals(2, activeNeighbors);
  }

  /**
   * Tests the {@link Grid#applyNextStates()} method.
   * Verifies that the next state for each cell is correctly applied and the cell's state is updated.
   */
  @Test
  public void testApplyNextStates() {

    grid.getCell(2, 2).setNextState(State.ACTIVE);
    grid.getCell(1, 1).setNextState(State.FILLED);


    grid.applyNextStates();


    assertEquals(State.ACTIVE, grid.getCell(2, 2).getState());
    assertEquals(State.FILLED, grid.getCell(1, 1).getState());
  }

  /**
   * Tests the grid reset functionality.
   * Verifies that all cells in the grid are reset to a specified state (in this case, {@link State#EMPTY}).
   */
  @Test
  public void testResetGrid() {

    grid.getCell(0, 0).setState(State.ACTIVE);
    grid.getCell(1, 1).setState(State.FILLED);


    grid.resetGrid(State.EMPTY);


    for (int r = 0; r < 5; r++) {
      for (int c = 0; c < 5; c++) {
        assertEquals(State.EMPTY, grid.getCell(r, c).getState());
      }
    }
  }
}
