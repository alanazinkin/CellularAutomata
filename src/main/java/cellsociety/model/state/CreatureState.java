package cellsociety.model.state;

import cellsociety.model.StateInterface;

/**
 * Represents the state of a creature in the simulation.
 * <p>
 * Each creature state is tied to a specific behavior program and has a unique numeric
 * representation that is used throughout the simulation to differentiate between states.
 * </p>
 *
 * <p>
 * Available states:
 * <ul>
 *   <li>{@code EMPTY} - Represents a cell with no creature present.</li>
 *   <li>{@code HUNTER} - Indicates a creature that is actively hunting.</li>
 *   <li>{@code WANDERER} - Represents a creature that roams without a specific target.</li>
 *   <li>{@code FLYTRAP} - Denotes a creature with a behavior similar to a flytrap.</li>
 * </ul>
 * </p>
 *
 * @author Tatum McKinnis
 */
public enum CreatureState implements StateInterface {
  /**
   * Default empty state - no creature present.
   */
  EMPTY(0),

  /**
   * Represents a hunter-type creature.
   */
  HUNTER(1),

  /**
   * Represents a wanderer-type creature.
   */
  WANDERER(2),

  /**
   * Represents a flytrap-type creature.
   */
  FLYTRAP(3);

  /**
   * The numeric value corresponding to this creature state.
   */
  private final int numericValue;

  /**
   * Constructs a creature state with the given numeric value.
   *
   * @param numericValue the numeric representation of this state
   */
  CreatureState(int numericValue) {
    this.numericValue = numericValue;
  }

  /**
   * Returns the string representation of the state.
   *
   * @return the name of the state
   */
  @Override
  public String getStateValue() {
    return this.name();
  }

  /**
   * Returns the numeric value associated with this state.
   *
   * @return the numeric representation of the state
   */
  @Override
  public int getNumericValue() {
    return numericValue;
  }
}






