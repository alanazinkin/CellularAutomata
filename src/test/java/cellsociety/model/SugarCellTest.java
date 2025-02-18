package cellsociety.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import cellsociety.model.state.SugarScapeState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for SugarCell in the SugarScape simulation.
 * Tests cell initialization, sugar management, and position calculations.
 */
class SugarCellTest {
  private SugarCell cell;
  private static final int ROW = 2;
  private static final int COL = 3;
  private static final int MAX_SUGAR = 10;

  @BeforeEach
  void setUp() {
    cell = new SugarCell(ROW, COL, SugarScapeState.EMPTY);
    cell.setMaxSugar(MAX_SUGAR);
  }

  @Test
  void constructor_WithValidParams_CreatesCell() {
    assertEquals(ROW, cell.getRow());
    assertEquals(COL, cell.getCol());
    assertEquals(0, cell.getSugar());
    assertEquals(MAX_SUGAR, cell.getMaxSugar());
  }

  @Test
  void constructor_WithNullState_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new SugarCell(ROW, COL, null));
  }

  @Test
  void setSugar_WithNegativeAmount_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> cell.setSugar(-1));
  }

  @Test
  void setMaxSugar_WithNegativeAmount_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> cell.setMaxSugar(-1));
  }

  @Test
  void addSugar_WithinCapacity_AddsFullAmount() {
    int addAmount = 5;
    int added = cell.addSugar(addAmount);
    assertEquals(addAmount, added);
    assertEquals(addAmount, cell.getSugar());
  }

  @Test
  void addSugar_BeyondCapacity_AddsPartialAmount() {
    int addAmount = MAX_SUGAR + 5;
    int added = cell.addSugar(addAmount);
    assertEquals(MAX_SUGAR, added);
    assertEquals(MAX_SUGAR, cell.getSugar());
  }

  @Test
  void removeSugar_WithinAvailable_RemovesFullAmount() {
    cell.setSugar(MAX_SUGAR);
    int removeAmount = 5;
    int removed = cell.removeSugar(removeAmount);
    assertEquals(removeAmount, removed);
    assertEquals(MAX_SUGAR - removeAmount, cell.getSugar());
  }

  @Test
  void removeSugar_BeyondAvailable_RemovesPartialAmount() {
    cell.setSugar(5);
    int removeAmount = 10;
    int removed = cell.removeSugar(removeAmount);
    assertEquals(5, removed);
    assertEquals(0, cell.getSugar());
  }

  @Test
  void distanceTo_WithValidCell_CalculatesManhattanDistance() {
    SugarCell other = new SugarCell(4, 6, SugarScapeState.EMPTY);
    int expectedDistance = Math.abs(4 - ROW) + Math.abs(6 - COL);
    assertEquals(expectedDistance, cell.distanceTo(other));
  }

  @Test
  void distanceTo_WithNonSugarCell_ReturnsZero() {
    Cell other = new Cell(SugarScapeState.EMPTY);
    assertEquals(0, cell.distanceTo(other));
  }

  @Test
  void hasMoreSugarThan_WithLessSugar_ReturnsFalse() {
    SugarCell other = new SugarCell(0, 0, SugarScapeState.EMPTY);
    cell.setSugar(5);
    other.setSugar(10);
    assertFalse(cell.hasMoreSugarThan(other));
  }

  @Test
  void hasMoreSugarThan_WithMoreSugar_ReturnsTrue() {
    SugarCell other = new SugarCell(0, 0, SugarScapeState.EMPTY);
    cell.setSugar(10);
    other.setSugar(5);
    assertTrue(cell.hasMoreSugarThan(other));
  }
}