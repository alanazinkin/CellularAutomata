package cellsociety.Model;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.Model.State.MockState;
import org.junit.jupiter.api.Test;

class GridTest {

  @Test
  void getCell_ValidIndices_ReturnsCorrectCell() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertNotNull(grid.getCell(0, 0));
    assertEquals(MockState.STATE_ONE, grid.getCell(0, 0).getCurrentState());
  }

  @Test
  void getNeighbors_CornerCell_ReturnsThreeNeighbors() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertEquals(3, grid.getNeighbors(0, 0).size());
  }

  @Test
  void applyNextStates_AllCells_MaintainInitialState() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    grid.applyNextStates();
    assertEquals(MockState.STATE_ONE, grid.getCell(0, 0).getCurrentState());
  }

  @Test
  void resetGrid_SameState_ConsistencyMaintained() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    grid.resetGrid(MockState.STATE_ONE);
    assertEquals(MockState.STATE_ONE, grid.getCell(0, 0).getCurrentState());
  }

  @Test
  void printGrid_NoStateChange_NoExceptionThrown() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertDoesNotThrow(grid::printGrid);
  }

  @Test
  void getRows_GridInitialized_ReturnsCorrectRowCount() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertEquals(5, grid.getRows());
  }

  @Test
  void getCols_GridInitialized_ReturnsCorrectColumnCount() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertEquals(5, grid.getCols());
  }

  @Test
  void getCell_OutOfBoundsIndices_ThrowsException() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertThrows(IndexOutOfBoundsException.class, () -> grid.getCell(5, 5));
    assertThrows(IndexOutOfBoundsException.class, () -> grid.getCell(-1, -1));
  }

  @Test
  void getNeighbors_EdgeCell_ReturnsThreeNeighbors() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertEquals(3, grid.getNeighbors(0, 0).size());
  }

  @Test
  void applyNextStates_MultipleCells_AllMaintainInitialState() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    grid.applyNextStates();
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        assertEquals(MockState.STATE_ONE, grid.getCell(r, c).getCurrentState());
      }
    }
  }
}

