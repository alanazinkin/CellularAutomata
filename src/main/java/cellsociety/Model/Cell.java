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
   * Constructs a new {@code Cell} with the specified initial state.
   *
   * @param state The initial state of the cell.
   */
  public Cell(State state) {
    this.state = state;
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
}
