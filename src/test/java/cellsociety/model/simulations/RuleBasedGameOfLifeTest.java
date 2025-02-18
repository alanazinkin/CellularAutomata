package cellsociety.model.simulations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
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
import org.mockito.Mockito;

class RuleBasedGameOfLifeTest {

  private SimulationConfig mockConfig;
  private Grid mockGrid;
  private static final int GRID_SIZE = 3;
  private Cell[][] cellGrid;

  @BeforeEach
  void setUp() {
    mockConfig = mock(SimulationConfig.class);
    mockGrid = mock(Grid.class);
    cellGrid = new Cell[GRID_SIZE][GRID_SIZE];

    // Initialize all cells with default state
    for(int r = 0; r < GRID_SIZE; r++) {
      for(int c = 0; c < GRID_SIZE; c++) {
        cellGrid[r][c] = new Cell(GameOfLifeState.DEAD);
        when(mockGrid.getCell(r, c)).thenReturn(cellGrid[r][c]);
      }
    }

    // Configure grid dimensions
    when(mockGrid.getRows()).thenReturn(GRID_SIZE);
    when(mockGrid.getCols()).thenReturn(GRID_SIZE);

    // Configure simulation config
    when(mockConfig.getWidth()).thenReturn(GRID_SIZE);
    when(mockConfig.getHeight()).thenReturn(GRID_SIZE);
    when(mockConfig.getInitialStates()).thenReturn(new int[GRID_SIZE * GRID_SIZE]);
  }

  @Test
  void applyRules_LiveCellWithTwoNeighbors_SurvivesInMazeRules() {
    // Configure maze rules (B3/S12345)
    configureSimulation(312345);

    // Set up test cell and neighbors
    Cell testCell = cellGrid[1][1];
    testCell.setCurrentState(GameOfLifeState.ALIVE);

    // Configure neighbors (2 alive)
    List<Cell> neighbors = List.of(
        new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.DEAD)
    );
    when(mockGrid.getNeighbors(1, 1)).thenReturn(neighbors);

    new RuleBasedGameOfLife(mockConfig, mockGrid).applyRules();
    assertEquals(GameOfLifeState.ALIVE, testCell.getNextState());
  }

  @Test
  void applyRules_DeadCellWithThreeNeighbors_BornInConwaysRules() {
    // Configure Conway's rules (B3/S23)
    configureSimulation(323);

    // Set up test cell
    Cell testCell = cellGrid[1][1];
    testCell.setCurrentState(GameOfLifeState.DEAD);

    // Configure neighbors (3 alive)
    List<Cell> neighbors = List.of(
        new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.ALIVE)
    );
    when(mockGrid.getNeighbors(1, 1)).thenReturn(neighbors);

    new RuleBasedGameOfLife(mockConfig, mockGrid).applyRules();
    assertEquals(GameOfLifeState.ALIVE, testCell.getNextState());
  }

  @Test
  void constructor_InvalidRuleCode_ThrowsException() {
    Map<String, Double> params = new HashMap<>();
    params.put("ruleCode", 99999.0); // Invalid B999/S99
    when(mockConfig.getParameters()).thenReturn(params);

    assertThrows(IllegalArgumentException.class,
        () -> new RuleBasedGameOfLife(mockConfig, mockGrid));
  }

  private void configureSimulation(int ruleCode) {
    Map<String, Double> params = new HashMap<>();
    params.put("ruleCode", (double) ruleCode);
    when(mockConfig.getParameters()).thenReturn(params);
  }
}