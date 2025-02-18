package cellsociety.model.simulations;

import static org.junit.Assert.*;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.state.SandState;
import cellsociety.controller.SimulationConfig;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;

public class SandTest {

  private Sand simulation;
  private Grid grid;
  private SimulationConfig config;
  private static final int SIZE = 5;

  @Before
  public void setUp() {
    grid = new Grid(SIZE, SIZE, SandState.EMPTY);
    int[] initialStates = new int[SIZE * SIZE];
    config = new SimulationConfig("Sand", "Test", "Author", "Desc", SIZE, SIZE, initialStates, new HashMap<>());
    simulation = new Sand(config, grid);
  }

  @Test
  public void testSandFallsDown() {
    simulation.setCellState(0, 2, 1); // Sand at top
    simulation.step();

    assertEquals("Sand should move down", SandState.SAND, grid.getCell(1, 2).getCurrentState());
    assertEquals("Old position should be empty", SandState.EMPTY, grid.getCell(0, 2).getCurrentState());
  }

  @Test
  public void testSandStopsAtBottom() {
    simulation.setCellState(4, 2, 1); // Sand at bottom row
    simulation.step();

    assertEquals("Sand should stay at bottom", SandState.SAND, grid.getCell(4, 2).getCurrentState());
  }

  @Test
  public void testSandFormsDiagonalPile() {
    // Wall on left
    simulation.setCellState(2, 1, 2);
    // Sand particles
    simulation.setCellState(0, 0, 1);
    simulation.setCellState(0, 1, 1);

    simulation.step();
    simulation.step();

    assertEquals("Should form diagonal pile", SandState.SAND, grid.getCell(1, 1).getCurrentState());
    assertEquals("Should form diagonal pile", SandState.SAND, grid.getCell(2, 2).getCurrentState());
  }

  @Test
  public void testWallBlocksSand() {
    simulation.setCellState(0, 2, 1); // Sand
    simulation.setCellState(1, 2, 2); // Wall

    simulation.step();

    assertEquals("Sand should stay above wall", SandState.SAND, grid.getCell(0, 2).getCurrentState());
    assertEquals("Wall should remain intact", SandState.WALL, grid.getCell(1, 2).getCurrentState());
  }

  @Test
  public void testWaterFlowsAroundObstacles() {
    simulation.setCellState(0, 2, 3); // Water
    simulation.setCellState(1, 2, 2); // Center wall
    simulation.setCellState(1, 1, 2); // Left wall

    simulation.step();

    assertEquals("Water should flow right", SandState.WATER, grid.getCell(1, 3).getCurrentState());
  }

  @Test
  public void testWallsPersistThroughSteps() {
    simulation.setCellState(2, 2, 2); // Wall
    simulation.setCellState(1, 2, 1); // Sand above
    simulation.setCellState(3, 2, 3); // Water below

    simulation.step();
    simulation.step();

    assertEquals("Wall should persist", SandState.WALL, grid.getCell(2, 2).getCurrentState());
    assertEquals("Sand should stack above", SandState.SAND, grid.getCell(2, 2).getCurrentState());
    assertEquals("Water should flow around", SandState.WATER, grid.getCell(4, 2).getCurrentState());
  }

  @Test
  public void testProcessingOrder_SandBeforeWater() {
    simulation.setCellState(3, 2, 1); // Sand near bottom
    simulation.setCellState(0, 2, 3); // Water at top

    simulation.step();

    assertNotEquals("Sand should block water", SandState.WATER, grid.getCell(4, 2).getCurrentState());
    assertEquals("Water should be above sand", SandState.WATER, grid.getCell(3, 2).getCurrentState());
  }

  @Test
  public void testCorners() {
    simulation.setCellState(0, 0, 1); // Sand top-left
    simulation.setCellState(0, 4, 3); // Water top-right

    simulation.step();

    assertEquals("Sand should move down", SandState.SAND, grid.getCell(1, 0).getCurrentState());
    assertTrue("Water should spread from corner",
        grid.getCell(1, 4).getCurrentState() == SandState.WATER ||
            grid.getCell(0, 3).getCurrentState() == SandState.WATER);
  }

  @Test
  public void testFullGridStability() {
    // Fill entire grid with walls
    for (int r = 0; r < SIZE; r++) {
      for (int c = 0; c < SIZE; c++) {
        simulation.setCellState(r, c, 2);
      }
    }

    simulation.step();

    // Verify all walls remain
    for (int r = 0; r < SIZE; r++) {
      for (int c = 0; c < SIZE; c++) {
        assertEquals("All walls should persist", SandState.WALL, grid.getCell(r, c).getCurrentState());
      }
    }
  }
}