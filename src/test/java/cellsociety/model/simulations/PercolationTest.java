package cellsociety.model.simulations;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Grid;
import cellsociety.model.state.PercolationState;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests for the {@link Percolation} class. Naming convention:
 * [UnitOfWork_StateUnderTest_ExpectedBehavior]
 */
class PercolationTest {

  private static final double PROBABILITY_ONE = 1.0;
  private static final double PROBABILITY_ZERO = 0.0;
  private static final double INVALID_PROBABILITY_NEGATIVE = -0.1;
  private static final double INVALID_PROBABILITY_OVER_ONE = 1.1;

  /**
   * Creates a simple 2x2 grid for testing. The grid is initialized with
   * {@code PercolationState.OPEN} for all cells.
   */
  private Grid createTestGrid() {
    return new Grid(2, 2, PercolationState.OPEN);
  }

  /**
   * Creates a SimulationConfig instance for Percolation simulation tests. Dummy values are provided
   * for type, title, author, description, dimensions, initialStates, and parameters.
   */
  private SimulationConfig createSimulationConfigForPercolation() {
    return new SimulationConfig(
        "Percolation",
        "Percolation Simulation",
        "Test Author",
        "Testing Percolation simulation",
        2, 2,
        new int[4],
        new HashMap<>()
    );
  }

  /**
   * applyRules: Open cells adjacent to a percolated cell become percolated with probability 1.
   * Input: Cell (0,0) is set to PERCOLATED and probability is set to 1.0.
   */
  @Test
  void applyRules_OpenCellWithPercolatedNeighbor_BecomesPercolated() {
    Grid grid = createTestGrid();
    grid.getCell(0, 0).setCurrentState(PercolationState.PERCOLATED);
    SimulationConfig simConfig = createSimulationConfigForPercolation();
    Percolation simulation = new Percolation(simConfig, grid, PROBABILITY_ONE);

    simulation.applyRules();
    grid.applyNextStates();

    // All adjacent cells should become percolated due to propagation.
    assertEquals(PercolationState.PERCOLATED, grid.getCell(0, 1).getCurrentState(),
        "Cell (0,1) should become percolated.");
    assertEquals(PercolationState.PERCOLATED, grid.getCell(1, 0).getCurrentState(),
        "Cell (1,0) should become percolated.");
    assertEquals(PercolationState.PERCOLATED, grid.getCell(1, 1).getCurrentState(),
        "Cell (1,1) should become percolated.");
  }

  /**
   * applyRules: Open cells remain unchanged if no percolated neighbor exists. Input: The grid
   * contains no percolated neighbor for cell (0,0) (and one cell is BLOCKED).
   */
  @Test
  void applyRules_OpenCellsWithoutPercolatedNeighbor_RemainUnchanged() {
    Grid grid = new Grid(2, 2, PercolationState.OPEN);
    grid.getCell(0, 1).setCurrentState(PercolationState.BLOCKED);
    SimulationConfig simConfig = createSimulationConfigForPercolation();
    Percolation simulation = new Percolation(simConfig, grid, PROBABILITY_ONE);

    simulation.applyRules();
    grid.applyNextStates();

    assertEquals(PercolationState.OPEN, grid.getCell(0, 0).getCurrentState(),
        "Cell (0,0) should remain open.");
    assertEquals(PercolationState.BLOCKED, grid.getCell(0, 1).getCurrentState(),
        "Cell (0,1) should remain blocked.");
    assertEquals(PercolationState.OPEN, grid.getCell(1, 0).getCurrentState(),
        "Cell (1,0) should remain open.");
    assertEquals(PercolationState.OPEN, grid.getCell(1, 1).getCurrentState(),
        "Cell (1,1) should remain open.");
  }

  /**
   * applyRules: With a percolation probability of 0, even cells with a percolated neighbor remain
   * unchanged. Input: Cell (0,0) is set to PERCOLATED but the probability is 0.0.
   */
  @Test
  void applyRules_WithProbabilityZero_OpenCellsRemainOpen() {
    Grid grid = new Grid(2, 2, PercolationState.OPEN);
    grid.getCell(0, 0).setCurrentState(PercolationState.PERCOLATED);
    SimulationConfig simConfig = createSimulationConfigForPercolation();
    Percolation simulation = new Percolation(simConfig, grid, PROBABILITY_ZERO);

    simulation.applyRules();
    grid.applyNextStates();

    assertEquals(PercolationState.PERCOLATED, grid.getCell(0, 0).getCurrentState(),
        "Cell (0,0) should remain percolated.");
    assertEquals(PercolationState.OPEN, grid.getCell(0, 1).getCurrentState(),
        "Cell (0,1) should remain open due to zero probability.");
    assertEquals(PercolationState.OPEN, grid.getCell(1, 0).getCurrentState(),
        "Cell (1,0) should remain open due to zero probability.");
    assertEquals(PercolationState.OPEN, grid.getCell(1, 1).getCurrentState(),
        "Cell (1,1) should remain open due to zero probability.");
  }

  /**
   * initializeColorMap: Returns the correct mapping of Percolation states to colors. Input: A grid
   * with default state OPEN.
   */
  @Test
  void initializeColorMap_StateMappingIsCorrect() {
    Grid grid = createTestGrid();
    SimulationConfig simConfig = createSimulationConfigForPercolation();
    Percolation simulation = new Percolation(simConfig, grid, PROBABILITY_ONE);
    Map<?, ?> stateMap = simulation.initializeColorMap();

    assertAll("State Map Validity",
        () -> assertEquals(Color.WHITE, stateMap.get(PercolationState.OPEN),
            "PercolationState.OPEN should map to WHITE."),
        () -> assertEquals(Color.LIGHTBLUE, stateMap.get(PercolationState.PERCOLATED),
            "PercolationState.PERCOLATED should map to LIGHTBLUE."),
        () -> assertEquals(Color.BLACK, stateMap.get(PercolationState.BLOCKED),
            "PercolationState.BLOCKED should map to BLACK.")
    );
  }

  /**
   * PercolationConstructor: Constructing with a null grid should throw an IllegalArgumentException.
   * Input: Null grid.
   */
  @Test
  void PercolationConstructor_NullGrid_ThrowsIllegalArgumentException() {
    SimulationConfig simConfig = createSimulationConfigForPercolation();
    assertThrows(IllegalArgumentException.class, () -> {
          new Percolation(simConfig, null, PROBABILITY_ONE);
        },
        "Constructing a Percolation simulation with a null grid should throw an IllegalArgumentException.");
  }

  /**
   * PercolationConstructor: Constructing with an invalid probability should throw an
   * IllegalArgumentException. Input: Negative probability and probability greater than 1.
   */
  @Test
  void PercolationConstructor_InvalidProbability_ThrowsIllegalArgumentException() {
    Grid grid = createTestGrid();
    SimulationConfig simConfig = createSimulationConfigForPercolation();
    assertAll("Invalid probabilities",
        () -> assertThrows(IllegalArgumentException.class,
            () -> new Percolation(simConfig, grid, INVALID_PROBABILITY_NEGATIVE),
            "Constructing with a negative probability should throw an exception."),
        () -> assertThrows(IllegalArgumentException.class,
            () -> new Percolation(simConfig, grid, INVALID_PROBABILITY_OVER_ONE),
            "Constructing with a probability greater than 1 should throw an exception.")
    );
  }
}

