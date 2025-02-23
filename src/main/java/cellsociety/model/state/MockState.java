package cellsociety.model.state;

import cellsociety.model.StateInterface;

/**
 * Mock state implementation used for testing purposes. Implements the StateInterface.
 */
public enum MockState implements StateInterface {

  STATE_ONE("State One"),
  STATE_TWO("State Two");

  private final String stateValue;

  MockState(String stateValue) {
    this.stateValue = stateValue;
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
    return 0;
  }
}


