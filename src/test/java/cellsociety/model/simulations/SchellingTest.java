package cellsociety.model.simulations;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.AgentCell;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.state.SchellingState;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests for the {@link Schelling} simulation. Naming convention:
 * [UnitOfWork_StateUnderTest_ExpectedBehavior]
 */
public class SchellingTest {

  /**
   * Helper method to replace a cell in the grid using the public API.
   *
   * @param grid the grid whose cell will be replaced
   * @param row  the row index of the cell to replace
   * @param col  the column index of the cell to replace
   * @param cell the new cell to insert at the specified location
   */
  private void replaceCell(Grid grid, int row, int col, Cell cell) {
    grid.setCellAt(row, col, cell);
  }

  /**
   * Creates a SimulationConfig instance for Schelling simulation tests. Dummy values are provided
   * for type, title, author, description, dimensions, initialStates, and parameters.
   *
   * @param rows the number of rows in the grid
   * @param cols the number of columns in the grid
   * @return a new SimulationConfig instance
   */
  private SimulationConfig createSchellingSimConfig(int rows, int cols) {
    return new SimulationConfig(
        "Schelling",
        "Schelling Simulation",
        "Test Author",
        "Testing Schelling simulation",
        rows, cols,
        new int[rows * cols],
        new HashMap<>()
    );
  }

  /**
   * applyRules: In a 1×1 grid the lone agent has no neighbors and is automatically satisfied.
   * Input: Single agent in a 1x1 grid. Expected: The agent remains unchanged.
   */
  @Test
  void applyRules_LoneAgentWithNoNeighbors_RemainsSatisfied() {
    Grid grid = new Grid(1, 1, SchellingState.EMPTY_CELL);
    AgentCell testCell = new AgentCell(SchellingState.AGENT, 1);
    replaceCell(grid, 0, 0, testCell);

    SimulationConfig simConfig = createSchellingSimConfig(1, 1);
    Schelling simulation = new Schelling(simConfig, grid, 0.5);
    simulation.applyRules();

    AgentCell cell = (AgentCell) grid.getCell(0, 0);
    assertEquals(SchellingState.AGENT, cell.getCurrentState(),
        "Agent should remain in place as it has no neighbors.");
    assertEquals(1, cell.getAgentGroup(), "Agent group should remain unchanged.");
  }

  /**
   * applyRules: An unsatisfied agent in a 2×2 grid moves to an empty cell where it is satisfied.
   * Input: 2×2 grid with an unsatisfied agent (group 1) and a neighbor agent (group 2); two cells
   * are empty. Expected: The unsatisfied agent leaves its original cell and moves to one of the
   * empty cells.
   */
  @Test
  void applyRules_UnsatisfiedAgentIn2x2_MovesToEmptyCell() {
    Grid grid = new Grid(2, 2, SchellingState.EMPTY_CELL);
    AgentCell agentCell1 = new AgentCell(SchellingState.AGENT,
        1); // unsatisfied due to neighbor group mismatch
    AgentCell agentCell2 = new AgentCell(SchellingState.AGENT, 2);
    AgentCell emptyCell1 = new AgentCell(SchellingState.EMPTY_CELL, 0);
    AgentCell emptyCell2 = new AgentCell(SchellingState.EMPTY_CELL, 0);

    replaceCell(grid, 0, 0, agentCell1);
    replaceCell(grid, 0, 1, agentCell2);
    replaceCell(grid, 1, 0, emptyCell1);
    replaceCell(grid, 1, 1, emptyCell2);

    SimulationConfig simConfig = createSchellingSimConfig(2, 2);
    Schelling simulation = new Schelling(simConfig, grid, 0.5);
    simulation.applyRules();

    AgentCell sourceCell = (AgentCell) grid.getCell(0, 0);
    assertEquals(SchellingState.EMPTY_CELL, sourceCell.getCurrentState(),
        "The unsatisfied agent should leave its original cell.");

    AgentCell destCell1 = (AgentCell) grid.getCell(1, 0);
    AgentCell destCell2 = (AgentCell) grid.getCell(1, 1);
    boolean moved =
        (destCell1.getCurrentState() == SchellingState.AGENT && destCell1.getAgentGroup() == 1) ||
            (destCell2.getCurrentState() == SchellingState.AGENT && destCell2.getAgentGroup() == 1);
    assertTrue(moved, "One of the empty cells should now contain the moving agent from (0,0).");
  }

  /**
   * Tests the behavior of {@code applyRules()} when a {@code null} grid is passed to the
   * {@code Schelling} simulation.
   * <p>
   * This test verifies that attempting to invoke {@code applyRules()} on a {@code Schelling}
   * simulation instantiated with a {@code null} grid results in an
   * {@code IllegalArgumentException}.
   * </p>
   * <p>
   * Expected behavior:
   * <ul>
   *   <li>When the simulation is created with a {@code null} grid, an {@code IllegalArgumentException} should be thrown in the constructor.</li>
   * </ul>
   * </p>
   *
   * @throws IllegalArgumentException if a {@code null} grid is provided to the {@code Schelling}
   *                                  simulation.
   */
  @Test
  void applyRules_NullGrid_ThrowsIllegalArgumentException() {
    SimulationConfig simConfig = createSchellingSimConfig(1, 1);
    assertThrows(IllegalArgumentException.class, () -> new Schelling(simConfig, null, 0.5),
        "Schelling simulation constructor should throw IllegalArgumentException when grid is null.");
  }

  /**
   * applyRules: If the grid contains a cell that is not an AgentCell, applyRules() should throw a
   * ClassCastException. Input: A grid cell is replaced with a non-AgentCell.
   */
  @Test
  void applyRules_InvalidCellTypeInGrid_ThrowsClassCastException() {
    Grid grid = new Grid(1, 1, SchellingState.EMPTY_CELL);
    // Insert a non-AgentCell into the grid.
    Cell invalidCell = new Cell(SchellingState.AGENT);
    replaceCell(grid, 0, 0, invalidCell);

    SimulationConfig simConfig = createSchellingSimConfig(1, 1);
    Schelling simulation = new Schelling(simConfig, grid, 0.5);
    assertThrows(ClassCastException.class, simulation::applyRules,
        "applyRules() should throw ClassCastException when a cell is not an AgentCell.");
  }

  /**
   * constructor: Verifies that creating a Schelling simulation with a negative tolerance throws an
   * IllegalArgumentException. Input: A valid grid and SimulationConfig, but with a negative
   * tolerance value. Expected: The constructor throws an IllegalArgumentException with the message
   * "Tolerance must be between 0.0 and 1.0."
   */
  @Test
  void constructor_NegativeTolerance_ThrowsIllegalArgumentException() {
    Grid grid = new Grid(1, 1, SchellingState.EMPTY_CELL);
    SimulationConfig simConfig = createSchellingSimConfig(1, 1);
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> new Schelling(simConfig, grid, -0.1),
        "Schelling simulation constructor should throw IllegalArgumentException when negative tolerance is provided.");
    assertEquals("Tolerance must be between 0.0 and 1.0.", exception.getMessage(),
        "Exception message should indicate the valid tolerance range.");
  }
}



