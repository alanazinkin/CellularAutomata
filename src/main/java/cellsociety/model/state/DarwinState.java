package cellsociety.model.state;

import cellsociety.model.StateInterface;

/**
 * Represents the possible states of a cell in the Darwin simulation.
 *
 * @author Tatum McKinnis
 */
public enum DarwinState implements StateInterface {
  EMPTY(0, "Empty"),
  CREATURE(1, "Creature");

  private final int numericValue;
  private final String stateValue;

  /**
   * Constructs a new DarwinState.
   *
   * @param numericValue The numeric representation of this state
   * @param stateValue   The string representation of this state
   */
  DarwinState(int numericValue, String stateValue) {
    this.numericValue = numericValue;
    this.stateValue = stateValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getStateValue() {
    return stateValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getNumericValue() {
    return numericValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return stateValue;
  }
}