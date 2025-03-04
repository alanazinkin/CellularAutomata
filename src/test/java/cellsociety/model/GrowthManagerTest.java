package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cellsociety.model.simulations.SugarScape;
import cellsociety.model.state.SugarScapeState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for GrowthManager. Tests the sugar growth and regrowth functionality in the SugarScape
 * simulation.
 */
public class GrowthManagerTest {

  private GrowthManager growthManager;
  private SugarScape mockSimulation;
  private Grid mockGrid;
  private SugarCell mockCell;

  @BeforeEach
  void setUp() {
    mockSimulation = mock(SugarScape.class);
    mockGrid = mock(Grid.class);
    mockCell = mock(SugarCell.class);

    when(mockSimulation.getGrid()).thenReturn(mockGrid);
    when(mockGrid.getRows()).thenReturn(2);
    when(mockGrid.getCols()).thenReturn(2);
    when(mockGrid.getCell(anyInt(), anyInt())).thenReturn(mockCell);
    when(mockCell.getCurrentState()).thenReturn(SugarScapeState.EMPTY);

    growthManager = new GrowthManager(mockSimulation);
  }

  /**
   * Tests that sugar grows back properly on empty cells at the correct interval.
   */
  @Test
  void applyGrowBack_CorrectInterval_SugarGrowsBack() {
    when(mockCell.getSugar()).thenReturn(5);
    when(mockCell.getMaxSugar()).thenReturn(10);

    growthManager.applyGrowBack(4, 2, 1); // currentTick divisible by interval

    verify(mockCell, times(4)).setSugar(6); // 4 cells in 2x2 grid
    verify(mockCell, times(4)).setNextState(SugarScapeState.SUGAR);
    verify(mockGrid).applyNextStates();
  }

  /**
   * Tests that sugar does not grow back when the current tick is not at the interval.
   */
  @Test
  void applyGrowBack_WrongInterval_NoGrowth() {
    growthManager.applyGrowBack(3, 2, 1); // currentTick not divisible by interval

    verify(mockCell, never()).setSugar(anyInt());
    verify(mockCell, never()).setNextState(any());
    verify(mockGrid, never()).applyNextStates();
  }

  /**
   * Tests that sugar growth is limited by the cell's maximum capacity.
   */
  @Test
  void applyGrowBack_AtMaxCapacity_NoMoreGrowth() {
    when(mockCell.getSugar()).thenReturn(10);
    when(mockCell.getMaxSugar()).thenReturn(10);

    growthManager.applyGrowBack(2, 2, 1);

    verify(mockCell, times(4)).setSugar(10); // Should stay at max
    verify(mockCell, times(4)).setNextState(SugarScapeState.SUGAR);
  }

  /**
   * Tests that sugar only grows on empty cells.
   */
  @Test
  void applyGrowBack_NonEmptyCell_NoGrowth() {
    when(mockCell.getCurrentState()).thenReturn(SugarScapeState.AGENT);

    growthManager.applyGrowBack(2, 2, 1);

    verify(mockCell, never()).setSugar(anyInt());
    verify(mockCell, never()).setNextState(any());
  }

  /**
   * Tests that an IllegalArgumentException is thrown for negative interval.
   */
  @Test
  void applyGrowBack_NegativeInterval_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () ->
        growthManager.applyGrowBack(0, -1, 1));
  }

  /**
   * Tests that an IllegalArgumentException is thrown for negative growth rate.
   */
  @Test
  void applyGrowBack_NegativeGrowthRate_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () ->
        growthManager.applyGrowBack(0, 1, -1));
  }
}
