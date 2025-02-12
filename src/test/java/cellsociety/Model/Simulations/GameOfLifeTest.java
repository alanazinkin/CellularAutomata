package cellsociety.Model.Simulations;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.State.GameOfLifeState;
import java.util.HashMap;
import cellsociety.Controller.SimulationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Combined JUnit tests for the GameOfLife simulation. Naming convention:
 * [UnitOfWork_StateUnderTest_ExpectedBehavior]
 */
public class GameOfLifeTest {

  private Grid grid;
  private GameOfLife simulation;
  private SimulationConfig simulationConfig;

  /**
   * Sets up a 3x3 grid with all cells initially set to DEAD and creates a SimulationConfig. The
   * SimulationConfig is created with dummy values appropriate for a GameOfLife simulation.
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
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        Cell cell = grid.getCell(r, c);
        cell.setCurrentState(GameOfLifeState.DEAD);
      }
    }
    simulation = new GameOfLife(simulationConfig, grid);
  }

  /**
   * applyRules: A live cell with fewer than 2 live neighbors dies (underpopulation). Input: Center
   * cell is ALIVE with 0 live neighbors.
   */
  @Test
  void applyRules_LiveCellWithFewerThanTwoLiveNeighbors_Dies() {
    Cell center = grid.getCell(1, 1);
    center.setCurrentState(GameOfLifeState.ALIVE);

    simulation.applyRules();

    assertEquals(GameOfLifeState.DEAD, center.getNextState(),
        "A live cell with fewer than 2 live neighbors should die (underpopulation).");
  }

  /**
   * applyRules: A live cell with exactly 2 live neighbors survives. Input: Center cell is ALIVE
   * with exactly 2 live neighbors.
   */
  @Test
  void applyRules_LiveCellWithTwoLiveNeighbors_Survives() {
    Cell center = grid.getCell(1, 1);
    center.setCurrentState(GameOfLifeState.ALIVE);
    grid.getCell(0, 0).setCurrentState(GameOfLifeState.ALIVE);
    grid.getCell(0, 1).setCurrentState(GameOfLifeState.ALIVE);

    simulation.applyRules();

    assertEquals(GameOfLifeState.ALIVE, center.getNextState(),
        "A live cell with 2 live neighbors should survive.");
  }

  /**
   * applyRules: A live cell with 4 live neighbors dies (overpopulation). Input: Center cell is
   * ALIVE with 4 live neighbors.
   */
  @Test
  void applyRules_LiveCellWithFourLiveNeighbors_Dies() {
    Cell center = grid.getCell(1, 1);
    center.setCurrentState(GameOfLifeState.ALIVE);
    grid.getCell(0, 0).setCurrentState(GameOfLifeState.ALIVE);
    grid.getCell(0, 1).setCurrentState(GameOfLifeState.ALIVE);
    grid.getCell(1, 0).setCurrentState(GameOfLifeState.ALIVE);
    grid.getCell(1, 2).setCurrentState(GameOfLifeState.ALIVE);

    simulation.applyRules();

    assertEquals(GameOfLifeState.DEAD, center.getNextState(),
        "A live cell with 4 live neighbors should die (overpopulation).");
  }

  /**
   * applyRules: A dead cell with exactly 3 live neighbors becomes alive (reproduction). Input:
   * Center cell is DEAD with 3 live neighbors.
   */
  @Test
  void applyRules_DeadCellWithThreeLiveNeighbors_BecomesAlive() {
    Cell center = grid.getCell(1, 1);
    center.setCurrentState(GameOfLifeState.DEAD);
    grid.getCell(0, 0).setCurrentState(GameOfLifeState.ALIVE);
    grid.getCell(0, 1).setCurrentState(GameOfLifeState.ALIVE);
    grid.getCell(1, 0).setCurrentState(GameOfLifeState.ALIVE);

    simulation.applyRules();

    assertEquals(GameOfLifeState.ALIVE, center.getNextState(),
        "A dead cell with 3 live neighbors should become alive.");
  }

  /**
   * applyRules: A dead cell with 2 live neighbors remains dead. Input: Center cell is DEAD with 2
   * live neighbors.
   */
  @Test
  void applyRules_DeadCellWithTwoLiveNeighbors_RemainsDead() {
    Cell center = grid.getCell(1, 1);
    center.setCurrentState(GameOfLifeState.DEAD);
    grid.getCell(0, 0).setCurrentState(GameOfLifeState.ALIVE);
    grid.getCell(0, 1).setCurrentState(GameOfLifeState.ALIVE);

    simulation.applyRules();

    assertEquals(GameOfLifeState.DEAD, center.getNextState(),
        "A dead cell with 2 live neighbors should remain dead.");
  }

  /**
   * Tests the behavior of the {@code GameOfLife} constructor when a {@code null} grid is provided.
   * <p>
   * This test ensures that passing a {@code null} grid to the {@code GameOfLife} constructor
   * results in an {@code IllegalArgumentException}, preventing the creation of an invalid
   * simulation instance.
   * </p>
   *
   * <p>Expected behavior:</p>
   * <ul>
   *   <li>When the simulation is created with a {@code null} grid, an {@code IllegalArgumentException} should be thrown.</li>
   *   <li>The exception message should not be null.</li>
   * </ul>
   *
   * @throws IllegalArgumentException if a {@code null} grid is provided to the {@code GameOfLife}
   *                                  simulation.
   */
  @Test
  void GameOfLifeConstructor_NullGrid_ThrowsIllegalArgumentException() {
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> new GameOfLife(simulationConfig, null),
        "Constructing GameOfLife with null grid should throw IllegalArgumentException.");
    assertNotNull(exception.getMessage(), "Exception message should not be null");
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

}


