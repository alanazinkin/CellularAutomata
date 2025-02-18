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

public class SchellingTest {
  // Add constants to match Schelling class
  private static final int EMPTY_AGENT_GROUP = 0;
  private static final int AGENT_STATE_KEY = 1;

  // Keep existing helper methods exactly as they are
  private void replaceCell(Grid grid, int row, int col, Cell cell) {
    grid.setCellAt(row, col, cell);
  }

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

  @Test
  void applyRules_LoneAgentWithNoNeighbors_RemainsSatisfied() {
    // Create a grid with all empty cells initially
    Grid grid = new Grid(1, 1, SchellingState.EMPTY_CELL);

    // Create test cell as an agent with state key 1 (type A)
    AgentCell testCell = new AgentCell(SchellingState.AGENT, AGENT_STATE_KEY);
    replaceCell(grid, 0, 0, testCell);

    // Create simulation config for 1x1 grid
    SimulationConfig simConfig = createSchellingSimConfig(1, 1);

    // Create Schelling simulation with low tolerance
    Schelling simulation = new Schelling(simConfig, grid, 0.3);

    // Verify initial state
    AgentCell initialCell = (AgentCell) grid.getCell(0, 0);
    assertEquals(SchellingState.AGENT, initialCell.getCurrentState(), "Initial state should be AGENT");
    assertEquals(AGENT_STATE_KEY, initialCell.getAgentGroup(), "Initial group should be AGENT_STATE_KEY");

    // Apply rules and update grid
    simulation.applyRules();
    grid.applyNextStates();

    // Check final state
    AgentCell finalCell = (AgentCell) grid.getCell(0, 0);
    assertEquals(SchellingState.AGENT, finalCell.getCurrentState(),
        "Agent should remain in place as it has no neighbors.");
    assertEquals(AGENT_STATE_KEY, finalCell.getAgentGroup(),
        "Agent group should remain unchanged.");
  }

  @Test
  void applyRules_UnsatisfiedAgentIn2x2_MovesToEmptyCell() {
    // Create 2x2 grid
    Grid grid = new Grid(2, 2, SchellingState.EMPTY_CELL);

    // Create cells with specific states and groups
    AgentCell agent1 = new AgentCell(SchellingState.AGENT, AGENT_STATE_KEY);
    AgentCell agent2 = new AgentCell(SchellingState.AGENT, 2);
    AgentCell empty1 = new AgentCell(SchellingState.EMPTY_CELL, EMPTY_AGENT_GROUP);
    AgentCell empty2 = new AgentCell(SchellingState.EMPTY_CELL, EMPTY_AGENT_GROUP);

    // Place cells in grid
    replaceCell(grid, 0, 0, agent1);  // Type A agent
    replaceCell(grid, 0, 1, agent2);  // Type B agent
    replaceCell(grid, 1, 0, empty1);  // Empty cell
    replaceCell(grid, 1, 1, empty2);  // Empty cell

    SimulationConfig simConfig = createSchellingSimConfig(2, 2);
    Schelling simulation = new Schelling(simConfig, grid, 0.8);  // High tolerance to force movement

    // Verify initial setup
    assertEquals(SchellingState.AGENT, ((AgentCell)grid.getCell(0, 0)).getCurrentState(),
        "Initial state of (0,0) should be AGENT");
    assertEquals(AGENT_STATE_KEY, ((AgentCell)grid.getCell(0, 0)).getAgentGroup(),
        "Initial group of (0,0) should be AGENT_STATE_KEY");

    // Apply rules
    simulation.applyRules();
    grid.applyNextStates();

    // Verify agent moved from original position
    AgentCell sourceCell = (AgentCell) grid.getCell(0, 0);
    assertEquals(SchellingState.EMPTY_CELL, sourceCell.getCurrentState(),
        "Original cell should now be empty");
    assertEquals(EMPTY_AGENT_GROUP, sourceCell.getAgentGroup(),
        "Original cell should have empty group");

    // Check that agent moved to one of the empty cells
    AgentCell destCell1 = (AgentCell) grid.getCell(1, 0);
    AgentCell destCell2 = (AgentCell) grid.getCell(1, 1);
    boolean moved = (destCell1.getCurrentState() == SchellingState.AGENT &&
        destCell1.getAgentGroup() == AGENT_STATE_KEY) ||
        (destCell2.getCurrentState() == SchellingState.AGENT &&
            destCell2.getAgentGroup() == AGENT_STATE_KEY);
    assertTrue(moved, "Agent should have moved to one of the empty cells");
  }
  @Test
  void applyRules_NullGrid_ThrowsIllegalArgumentException() {
    SimulationConfig simConfig = createSchellingSimConfig(1, 1);
    assertThrows(IllegalArgumentException.class, () -> new Schelling(simConfig, null, 0.5),
        "Schelling simulation constructor should throw IllegalArgumentException when grid is null.");
  }

  @Test
  void applyRules_InvalidCellTypeInGrid_ThrowsClassCastException() {
    Grid grid = new Grid(1, 1, SchellingState.EMPTY_CELL);

    // Place a regular Cell instead of an AgentCell
    Cell invalidCell = new Cell(SchellingState.AGENT);
    replaceCell(grid, 0, 0, invalidCell);

    SimulationConfig simConfig = createSchellingSimConfig(1, 1);
    Schelling simulation = new Schelling(simConfig, grid, 0.5);

    assertThrows(ClassCastException.class, simulation::applyRules, "Should throw ClassCastException when trying to process non-AgentCell");
  }

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