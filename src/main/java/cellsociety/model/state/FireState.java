package cellsociety.model.state;

import cellsociety.model.StateInterface;

/**
 * Enum representing the states for the Spreading of Fire simulation.
 * <p>
 * The Spreading of Fire simulation includes four states: TREE, BURNING, BURNT, and EMPTY. A tree
 * may catch fire (TREE), burn (BURNING), or remain as an empty cell after burning out (BURNT).
 * Empty cells can also be represented as (EMPTY).
 * </p>
 * <p>
 * Each enum constant in FireState represents a specific state of a cell in the simulation. The enum
 * implements the StateInterface interface, ensuring that each state can be handled polymorphically
 * across various simulations.
 * @author Tatum McKinnis
 */
public enum FireState implements StateInterface {

  /**
   * Represents a tree in the Spreading of Fire simulation, ready to catch fire.
   */
  TREE("Tree", 1),

  /**
   * Represents a burning tree in the Spreading of Fire simulation.
   */
  BURNING("Burning", 2),

  /**
   * Represents a burnt tree (or cell) in the Spreading of Fire simulation.
   */
  BURNT("Burnt", 3),

  /**
   * Represents an empty cell in the Spreading of Fire simulation.
   */
  EMPTY("Empty", 0);

  private final String stateValue;
  private final int numericValue;

  /**
   * Constructor for the FireState enum, assigning both a string value and a numeric value to each
   * state.
   *
   * @param stateValue   the string representation of the state (e.g., "Tree", "Burning")
   * @param numericValue the numeric representation of the state (e.g., 1 for TREE, 2 for BURNING)
   */
  FireState(String stateValue, int numericValue) {
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
   * Returns the numeric value associated with the state.
   *
   * @return the numeric value of the state
   */
  public int getNumericValue() {
    return numericValue;
  }

  /**
   * Returns the FireState corresponding to the given numeric value.
   *
   * @param value the numeric value (e.g., 0, 1, 2, or 3) parsed from the XML or configuration file
   * @return the corresponding FireState
   * @throws IllegalArgumentException if no matching state is found for the provided value
   */
  public static FireState fromValue(int value) {
    for (FireState state : FireState.values()) {
      if (state.getNumericValue() == value) {
        return state;
      }
    }
    throw new IllegalArgumentException("Invalid state value: " + value);
  }
}
