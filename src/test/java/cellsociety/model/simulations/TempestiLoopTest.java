package cellsociety.model.simulations;


import cellsociety.controller.SimulationConfig;
import cellsociety.model.Grid;
import cellsociety.model.state.GameOfLifeState;
import cellsociety.model.state.LangtonState;
import cellsociety.model.StateInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Test class for the TempestiLoop simulation implementation. Tests include validation of constructor
 * parameters, initialization of state and color maps, and verification of state transition rules.
 * Uses a 5x5 grid for testing basic functionality and edge cases.
 *
 * @author Tatum McKinnis
 */
class TempestiLoopTest {

  /**
   * The height of the test grid used across all test cases.
   */
  private static final int TEST_HEIGHT = 5;

  /**
   * The width of the test grid used across all test cases.
   */
  private static final int TEST_WIDTH = 5;

  /**
   * Configuration object containing simulation parameters.
   */
  private SimulationConfig validConfig;

  /**
   * Grid object used for testing the simulation.
   */
  private Grid testGrid;

  /**
   * Instance of TempestiLoop simulation being tested.
   */
  private TempestiLoop tempestiLoop;
  private Grid validGrid;


  /**
   * Sets up the test environment before each test case.
   * Initializes a valid configuration, creates a grid with default empty state,
   * and instantiates a new TempestiLoop simulation.
   */
  @BeforeEach
  void setUp() {
    validConfig = new DummySimulationConfig(TEST_HEIGHT, TEST_WIDTH);
    testGrid = new Grid(TEST_HEIGHT, TEST_WIDTH, LangtonState.EMPTY);
    validGrid = new Grid(validConfig.getWidth(), validConfig.getHeight(), GameOfLifeState.ALIVE);
    tempestiLoop = new TempestiLoop(validConfig, testGrid);
  }


  /**
   * Tests that the constructor accepts valid dimensions without throwing exceptions.
   * Creates a new grid with standard test dimensions and verifies successful instantiation.
   */
  @Test
  void constructor_ValidDimensions_DoesNotThrowException() {
    Grid grid = new Grid(TEST_HEIGHT, TEST_WIDTH, LangtonState.EMPTY);
    assertDoesNotThrow(() -> new TempestiLoop(validConfig, grid));
  }

  /**
   * Tests that the constructor properly handles negative width values.
   * Expects an IllegalArgumentException to be thrown when creating a grid with negative width.
   */
  @Test
  void constructor_NegativeWidth_ThrowsIllegalArgumentException() {
    SimulationConfig invalidConfig = new DummySimulationConfig(5, -3);
    Grid validGrid = new Grid(5, 5, LangtonState.EMPTY);  // Use valid grid dimensions
    assertThrows(IllegalArgumentException.class, () -> new TempestiLoop(invalidConfig, validGrid));
  }

  /**
   * Tests that the constructor properly handles zero height values.
   * Expects an IllegalArgumentException to be thrown when creating a grid with zero height.
   */
  @Test
  void constructor_ZeroHeight_ThrowsIllegalArgumentException() {
    SimulationConfig invalidConfig = new DummySimulationConfig(0, 5);
    Grid validGrid = new Grid(5, 5, LangtonState.EMPTY);  // Use valid grid dimensions
    assertThrows(IllegalArgumentException.class, () -> new TempestiLoop(invalidConfig, validGrid));
  }

  /**
   * Tests the initialization of the color map.
   * Verifies that the map contains all expected states and proper color mappings.
   */
  @Test
  void initializeColorMap_ExpectedBehavior_ReturnsCorrectColorMap() {
    Map<StateInterface, String> colorMap = tempestiLoop.initializeColorMap();
    assertEquals(8, colorMap.size());
    assertTrue(colorMap.containsKey(LangtonState.EMPTY));
  }

  /**
   * Tests the initialization of the state map.
   * Verifies that the map contains all required states and proper integer mappings.
   */
  @Test
  void initializeStateMap_ExpectedBehavior_ReturnsCorrectStateMap() {
    Map<Integer, StateInterface> stateMap = tempestiLoop.initializeStateMap();
    assertEquals(8, stateMap.size());
    assertEquals(LangtonState.SHEATH, stateMap.get(1));
  }

  /**
   * Tests the transition rule from empty state to sheath state.
   * Verifies that an empty cell transitions to sheath state when adjacent to an advance cell.
   *
   * @throws Exception if there is an error invoking the applyRules method
   */
  @Test
  void applyRules_EmptyCellWithAdvanceNeighbor_TransitionsToSheath() throws Exception {
    Grid grid = tempestiLoop.getGrid();
    grid.getCell(0, 1).setCurrentState(LangtonState.ADVANCE);
    invokeApplyRules();
    assertEquals(LangtonState.SHEATH, grid.getCell(1, 1).getNextState());
  }

  /**
   * Tests the transition rule from sheath state to temporary state.
   * Verifies that a sheath cell transitions to temporary state when adjacent to an init cell.
   *
   * @throws Exception if there is an error invoking the applyRules method
   */
  @Test
  void applyRules_SheathCellWithInitNeighbor_TransitionsToTemp() throws Exception {
    Grid grid = tempestiLoop.getGrid();
    grid.getCell(1, 1).setCurrentState(LangtonState.SHEATH);
    grid.getCell(0, 1).setCurrentState(LangtonState.INIT);
    invokeApplyRules();
    assertEquals(LangtonState.TEMP, grid.getCell(1, 1).getNextState());
  }

  /**
   * Tests the transition rule from core state to init state.
   * Verifies that a core cell transitions to init state when surrounded by sheath cells.
   *
   * @throws Exception if there is an error invoking the applyRules method
   */
  @Test
  void applyRules_CoreWithSheathNeighbors_TransitionsToInit() throws Exception {
    Grid grid = tempestiLoop.getGrid();
    grid.getCell(1, 1).setCurrentState(LangtonState.CORE);
    grid.getCell(0, 1).setCurrentState(LangtonState.SHEATH);
    grid.getCell(1, 2).setCurrentState(LangtonState.SHEATH);
    invokeApplyRules();
    assertEquals(LangtonState.INIT, grid.getCell(1, 1).getNextState());
  }

  /**
   * Helper method to invoke the private applyRules method using reflection.
   * This method is necessary for testing the internal behavior of the TempestiLoop class.
   *
   * @throws Exception if there is an error accessing or invoking the method
   */
  private void invokeApplyRules() throws Exception {
    Method applyRules = TempestiLoop.class.getDeclaredMethod("applyRules");
    applyRules.setAccessible(true);
    applyRules.invoke(tempestiLoop);
  }

  /**
   * Dummy implementation of SimulationConfig for testing purposes.
   * Provides basic configuration functionality without full simulation implementation.
   */
  private static class DummySimulationConfig extends SimulationConfig {

    /**
     * The height of the simulation grid.
     */
    private final int height;

    /**
     * The width of the simulation grid.
     */
    private final int width;

    /**
     * Array containing the initial states for all cells in the grid.
     */
    private final int[] initialStates;

    /**
     * Constructs a new DummySimulationConfig with specified dimensions.
     *
     * @param height the height of the simulation grid
     * @param width the width of the simulation grid
     */
    public DummySimulationConfig(int height, int width) {
      this.height = height;
      this.width = width;
      this.initialStates = (height > 0 && width > 0) ?
          new int[height * width] :
          new int[0];
    }

    /**
     * Gets the height of the simulation grid.
     *
     * @return the height value
     */
    @Override
    public int getHeight() {
      return height;
    }

    /**
     * Gets the width of the simulation grid.
     *
     * @return the width value
     */
    @Override
    public int getWidth() {
      return width;
    }

    /**
     * Gets the array of initial states for all cells in the grid.
     *
     * @return array of initial state values
     */
    @Override
    public int[] getInitialStates() {
      return initialStates;
    }
  }
}
