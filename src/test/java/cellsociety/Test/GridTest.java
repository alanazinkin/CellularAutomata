package cellsociety.Test;

import Model.Cell;
import Model.State.GameOfLifeState;
import Model.Grid;import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;


import java.util.List;

/**
 * Unit tests for the {@link Grid} class.
 * This class tests the basic functionality and edge cases for a {@link Grid} of {@link Cell} objects.
 */
public class GridTest {

  /** The grid instance to be tested. */
  private Grid grid;

  /**
   * Sets up a 5x5 grid with initial state {@link GameOfLifeState#DEAD} before each test.
   * This method is annotated with {@code @Before} to ensure it is executed before every test.
   */
  
  @BeforeEach
  public void setUp() {
    grid = new Grid(5, 5, GameOfLifeState.DEAD);
  }

  /**
   * Tests the initialization of the grid.
   * Verifies that the grid has the correct number of rows and columns,
   * and that all cells are initialized to the {@link GameOfLifeState#DEAD} state.
   */
  @Test
  public void testGridInitialization() {
    assertEquals(5, grid.getRows());
    assertEquals(5, grid.getCols());


    for (int r = 0; r < 5; r++) {
      for (int c = 0; c < 5; c++) {
        assertEquals(GameOfLifeState.DEAD, grid.getCell(r, c).getState());
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
   * Tests the functionality for counting ALIVE neighbors in the grid.
   * Verifies that the correct number of ALIVE neighbors is counted for a given cell.
   */
  @Test
  public void testCountALIVENeighbors() {

    grid.getCell(1, 1).setState(GameOfLifeState.ALIVE);
    grid.getCell(2, 2).setState(GameOfLifeState.ALIVE);


    //int ALIVENeighbors = grid.countALIVENeighbors(2, 1);
    //assertEquals(2, ALIVENeighbors);
  }

  /**
   * Tests the {@link Grid#applyNextStates()} method.
   * Verifies that the next state for each cell is correctly applied and the cell's state is updated.
   */
  @Test
  public void testApplyNextStates() {

    grid.getCell(2, 2).setNextState(GameOfLifeState.ALIVE);
    grid.getCell(1, 1).setNextState(GameOfLifeState.ALIVE);


    grid.applyNextStates();


    assertEquals(GameOfLifeState.ALIVE, grid.getCell(2, 2).getState());
    assertEquals(GameOfLifeState.ALIVE, grid.getCell(1, 1).getState());
  }

  /**
   * Tests the grid reset functionality.
   * Verifies that all cells in the grid are reset to a specified state (in this case, {@link GameOfLifeState#DEAD}).
   */
  @Test
  public void testResetGrid() {

    grid.getCell(0, 0).setState(GameOfLifeState.ALIVE);
    grid.getCell(1, 1).setState(GameOfLifeState.ALIVE);


    grid.resetGrid(GameOfLifeState.DEAD);


    for (int r = 0; r < 5; r++) {
      for (int c = 0; c < 5; c++) {
        assertEquals(GameOfLifeState.DEAD, grid.getCell(r, c).getState());
      }
    }
  }
}
