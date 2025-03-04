package cellsociety.model.state;

import cellsociety.model.StateInterface;

/**
 * Mock state implementation used for testing purposes. Implements the StateInterface.
 *
 * @author Tatum McKinnis
 */
public enum MockState implements StateInterface {

  STATE_ONE("State One", 1),
  STATE_TWO("State Two", 2);

  private final String stateValue;
  private final int numericValue;

  /**
   * Constructs a new MockState with the specified state value and numeric value.
   *
   * @param stateValue   the string representation of the state.
   * @param numericValue the numeric identifier for the state.
   */
  MockState(String stateValue, int numericValue) {
    this.stateValue = stateValue;
    this.numericValue = numericValue;
  }

  /**
   * Returns the state value as a string.
   *
   * @return the string representation of the mock state.
   */
  @Override
  public String getStateValue() {
    return stateValue;
  }

  /**
   * Returns the numeric value associated with this state.
   *
   * @return the numeric identifier of the mock state.
   */
  @Override
  public int getNumericValue() {
    return numericValue;
  }
}



