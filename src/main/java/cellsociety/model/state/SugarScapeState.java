package cellsociety.model.state;

import cellsociety.model.StateInterface;

/**
 * Enum representing different states in the SugarScape simulation.
 * <p>
 * This enum implements the StateInterface to provide consistent state representation across
 * different simulations. Each state represents a different condition of a cell in the SugarScape
 * grid.
 * </p>
 * @author Tatum McKinnis
 */
public enum SugarScapeState implements StateInterface {
  EMPTY("Empty",  0),      // Cell contains no sugar and no agent
  SUGAR("Sugar", 1),      // Cell contains sugar but no agent
  AGENT("Agent", 2);      // Cell contains an agent

  private final String stateValue;
  private final int numericValue;

  /**
   * Constructor for SugarScapeState.
   *
   * @param stateValue the string representation of the state
   */
  SugarScapeState(String stateValue, int numericValue) {
    this.stateValue = stateValue;
    this.numericValue = numericValue;
  }

  /**
   * Returns the string representation of the state.
   * <p>
   * This implementation provides a human-readable version of the state, fulfilling the contract
   * specified by StateInterface.
   * </p>
   *
   * @return the string value representing this state
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
