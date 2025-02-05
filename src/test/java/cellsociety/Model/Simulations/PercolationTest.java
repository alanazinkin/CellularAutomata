package cellsociety.Model.Simulations;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.Model.Grid;
import cellsociety.Model.State.PercolationState;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests for the {@link Percolation} class.
 * <p>
 * This class contains both positive tests (to verify expected behavior) and negative tests
 * to ensure that invalid parameters cause exceptions.
 * </p>
 */
class PercolationTest {

  private static final double PROBABILITY_ONE = 1.0;
  private static final double PROBABILITY_ZERO = 0.0;
  private static final double INVALID_PROBABILITY_NEGATIVE = -0.1;
  private static final double INVALID_PROBABILITY_OVER_ONE = 1.1;

  /**
   * Utility method to create a simple 2x2 grid for testing.
   * The grid is initialized with {@code PercolationState.OPEN} for all cells.
   *
   * @return a new {@code Grid} configured for testing.
   */
  private Grid createTestGrid() {
    return new Grid(2, 2, PercolationState.OPEN);
  }

  /**
   * Tests that {@code applyRules()} correctly percolates open cells adjacent to a percolated cell.
   * <p>
   * This test sets up a 2x2 grid where cell (0,0) is set to PERCOLATED and uses a probability
   * of 1.0 to guarantee that adjacent open cells become percolated.
   * </p>
   */
  @Test
  void testApplyRulesOpenToPercolated() {
    Grid grid = new Grid(2, 2, PercolationState.OPEN);
    grid.getCell(0, 0).setState(PercolationState.PERCOLATED);
    Percolation simulation = new Percolation(grid, PROBABILITY_ONE);

    simulation.applyRules();
    grid.applyNextStates();

    assertEquals(PercolationState.PERCOLATED, grid.getCell(0, 1).getState(),
        "Cell (0,1) should become percolated.");
    assertEquals(PercolationState.PERCOLATED, grid.getCell(1, 0).getState(),
        "Cell (1,0) should become percolated.");
    assertEquals(PercolationState.PERCOLATED, grid.getCell(1, 1).getState(),
        "Cell (1,1) should become percolated.");
  }

  /**
   * Tests that {@code applyRules()} leaves open cells unchanged when no percolated neighbors exist.
   */
  @Test
  void testApplyRulesNoPercolation() {
    Grid grid = new Grid(2, 2, PercolationState.OPEN);
    grid.getCell(0, 1).setState(PercolationState.BLOCKED);
    Percolation simulation = new Percolation(grid, PROBABILITY_ONE);

    simulation.applyRules();
    grid.applyNextStates();

    assertEquals(PercolationState.OPEN, grid.getCell(0, 0).getState(),
        "Cell (0,0) should remain open.");
    assertEquals(PercolationState.BLOCKED, grid.getCell(0, 1).getState(),
        "Cell (0,1) should remain blocked.");
    assertEquals(PercolationState.OPEN, grid.getCell(1, 0).getState(),
        "Cell (1,0) should remain open.");
    assertEquals(PercolationState.OPEN, grid.getCell(1, 1).getState(),
        "Cell (1,1) should remain open.");
  }

  /**
   * Tests that with a percolation probability of 0.0, open cells remain open even if they have percolated neighbors.
   */
  @Test
  void testApplyRulesProbabilityZero() {
    Grid grid = new Grid(2, 2, PercolationState.OPEN);
    grid.getCell(0, 0).setState(PercolationState.PERCOLATED);
    Percolation simulation = new Percolation(grid, PROBABILITY_ZERO);

    simulation.applyRules();
    grid.applyNextStates();

    assertEquals(PercolationState.PERCOLATED, grid.getCell(0, 0).getState(),
        "Cell (0,0) should remain percolated.");
    assertEquals(PercolationState.OPEN, grid.getCell(0, 1).getState(),
        "Cell (0,1) should remain open due to zero probability.");
    assertEquals(PercolationState.OPEN, grid.getCell(1, 0).getState(),
        "Cell (1,0) should remain open due to zero probability.");
    assertEquals(PercolationState.OPEN, grid.getCell(1, 1).getState(),
        "Cell (1,1) should remain open due to zero probability.");
  }

  /**
   * Tests that {@code initializeStateMap()} returns the correct mapping of Percolation states to colors.
   */
  @Test
  void testInitializeStateMap() {
    Grid grid = createTestGrid();
    Percolation simulation = new Percolation(grid, PROBABILITY_ONE);
    Map<?, ?> stateMap = simulation.initializeStateMap();

    assertAll("State Map Validity",
        () -> assertEquals(javafx.scene.paint.Color.WHITE, stateMap.get(PercolationState.OPEN),
            "PercolationState.OPEN should map to WHITE."),
        () -> assertEquals(javafx.scene.paint.Color.LIGHTBLUE, stateMap.get(PercolationState.PERCOLATED),
            "PercolationState.PERCOLATED should map to LIGHTBLUE."),
        () -> assertEquals(javafx.scene.paint.Color.BLACK, stateMap.get(PercolationState.BLOCKED),
            "PercolationState.BLOCKED should map to BLACK.")
    );
  }

  /////////////////
  // NEGATIVE TESTS for Percolation
  /////////////////

  /**
   * Negative test to verify that constructing a Percolation simulation with a null grid throws an exception.
   */
  @Test
  void testPercolationConstructorNullGrid() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Percolation(null, PROBABILITY_ONE);
    }, "Constructing a Percolation simulation with a null grid should throw an IllegalArgumentException.");
  }

  /**
   * Negative test to verify that constructing a Percolation simulation with an invalid probability
   * (e.g., negative or greater than 1) throws an exception.
   */
  @Test
  void testPercolationConstructorInvalidProbability() {
    Grid grid = createTestGrid();
    assertAll("Invalid probabilities",
        () -> assertThrows(IllegalArgumentException.class, () -> new Percolation(grid, INVALID_PROBABILITY_NEGATIVE),
            "Constructing with a negative probability should throw an exception."),
        () -> assertThrows(IllegalArgumentException.class, () -> new Percolation(grid, INVALID_PROBABILITY_OVER_ONE),
            "Constructing with a probability greater than 1 should throw an exception.")
    );
  }
}
