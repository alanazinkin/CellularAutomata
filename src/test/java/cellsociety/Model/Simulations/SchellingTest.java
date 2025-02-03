package cellsociety.Model.Simulations;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.Model.AgentCell;
import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.State.SchellingState;
import org.junit.jupiter.api.Test;

public class SchellingTest {

  /**
   * Helper method to replace a cell in the grid using the public API.
   *
   * @param grid the grid whose cell will be replaced
   * @param row the row index of the cell to replace
   * @param col the column index of the cell to replace
   * @param cell the new cell to insert at the specified location
   */
  private void replaceCell(Grid grid, int row, int col, Cell cell) {
    grid.setCellAt(row, col, cell);
  }

  /**
   * Positive test: In a 1×1 grid the lone agent has no neighbors and is automatically satisfied.
   * The agent should remain unchanged.
   */
  @Test
  public void testApplyRules_NoUnsatisfiedAgents() {
    Grid grid = new Grid(1, 1, SchellingState.EMPTY_CELL);

    AgentCell testCell = new AgentCell(SchellingState.AGENT, 1);
    replaceCell(grid, 0, 0, testCell);

    Schelling simulation = new Schelling(grid, 0.5);
    simulation.applyRules();

    AgentCell cell = (AgentCell) grid.getCell(0, 0);
    assertEquals(SchellingState.AGENT, cell.getState(),
        "Agent should remain in place as it has no neighbors.");
    assertEquals(1, cell.getAgentGroup(), "Agent group should remain unchanged.");
  }

  /**
   * Positive test: In a 2×2 grid an unsatisfied agent should move from its original location
   * to an empty cell where it is satisfied.
   *
   * Grid layout:
   *   (0,0): Agent (group 1) – unsatisfied (neighbor (0,1) is group 2)
   *   (0,1): Agent (group 2)
   *   (1,0) and (1,1): Empty cells (group -1)
   */
  @Test
  public void testApplyRules_UnsatisfiedAgentMoves() {
    Grid grid = new Grid(2, 2, SchellingState.EMPTY_CELL);

    AgentCell agentCell1 = new AgentCell(SchellingState.AGENT, 1); // unsatisfied
    AgentCell agentCell2 = new AgentCell(SchellingState.AGENT, 2);
    AgentCell emptyCell1 = new AgentCell(SchellingState.EMPTY_CELL, -1);
    AgentCell emptyCell2 = new AgentCell(SchellingState.EMPTY_CELL, -1);

    replaceCell(grid, 0, 0, agentCell1);
    replaceCell(grid, 0, 1, agentCell2);
    replaceCell(grid, 1, 0, emptyCell1);
    replaceCell(grid, 1, 1, emptyCell2);

    Schelling simulation = new Schelling(grid, 0.5);
    simulation.applyRules();

    AgentCell sourceCell = (AgentCell) grid.getCell(0, 0);
    assertEquals(SchellingState.EMPTY_CELL, sourceCell.getState(),
        "The unsatisfied agent should leave its original cell.");

    AgentCell destCell1 = (AgentCell) grid.getCell(1, 0);
    AgentCell destCell2 = (AgentCell) grid.getCell(1, 1);
    boolean moved = (destCell1.getState() == SchellingState.AGENT && destCell1.getAgentGroup() == 1) ||
        (destCell2.getState() == SchellingState.AGENT && destCell2.getAgentGroup() == 1);
    assertTrue(moved,
        "One of the empty cells should now contain the moving agent from (0,0).");
  }

  /**
   * Negative test: When a null grid is passed to the Schelling simulation,
   * calling applyRules() should throw a NullPointerException.
   */
  @Test
  public void testApplyRules_NullGrid_ThrowsException() {
    Schelling simulation = new Schelling(null, 0.5);
    assertThrows(NullPointerException.class, simulation::applyRules,
        "applyRules() should throw NullPointerException when grid is null.");
  }

  /**
   * Negative test: If the grid contains a cell that is not an AgentCell,
   * applyRules() should throw a ClassCastException.
   */
  @Test
  public void testApplyRules_InvalidCellType_ThrowsException() {
    Grid grid = new Grid(1, 1, SchellingState.EMPTY_CELL);

    Cell invalidCell = new Cell(SchellingState.AGENT);
    replaceCell(grid, 0, 0, invalidCell);

    Schelling simulation = new Schelling(grid, 0.5);
    assertThrows(ClassCastException.class, simulation::applyRules,
        "applyRules() should throw ClassCastException when a cell is not an AgentCell.");
  }
}

