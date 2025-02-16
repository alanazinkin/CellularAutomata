package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.model.state.MockState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Cell} functionality.
 * <p>
 * This class verifies proper state management in a cell including retrieval,
 * updates, application of the next state, resetting, and string representation.
 *  Naming convention:
 *  * [UnitOfWork_StateUnderTest_ExpectedBehavior]
 * </p>
 * @author Tatum McKinnis
 */
class CellTest {

  private Cell cell;
  private MockState stateOne;
  private MockState stateTwo;

  /**
   * Sets up a new {@link Cell} instance before each test.
   */
  @BeforeEach
  void setUp() {
    stateOne = MockState.STATE_ONE;
    stateTwo = MockState.STATE_TWO;
    cell = new Cell(stateOne);
  }

  /**
   * Tests retrieval of the initial state.
   * <p>
   * Verifies that a newly constructed {@link Cell} returns its initial state.
   * </p>
   */
  @Test
  void getCurrentState_InitialState_ReturnsStateOne() {
    assertEquals(stateOne, cell.getCurrentState(), "Cell state should be STATE_ONE");
  }

  /**
   * Tests updating the current state with a valid new state.
   * <p>
   * Verifies that {@link Cell#setCurrentState(MockState)} correctly updates the state.
   * </p>
   */
  @Test
  void setCurrentState_ValidNewState_UpdatesCurrentState() {
    cell.setCurrentState(stateTwo);
    assertEquals(stateTwo, cell.getCurrentState(),
        "Cell state should be STATE_TWO after setting it");
  }

  /**
   * Tests setting the current state to null.
   * <p>
   * Verifies that setting the state to null throws an {@link IllegalArgumentException}.
   * </p>
   */
  @Test
  void setCurrentState_NullValue_ThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> cell.setCurrentState(null),
        "Setting state to null should throw IllegalArgumentException");
  }

  /**
   * Tests applying the next state when it has been set.
   * <p>
   * Verifies that after setting the next state, applying it updates the current state.
   * </p>
   */
  @Test
  void applyNextState_WithNextStateSet_UpdatesCurrentState() {
    cell.setNextState(stateTwo);
    cell.applyNextState();
    assertEquals(stateTwo, cell.getCurrentState(),
        "Cell should transition to STATE_TWO after next state is applied");
  }

  /**
   * Tests setting the next state to null.
   * <p>
   * Verifies that setting the next state to null throws an {@link IllegalArgumentException}.
   * </p>
   */
  @Test
  void setNextState_NullValue_ThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> cell.setNextState(null),
        "Setting next state to null should throw IllegalArgumentException");
  }

  /**
   * Tests that after applying the next state, the current state matches the next state.
   * <p>
   * Verifies that the state update is properly reflected in the current state.
   * </p>
   */
  @Test
  void applyNextState_AfterSetting_CurrentStateMatchesNextState() {
    cell.setNextState(stateTwo);
    cell.applyNextState();
    assertEquals(stateTwo, cell.getCurrentState(),
        "Cell should change to STATE_TWO after applying the next state");
  }

  /**
   * Tests that applying the next state changes the current state.
   * <p>
   * Verifies that the updated state matches the next state after application.
   * </p>
   */
  @Test
  void applyNextState_WithNextStateSet_StateChangedToNextState() {
    cell.setNextState(stateTwo);
    cell.applyNextState();
    assertEquals(stateTwo, cell.getCurrentState(),
        "Cell state should match next state after applying it");
  }

  /**
   * Tests resetting the next state.
   * <p>
   * Verifies that calling {@link Cell#resetNextState()} resets the next state to the current state.
   * </p>
   */
  @Test
  void resetNextState_AfterNextStateSet_ResetsToCurrentState() {
    cell.setNextState(stateTwo);
    cell.resetNextState();
    assertEquals(stateOne, cell.getCurrentState(), "Next state should reset to current state");
  }

  /**
   * Tests that resetting the next state maintains consistency.
   * <p>
   * Verifies that after reset, the current state remains unchanged.
   * </p>
   */
  @Test
  void resetNextState_AfterNextStateSet_MaintainsConsistency() {
    cell.setNextState(stateTwo);
    cell.resetNextState();
    assertEquals(stateOne, cell.getCurrentState(),
        "Next state reset should maintain current state consistency");
  }

  /**
   * Tests resetting the cell state with a new state.
   * <p>
   * Verifies that {@link Cell#resetState(MockState)} updates both the current and next states.
   * </p>
   */
  @Test
  void resetState_ValidNewState_UpdatesCurrentAndNextStates() {
    cell.resetState(stateTwo);
    assertEquals(stateTwo, cell.getCurrentState(),
        "Reset state should set both current and next state to STATE_TWO");
  }

  /**
   * Tests resetting the cell state with a different state.
   * <p>
   * Verifies that resetting updates both the current and next states accordingly.
   * </p>
   */
  @Test
  void resetState_ValidDifferentState_UpdatesBothStates() {
    cell.resetState(stateTwo);
    assertEquals(stateTwo, cell.getCurrentState(),
        "Resetting state should update both current and next state");
  }

  /**
   * Tests the string representation of the cell.
   * <p>
   * Verifies that {@link Cell#toString()} returns the string representation of the current state.
   * </p>
   */
  @Test
  void toString_CurrentState_ReturnsStateString() {
    assertEquals(stateOne.toString(), cell.toString(),
        "toString should return the string representation of the current state");
  }

  /**
   * Tests retrieval of the next state.
   * <p>
   * Verifies that {@link Cell#getNextState()} returns the correct next state after it has been set.
   * </p>
   */
  @Test
  void getNextState_WithNextStateSet_ReturnsNextState() {
    cell.setNextState(stateTwo);
    assertEquals(stateTwo, cell.getNextState(),
        "getNextState should return the next state of the cell");
  }
}



