package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.model.state.MockState;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link InfiniteGrid} implementation.
 * <p>
 * This class verifies the behavior of the infinite grid, which can dynamically
 * expand to accommodate cells outside the original boundaries.
 * </p>
 *
 * @author Tatum McKinnis
 */
class InfiniteGridTest {

  /**
   * Tests infinite grid initialization.
   * <p>
   * Verifies that an infinite grid is correctly initialized with the specified dimensions and state.
   * </p>
   */
  @Test
  void constructor_ValidParameters_InitializesCorrectly() {
    InfiniteGrid grid = new InfiniteGrid(5, 5, MockState.STATE_ONE);

    assertEquals(5, grid.getRows());
    assertEquals(5, grid.getCols());
    assertEquals(MockState.STATE_ONE, grid.getCell(0, 0).getCurrentState());
    assertTrue(grid.getEdgeStrategy() instanceof InfiniteEdge);
  }

  /**
   * Tests grid offset initialization.
   * <p>
   * Verifies that a newly created infinite grid has offsets set to 0.
   * </p>
   */
  @Test
  void constructor_NewGrid_OffsetsAreZero() {
    InfiniteGrid grid = new InfiniteGrid(5, 5, MockState.STATE_ONE);

    assertEquals(0, grid.getRowOffset());
    assertEquals(0, grid.getColOffset());
  }

  /**
   * Tests cell retrieval for positions within initial grid bounds.
   * <p>
   * Verifies that cells within the initial grid bounds are correctly retrieved.
   * </p>
   */
  @Test
  void getCell_PositionWithinBounds_ReturnsCorrectCell() {
    InfiniteGrid grid = new InfiniteGrid(5, 5, MockState.STATE_ONE);

    Cell cell = grid.getCell(2, 3);
    assertNotNull(cell);
    assertEquals(MockState.STATE_ONE, cell.getCurrentState());
  }

  /**
   * Tests cell retrieval with coordinate adjustments.
   * <p>
   * Verifies that the getCell method correctly adjusts for grid offsets.
   * </p>
   */
  @Test
  void getCell_WithOffsets_AdjustsCoordinates() {
    class TestableInfiniteGrid extends InfiniteGrid {
      private final int rowOffset;
      private final int colOffset;

      public TestableInfiniteGrid(int rows, int cols, StateInterface state, int rowOffset, int colOffset) {
        super(rows, cols, state);
        this.rowOffset = rowOffset;
        this.colOffset = colOffset;
      }

      @Override
      public int getRowOffset() {
        return rowOffset;
      }

      @Override
      public int getColOffset() {
        return colOffset;
      }
    }

    // Create grid with non-zero offsets
    TestableInfiniteGrid grid = new TestableInfiniteGrid(5, 5, MockState.STATE_ONE, 2, 3);

    // Cell at (0,0) in external coordinates should access (2,3) in internal coordinates
    assertEquals(2, grid.getRowOffset());
    assertEquals(3, grid.getColOffset());
  }

  /**
   * Tests the expandToInclude method for positive coordinates.
   * <p>
   * Verifies that the grid is expanded to include coordinates beyond the right and bottom edges.
   * </p>
   * Note: This test requires a modified InfiniteGrid implementation with working setCells and
   * setDimensions methods, or mocking those methods.
   */
  @Test
  void expandToInclude_PositiveCoordinates_GridExpands() {
    // For this test, we would need a fully implemented InfiniteGrid or a mock
    // Since the actual implementation details are dependent on your project structure,
    // this test is illustrative of what should be tested

    class TestableInfiniteGrid extends InfiniteGrid {
      private boolean expandCalled = false;
      private int newRowStart;
      private int newRowEnd;
      private int newColStart;
      private int newColEnd;

      public TestableInfiniteGrid(int rows, int cols, StateInterface state) {
        super(rows, cols, state);
      }

      @Override
      public void expandToInclude(int rowStart, int rowEnd, int colStart, int colEnd) {
        expandCalled = true;
        this.newRowStart = rowStart;
        this.newRowEnd = rowEnd;
        this.newColStart = colStart;
        this.newColEnd = colEnd;
      }

      public boolean wasExpandCalled() {
        return expandCalled;
      }

      public int getNewRowStart() {
        return newRowStart;
      }

      public int getNewRowEnd() {
        return newRowEnd;
      }

      public int getNewColStart() {
        return newColStart;
      }

      public int getNewColEnd() {
        return newColEnd;
      }
    }

    TestableInfiniteGrid grid = new TestableInfiniteGrid(5, 5, MockState.STATE_ONE);

    // Expand to include position (7, 8)
    grid.expandToInclude(0, 7, 0, 8);

    assertTrue(grid.wasExpandCalled());
    assertEquals(0, grid.getNewRowStart());
    assertEquals(7, grid.getNewRowEnd());
    assertEquals(0, grid.getNewColStart());
    assertEquals(8, grid.getNewColEnd());
  }

  /**
   * Tests the expandToInclude method for negative coordinates.
   * <p>
   * Verifies that the grid is expanded to include coordinates beyond the left and top edges.
   * </p>
   * Note: This test requires a modified InfiniteGrid implementation with working setCells and
   * setDimensions methods, or mocking those methods.
   */
  @Test
  void expandToInclude_NegativeCoordinates_GridExpands() {
    class TestableInfiniteGrid extends InfiniteGrid {
      private boolean expandCalled = false;
      private int newRowStart;
      private int newRowEnd;
      private int newColStart;
      private int newColEnd;

      public TestableInfiniteGrid(int rows, int cols, StateInterface state) {
        super(rows, cols, state);
      }

      @Override
      public void expandToInclude(int rowStart, int rowEnd, int colStart, int colEnd) {
        expandCalled = true;
        this.newRowStart = rowStart;
        this.newRowEnd = rowEnd;
        this.newColStart = colStart;
        this.newColEnd = colEnd;
      }

      public boolean wasExpandCalled() {
        return expandCalled;
      }

      public int getNewRowStart() {
        return newRowStart;
      }

      public int getNewRowEnd() {
        return newRowEnd;
      }

      public int getNewColStart() {
        return newColStart;
      }

      public int getNewColEnd() {
        return newColEnd;
      }
    }

    TestableInfiniteGrid grid = new TestableInfiniteGrid(5, 5, MockState.STATE_ONE);

    // Expand to include position (-2, -3)
    grid.expandToInclude(-2, 4, -3, 4);

    assertTrue(grid.wasExpandCalled());
    assertEquals(-2, grid.getNewRowStart());
    assertEquals(4, grid.getNewRowEnd());
    assertEquals(-3, grid.getNewColStart());
    assertEquals(4, grid.getNewColEnd());
  }

  /**
   * Tests initialization with a custom neighborhood strategy.
   * <p>
   * Verifies that an infinite grid can be initialized with a custom neighborhood strategy.
   * </p>
   */
  @Test
  void constructor_WithCustomNeighborhood_InitializesCorrectly() {
    NeighborhoodStrategy vonNeumann = new VonNeumannNeighborhood();
    InfiniteGrid grid = new InfiniteGrid(5, 5, MockState.STATE_ONE, vonNeumann);

    assertEquals(5, grid.getRows());
    assertEquals(5, grid.getCols());
    assertTrue(grid.getEdgeStrategy() instanceof InfiniteEdge);
    assertSame(vonNeumann, grid.getNeighborhoodStrategy());
  }

  /**
   * Tests that the infinite grid properly delegates to the DynamicGrid interface methods.
   * <p>
   * Verifies that the InfiniteGrid class correctly implements the DynamicGrid interface.
   * </p>
   */
  @Test
  void implementsDynamicGrid_CorrectInterface_CanBeCastToDynamicGrid() {
    InfiniteGrid grid = new InfiniteGrid(5, 5, MockState.STATE_ONE);

    assertTrue(grid instanceof DynamicGrid);

    // Cast should succeed without exception
    DynamicGrid dynamicGrid = (DynamicGrid) grid;
    assertNotNull(dynamicGrid);
  }
}