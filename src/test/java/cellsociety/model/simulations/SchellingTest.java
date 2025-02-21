package cellsociety.model.simulations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.AgentCell;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.state.SchellingState;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

/**
 * The SchellingTest class simulates a simple agent-based model based on Thomas Schelling's model of
 * segregation. The class tests the behavior of agents within a grid, where agents can be in
 * different groups and move based on their surrounding neighbors. This model uses a set of
 * predefined constants to represent agent states and groups, and provides methods for simulating
 * the movement of agents to create a pattern of segregation.
 *
 * <p>The constants defined in this class represent different states
 * for agents and groups within the simulation:</p>
 *
 * <ul>
 *   <li>{@link #EMPTY_AGENT_GROUP} represents an empty group.</li>
 *   <li>{@link #AGENT_STATE_KEY} represents the state of an agent.</li>
 * </ul>
 *
 * @author Tatum McKinnis
 */

public class SchellingTest {

  private static final int EMPTY_AGENT_GROUP = 0;
  private static final int AGENT_STATE_KEY = 1;

  /**
   * Replaces a cell in the grid with a new cell.
   *
   * @param grid the grid where the cell is located
   * @param row  the row index of the cell
   * @param col  the column index of the cell
   * @param cell the new cell to be placed in the grid
   */
  private void replaceCell(Grid grid, int row, int col, Cell cell) {
    grid.setCellAt(row, col, cell);
  }

  /**
   * Creates a simulation configuration for the Schelling model.
   *
   * @param rows the number of rows in the grid
   * @param cols the number of columns in the grid
   * @return a configured SimulationConfig instance
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
   * Tests that a lone agent with no neighbors remains satisfied.
   */
  @Test
  void applyRules_LoneAgentWithNoNeighbors_RemainsSatisfied() {
    Grid grid = new Grid(1, 1, SchellingState.EMPTY_CELL);

    AgentCell testCell = new AgentCell(SchellingState.AGENT, AGENT_STATE_KEY);
    replaceCell(grid, 0, 0, testCell);

    SimulationConfig simConfig = createSchellingSimConfig(1, 1);
    Schelling simulation = new Schelling(simConfig, grid, 0.3);

    AgentCell initialCell = (AgentCell) grid.getCell(0, 0);
    assertEquals(SchellingState.AGENT, initialCell.getCurrentState(),
        "Initial state should be AGENT");
    assertEquals(AGENT_STATE_KEY, initialCell.getAgentGroup(),
        "Initial group should be AGENT_STATE_KEY");

    simulation.applyRules();
    grid.applyNextStates();

    AgentCell finalCell = (AgentCell) grid.getCell(0, 0);
    assertEquals(SchellingState.AGENT, finalCell.getCurrentState(),
        "Agent should remain in place as it has no neighbors.");
    assertEquals(AGENT_STATE_KEY, finalCell.getAgentGroup(),
        "Agent group should remain unchanged.");
  }

  /**
   * Tests that an unsatisfied agent in a 2x2 grid moves to an empty cell.
   */
  @Test
  void applyRules_UnsatisfiedAgentIn2x2_MovesToEmptyCell() {
    Grid grid = new Grid(2, 2, SchellingState.EMPTY_CELL);

    AgentCell agent1 = new AgentCell(SchellingState.AGENT, AGENT_STATE_KEY);
    AgentCell agent2 = new AgentCell(SchellingState.AGENT, 2);
    AgentCell empty1 = new AgentCell(SchellingState.EMPTY_CELL, EMPTY_AGENT_GROUP);
    AgentCell empty2 = new AgentCell(SchellingState.EMPTY_CELL, EMPTY_AGENT_GROUP);

    replaceCell(grid, 0, 0, agent1);
    replaceCell(grid, 0, 1, agent2);
    replaceCell(grid, 1, 0, empty1);
    replaceCell(grid, 1, 1, empty2);

    SimulationConfig simConfig = createSchellingSimConfig(2, 2);
    Schelling simulation = new Schelling(simConfig, grid, 0.8);

    simulation.applyRules();
    grid.applyNextStates();

    AgentCell sourceCell = (AgentCell) grid.getCell(0, 0);
    assertEquals(SchellingState.EMPTY_CELL, sourceCell.getCurrentState(),
        "Original cell should now be empty");
    assertEquals(EMPTY_AGENT_GROUP, sourceCell.getAgentGroup(),
        "Original cell should have empty group");

    AgentCell destCell1 = (AgentCell) grid.getCell(1, 0);
    AgentCell destCell2 = (AgentCell) grid.getCell(1, 1);
    boolean moved = (destCell1.getCurrentState() == SchellingState.AGENT &&
        destCell1.getAgentGroup() == AGENT_STATE_KEY) ||
        (destCell2.getCurrentState() == SchellingState.AGENT &&
            destCell2.getAgentGroup() == AGENT_STATE_KEY);
    assertTrue(moved, "Agent should have moved to one of the empty cells");
  }

  /**
   * Tests that passing a null grid to the constructor throws an IllegalArgumentException.
   */
  @Test
  void applyRules_NullGrid_ThrowsIllegalArgumentException() {
    SimulationConfig simConfig = createSchellingSimConfig(1, 1);
    assertThrows(IllegalArgumentException.class, () -> new Schelling(simConfig, null, 0.5),
        "Schelling simulation constructor should throw IllegalArgumentException when grid is null.");
  }

  /**
   * Tests that a regular Cell is automatically converted to an AgentCell
   */
  @Test
  void applyRules_NonAgentCell_ConvertsToAgentCell() {
    Grid grid = new Grid(1, 1, SchellingState.EMPTY_CELL);

    Cell regularCell = new Cell(SchellingState.AGENT);
    replaceCell(grid, 0, 0, regularCell);

    SimulationConfig simConfig = createSchellingSimConfig(1, 1);
    Schelling simulation = new Schelling(simConfig, grid, 0.5);

    simulation.applyRules();

    Cell convertedCell = grid.getCell(0, 0);
    assertTrue(convertedCell instanceof AgentCell,
        "Regular Cell should be converted to AgentCell");
    assertEquals(SchellingState.AGENT, convertedCell.getCurrentState(),
        "Converted cell should maintain AGENT state");
    assertTrue(((AgentCell)convertedCell).getAgentGroup() > 0,
        "Converted cell should have a valid agent group");
  }


  /**
   * Tests that a negative tolerance value in the constructor throws an IllegalArgumentException.
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
