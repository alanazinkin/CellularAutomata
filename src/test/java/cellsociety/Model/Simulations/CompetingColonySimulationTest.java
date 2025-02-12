package cellsociety.Model.Simulations;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.ColonyState;
import cellsociety.Model.Grid;
import cellsociety.Model.StateInterface;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;

/**
 * JUnit tests for CompetingColonySimulation with proper grid-state alignment.
 */
public class CompetingColonySimulationTest {

  private static final int GRID_SIZE = 3;
  private static final String SIM_TYPE = "CompetingColony";
  private SimulationConfig validConfig;
  private Grid testGrid;

  @Before
  public void setUp() {
    // 1. Create configuration with matching grid size
    Map<String, Double> params = new HashMap<>();
    params.put("numStates", 3.0);
    params.put("threshold", 30.0);

    // 2. Create config with proper initial states
    validConfig = new SimulationConfig(
        SIM_TYPE,
        "Bacteria Competition",
        "Test Author",
        "Test Description",
        GRID_SIZE,
        GRID_SIZE,
        new int[GRID_SIZE * GRID_SIZE], // 9 elements for 3x3 grid
        params
    );

    // 3. Create grid using states from the simulation
    testGrid = createValidGrid();
  }

  private Grid createValidGrid() {
    // Temporary simulation to access valid states
    Grid tempGrid = new Grid(GRID_SIZE, GRID_SIZE, new ColonyState(0));
    CompetingColonySimulation tempSim = new CompetingColonySimulation(validConfig, tempGrid);

    // Create actual test grid with proper initialization
    Grid grid = new Grid(GRID_SIZE, GRID_SIZE, tempSim.getStateMap().get(0));
    initializeGridFromConfig(grid, validConfig);
    return grid;
  }

  private void initializeGridFromConfig(Grid grid, SimulationConfig config) {
    int[] states = config.getInitialStates();
    int cellIndex = 0;
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        grid.getCell(r, c).setCurrentState(
            new ColonyState(states[cellIndex])
        );
        cellIndex++;
      }
    }
  }

  @Test
  public void testStateMapInitialization() {
    CompetingColonySimulation sim = new CompetingColonySimulation(validConfig, testGrid);
    Map<Integer, StateInterface> stateMap = sim.getStateMap();

    assertEquals(3, stateMap.size());
    for (int i = 0; i < 3; i++) {
      assertNotNull("State " + i + " is null", stateMap.get(i));
      assertEquals(String.valueOf(i), stateMap.get(i).getStateValue());
    }
  }

  @Test
  public void testThresholdBasedTransition() {
    CompetingColonySimulation sim = new CompetingColonySimulation(validConfig, testGrid);
    Map<Integer, StateInterface> states = sim.getStateMap();

    // Set center cell to state 0
    testGrid.getCell(1, 1).setCurrentState(states.get(0));

    // Set 4 neighbors to state 1 (50% > 30% threshold)
    int[][] neighborCells = {{0,0}, {0,1}, {0,2}, {1,0}};
    for (int[] cell : neighborCells) {
      testGrid.getCell(cell[0], cell[1]).setCurrentState(states.get(1));
    }

    sim.step();
    assertEquals("1", testGrid.getCell(1, 1).getCurrentState().getStateValue());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidStateCount() {
    Map<String, Double> badParams = new HashMap<>();
    badParams.put("numStates", 0.0); // Invalid
    badParams.put("threshold", 30.0);

    SimulationConfig invalidConfig = new SimulationConfig(
        SIM_TYPE,
        "Invalid Simulation",
        "Test Author",
        "Test Description",
        GRID_SIZE,
        GRID_SIZE,
        new int[GRID_SIZE * GRID_SIZE],
        badParams
    );

    new CompetingColonySimulation(invalidConfig, testGrid);
  }
}