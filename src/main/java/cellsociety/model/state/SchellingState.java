package cellsociety.model.state;

import cellsociety.model.StateInterface;

/**
 * Enum representing the states for Schelling's Model of Segregation.
 * <p>
 * Schelling's Model of Segregation simulation has two states: AGENT and EMPTY_CELL. AGENT
 * represents an individual agent in the simulation, and EMPTY_CELL represents an empty space where
 * no agent exists. These states help model the segregation behavior of agents in a grid.
 * </p>
 * <p>
 * This enum implements the {@link StateInterface} interface to ensure consistency across different
 * state enums in grid-based simulations.
 * @author Tatum McKinnis
 * </p>
 */
public enum SchellingState implements StateInterface {

  /**
   * Represents an agent in the Schelling's Model of Segregation simulation.
   */
  AGENT("Agent", 1),

  /**
   * Represents an empty cell in the Schelling's Model of Segregation simulation.
   */
  EMPTY_CELL("Empty Cell", 0);

  private final String stateValue;
  private final int numericValue;

  /**
   * Constructor for the SchellingState enum, assigning a string value to each state.
   *
   * @param stateValue the string value representing the state (e.g., "Agent", "Empty Cell")
   */
  SchellingState(String stateValue, int numericValue) {
    this.stateValue = stateValue;
    this.numericValue = numericValue;
  }

  /**
   * Returns the string value associated with the state.
   *
   * @return the string value of the state
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
