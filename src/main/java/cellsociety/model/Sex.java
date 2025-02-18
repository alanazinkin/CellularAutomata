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
   * Gets the human-readable name of the sex.
   *
   * @return the display name of the sex
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Gets the opposite sex.
   * <p>
   * Used in reproduction rules to find compatible mates.
   * </p>
   *
   * @return FEMALE if called on MALE, MALE if called on FEMALE
   */
  public Sex getOpposite() {
    return this == MALE ? FEMALE : MALE;
  }

  @Override
  public String toString() {
    return displayName;
  }
}
