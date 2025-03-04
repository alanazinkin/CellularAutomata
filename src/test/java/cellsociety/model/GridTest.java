package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.model.state.MockState;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Grid} functionality.
 * <p>
 * This class verifies grid operations such as cell retrieval, neighbor determination, state
 * transitions, grid reset, grid printing, and the new edge and neighborhood strategy
 * functionality.
 * </p>
 *
 * @author Tatum McKinnis
 */
class GridTest {

  /**
   * Tests cell retrieval with valid indices.
   * <p>
   * Verifies that {@link Grid#getCell(int, int)} returns a cell with the expected initial state.
   * </p>
   */
  @Test
  void getCell_ValidIndices_ReturnsCorrectCell() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertNotNull(grid.getCell(0, 0));
    assertEquals(MockState.STATE_ONE, grid.getCell(0, 0).getCurrentState());
  }

  /**
   * Tests neighbor retrieval for a corner cell.
   * <p>
   * Verifies that a corner cell has exactly three neighbors.
   * </p>
   */
  @Test
  void getNeighbors_CornerCell_ReturnsThreeNeighbors() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertEquals(3, grid.getNeighbors(0, 0).size());
  }

  /**
   * Tests applying next states for all cells in the grid.
   * <p>
   * Verifies that after applying next states, cells maintain their initial state.
   * </p>
   */
  @Test
  void applyNextStates_AllCells_MaintainInitialState() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    grid.applyNextStates();
    assertEquals(MockState.STATE_ONE, grid.getCell(0, 0).getCurrentState());
  }

  /**
   * Tests grid reset functionality.
   * <p>
   * Verifies that resetting the grid with the same state maintains consistency across all cells.
   * </p>
   */
  @Test
  void resetGrid_SameState_ConsistencyMaintained() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    grid.resetGrid(MockState.STATE_ONE);
    assertEquals(MockState.STATE_ONE, grid.getCell(0, 0).getCurrentState());
  }

  /**
   * Tests grid printing functionality.
   * <p>
   * Verifies that calling {@link Grid#printGrid()} does not throw any exceptions.
   * </p>
   */
  @Test
  void printGrid_NoStateChange_NoExceptionThrown() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertDoesNotThrow(grid::printGrid);
  }

  /**
   * Tests retrieval of the number of rows in the grid.
   * <p>
   * Verifies that {@link Grid#getRows()} returns the correct row count.
   * </p>
   */
  @Test
  void getRows_GridInitialized_ReturnsCorrectRowCount() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertEquals(5, grid.getRows());
  }

  /**
   * Tests retrieval of the number of columns in the grid.
   * <p>
   * Verifies that {@link Grid#getCols()} returns the correct column count.
   * </p>
   */
  @Test
  void getCols_GridInitialized_ReturnsCorrectColumnCount() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertEquals(5, grid.getCols());
  }

  /**
   * Tests cell retrieval with out-of-bounds indices.
   * <p>
   * Verifies that {@link Grid#getCell(int, int)} throws an {@link IndexOutOfBoundsException} for
   * invalid indices.
   * </p>
   */
  @Test
  void getCell_OutOfBoundsIndices_ThrowsException() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertThrows(IndexOutOfBoundsException.class, () -> grid.getCell(5, 5));
    assertThrows(IndexOutOfBoundsException.class, () -> grid.getCell(-1, -1));
  }

  /**
   * Tests neighbor retrieval for an edge cell.
   * <p>
   * Verifies that an edge cell (in this case a corner cell) returns exactly three neighbors.
   * </p>
   */
  @Test
  void getNeighbors_EdgeCell_ReturnsThreeNeighbors() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    assertEquals(3, grid.getNeighbors(0, 0).size());
  }

  /**
   * Tests applying next states for multiple cells in the grid.
   * <p>
   * Verifies that after applying next states, every cell in the grid maintains the initial state.
   * </p>
   */
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


  /**
   * Tests grid initialization with custom edge and neighborhood strategies.
   * <p>
   * Verifies that a grid can be successfully initialized with custom strategies.
   * </p>
   */
  @Test
  void gridConstructor_WithStrategies_InitializesCorrectly() {
    EdgeStrategy edgeStrategy = new BoundedEdge();
    NeighborhoodStrategy neighborhoodStrategy = new MooreNeighborhood();

    Grid grid = new Grid(5, 5, MockState.STATE_ONE, edgeStrategy, neighborhoodStrategy);

    assertNotNull(grid);
    assertEquals(5, grid.getRows());
    assertEquals(5, grid.getCols());
    assertEquals(MockState.STATE_ONE, grid.getCell(0, 0).getCurrentState());
    assertSame(edgeStrategy, grid.getEdgeStrategy());
    assertSame(neighborhoodStrategy, grid.getNeighborhoodStrategy());
  }

  /**
   * Tests setting a new edge strategy on an existing grid.
   * <p>
   * Verifies that the edge strategy can be changed and takes effect.
   * </p>
   */
  @Test
  void setEdgeStrategy_NewStrategy_StrategyIsUpdated() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    EdgeStrategy newEdgeStrategy = new ToroidalEdge();

    grid.setEdgeStrategy(newEdgeStrategy);

    assertSame(newEdgeStrategy, grid.getEdgeStrategy());
  }

  /**
   * Tests setting a new neighborhood strategy on an existing grid.
   * <p>
   * Verifies that the neighborhood strategy can be changed and takes effect.
   * </p>
   */
  @Test
  void setNeighborhoodStrategy_NewStrategy_StrategyIsUpdated() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    NeighborhoodStrategy newNeighborhoodStrategy = new VonNeumannNeighborhood();

    grid.setNeighborhoodStrategy(newNeighborhoodStrategy);

    assertSame(newNeighborhoodStrategy, grid.getNeighborhoodStrategy());
  }

  /**
   * Tests setting null edge strategy.
   * <p>
   * Verifies that setting a null edge strategy throws a NullPointerException.
   * </p>
   */
  @Test
  void setEdgeStrategy_NullStrategy_ThrowsException() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    assertThrows(NullPointerException.class, () -> grid.setEdgeStrategy(null));
  }

  /**
   * Tests setting null neighborhood strategy.
   * <p>
   * Verifies that setting a null neighborhood strategy throws a NullPointerException.
   * </p>
   */
  @Test
  void setNeighborhoodStrategy_NullStrategy_ThrowsException() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    assertThrows(NullPointerException.class, () -> grid.setNeighborhoodStrategy(null));
  }

  /**
   * Tests changing the edge strategy and how it affects neighbor counts.
   * <p>
   * Verifies that using a toroidal edge strategy allows wrapping and increases neighbor count.
   * </p>
   */
  @Test
  void getNeighbors_ToroidalEdge_IncreaseNeighborCount() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    int originalNeighborCount = grid.getNeighbors(0, 0).size();

    grid.setEdgeStrategy(new ToroidalEdge());

    int newNeighborCount = grid.getNeighbors(0, 0).size();
    assertTrue(newNeighborCount > originalNeighborCount);
    assertEquals(8, newNeighborCount);
  }

  /**
   * Tests changing the neighborhood strategy and how it affects neighbor counts.
   * <p>
   * Verifies that using a Von Neumann neighborhood reduces neighbor count for interior cells.
   * </p>
   */
  @Test
  void getNeighbors_VonNeumannNeighborhood_ReducesNeighborCount() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);
    int originalNeighborCount = grid.getNeighbors(2, 2).size();
    assertEquals(8, originalNeighborCount);

    grid.setNeighborhoodStrategy(new VonNeumannNeighborhood());

    int newNeighborCount = grid.getNeighbors(2, 2).size();
    assertEquals(4, newNeighborCount); // Interior with von Neumann should have 4 neighbors
  }

  /**
   * Tests validation of positions with bounded edge strategy.
   * <p>
   * Verifies that positions outside the grid are identified as invalid.
   * </p>
   */
  @Test
  void isValidPosition_BoundedEdge_OutOfBoundsIsInvalid() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE, new BoundedEdge(), new MooreNeighborhood());

    assertTrue(grid.isValidPosition(0, 0));
    assertTrue(grid.isValidPosition(4, 4));
    assertFalse(grid.isValidPosition(-1, 0));
    assertFalse(grid.isValidPosition(0, -1));
    assertFalse(grid.isValidPosition(5, 0));
    assertFalse(grid.isValidPosition(0, 5));
  }

  /**
   * Tests validation of positions with toroidal edge strategy.
   * <p>
   * Verifies that all positions are considered valid with toroidal edge.
   * </p>
   */
  @Test
  void isValidPosition_ToroidalEdge_AllPositionsAreValid() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE, new ToroidalEdge(), new MooreNeighborhood());

    assertTrue(grid.isValidPosition(0, 0));
    assertTrue(grid.isValidPosition(4, 4));
    assertTrue(grid.isValidPosition(-1, 0));
    assertTrue(grid.isValidPosition(0, -1));
    assertTrue(grid.isValidPosition(5, 0));
    assertTrue(grid.isValidPosition(0, 5));
    assertTrue(grid.isValidPosition(10, 10));
  }

  /**
   * Tests the default edge and neighborhood strategies.
   * <p>
   * Verifies that a grid created without explicit strategies uses bounded edge and Moore
   * neighborhood.
   * </p>
   */
  @Test
  void gridConstructor_DefaultStrategies_UsesBoundedEdgeAndMooreNeighborhood() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE);

    assertTrue(grid.getEdgeStrategy() instanceof BoundedEdge);
    assertTrue(grid.getNeighborhoodStrategy() instanceof MooreNeighborhood);
  }

  /**
   * Tests getting neighbors from a cell at grid edge with Von Neumann neighborhood.
   * <p>
   * Verifies that an edge cell with Von Neumann neighborhood has only 2 neighbors.
   * </p>
   */
  @Test
  void getNeighbors_EdgeCellWithVonNeumann_ReturnsTwoNeighbors() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE,
        new BoundedEdge(), new VonNeumannNeighborhood());

    List<Cell> neighbors = grid.getNeighbors(0, 0);
    assertEquals(2, neighbors.size());
  }

  /**
   * Tests getting neighbors with extended Moore neighborhood.
   * <p>
   * Verifies that an extended Moore neighborhood includes cells beyond immediate adjacency.
   * </p>
   */
  @Test
  void getNeighbors_ExtendedMooreNeighborhood_ReturnsMoreNeighbors() {
    Grid grid = new Grid(5, 5, MockState.STATE_ONE,
        new BoundedEdge(), new ExtendedMooreNeighborhood(2));

    List<Cell> neighbors = grid.getNeighbors(2, 2);
    assertTrue(neighbors.size() > 8);
  }
}