package cellsociety.Model.Simulations;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.Model.AgentCell;
import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.State.SchellingState;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

public class SchellingTest {

  /**
   * Helper method to replace the Grid's internal cells array with our own.
   *
   * @param grid the Grid whose cells field will be replaced
   * @param cells the new 2D Cell array to use in the grid
   * @throws Exception if reflection fails
   */
  private void setGridCells(Grid grid, Cell[][] cells) throws Exception {
    Field field = Grid.class.getDeclaredField("cells");
    field.setAccessible(true);
    field.set(grid, cells);
  }

  /**
   * Positive test: In a 1×1 grid the lone agent has no neighbors and is automatically satisfied.
   * The agent should remain unchanged.
   */
  @Test
  public void testApplyRules_NoUnsatisfiedAgents() throws Exception {
    Grid grid = new Grid(1, 1, SchellingState.EMPTY_CELL);

    AgentCell[][] cells = new AgentCell[1][1];
    cells[0][0] = new AgentCell(SchellingState.AGENT, 1);

    setGridCells(grid, cells);

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
  public void testApplyRules_UnsatisfiedAgentMoves() throws Exception {
    // Create a 2×2 grid.
    Grid grid = new Grid(2, 2, SchellingState.EMPTY_CELL);

    // Prepare a 2×2 array of AgentCell objects.
    AgentCell[][] cells = new AgentCell[2][2];
    // (0,0): Agent group 1 (unsatisfied because neighbor is in a different group)
    cells[0][0] = new AgentCell(SchellingState.AGENT, 1);
    // (0,1): Agent group 2
    cells[0][1] = new AgentCell(SchellingState.AGENT, 2);
    // (1,0) and (1,1): Empty cells (group -1)
    cells[1][0] = new AgentCell(SchellingState.EMPTY_CELL, -1);
    cells[1][1] = new AgentCell(SchellingState.EMPTY_CELL, -1);

    setGridCells(grid, cells);

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
  public void testApplyRules_InvalidCellType_ThrowsException() throws Exception {
    Grid grid = new Grid(1, 1, SchellingState.EMPTY_CELL);

    Cell[][] cells = new Cell[1][1];
    cells[0][0] = new Cell(SchellingState.AGENT);

    setGridCells(grid, cells);

    Schelling simulation = new Schelling(grid, 0.5);
    assertThrows(ClassCastException.class, simulation::applyRules,
        "applyRules() should throw ClassCastException when a cell is not an AgentCell.");
  }
}
