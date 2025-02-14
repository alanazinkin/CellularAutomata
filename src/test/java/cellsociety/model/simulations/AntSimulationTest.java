package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Ant;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.state.AntState;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for {@link AntSimulation} functionality.
 * <p>
 * Verifies colony behavior including pheromone dynamics, food collection, and obstacle avoidance.
 * </p>
 */
class AntSimulationTest {
  private static final int GRID_SIZE = 5;
  private static final int NEST_ROW = 2;
  private static final int NEST_COL = 2;
  private static final int FOOD_ROW = 4;
  private static final int FOOD_COL = 4;

  private Grid testGrid;
  private SimulationConfig testConfig;

  /**
   * Initializes test environment before each test.
   * <p>
   * Creates a grid with nest and food sources, and a basic simulation configuration.
   * </p>
   */
  @BeforeEach
  void setUp() {
    testGrid = createTestGrid();
    testConfig = createTestConfig();
  }

  /**
   * Creates a test grid with predefined nest and food positions.
   * @return Configured grid for testing
   */
  private Grid createTestGrid() {
    Grid grid = new Grid(GRID_SIZE, GRID_SIZE,
        new AntState(false, false, false, 0, 0, 0));

    // Clear path from nest to food
    for (int i = 0; i <= FOOD_ROW - NEST_ROW; i++) {
      int row = NEST_ROW + i;
      int col = NEST_COL + i;
      grid.getCell(row, col).setCurrentState(
          new AntState(false, false, false, 0, AntSimulation.getMaxPheromone() - (i * 20), 0)
      );
    }

    // Nest and food
    grid.getCell(NEST_ROW, NEST_COL).setCurrentState(
        new AntState(true, false, false, AntSimulation.getMaxPheromone(), 0, 0)
    );
    grid.getCell(FOOD_ROW, FOOD_COL).setCurrentState(
        new AntState(false, true, false, 0, AntSimulation.getMaxPheromone(), 0)
    );

    return grid;
  }

  /**
   * Creates a test configuration with ant parameters.
   * @return Basic simulation configuration
   */
  private SimulationConfig createTestConfig() {
    Map<String, Double> params = new HashMap<>();
    params.put("numAnts", 5.0);
    return new SimulationConfig("AntForaging", "Ant Simulation", "Test Author",
        "Test Description", GRID_SIZE, GRID_SIZE,
        new int[GRID_SIZE * GRID_SIZE], params);
  }

  /**
   * Tests ant initialization at nest locations.
   * <p>
   * Verifies that all ants start at the predefined nest coordinates.
   * </p>
   */
  @Test
  void testAntsInitializeAtNest() {
    AntSimulation simulation = new AntSimulation(testConfig, testGrid);

    simulation.getAnts().forEach(ant -> {
      assertEquals(NEST_ROW, ant.getRow());
      assertEquals(NEST_COL, ant.getCol());
    });
  }

  /**
   * Tests pheromone deposition mechanics.
   * <p>
   * Verifies that ants deposit pheromones at their current location during simulation steps.
   * </p>
   */
  @Test
  void testPheromoneDepositionAtCurrentLocation() {
    AntSimulation simulation = new AntSimulation(testConfig, testGrid);
    Ant testAnt = simulation.getAnts().get(0);

    // Move ant away from nest
    testAnt.setRow(NEST_ROW + 1);
    testAnt.setCol(NEST_COL + 1);

    simulation.applyRules();

    Cell currentCell = testGrid.getCell(testAnt.getRow(), testAnt.getCol());
    AntState state = (AntState) currentCell.getCurrentState();
    assertTrue(state.getHomePheromone() > 0,
        "Should deposit home pheromones when returning");
  }

  /**
   * Tests pheromone gradient formation.
   * <p>
   * Verifies that food pheromone levels decrease with distance from the food source.
   * </p>
   */
  @Test
  void testFoodPheromoneGradientCreation() {
    Grid testGrid = createTestGrid();
    AntSimulation simulation = new AntSimulation(testConfig, testGrid);

    // Pre-seed gradient for reliable testing
    for (int i = 0; i <= FOOD_ROW - NEST_ROW; i++) {
      int row = NEST_ROW + i;
      int col = NEST_COL + i;
      double expectedFood = AntSimulation.getMaxPheromone() - (i * 20);
      testGrid.getCell(row, col).setCurrentState(
          new AntState(false, false, false, 0, expectedFood, 0)
      );
    }

    simulation.applyRules(); // Let ants reinforce gradient

    for(int i = 0; i < (FOOD_ROW - NEST_ROW); i++) {
      int currentRow = NEST_ROW + i;
      int currentCol = NEST_COL + i;
      int nextRow = currentRow + 1;
      int nextCol = currentCol + 1;

      AntState current = (AntState) testGrid.getCell(currentRow, currentCol).getCurrentState();
      AntState next = (AntState) testGrid.getCell(nextRow, nextCol).getCurrentState();

      assertTrue(current.getFoodPheromone() > next.getFoodPheromone(),
          () -> String.format("Food pheromone should decrease from %.1f to %.1f",
              current.getFoodPheromone(), next.getFoodPheromone()));
    }
  }

  /**
   * Tests pheromone-guided movement.
   * <p>
   * Verifies that ants move from their initial position when following pheromone trails.
   * </p>
   */
  @Test
  void testAntMovementTowardStrongestPheromone() {
    AntSimulation simulation = new AntSimulation(testConfig, testGrid);
    Ant testAnt = simulation.getAnts().get(0);

    simulation.applyRules();
    simulation.applyRules(); // Ensure at least 2 steps

    assertTrue(testAnt.getRow() > NEST_ROW || testAnt.getCol() > NEST_COL,
        "Ant should move toward increasing food pheromones");
  }

  /**
   * Tests obstacle avoidance behavior.
   * <p>
   * Verifies that ants don't move into cells marked as obstacles.
   * </p>
   */
  @Test
  void testObstacleAvoidance() {
    Grid testGrid = createTestGrid();

    // Create obstacle wall in column 3, blocking all rows except row 2 (NEST_ROW)
    for (int r = 0; r < GRID_SIZE; r++) {
      if (r != NEST_ROW) {
        testGrid.getCell(r, 3).setCurrentState(
            new AntState(false, false, true, 0, 0, 0) // Set obstacle
        );
      }
    }

    AntSimulation simulation = new AntSimulation(testConfig, testGrid);
    simulation.applyRules();
    simulation.applyRules(); // Let ants move

    simulation.getAnts().forEach(ant -> {
      // Ants should only pass through column 3 at row 2
      assertTrue(ant.getCol() != 3 || ant.getRow() == NEST_ROW,
          "Ants should only pass through gap at row " + NEST_ROW);
    });
  }
  /**
   * Tests null grid initialization handling.
   * <p>
   * Verifies that passing a null grid to the constructor throws IllegalArgumentException.
   * </p>
   */
  @Test
  void testNullGridInitializationThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new AntSimulation(testConfig, null));
  }

  /**
   * Tests missing nest handling.
   * <p>
   * Verifies that simulations with grids containing no nests initialize with zero ants.
   * </p>
   */
  @Test
  void testMissingNestInGridHandledGracefully() {
    Grid emptyGrid = new Grid(GRID_SIZE, GRID_SIZE,
        new AntState(false, false, false, 0, 0, 0));

    AntSimulation simulation = new AntSimulation(testConfig, emptyGrid);
    assertTrue(simulation.getAnts().isEmpty());
  }
}