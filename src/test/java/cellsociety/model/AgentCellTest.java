package cellsociety.Model;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.Model.State.MockState;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link AgentCell} functionality.
 * <p>
 * This class verifies proper initialization and manipulation of the agent group in an
 * {@link AgentCell} instance.
 *  Naming convention:
 *  * [UnitOfWork_StateUnderTest_ExpectedBehavior]
 * </p>
 * @author Tatum McKinnis
 */
class AgentCellTest {

  private final MockState mockState = MockState.STATE_ONE;

  /**
   * Tests constructor with a valid agent group.
   * <p>
   * Verifies that an {@link AgentCell} is initialized with the correct agent group.
   * </p>
   */
  @Test
  void Constructor_ValidAgentGroup_AgentGroupSetCorrectly() {
    AgentCell cell = new AgentCell(mockState, 3);
    assertEquals(3, cell.getAgentGroup());
  }

  /**
   * Tests constructor with a negative agent group.
   * <p>
   * Verifies that constructing an {@link AgentCell} with a negative agent group
   * throws an {@link IllegalArgumentException}.
   * </p>
   */
  @Test
  void Constructor_NegativeAgentGroup_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> {
      new AgentCell(mockState, -1);
    });
  }

  /**
   * Tests setting the agent group with a valid value.
   * <p>
   * Verifies that {@link AgentCell#setAgentGroup(int)} correctly updates the agent group.
   * </p>
   */
  @Test
  void setAgentGroup_ValidAgentGroup_UpdatesAgentGroup() {
    AgentCell cell = new AgentCell(mockState, 0);
    cell.setAgentGroup(5);
    assertEquals(5, cell.getAgentGroup());
  }

  /**
   * Tests setting the agent group with a negative value.
   * <p>
   * Verifies that attempting to set a negative agent group throws an {@link IllegalArgumentException}.
   * </p>
   */
  @Test
  void setAgentGroup_NegativeAgentGroup_ThrowsException() {
    AgentCell cell = new AgentCell(mockState, 0);
    assertThrows(IllegalArgumentException.class, () -> {
      cell.setAgentGroup(-2);
    });
  }

  /**
   * Tests retrieval of the agent group immediately after construction.
   * <p>
   * Verifies that {@link AgentCell#getAgentGroup()} returns the initial value set in the constructor.
   * </p>
   */
  @Test
  void getAgentGroup_AfterConstruction_ReturnsInitialValue() {
    AgentCell cell = new AgentCell(mockState, 2);
    assertEquals(2, cell.getAgentGroup());
  }

  /**
   * Tests retrieval of the agent group after updating it.
   * <p>
   * Verifies that {@link AgentCell#getAgentGroup()} returns the updated agent group value.
   * </p>
   */
  @Test
  void getAgentGroup_AfterSet_ReturnsUpdatedValue() {
    AgentCell cell = new AgentCell(mockState, 2);
    cell.setAgentGroup(4);
    assertEquals(4, cell.getAgentGroup());
  }
}
