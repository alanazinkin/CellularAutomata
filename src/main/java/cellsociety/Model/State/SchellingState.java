package cellsociety.Model.State;
import cellsociety.Model.StateInterface;

/**
 * Enum representing the states for Schelling's Model of Segregation.
 * <p>
 * Schelling's Model of Segregation simulation has two states: AGENT and EMPTY_CELL.
 * AGENT represents an individual agent in the simulation, and EMPTY_CELL represents
 * an empty space where no agent exists. These states help model the segregation
 * behavior of agents in a grid.
 * </p>
 * <p>
 * This enum implements the {@link StateInterface} interface to ensure consistency across
 * different state enums in grid-based simulations.
 * </p>
 */
public enum SchellingState implements StateInterface {

  /**
   * Represents an agent in the Schelling's Model of Segregation simulation.
   */
  AGENT("Agent"),

  /**
   * Represents an empty cell in the Schelling's Model of Segregation simulation.
   */
  EMPTY_CELL("Empty Cell");

  private final String stateValue;

  /**
   * Constructor for the SchellingState enum, assigning a string value to each state.
   *
   * @param stateValue the string value representing the state (e.g., "Agent", "Empty Cell")
   */
  SchellingState(String stateValue) {
    this.stateValue = stateValue;
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

}
