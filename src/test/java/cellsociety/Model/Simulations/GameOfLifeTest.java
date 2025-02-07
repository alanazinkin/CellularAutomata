package cellsociety.Model.Simulations;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.State.GameOfLifeState;
import java.lang.reflect.Field;
import java.util.HashMap;
import cellsociety.Controller.SimulationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Combined JUnit tests for the GameOfLife simulation.
 * Naming convention: [UnitOfWork_StateUnderTest_ExpectedBehavior]
 */
public class GameOfLifeTest {

  private Grid grid;
  private GameOfLife simulation;
  private SimulationConfig simulationConfig;

  /**
   * Sets up a 3x3 grid with all cells initially set to DEAD and creates a SimulationConfig.
   * The SimulationConfig is created with dummy values appropriate for a GameOfLife simulation.
   */
  @BeforeEach
  public void setup() {
    simulationConfig = new SimulationConfig(
        "GameOfLife",
        "Game of Life Simulation",
        "Test Author",
        "Testing GameOfLife simulation",
        3, 3,
        new int[9],
        new HashMap<>()
    );
    grid = new Grid(3, 3, GameOfLifeState.DEAD);
    // Ensure every cell is explicitly set to DEAD.
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        Cell cell = grid.getCell(r, c);
        cell.setState(GameOfLifeState.DEAD);
      }
    }
    simulation = new GameOfLife(simulationConfig, grid);
  }

  // =================================
  // Positive (Model) Tests
  // =================================

  /**
   * applyRules: A live cell with fewer than 2 live neighbors dies (underpopulation).
   * Input: Center cell is ALIVE with 0 live neighbors.
   */
  @Test
  void applyRules_LiveCellWithFewerThanTwoLiveNeighbors_Dies() {
    Cell center = grid.getCell(1, 1);
    center.setState(GameOfLifeState.ALIVE);

    simulation.applyRules();

    assertEquals(GameOfLifeState.DEAD, center.getNextState(),
        "A live cell with fewer than 2 live neighbors should die (underpopulation).");
  }

  /**
   * applyRules: A live cell with exactly 2 live neighbors survives.
   * Input: Center cell is ALIVE with exactly 2 live neighbors.
   */
  @Test
  void applyRules_LiveCellWithTwoLiveNeighbors_Survives() {
    Cell center = grid.getCell(1, 1);
    center.setState(GameOfLifeState.ALIVE);
    grid.getCell(0, 0).setState(GameOfLifeState.ALIVE);
    grid.getCell(0, 1).setState(GameOfLifeState.ALIVE);

    simulation.applyRules();

    assertEquals(GameOfLifeState.ALIVE, center.getNextState(),
        "A live cell with 2 live neighbors should survive.");
  }

  /**
   * applyRules: A live cell with 4 live neighbors dies (overpopulation).
   * Input: Center cell is ALIVE with 4 live neighbors.
   */
  @Test
  void applyRules_LiveCellWithFourLiveNeighbors_Dies() {
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
   * applyRules: A dead cell with exactly 3 live neighbors becomes alive (reproduction).
   * Input: Center cell is DEAD with 3 live neighbors.
   */
  @Test
  void applyRules_DeadCellWithThreeLiveNeighbors_BecomesAlive() {
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
   * applyRules: A dead cell with 2 live neighbors remains dead.
   * Input: Center cell is DEAD with 2 live neighbors.
   */
  @Test
  void applyRules_DeadCellWithTwoLiveNeighbors_RemainsDead() {
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
   * GameOfLifeConstructor: Passing a null grid should throw NullPointerException.
   * Input: Null grid.
   */
  @Test
  void GameOfLifeConstructor_NullGrid_ThrowsNullPointerException() {
    assertThrows(NullPointerException.class,
        () -> new GameOfLife(simulationConfig, null),
        "Constructing GameOfLife with null grid should throw NullPointerException.");
  }

  /**
   * GameOfLifeConstructor: Passing a null SimulationConfig should throw NullPointerException.
   * Input: Null SimulationConfig.
   */
  @Test
  void GameOfLifeConstructor_NullSimulationConfig_ThrowsNullPointerException() {
    assertThrows(NullPointerException.class,
        () -> new GameOfLife(null, grid),
        "Constructing GameOfLife with null SimulationConfig should throw NullPointerException.");
  }

  /**
   * Grid_getCell: Using an invalid row index should throw IndexOutOfBoundsException.
   * Input: Negative row index.
   */
  @Test
  void Grid_getCell_InvalidRowIndex_ThrowsIndexOutOfBoundsException() {
    assertThrows(IndexOutOfBoundsException.class,
        () -> grid.getCell(-1, 0),
        "Accessing a cell with an invalid row index should throw IndexOutOfBoundsException.");
  }

  /**
   * Grid_getCell: Using an invalid column index should throw IndexOutOfBoundsException.
   * Input: Column index equal to grid.getCols().
   */
  @Test
  void Grid_getCell_InvalidColumnIndex_ThrowsIndexOutOfBoundsException() {
    assertThrows(IndexOutOfBoundsException.class,
        () -> grid.getCell(0, grid.getCols()),
        "Accessing a cell with an invalid column index should throw IndexOutOfBoundsException.");
  }

  /**
   * Cell_setState: Setting a cell's state to null should throw IllegalArgumentException.
   * Input: Null state.
   */
  @Test
  void Cell_setState_NullState_ThrowsIllegalArgumentException() {
    Cell cell = grid.getCell(0, 0);
    assertThrows(IllegalArgumentException.class,
        () -> cell.setState(null),
        "Setting a cell's state to null should throw IllegalArgumentException.");
  }

  /**
   * Cell_setNextState: Setting a cell's next state to null should throw IllegalArgumentException.
   * Input: Null next state.
   */
  @Test
  void Cell_setNextState_NullState_ThrowsIllegalArgumentException() {
    Cell cell = grid.getCell(0, 0);
    assertThrows(IllegalArgumentException.class,
        () -> cell.setNextState(null),
        "Setting a cell's next state to null should throw IllegalArgumentException.");
  }

  /**
   * applyRules: If a cell in the grid has a null state, applyRules should throw IllegalArgumentException.
   * Input: Manually set a cell's state to null using reflection.
   */
  @Test
  void applyRules_NullCellStateInGrid_ThrowsIllegalArgumentException() throws NoSuchFieldException, IllegalAccessException {
    Cell cell = grid.getCell(1, 1);
    // Bypass the setState check using reflection to simulate a null state.
    Field stateField = cell.getClass().getDeclaredField("state");
    stateField.setAccessible(true);
    stateField.set(cell, null);

    assertThrows(IllegalArgumentException.class,
        () -> simulation.applyRules(),
        "applyRules should throw IllegalArgumentException when a cell's state is null.");
  }

  /**
   * GridConstructor: Constructing a grid with negative rows should throw IllegalArgumentException.
   * Input: Negative number of rows.
   */
  @Test
  void GridConstructor_NegativeRows_ThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Grid(-3, 3, GameOfLifeState.DEAD),
        "Constructing a grid with negative rows should throw IllegalArgumentException.");
  }

  /**
   * GridConstructor: Constructing a grid with negative columns should throw IllegalArgumentException.
   * Input: Negative number of columns.
   */
  @Test
  void GridConstructor_NegativeCols_ThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Grid(3, -3, GameOfLifeState.DEAD),
        "Constructing a grid with negative columns should throw IllegalArgumentException.");
  }

  /**
   * Cell_setState: Setting a cell's state using an invalid type should throw ClassCastException.
   * Input: Attempting to cast a String to GameOfLifeState.
   */
  @Test
  void Cell_setState_InvalidType_ThrowsClassCastException() {
    Cell cell = grid.getCell(0, 0);
    assertThrows(ClassCastException.class,
        () -> cell.setState((GameOfLifeState) ((Object) "Invalid")),
        "Setting a cell's state with an invalid type should throw ClassCastException.");
  }
}


