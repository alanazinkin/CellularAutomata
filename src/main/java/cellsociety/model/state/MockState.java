package cellsociety.model.state;

import cellsociety.model.StateInterface;

/**
 * Mock state implementation used for testing purposes. Implements the StateInterface.
 */
public enum MockState implements StateInterface {

  STATE_ONE("State One", 1),
  STATE_TWO("State Two", 2);

  private final String stateValue;
  private final int numericValue;

  MockState(String stateValue, int numericValue) {
    this.stateValue = stateValue;
    this.numericValue = numericValue;
  }

  /**
   * Returns the state value as a string.
   *
   * @return the string representation of the mock state
   */
  @Override
  public String getStateValue() {
    return stateValue;
  }

  @Override
  public int getNumericValue() {
    return numericValue;
  }
}


