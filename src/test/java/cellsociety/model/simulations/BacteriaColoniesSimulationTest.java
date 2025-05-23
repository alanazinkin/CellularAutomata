package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Grid;
import cellsociety.model.state.BacteriaState;
import cellsociety.model.StateInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.HashMap;

/**
 * Test class for BacteriaColoniesSimulation. Tests the functionality of the bacteria colonies
 * simulation including state transitions, neighbor interactions, and edge cases.
 *
 * @author Tatum McKinnis
 */
class BacteriaColoniesSimulationTest {

  private static final int GRID_SIZE = 3;
  private SimulationConfig config;
  private BacteriaColoniesSimulation simulation;

  @BeforeEach
  void setUp() {
    Map<String, Double> parameters = new HashMap<>();
    parameters.put("neighborThreshold", 3.0);

    // Create a 3x3 grid with all ROCK states initially
    int[] initialStates = new int[GRID_SIZE * GRID_SIZE];
    // 0 represents ROCK state

    config = new SimulationConfig(
        "Bacteria",
        "Bacteria Colonies",
        "Test Author",
        "Test Description",
        GRID_SIZE,
        GRID_SIZE,
        initialStates,
        parameters,
        "Default"
    );

    simulation = new BacteriaColoniesSimulation(config, new Grid(3, 3, BacteriaState.ROCK));
  }

  /**
   * Tests initialization of color map with all required states.
   */
  @Test
  void getColorMap_WhenInitialized_ContainsAllStates() {
    Map<StateInterface, String> colorMap = simulation.getColorMap();

    assertEquals(3, colorMap.size());
    assertTrue(colorMap.containsKey(BacteriaState.ROCK));
    assertTrue(colorMap.containsKey(BacteriaState.PAPER));
    assertTrue(colorMap.containsKey(BacteriaState.SCISSORS));
    assertNotNull(colorMap.get(BacteriaState.ROCK));
    assertNotNull(colorMap.get(BacteriaState.PAPER));
    assertNotNull(colorMap.get(BacteriaState.SCISSORS));
  }

  /**
   * Tests initialization of state map with all required mappings.
   */
  @Test
  void getStateMap_WhenInitialized_ContainsAllMappings() {
    Map<Integer, StateInterface> stateMap = simulation.getStateMap();

    assertEquals(3, stateMap.size());
    assertEquals(BacteriaState.ROCK, stateMap.get(0));
    assertEquals(BacteriaState.PAPER, stateMap.get(1));
    assertEquals(BacteriaState.SCISSORS, stateMap.get(2));
  }

  /**
   * Tests that a cell changes state when surrounded by sufficient winning neighbors.
   */
  @Test
  void step_CellSurroundedByWinningNeighbors_ChangesState() {
    Grid grid = simulation.getGrid();
    // Set up center cell as SCISSORS surrounded by ROCK
    grid.getCell(1, 1).setCurrentState(BacteriaState.SCISSORS);
    for (int r = 0; r < GRID_SIZE; r++) {
      for (int c = 0; c < GRID_SIZE; c++) {
        if (r != 1 || c != 1) {
          grid.getCell(r, c).setCurrentState(BacteriaState.ROCK);
        }
      }
    }

    simulation.step();

    assertEquals(BacteriaState.ROCK, grid.getCell(1, 1).getCurrentState());
  }

  /**
   * Tests that a cell maintains state when insufficient winning neighbors are present.
   */
  @Test
  void step_InsufficientWinningNeighbors_MaintainsState() {
    Grid grid = simulation.getGrid();
    // Set up center cell as SCISSORS with only 2 ROCK neighbors
    // First set all cells to PAPER (which doesn't beat SCISSORS)
    for (int r = 0; r < GRID_SIZE; r++) {
      for (int c = 0; c < GRID_SIZE; c++) {
        grid.getCell(r, c).setCurrentState(BacteriaState.PAPER);
      }
    }

    // Now set up our test scenario
    grid.getCell(1, 1).setCurrentState(BacteriaState.SCISSORS);  // Center cell
    grid.getCell(0, 0).setCurrentState(BacteriaState.ROCK);     // Only 2 ROCK neighbors
    grid.getCell(0, 1).setCurrentState(BacteriaState.ROCK);     // Not enough to trigger change

    simulation.step();

    assertEquals(BacteriaState.SCISSORS, grid.getCell(1, 1).getCurrentState());
  }

  /**
   * Tests configuration with invalid neighborThreshold parameter.
   */
  @Test
  void constructor_InvalidThreshold_UsesDefaultThreshold() {
    Map<String, Double> parameters = new HashMap<>();
    parameters.put("neighborThreshold", -1.0); // Invalid threshold

    SimulationConfig invalidConfig = new SimulationConfig(
        "Bacteria", "Test", "Author", "Description",
        GRID_SIZE, GRID_SIZE, new int[GRID_SIZE * GRID_SIZE], parameters,
        "Default"
    );

    BacteriaColoniesSimulation sim = new BacteriaColoniesSimulation(invalidConfig,
        new Grid(invalidConfig.getWidth(),
            invalidConfig.getHeight(), BacteriaState.PAPER));

    // Set up a situation where 3 neighbors (default threshold) should cause a change
    Grid grid = sim.getGrid();
    grid.getCell(1, 1).setCurrentState(BacteriaState.SCISSORS);
    grid.getCell(0, 0).setCurrentState(BacteriaState.ROCK);
    grid.getCell(0, 1).setCurrentState(BacteriaState.ROCK);
    grid.getCell(0, 2).setCurrentState(BacteriaState.ROCK);

    sim.step();
    assertEquals(BacteriaState.ROCK, grid.getCell(1, 1).getCurrentState());
  }

  /**
   * Tests empty initial states array handling.
   */
  @Test
  void constructor_EmptyInitialStates_ThrowsIllegalArgumentException() {
    SimulationConfig invalidConfig = new SimulationConfig(
        "Bacteria", "Test", "Author", "Description",
        GRID_SIZE, GRID_SIZE, new int[0], new HashMap<>(),
        "Default"
    );

    assertThrows(IllegalArgumentException.class, () ->
        new BacteriaColoniesSimulation(invalidConfig, new Grid(invalidConfig.getWidth(),
            invalidConfig.getHeight(), BacteriaState.PAPER)));
  }

  /**
   * Tests complex scenario with multiple competing states.
   */
  @Test
  void step_ComplexInteractions_UpdatesCorrectly() {
    Grid grid = simulation.getGrid();
    // Set up a complex pattern
    grid.getCell(0, 0).setCurrentState(BacteriaState.ROCK);
    grid.getCell(0, 1).setCurrentState(BacteriaState.PAPER);
    grid.getCell(0, 2).setCurrentState(BacteriaState.SCISSORS);
    grid.getCell(1, 0).setCurrentState(BacteriaState.PAPER);
    grid.getCell(1, 1).setCurrentState(BacteriaState.SCISSORS);
    grid.getCell(1, 2).setCurrentState(BacteriaState.ROCK);
    grid.getCell(2, 0).setCurrentState(BacteriaState.SCISSORS);
    grid.getCell(2, 1).setCurrentState(BacteriaState.ROCK);
    grid.getCell(2, 2).setCurrentState(BacteriaState.PAPER);

    simulation.step();

    // Center cell (SCISSORS) should change due to surrounding ROCK majority
    StateInterface centerState = grid.getCell(1, 1).getCurrentState();
    assertEquals(BacteriaState.ROCK, centerState);
  }
}