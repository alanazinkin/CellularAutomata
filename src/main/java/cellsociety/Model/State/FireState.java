package Model.State;

import Model.StateInterface;

/**
 * Enum representing the states for the Spreading of Fire simulation.
 * <p>
 * The Spreading of Fire simulation includes four states: TREE, BURNING, BURNT, and EMPTY.
 * A tree may catch fire (TREE), burn (BURNING), or remain as an empty cell after burning out (BURNT).
 * Empty cells can also be represented as (EMPTY).
 * </p>
 *
 * Each enum constant in FireState represents a specific state of a cell in the simulation.
 * The enum implements the State interface, ensuring that each state can be handled
 * polymorphically across various simulations.
 */
public enum FireState implements StateInterface {

  /**
   * Represents a tree in the Spreading of Fire simulation, ready to catch fire.
   */
  TREE("Tree"),

  /**
   * Represents a burning tree in the Spreading of Fire simulation.
   */
  BURNING("Burning"),

  /**
   * Represents a burnt tree (empty cell) in the Spreading of Fire simulation.
   */
  BURNT("Burnt"),

  /**
   * Represents an empty cell in the Spreading of Fire simulation.
   */
  EMPTY("Empty");

  private final String stateValue;

  /**
   * Constructor for the FireState enum, assigning a string value to each state.
   *
   * @param stateValue the string value representing the state (e.g., "Tree", "Burning")
   */
  FireState(String stateValue) {
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
