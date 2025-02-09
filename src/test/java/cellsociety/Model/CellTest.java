package cellsociety.Model;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.Model.State.MockState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CellTest {

  private Cell cell;
  private MockState stateOne;
  private MockState stateTwo;

  @BeforeEach
  void setUp() {
    stateOne = MockState.STATE_ONE;
    stateTwo = MockState.STATE_TWO;
    cell = new Cell(stateOne);
  }

  @Test
  void getState_InitialState_ReturnsStateOne() {
    assertEquals(stateOne, cell.getCurrentState(), "Cell state should be STATE_ONE");
  }

  @Test
  void setState_NewState_UpdatesCurrentState() {
    cell.setCurrentState(stateTwo);
    assertEquals(stateTwo, cell.getCurrentState(), "Cell state should be STATE_TWO after setting it");
  }

  @Test
  void setState_NullValue_ThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> cell.setCurrentState(null),
        "Setting state to null should throw IllegalArgumentException");
  }

  @Test
  void applyNextState_NextStateSet_UpdatesCurrentState() {
    cell.setNextState(stateTwo);
    cell.applyNextState();
    assertEquals(stateTwo, cell.getCurrentState(), "Cell should transition to STATE_TWO after next state is applied");
  }

  @Test
  void setNextState_NullValue_ThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> cell.setNextState(null),
        "Setting next state to null should throw IllegalArgumentException");
  }

  @Test
  void applyNextState_AfterSet_CurrentStateMatchesNextState() {
    cell.setNextState(stateTwo);
    cell.applyNextState();
    assertEquals(stateTwo, cell.getCurrentState(), "Cell should change to STATE_TWO after applying the next state");
  }

  @Test
  void applyNextState_NextStateSet_StateChangedToNextState() {
    cell.setNextState(stateTwo);
    cell.applyNextState();
    assertEquals(stateTwo, cell.getCurrentState(), "Cell state should match next state after applying it");
  }

  @Test
  void resetNextState_AfterSet_ResetsToCurrentState() {
    cell.setNextState(stateTwo);
    cell.resetNextState();
    assertEquals(stateOne, cell.getCurrentState(), "Next state should reset to current state");
  }

  @Test
  void resetNextState_AfterSet_MaintainsConsistency() {
    cell.setNextState(stateTwo);
    cell.resetNextState();
    assertEquals(stateOne, cell.getCurrentState(), "Next state reset should maintain current state consistency");
  }

  @Test
  void resetState_NewState_UpdatesCurrentAndNextStates() {
    cell.resetState(stateTwo);
    assertEquals(stateTwo, cell.getCurrentState(), "Reset state should set both current and next state to STATE_TWO");
  }

  @Test
  void resetState_DifferentState_UpdatesBothStates() {
    cell.resetState(stateTwo);
    assertEquals(stateTwo, cell.getCurrentState(), "Resetting state should update both current and next state");
  }

  @Test
  void toString_CurrentState_ReturnsStateString() {
    assertEquals(stateOne.toString(), cell.toString(), "toString should return the string representation of the current state");
  }

  @Test
  void getNextState_NextStateSet_ReturnsNextState() {
    cell.setNextState(stateTwo);
    assertEquals(stateTwo, cell.getNextState(), "getNextState should return the next state of the cell");
  }
}


