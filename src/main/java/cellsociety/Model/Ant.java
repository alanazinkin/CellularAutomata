package cellsociety.Model;

/**
 * Represents an individual ant in the simulation, tracking its position, orientation, and whether
 * it is carrying food.
 */
public class Ant {

  private int row;
  private int col;
  private Orientation orientation;
  private boolean hasFood;

  /**
   * Constructs an ant at a specific grid location.
   *
   * @param row         initial row position
   * @param col         initial column position
   * @param orientation initial facing direction
   * @param hasFood     true if the ant is carrying food
   */
  public Ant(int row, int col, Orientation orientation, boolean hasFood) {
    this.row = row;
    this.col = col;
    this.orientation = orientation;
    this.hasFood = hasFood;
  }

  /**
   * Gets the ant's current row.
   *
   * @return row index
   */
  public int getRow() {
    return row;
  }

  /**
   * Sets the ant's row position.
   *
   * @param row new row index
   */
  public void setRow(int row) {
    this.row = row;
  }

  /**
   * Gets the ant's current column.
   *
   * @return column index
   */
  public int getCol() {
    return col;
  }

  /**
   * Sets the ant's column position.
   *
   * @param col new column index
   */
  public void setCol(int col) {
    this.col = col;
  }

  /**
   * Gets the ant's current orientation.
   *
   * @return facing direction
   */
  public Orientation getOrientation() {
    return orientation;
  }

  /**
   * Sets the ant's orientation.
   *
   * @param orientation new facing direction
   */
  public void setOrientation(Orientation orientation) {
    this.orientation = orientation;
  }

  /**
   * Checks if the ant is carrying food.
   *
   * @return true if carrying food
   */
  public boolean hasFood() {
    return hasFood;
  }

  /**
   * Sets whether the ant is carrying food.
   *
   * @param hasFood true to indicate food is being carried
   */
  public void setHasFood(boolean hasFood) {
    this.hasFood = hasFood;
  }
}
