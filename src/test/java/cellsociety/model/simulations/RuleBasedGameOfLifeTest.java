package cellsociety.model.simulations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Grid;
import cellsociety.model.Cell;
import cellsociety.model.state.GameOfLifeState;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for RuleBasedGameOfLife.
 * <p>
 * Verifies rule application for different Game of Life variants with both
 * numeric and B/S notation formats.
 * </p>
 *
 * @author Tatum McKinnis
 */
class RuleBasedGameOfLifeTest {

  private SimulationConfig mockConfig;
  private Grid mockGrid;
  private static final int GRID_SIZE = 3;
  private Cell[][] cellGrid;

  /**
   * Sets up a mock simulation environment before each test.
   */
  @BeforeEach
  void setUp() {
    mockConfig = mock(SimulationConfig.class);
    mockGrid = mock(Grid.class);
    cellGrid = new Cell[GRID_SIZE][GRID_SIZE];

    for (int r = 0; r < GRID_SIZE; r++) {
      for (int c = 0; c < GRID_SIZE; c++) {
        cellGrid[r][c] = new Cell(GameOfLifeState.DEAD);
        when(mockGrid.getCell(r, c)).thenReturn(cellGrid[r][c]);
      }
    }

    when(mockGrid.getRows()).thenReturn(GRID_SIZE);
    when(mockGrid.getCols()).thenReturn(GRID_SIZE);

    when(mockConfig.getWidth()).thenReturn(GRID_SIZE);
    when(mockConfig.getHeight()).thenReturn(GRID_SIZE);
    when(mockConfig.getInitialStates()).thenReturn(new int[GRID_SIZE * GRID_SIZE]);
  }

  /**
   * Tests that a live cell with two neighbors survives under Maze rules using numeric format.
   */
  @Test
  void applyRules_LiveCellWithTwoNeighbors_SurvivesInMazeRules_Numeric() {
    configureSimulation(312345);

    Cell testCell = cellGrid[1][1];
    testCell.setCurrentState(GameOfLifeState.ALIVE);

    List<Cell> neighbors = List.of(
        new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.DEAD)
    );
    when(mockGrid.getNeighbors(1, 1)).thenReturn(neighbors);

    new RuleBasedGameOfLife(mockConfig, mockGrid).applyRules();
    assertEquals(GameOfLifeState.ALIVE, testCell.getNextState());
  }

  /**
   * Tests that a live cell with two neighbors survives under Maze rules using B/S notation.
   */
  @Test
  void applyRules_LiveCellWithTwoNeighbors_SurvivesInMazeRules_BSNotation() {
    configureSimulationWithStringRule("B3/S12345");

    Cell testCell = cellGrid[1][1];
    testCell.setCurrentState(GameOfLifeState.ALIVE);

    List<Cell> neighbors = List.of(
        new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.DEAD)
    );
    when(mockGrid.getNeighbors(1, 1)).thenReturn(neighbors);

    new RuleBasedGameOfLife(mockConfig, mockGrid).applyRules();
    assertEquals(GameOfLifeState.ALIVE, testCell.getNextState());
  }

  /**
   * Tests that a dead cell with three neighbors is born under Conway's rules using numeric format.
   */
  @Test
  void applyRules_DeadCellWithThreeNeighbors_BornInConwaysRules_Numeric() {
    configureSimulation(323);

    Cell testCell = cellGrid[1][1];
    testCell.setCurrentState(GameOfLifeState.DEAD);

    List<Cell> neighbors = List.of(
        new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.ALIVE)
    );
    when(mockGrid.getNeighbors(1, 1)).thenReturn(neighbors);

    new RuleBasedGameOfLife(mockConfig, mockGrid).applyRules();
    assertEquals(GameOfLifeState.ALIVE, testCell.getNextState());
  }

  /**
   * Tests that a dead cell with three neighbors is born under Conway's rules using B/S notation.
   */
  @Test
  void applyRules_DeadCellWithThreeNeighbors_BornInConwaysRules_BSNotation() {
    configureSimulationWithStringRule("B3/S23");

    Cell testCell = cellGrid[1][1];
    testCell.setCurrentState(GameOfLifeState.DEAD);

    List<Cell> neighbors = List.of(
        new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.ALIVE)
    );
    when(mockGrid.getNeighbors(1, 1)).thenReturn(neighbors);

    new RuleBasedGameOfLife(mockConfig, mockGrid).applyRules();
    assertEquals(GameOfLifeState.ALIVE, testCell.getNextState());
  }

  /**
   * Tests that a dead cell with three neighbors is born under Conway's rules using S/B notation.
   */
  @Test
  void applyRules_DeadCellWithThreeNeighbors_BornInConwaysRules_SBNotation() {
    configureSimulationWithStringRule("S23/B3");

    Cell testCell = cellGrid[1][1];
    testCell.setCurrentState(GameOfLifeState.DEAD);

    List<Cell> neighbors = List.of(
        new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.ALIVE)
    );
    when(mockGrid.getNeighbors(1, 1)).thenReturn(neighbors);

    new RuleBasedGameOfLife(mockConfig, mockGrid).applyRules();
    assertEquals(GameOfLifeState.ALIVE, testCell.getNextState());
  }

  /**
   * Tests that an invalid numeric rule code throws an IllegalArgumentException.
   */
  @Test
  void constructor_InvalidNumericRuleCode_ThrowsException() {
    Map<String, Double> params = new HashMap<>();
    params.put("ruleCode", 99999.0);
    when(mockConfig.getParameters()).thenReturn(params);

    assertThrows(IllegalArgumentException.class,
        () -> new RuleBasedGameOfLife(mockConfig, mockGrid));
  }

  /**
   * Tests that an invalid B/S notation rule code throws an IllegalArgumentException.
   */
  @Test
  void constructor_InvalidBSNotationRuleCode_ThrowsException() {
    configureSimulationWithStringRule("X3/S23"); // Invalid format (X instead of B)

    assertThrows(IllegalArgumentException.class,
        () -> new RuleBasedGameOfLife(mockConfig, mockGrid));
  }

  /**
   * Tests that a rule code with invalid neighbor count throws an IllegalArgumentException.
   */
  @Test
  void constructor_InvalidNeighborCount_ThrowsException() {
    configureSimulationWithStringRule("B9/S23"); // Invalid neighbor count (9 is out of range)

    assertThrows(IllegalArgumentException.class,
        () -> new RuleBasedGameOfLife(mockConfig, mockGrid));
  }

  /**
   * Tests default behavior with no rule code specified.
   */
  @Test
  void constructor_NoRuleCodeSpecified_UsesDefault() {
    Map<String, Double> params = new HashMap<>();
    // No rule code specified, should use default (323.0 - Conway's rules)
    when(mockConfig.getParameters()).thenReturn(params);

    Cell testCell = cellGrid[1][1];
    testCell.setCurrentState(GameOfLifeState.DEAD);

    List<Cell> neighbors = List.of(
        new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.ALIVE)
    );
    when(mockGrid.getNeighbors(1, 1)).thenReturn(neighbors);

    new RuleBasedGameOfLife(mockConfig, mockGrid).applyRules();
    assertEquals(GameOfLifeState.ALIVE, testCell.getNextState());
  }

  /**
   * Configures the simulation with the given numeric rule code.
   *
   * @param ruleCode The rule code defining birth and survival conditions.
   */
  private void configureSimulation(int ruleCode) {
    Map<String, Double> params = new HashMap<>();
    params.put("ruleCode", (double) ruleCode);
    when(mockConfig.getParameters()).thenReturn(params);
  }

  /**
   * Configures the simulation with the given string rule code.
   *
   * @param ruleCode The rule code in B/S or S/B notation format.
   */
  @SuppressWarnings("unchecked")
  private void configureSimulationWithStringRule(String ruleCode) {
    Map<String, Double> params = new HashMap<>();
    // Use raw type to bypass type checking, allowing String in a Double map
    ((Map)params).put("ruleCode", ruleCode);
    when(mockConfig.getParameters()).thenReturn(params);
  }
}