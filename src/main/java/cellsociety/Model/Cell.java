package Model;

/**
 * Represents a cell that holds a state, which can be one of the following:
 * <ul>
 *   <li>{@link State#EMPTY}</li>
 *   <li>{@link State#FILLED}</li>
 *   <li>{@link State#ACTIVE}</li>
 * </ul>
 * This class provides methods to get and set the state of the cell.
 */
public class Cell {

  /**
   * The state of the cell.
   */
  private State state;

  /**
   * The next state to which the cell will transition.
   */
  private State nextState;

  /**
   * Constructs a new {@code Cell} with the specified initial state.
   *
   * @param state The initial state of the cell.
   */
  public Cell(State state) {
    this.state = state;
    this.nextState = state;
  }

  /**
   * Returns the current state of the cell.
   *
   * @return the state of the cell.
   */
  public State getState() {
    return state;
  }

  /**
   * Sets a new state for the cell.
   *
   * @param state The new state to set for the cell.
   */
  public void setState(State state) {
    this.state = state;
  }

  /**
   * Sets the next state for the cell, which will be applied in the next generation.
   *
   * @param nextState The new next state for the cell.
   */
  public void setNextState(State nextState) {
    this.nextState = nextState;
  }

  /**
   * Applies the next state to the cell, updating its current state.
   */
  public void applyNextState() {
    this.state = this.nextState;
  }

  /**
   * Resets the next state back to the current state after applying the update.
   */
  public void resetNextState() {
    this.nextState = this.state;
  }
}
