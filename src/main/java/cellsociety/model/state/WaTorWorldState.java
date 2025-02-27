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
 * @author Tatum McKinnis
 */
public enum WaTorWorldState implements StateInterface {

  /**
   * Represents a fish in the Wa-Tor World simulation.
   */
  FISH("Fish", 1),

  /**
   * Represents a shark in the Wa-Tor World simulation.
   */
  SHARK("Shark", 2),

  /**
   * Represents an empty cell in the Wa-Tor World simulation.
   */
  EMPTY("Empty", 0);

  private final String stateValue;
  private final int numericValue;

  /**
   * Constructor for the WaTorWorldState enum, assigning a string value to each state.
   *
   * @param stateValue the string value representing the state (e.g., "Fish", "Shark", "Empty")
   */
  WaTorWorldState(String stateValue, int numericValue) {
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
