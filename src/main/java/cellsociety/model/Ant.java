package cellsociety.model;

import java.util.Objects;

/**
 * Represents an individual ant in the simulation, tracking its position, orientation, and whether
 * it is carrying food.
 *
 * @author Tatum McKinnis
 */
public class Ant {

  private int row;
  private int col;
  private int lastRow;
  private int lastCol;
  private Orientation orientation;
  private boolean hasFood;
  private int steps;

  /**
   * Constructs an ant at a specific grid location.
   *
   * @param row         initial row position
   * @param col         initial column position
   * @param orientation initial facing direction
   * @param hasFood     true if the ant is carrying food
   */
  public Ant(int row, int col, Orientation orientation, boolean hasFood) {
    this.orientation = Objects.requireNonNull(orientation, "Orientation cannot be null");
    this.row = row;
    this.col = col;
    this.hasFood = hasFood;
    lastRow = -1;
    lastCol = -1;
    steps = 0;
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

  /**
   * Retrieves the last row index.
   *
   * @return the last row index as an integer.
   */
  public int getLastRow() {
    return lastRow;
  }

  /**
   * Retrieves the last column index.
   *
   * @return the last column index as an integer.
   */
  public int getLastCol() {
    return lastCol;
  }
  /**
   * Sets the last row index.
   *
   * @param lastRow the last row index to set.
   */
  public void setLastRow(int lastRow) {
    this.lastRow = lastRow;
  }

  /**
   * Sets the last column index.
   *
   * @param lastCol the last column index to set.
   */
  public void setLastCol(int lastCol) {
    this.lastCol = lastCol;
  }

  /**
   * Retrieves the number of steps.
   *
   * @return the number of steps as an integer.
   */
  public int getSteps() {
    return steps;
  }

  /**
   * Sets the number of steps.
   *
   * @param steps the number of steps to set.
   */
  public void setSteps(int steps) {
    this.steps = steps;
  }


}
