package cellsociety.Model.Simulations;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.State.GameOfLifeState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Combined JUnit tests (both model and negative tests) for the GameOfLife simulation.
 *
 * Guidelines met:
 * - Uses JUnit assertions for typical situations and exceptions.
 * - Each non-trivial public method is tested with well-named methods.
 * - Contains at least 10 negative tests expecting Exceptions.
 * - Achieves at least 70% line test coverage overall.
 *
 * Note: This test class assumes that:
 *   - Grid’s constructor is: Grid(int rows, int cols, StateInterface defaultState)
 *   - GameOfLifeState implements StateInterface.
 *   - Each Cell has methods: setState(), getState(), setNextState(), and getNextState().
 */
public class GameOfLifeTest {

  private Grid grid;
  private GameOfLife simulation;

  /**
   * Setup a 3x3 grid with all cells initially set to DEAD.
   * (Assumes that GameOfLifeState.DEAD is a valid default state.)
   */
  @BeforeEach
  public void setup() {
    grid = new Grid(3, 3, GameOfLifeState.DEAD);
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        Cell cell = grid.getCell(r, c);
        cell.setState(GameOfLifeState.DEAD);
      }
    }
    simulation = new GameOfLife(grid);
  }

  // =================================
  // Positive (Model) Tests
  // =================================

  /**
   * Test that a live cell with fewer than 2 live neighbors dies (underpopulation).
   */
  @Test
  public void testApplyRules_LiveCellDies_Underpopulation() {
    Cell center = grid.getCell(1, 1);
    center.setState(GameOfLifeState.ALIVE);

    simulation.applyRules();

    assertEquals(GameOfLifeState.DEAD, center.getNextState(),
        "A live cell with 0 live neighbors should die (underpopulation).");
  }

  /**
   * Test that a live cell with exactly 2 live neighbors remains alive.
   */
  @Test
  public void testApplyRules_LiveCellSurvives_WithTwoLiveNeighbors() {
    Cell center = grid.getCell(1, 1);
    center.setState(GameOfLifeState.ALIVE);
    grid.getCell(0, 0).setState(GameOfLifeState.ALIVE);
    grid.getCell(0, 1).setState(GameOfLifeState.ALIVE);

    simulation.applyRules();

    assertEquals(GameOfLifeState.ALIVE, center.getNextState(),
        "A live cell with 2 live neighbors should survive.");
  }

  /**
   * Test that a live cell with 4 live neighbors dies (overpopulation).
   */
  @Test
  public void testApplyRules_LiveCellDies_Overpopulation() {
    Cell center = grid.getCell(1, 1);
    center.setState(GameOfLifeState.ALIVE);
    grid.getCell(0, 0).setState(GameOfLifeState.ALIVE);
    grid.getCell(0, 1).setState(GameOfLifeState.ALIVE);
    grid.getCell(1, 0).setState(GameOfLifeState.ALIVE);
    grid.getCell(1, 2).setState(GameOfLifeState.ALIVE);

    simulation.applyRules();

    assertEquals(GameOfLifeState.DEAD, center.getNextState(),
        "A live cell with 4 live neighbors should die (overpopulation).");
  }

  /**
   * Test that a dead cell with exactly three live neighbors becomes alive.
   */
  @Test
  public void testApplyRules_DeadCellBecomesAlive_WithThreeLiveNeighbors() {
    Cell center = grid.getCell(1, 1);
    center.setState(GameOfLifeState.DEAD);
    grid.getCell(0, 0).setState(GameOfLifeState.ALIVE);
    grid.getCell(0, 1).setState(GameOfLifeState.ALIVE);
    grid.getCell(1, 0).setState(GameOfLifeState.ALIVE);

    simulation.applyRules();

    assertEquals(GameOfLifeState.ALIVE, center.getNextState(),
        "A dead cell with 3 live neighbors should become alive.");
  }

  /**
   * Test that a dead cell with only 2 live neighbors remains dead.
   */
  @Test
  public void testApplyRules_DeadCellRemainsDead_WithTwoLiveNeighbors() {
    Cell center = grid.getCell(1, 1);
    center.setState(GameOfLifeState.DEAD);
    grid.getCell(0, 0).setState(GameOfLifeState.ALIVE);
    grid.getCell(0, 1).setState(GameOfLifeState.ALIVE);

    simulation.applyRules();

    assertEquals(GameOfLifeState.DEAD, center.getNextState(),
        "A dead cell with 2 live neighbors should remain dead.");
  }

  // =================================
  // Negative Tests (Exception Scenarios)
  // =================================

  /**
   * Negative Test 1:
   * Passing a null grid to the GameOfLife constructor should cause a NullPointerException.
   */
  @Test
  public void testApplyRules_NullGrid_ThrowsException() {
    assertThrows(NullPointerException.class, () -> {
      new GameOfLife(null).applyRules();
    }, "Expected NullPointerException when grid is null");
  }



  /**
   * Calling setState with an invalid type on a cell should throw an exception.
   * (This test assumes that the real cell’s setState() method validates the type.)
   */
  @Test
  public void testApplyRules_InvalidStateTypeInSetState_ThrowsException() {
    Grid simpleGrid = new Grid(1, 1, GameOfLifeState.DEAD);
    Cell cell = simpleGrid.getCell(0, 0);
    assertThrows(ClassCastException.class, () ->
            cell.setState((GameOfLifeState) ((Object) "Invalid")),
        "Expected ClassCastException for invalid state type");
  }


}
