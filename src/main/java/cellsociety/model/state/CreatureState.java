package cellsociety.model.state;

import cellsociety.model.StateInterface;


/**
 * Represents the state of a creature in the simulation.
 * Each creature state is associated with a specific program defining its behavior.
 *
 * @author Simulation Developer
 */
public enum CreatureState implements StateInterface {
  /**
   * Default empty state - no creature present
   */
  EMPTY(0),

  /**
   * For any custom creature species, starting at index 1
   */
  HUNTER(1),
  WANDERER(2),
  FLYTRAP(3);

  private final int numericValue;

  /**
   * Constructs a creature state with the given numeric value.
   *
   * @param numericValue the numeric representation of this state
   */
  CreatureState(int numericValue) {
    this.numericValue = numericValue;
  }

  @Override
  public String getStateValue() {
    return this.name();
  }

  @Override
  public int getNumericValue() {
    return numericValue;
  }
}






