package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.state.LangtonState;
import cellsociety.model.StateInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

/**
 * JUnit tests for the TempestiLoop simulation class. Verifies correct behavior of state transitions,
 * grid updates, and exception handling for invalid configurations. Covers both positive and negative scenarios
 * to ensure robustness and adherence to Tempesti's Loop rules.
 *
 *
 * @author Tatum McKinnis
 */
class TempestiLoopTest {

  private static final int TEST_HEIGHT = 5;
  private static final int TEST_WIDTH = 5;
  private SimulationConfig validConfig;
  private TempestiLoop tempestiLoop;

  @BeforeEach
  void setUp() {
    validConfig = new DummySimulationConfig(TEST_HEIGHT, TEST_WIDTH);
    tempestiLoop = new TempestiLoop(validConfig);
  }

  @Test
  void testConstructorValidDimensions() {
    assertDoesNotThrow(() -> new TempestiLoop(validConfig));
  }

  @Test
  void testConstructorNegativeWidth() {
    SimulationConfig invalidConfig = new DummySimulationConfig(5, -3);
    assertThrows(IllegalArgumentException.class, () -> new TempestiLoop(invalidConfig));
  }

  @Test
  void testConstructorZeroHeight() {
    SimulationConfig invalidConfig = new DummySimulationConfig(0, 5);
    assertThrows(IllegalArgumentException.class, () -> new TempestiLoop(invalidConfig));
  }

  @Test
  void testInitializeColorMap() {
    Map<StateInterface, String> colorMap = tempestiLoop.initializeColorMap();
    assertEquals(8, colorMap.size());
    assertTrue(colorMap.containsKey(LangtonState.EMPTY));
  }

  @Test
  void testInitializeStateMap() {
    Map<Integer, StateInterface> stateMap = tempestiLoop.initializeStateMap();
    assertEquals(8, stateMap.size());
    assertEquals(LangtonState.SHEATH, stateMap.get(1));
  }

  @Test
  void testEmptyCellWithAdvanceNeighbor() throws Exception {
    Grid grid = tempestiLoop.getGrid();
    grid.getCell(0, 1).setCurrentState(LangtonState.ADVANCE);
    invokeApplyRules();
    assertEquals(LangtonState.SHEATH, grid.getCell(1, 1).getNextState());
  }

  @Test
  void testSheathCellWithInitNeighbor() throws Exception {
    Grid grid = tempestiLoop.getGrid();
    grid.getCell(1, 1).setCurrentState(LangtonState.SHEATH);
    grid.getCell(0, 1).setCurrentState(LangtonState.INIT);
    invokeApplyRules();
    assertEquals(LangtonState.TEMP, grid.getCell(1, 1).getNextState());
  }

  @Test
  void testCoreWithSheathNeighbors() throws Exception {
    Grid grid = tempestiLoop.getGrid();
    grid.getCell(1, 1).setCurrentState(LangtonState.CORE);
    grid.getCell(0, 1).setCurrentState(LangtonState.SHEATH);
    grid.getCell(1, 2).setCurrentState(LangtonState.SHEATH);
    invokeApplyRules();
    assertEquals(LangtonState.INIT, grid.getCell(1, 1).getNextState());
  }

  private void invokeApplyRules() throws Exception {
    Method applyRules = TempestiLoop.class.getDeclaredMethod("applyRules");
    applyRules.setAccessible(true);
    applyRules.invoke(tempestiLoop);
  }

  private static class DummySimulationConfig extends SimulationConfig {
    private final int height;
    private final int width;
    private final int[] initialStates;

    public DummySimulationConfig(int height, int width) {
      this.height = height;
      this.width = width;
      // Handle invalid dimensions safely for testing purposes
      this.initialStates = (height > 0 && width > 0) ?
          new int[height * width] :
          new int[0]; // Empty array for invalid dimensions
    }

    @Override
    public int getHeight() { return height; }

    @Override
    public int getWidth() { return width; }

    @Override
    public int[] getInitialStates() { return initialStates; }
  }
}