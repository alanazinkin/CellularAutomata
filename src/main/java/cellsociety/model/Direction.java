package cellsociety.model;

/**
 * Represents a direction with associated degree values.
 * @author Tatum McKinnis
 */
public enum Direction {
  NORTH(90),
  EAST(0),
  SOUTH(270),
  WEST(180);

  private final double degrees;

  Direction(double degrees) {
    this.degrees = degrees;
  }

  /**
   * Gets the degrees value for this direction.
   *
   * @return the degrees value
   */
  public double getDegrees() {
    return degrees;
  }

  /**
   * Converts a degree value to the nearest direction.
   *
   * @param degrees the degrees value
   * @return the corresponding direction
   */
  public static Direction fromDegrees(double degrees) {
    double normalizedDegrees = ((degrees % 360) + 360) % 360;

    if (normalizedDegrees >= 315 || normalizedDegrees < 45) {
      return EAST;
    } else if (normalizedDegrees >= 45 && normalizedDegrees < 135) {
      return NORTH;
    } else if (normalizedDegrees >= 135 && normalizedDegrees < 225) {
      return WEST;
    } else {
      return SOUTH;
    }
  }
}
