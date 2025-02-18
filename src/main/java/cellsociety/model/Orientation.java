package cellsociety.model;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Represents the eight possible directions an ant can face. Each direction includes delta row (dr)
 * and delta column (dc) values for grid navigation.
 *
 * @author Tatum McKinnis
 */
public enum Orientation {
  N(-1, 0),
  NE(-1, 1),
  E(0, 1),
  SE(1, 1),
  S(1, 0),
  SW(1, -1),
  W(0, -1),
  NW(-1, -1);

  private final int dr;
  private final int dc;

  /**
   * Constructs an orientation with directional offsets.
   *
   * @param dr row offset (-1, 0, or 1)
   * @param dc column offset (-1, 0, or 1)
   */
  Orientation(int dr, int dc) {
    this.dr = dr;
    this.dc = dc;
  }

  /**
   * Gets the three forward-facing directions relative to this orientation (front-left, front,
   * front-right).
   *
   * @return list of forward orientations
   */
  public List<Orientation> getForwardOrientations() {
    int ordinal = this.ordinal();
    Orientation left = Orientation.values()[(ordinal - 1 + 8) % 8];
    Orientation right = Orientation.values()[(ordinal + 1) % 8];
    return Arrays.asList(left, this, right);
  }

  /**
   * Converts directional offsets to an Orientation.
   *
   * @param dr row offset (-1, 0, or 1)
   * @param dc column offset (-1, 0, or 1)
   * @return corresponding Orientation
   * @throws IllegalArgumentException if invalid offsets
   */
  public static Orientation fromDrDc(int dr, int dc) {
    for (Orientation o : Orientation.values()) {
      if (o.dr == dr && o.dc == dc) {
        return o;
      }
    }
    throw new IllegalArgumentException("Invalid direction: dr=" + dr + ", dc=" + dc);
  }

  public static Orientation getRandom() {
    return values()[new Random().nextInt(values().length)];
  }

  /**
   * Gets the row offset for this orientation.
   *
   * @return delta row value
   */
  public int getDr() {
    return dr;
  }

  /**
   * Gets the column offset for this orientation.
   *
   * @return delta column value
   */
  public int getDc() {
    return dc;
  }
}
