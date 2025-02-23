package cellsociety.model.state;

import cellsociety.model.StateInterface;

/**
 * Enum representing the states for the Wa-Tor World simulation.
 * <p>
 * The Wa-Tor World simulation models a predator-prey ecosystem with three possible states: FISH,
 * SHARK, and EMPTY. FISH represents a fish in the simulation, SHARK represents a shark, and EMPTY
 * represents an empty cell where neither a fish nor a shark exists.
 * </p>
 * <p>
 * This enum implements the {@link StateInterface} interface to maintain consistency across various
 * state enums used in grid-based simulations.
 * </p>
 */
public enum WaTorWorldState implements StateInterface {

  /**
   * Represents a fish in the Wa-Tor World simulation.
   */
  FISH("Fish"),

  /**
   * Represents a shark in the Wa-Tor World simulation.
   */
  SHARK("Shark"),

  /**
   * Represents an empty cell in the Wa-Tor World simulation.
   */
  EMPTY("Empty");

  private final String stateValue;

  /**
   * Constructor for the WaTorWorldState enum, assigning a string value to each state.
   *
   * @param stateValue the string value representing the state (e.g., "Fish", "Shark", "Empty")
   */
  WaTorWorldState(String stateValue) {
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

  @Override
  public int getNumericValue() {
    return 0;
  }
}
