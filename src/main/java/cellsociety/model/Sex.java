package cellsociety.model;

/**
 * Represents the biological sex of agents in the SugarScape simulation.
 * <p>
 * This enum is used to facilitate reproduction rules in the simulation, where agents of opposite
 * sexes can reproduce when other conditions are met.
 * </p>
 *
 * @author Tatum McKinnis
 */
public enum Sex {
  /**
   * Represents male agents.
   */
  MALE("Male"),

  /**
   * Represents female agents.
   */
  FEMALE("Female");

  private final String displayName;

  /**
   * Constructs a Sex enum value with a display name.
   *
   * @param displayName the human-readable name of the sex
   */
  Sex(String displayName) {
    this.displayName = displayName;
  }

  /**
   * Returns the string representation of this object.
   * <p>
   * This method overrides {@code toString()} to return the display name of the object.
   * </p>
   *
   * @return the display name of the object
   */
  @Override
  public String toString() {
    return displayName;
  }
}
