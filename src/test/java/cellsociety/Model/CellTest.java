package cellsociety.Model;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.Model.State.GameOfLifeState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Cell} class.
 * <p>
 * This class contains tests for the functionality of the {@code Cell} class, which represents
 * a cell with a state that can change based on the simulation's rules. The tests ensure that
 * the cell behaves as expected in terms of state management, including getting, setting, and
 * transitioning between states.
 * </p>
 */
class CellTest {

  private Cell cell;
  private StateInterface aliveState;
  private StateInterface deadState;

  /**
   * Sets up the test environment before each test method.
   * <p>
   * Initializes a new {@code Cell} object with an {@link GameOfLifeState#ALIVE} state, and
   * creates the states for alive and dead states that will be used in the tests.
   * </p>
   */
  @BeforeEach
  void setUp() {
    aliveState = GameOfLifeState.ALIVE;
    deadState = GameOfLifeState.DEAD;
    cell = new Cell(aliveState);
  }

  /**
   * Tests the {@code getState} method.
   * <p>
   * This test ensures that the {@code getState} method correctly returns the current state of
   * the cell.
   * </p>
   */
  @Test
  void getState() {
    assertEquals(aliveState, cell.getState(), "Cell state should be ALIVE");
  }

  /**
   * Tests the {@code setState} method.
   * <p>
   * This test ensures that the {@code setState} method correctly updates the cell's state.
   * </p>
   */
  @Test
  void setState() {
    cell.setState(deadState);
    assertEquals(deadState, cell.getState(), "Cell state should be DEAD after setting it");
  }

  /**
   * Tests the {@code setState} method with a null value.
   * <p>
   * This test ensures that passing a {@code null} value to the {@code setState} method throws
   * an {@code IllegalArgumentException}.
   * </p>
   */
  @Test
  void setStateWithNullValue() {
    assertThrows(IllegalArgumentException.class, () -> cell.setState(null),
        "Setting state to null should throw IllegalArgumentException");
  }

  /**
   * Tests the {@code setNextState} method.
   * <p>
   * This test ensures that the {@code setNextState} method correctly updates the next state
   * of the cell and that it is applied correctly.
   * </p>
   */
  @Test
  void setNextState() {
    cell.setNextState(deadState);
    cell.applyNextState();
    assertEquals(deadState, cell.getState(), "Cell should transition to DEAD after next state is applied");
  }

  /**
   * Tests the {@code setNextState} method with a null value.
   * <p>
   * This test ensures that passing a {@code null} value to the {@code setNextState} method throws
   * an {@code IllegalArgumentException}.
   * </p>
   */
  @Test
  void setNextStateWithNullValue() {
    assertThrows(IllegalArgumentException.class, () -> cell.setNextState(null),
        "Setting next state to null should throw IllegalArgumentException");
  }

  /**
   * Tests the {@code applyNextState} method.
   * <p>
   * This test ensures that the {@code applyNextState} method correctly updates the cell's
   * current state to the next state.
   * </p>
   */
  @Test
  void applyNextState() {
    cell.setNextState(deadState);
    cell.applyNextState();
    assertEquals(deadState, cell.getState(), "Cell should change to DEAD after applying the next state");
  }

  /**
   * Tests the {@code applyNextState} method to change the state.
   * <p>
   * This test ensures that the cell's state is updated to match the next state after applying it.
   * </p>
   */
  @Test
  void applyNextStateToChangeState() {
    cell.setNextState(GameOfLifeState.DEAD);
    cell.applyNextState();
    assertEquals(GameOfLifeState.DEAD, cell.getState(), "Cell state should match next state after applying it");
  }

  /**
   * Tests the {@code resetNextState} method.
   * <p>
   * This test ensures that the {@code resetNextState} method correctly resets the next state
   * to the current state.
   * </p>
   */
  @Test
  void resetNextState() {
    cell.setNextState(deadState);
    cell.resetNextState();
    assertEquals(aliveState, cell.getState(), "Next state should reset to current state");
  }

  /**
   * Tests the {@code resetNextState} method to maintain consistency.
   * <p>
   * This test ensures that resetting the next state maintains the consistency of the cell's
   * state.
   * </p>
   */
  @Test
  void resetNextStateMaintainsConsistency() {
    cell.setNextState(deadState);
    cell.resetNextState();
    assertEquals(aliveState, cell.getState(), "Next state reset should maintain current state consistency");
  }

  /**
   * Tests the {@code resetState} method.
   * <p>
   * This test ensures that the {@code resetState} method resets both the current and next
   * states to the specified state.
   * </p>
   */
  @Test
  void resetState() {
    cell.resetState(deadState);
    assertEquals(deadState, cell.getState(), "Reset state should set both current and next state to DEAD");
  }

  /**
   * Tests the {@code resetState} method with a different state.
   * <p>
   * This test ensures that the {@code resetState} method works correctly when resetting the
   * state to a different value.
   * </p>
   */
  @Test
  void resetStateWithDifferentState() {
    cell.resetState(GameOfLifeState.DEAD);
    assertEquals(GameOfLifeState.DEAD, cell.getState(), "Resetting state should update both current and next state");
  }

  /**
   * Tests the {@code toString} method.
   * <p>
   * This test ensures that the {@code toString} method returns the string representation of
   * the current state of the cell.
   * </p>
   */
  @Test
  void toStringReturnsStateValue() {
    assertEquals(aliveState.toString(), cell.toString(), "toString should return the string representation of the current state");
  }

  /**
   * Tests the {@code getNextState} method.
   * <p>
   * This test ensures that the {@code getNextState} method correctly returns the next state
   * of the cell after it has been set using {@code setNextState}.
   * </p>
   */
  @Test
  void getNextState() {
    cell.setNextState(deadState);
    assertEquals(deadState, cell.getNextState(), "getNextState should return the next state of the cell");
  }
}

