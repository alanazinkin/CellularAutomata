package cellsociety.model.state;

import cellsociety.model.StateInterface;

/**
 * Enum representing different states in the SugarScape simulation.
 * <p>
 * This enum implements the StateInterface to provide consistent state representation across
 * different simulations. Each state represents a different condition of a cell in the SugarScape
 * grid.
 * </p>
 */
public enum SugarScapeState implements StateInterface {
  EMPTY("Empty"),      // Cell contains no sugar and no agent
  SUGAR("Sugar"),      // Cell contains sugar but no agent
  AGENT("Agent");      // Cell contains an agent

  private final String stateValue;

  /**
   * Constructor for SugarScapeState.
   *
   * @param stateValue the string representation of the state
   */
  SugarScapeState(String stateValue) {
    this.stateValue = stateValue;
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

  @Override
  public int getNumericValue() {
    return 0;
  }
}
