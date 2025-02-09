package cellsociety.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cellsociety.Model.State.MockState;
import org.junit.jupiter.api.Test;

class AgentCellTest {

  private final MockState mockState = MockState.STATE_ONE;

  @Test
  void Constructor_ValidAgentGroup_AgentGroupSetCorrectly() {
    AgentCell cell = new AgentCell(mockState, 3);
    assertEquals(3, cell.getAgentGroup());
  }

  @Test
  void Constructor_NegativeAgentGroup_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> {
      new AgentCell(mockState, -1);
    });
  }

  @Test
  void setAgentGroup_ValidAgentGroup_UpdatesAgentGroup() {
    AgentCell cell = new AgentCell(mockState, 0);
    cell.setAgentGroup(5);
    assertEquals(5, cell.getAgentGroup());
  }

  @Test
  void setAgentGroup_NegativeAgentGroup_ThrowsException() {
    AgentCell cell = new AgentCell(mockState, 0);
    assertThrows(IllegalArgumentException.class, () -> {
      cell.setAgentGroup(-2);
    });
  }

  @Test
  void getAgentGroup_AfterConstruction_ReturnsInitialValue() {
    AgentCell cell = new AgentCell(mockState, 2);
    assertEquals(2, cell.getAgentGroup());
  }

  @Test
  void getAgentGroup_AfterSet_ReturnsUpdatedValue() {
    AgentCell cell = new AgentCell(mockState, 2);
    cell.setAgentGroup(4);
    assertEquals(4, cell.getAgentGroup());
  }
}