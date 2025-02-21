package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.state.GameOfLifeState;
import cellsociety.model.state.LangtonState;
import cellsociety.model.simulations.TempestiLoop;
import cellsociety.model.StateInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

/**
 * JUnit tests for the TempestiLoop simulation class. Verifies correct behavior of state
 * transitions, grid updates, and exception handling for invalid configurations. Covers both
 * positive and negative scenarios to ensure robustness and adherence to Tempesti's Loop rules.
 *
 * @author Tatum McKinnis
 */
class TempestiLoopTest {

  private static final int TEST_HEIGHT = 5;
  private static final int TEST_WIDTH = 5;
  private SimulationConfig validConfig;
  private TempestiLoop tempestiLoop;
  private Grid validGrid = new Grid(validConfig.getWidth(), validConfig.getHeight(), GameOfLifeState.ALIVE);


  /**
   * Sets up the testing environment before each test. Initializes the valid configuration and
   * TempestiLoop instance.
   */
  @BeforeEach
  void setUp() {
    validConfig = new DummySimulationConfig(TEST_HEIGHT, TEST_WIDTH);
    tempestiLoop = new TempestiLoop(validConfig, validGrid);
  }

  /**
   * Verifies that the constructor does not throw an exception when provided with valid dimensions.
   */
  @Test
  void constructor_ValidDimensions_DoesNotThrowException() {
    assertDoesNotThrow(() -> new TempestiLoop(validConfig, validGrid));
  }

  /**
   * Verifies that the constructor throws an IllegalArgumentException when the width is negative.
   */
  @Test
  void constructor_NegativeWidth_ThrowsIllegalArgumentException() {
    SimulationConfig invalidConfig = new DummySimulationConfig(5, -3);
    assertThrows(IllegalArgumentException.class, () -> new TempestiLoop(invalidConfig, validGrid));
  }

  /**
   * Verifies that the constructor throws an IllegalArgumentException when the height is zero.
   */
  @Test
  void constructor_ZeroHeight_ThrowsIllegalArgumentException() {
    SimulationConfig invalidConfig = new DummySimulationConfig(0, 5);
    assertThrows(IllegalArgumentException.class, () -> new TempestiLoop(invalidConfig, validGrid));
  }

  /**
   * Verifies that the color map is initialized correctly with the expected number of states and
   * that the LangtonState.EMPTY is present.
   */
  @Test
  void initializeColorMap_ExpectedBehavior_ReturnsCorrectColorMap() {
    Map<StateInterface, String> colorMap = tempestiLoop.initializeColorMap();
    assertEquals(8, colorMap.size());
    assertTrue(colorMap.containsKey(LangtonState.EMPTY));
  }

  /**
   * Verifies that the state map is initialized correctly with the expected number of states and
   * that the LangtonState.SHEATH state is present for the key 1.
   */
  @Test
  void initializeStateMap_ExpectedBehavior_ReturnsCorrectStateMap() {
    Map<Integer, StateInterface> stateMap = tempestiLoop.initializeStateMap();
    assertEquals(8, stateMap.size());
    assertEquals(LangtonState.SHEATH, stateMap.get(1));
  }

  /**
   * Verifies that the empty cell with an ADVANCE neighbor transitions to the SHEATH state after
   * applying the rules.
   */
  @Test
  void applyRules_EmptyCellWithAdvanceNeighbor_TransitionsToSheath() throws Exception {
    Grid grid = tempestiLoop.getGrid();
    grid.getCell(0, 1).setCurrentState(LangtonState.ADVANCE);
    invokeApplyRules();
    assertEquals(LangtonState.SHEATH, grid.getCell(1, 1).getNextState());
  }

  /**
   * Verifies that the SHEATH cell with an INIT neighbor transitions to the TEMP state after
   * applying the rules.
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
   * Verifies that the CORE cell with SHEATH neighbors transitions to the INIT state after applying
   * the rules.
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
   *
   * @throws Exception if an error occurs while invoking the method
   */
  private void invokeApplyRules() throws Exception {
    Method applyRules = TempestiLoop.class.getDeclaredMethod("applyRules");
    applyRules.setAccessible(true);
    applyRules.invoke(tempestiLoop);
  }

  /**
   * Dummy implementation of SimulationConfig used for testing purposes.
   */
  private static class DummySimulationConfig extends SimulationConfig {

    private final int height;
    private final int width;
    private final int[] initialStates;

    public DummySimulationConfig(int height, int width) {
      this.height = height;
      this.width = width;
      this.initialStates = (height > 0 && width > 0) ?
          new int[height * width] :
          new int[0];
    }

    @Override
    public int getHeight() {
      return height;
    }

    @Override
    public int getWidth() {
      return width;
    }

    @Override
    public int[] getInitialStates() {
      return initialStates;
    }
  }
}
