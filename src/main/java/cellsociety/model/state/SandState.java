package cellsociety.model.state;

import cellsociety.model.StateInterface;

/**
 * Enumeration representing the possible states of cells in the Sand simulation. Each state
 * represents a different type of particle or space in the simulation.
 * <p>
 * The simulation includes: - EMPTY: Represents empty space where particles can move - SAND:
 * Represents sand particles that fall and stack - WALL: Represents immovable barriers - WATER:
 * Represents water particles that flow more freely than sand
 * </p>
 */
public enum SandState implements StateInterface {
  /**
   * Represents an empty cell where particles can move into.
   */
  EMPTY("Empty"),

  /**
   * Represents a sand particle that falls and stacks.
   */
  SAND("Sand"),

  /**
   * Represents an immovable wall that blocks particle movement.
   */
  WALL("Wall"),

  /**
   * Represents a water particle that flows more freely than sand.
   */
  WATER("Water");

  private final String stateValue;

  /**
   * Constructs a new SandState with the specified string value.
   *
   * @param stateValue The string representation of this state
   */
  SandState(String stateValue) {
    this.stateValue = stateValue;
  }

  /**
   * Returns the string representation of this state.
   *
   * @return The string value representing this state
   */
  @Override
  public String getStateValue() {
    return stateValue;
  }
}
