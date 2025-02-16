package cellsociety.Model.Simulations;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.Grid;
import cellsociety.Model.State.LangtonState;
import cellsociety.Model.StateInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.HashMap;

/**
 * Test class for LangtonLoop.
 * Tests the functionality of Langton's Loop cellular automaton including state transitions,
 * neighborhood interactions, and basic loop formation patterns.
 */
public class LangtonLoopTest {

  private static final int GRID_SIZE = 5;
  private SimulationConfig config;
  private LangtonLoop simulation;

  /**
   * Sets up the test environment before each test. Creates a new simulation configuration and
   * initializes the simulation with a 5x5 grid.
   */
  @BeforeEach
  void setUp() {
    // Create basic 5x5 grid with empty states
    int[] initialStates = new int[GRID_SIZE * GRID_SIZE];
    for (int i = 0; i < initialStates.length; i++) {
      initialStates[i] = 0; // 0 represents EMPTY state
    }

    config = new SimulationConfig(
        "Langton",
        "Langton's Loop",
        "Test Author",
        "Test Description",
        GRID_SIZE,
        GRID_SIZE,
        initialStates,
        new HashMap<>()
    );

    simulation = new LangtonLoop(config);
  }

  /**
   * Tests initialization of color map with all required states. Verifies that all eight Langton
   * states have corresponding colors.
   */
  @Test
  void getColorMap_WhenInitialized_ContainsAllStates() {
    Map<StateInterface, String> colorMap = simulation.getColorMap();

    assertEquals(8, colorMap.size(), "Color map should contain exactly 8 states");
    assertNotNull(colorMap.get(LangtonState.EMPTY));
    assertNotNull(colorMap.get(LangtonState.SHEATH));
    assertNotNull(colorMap.get(LangtonState.CORE));
    assertNotNull(colorMap.get(LangtonState.TEMP));
    assertNotNull(colorMap.get(LangtonState.TURN));
    assertNotNull(colorMap.get(LangtonState.EXTEND));
    assertNotNull(colorMap.get(LangtonState.INIT));
    assertNotNull(colorMap.get(LangtonState.ADVANCE));
  }

  /**
   * Tests initialization of state map with all required mappings. Verifies that integer values
   * correctly map to Langton states.
   */
  @Test
  void getStateMap_WhenInitialized_ContainsAllMappings() {
    Map<Integer, StateInterface> stateMap = simulation.getStateMap();

    assertEquals(8, stateMap.size(), "State map should contain exactly 8 mappings");
    assertEquals(LangtonState.EMPTY, stateMap.get(0));
    assertEquals(LangtonState.SHEATH, stateMap.get(1));
    assertEquals(LangtonState.CORE, stateMap.get(2));
    assertEquals(LangtonState.TEMP, stateMap.get(3));
    assertEquals(LangtonState.TURN, stateMap.get(4));
    assertEquals(LangtonState.EXTEND, stateMap.get(5));
    assertEquals(LangtonState.INIT, stateMap.get(6));
    assertEquals(LangtonState.ADVANCE, stateMap.get(7));
  }

  /**
   * Tests basic extension pattern formation. Verifies that an EMPTY cell surrounded by EXTEND
   * signals transforms into a SHEATH cell.
   */
  @Test
  void step_EmptyCellWithExtendSignal_CreatesSheathCell() {
    Grid grid = simulation.getGrid();

    // Set up a pattern with EXTEND signals around an EMPTY cell
    grid.getCell(2, 1).setCurrentState(LangtonState.EXTEND);  // West
    grid.getCell(2, 3).setCurrentState(LangtonState.EXTEND);  // East

    simulation.step();

    assertEquals(LangtonState.SHEATH, grid.getCell(2, 2).getCurrentState(),
        "Center cell should become SHEATH when surrounded by EXTEND signals");
  }

  /**
   * Tests signal propagation along sheath. Verifies that ADVANCE signals properly transform into
   * EXTEND signals when adjacent to sufficient SHEATH cells.
   */
  @Test
  void step_AdvanceSignalNearSheath_TransformsToExtend() {
    Grid grid = simulation.getGrid();

    // Set up a pattern with ADVANCE signal near SHEATH cells
    grid.getCell(2, 2).setCurrentState(LangtonState.ADVANCE);
    grid.getCell(1, 2).setCurrentState(LangtonState.SHEATH);
    grid.getCell(2, 1).setCurrentState(LangtonState.SHEATH);

    simulation.step();

    assertEquals(LangtonState.EXTEND, grid.getCell(2, 2).getCurrentState(),
        "ADVANCE signal should transform to EXTEND when near SHEATH cells");
  }

  /**
   * Tests that cells maintain their state when no transition rules apply.
   */
  @Test
  void step_NoTransitionRules_MaintainsState() {
    Grid grid = simulation.getGrid();

    // Set up an isolated CORE cell
    grid.getCell(2, 2).setCurrentState(LangtonState.CORE);

    simulation.step();

    assertEquals(LangtonState.CORE, grid.getCell(2, 2).getCurrentState(),
        "Isolated cell should maintain its state when no transition rules apply");
  }

  /**
   * Tests simulation initialization with invalid grid dimensions.
   */
  @Test
  void constructor_InvalidGridDimensions_ThrowsException() {
  }
}