package cellsociety.model;

/**
 * Represents a cell in the SugarScape simulation that contains sugar.
 * <p>
 * This cell extends the basic Cell class to add sugar-specific properties and behavior, including
 * tracking sugar amounts and maximum sugar capacity.
 * </p>
 *
 * @author Tatum McKinnis
 */
public class SugarCell extends Cell {

  private int sugar;
  private int maxSugar;
  private int row;
  private int col;

  /**
   * Creates a new SugarCell with specified position and initial state.
   *
   * @param row          the row position of the cell
   * @param col          the column position of the cell
   * @param initialState the initial state of the cell
   * @throws IllegalArgumentException if the initial state is null
   */
  public SugarCell(int row, int col, StateInterface initialState) {
    super(initialState);
    this.row = row;
    this.col = col;
    this.sugar = 0;
    this.maxSugar = 0;
  }

  /**
   * Gets the current amount of sugar in the cell.
   *
   * @return the current sugar amount
   */
  public int getSugar() {
    return sugar;
  }

  /**
   * Sets the amount of sugar in the cell.
   *
   * @param sugar the new sugar amount
   * @throws IllegalArgumentException if sugar is negative
   */
  public void setSugar(int sugar) {
    if (sugar < 0) {
      throw new IllegalArgumentException("Sugar amount cannot be negative");
    }
    this.sugar = sugar;
  }

  /**
   * Gets the maximum sugar capacity of the cell.
   *
   * @return the maximum sugar capacity
   */
  public int getMaxSugar() {
    return maxSugar;
  }

  /**
   * Sets the maximum sugar capacity of the cell.
   *
   * @param maxSugar the new maximum sugar capacity
   * @throws IllegalArgumentException if maxSugar is negative
   */
  public void setMaxSugar(int maxSugar) {
    if (maxSugar < 0) {
      throw new IllegalArgumentException("Maximum sugar capacity cannot be negative");
    }
    this.maxSugar = maxSugar;
  }

  /**
   * Gets the row position of the cell.
   *
   * @return the row position
   */
  public int getRow() {
    return row;
  }

  /**
   * Gets the column position of the cell.
   *
   * @return the column position
   */
  public int getCol() {
    return col;
  }

  /**
   * Calculates the Manhattan distance to another cell.
   *
   * @param other the other cell to calculate distance to
   * @return the Manhattan distance between this cell and the other cell
   */
  public int distanceTo(Cell other) {
    if (other instanceof SugarCell) {
      SugarCell otherSugarCell = (SugarCell) other;
      return Math.abs(this.row - otherSugarCell.row) +
          Math.abs(this.col - otherSugarCell.col);
    }
    return 0;  // Return 0 if other cell is not a SugarCell
  }

  /**
   * Adds sugar to the cell up to its maximum capacity.
   *
   * @param amount the amount of sugar to add
   * @return the actual amount of sugar added (may be less than requested if at capacity)
   */
  public int addSugar(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Cannot add negative sugar amount");
    }
    int spaceAvailable = maxSugar - sugar;
    int amountToAdd = Math.min(amount, spaceAvailable);
    sugar += amountToAdd;
    return amountToAdd;
  }

  /**
   * Removes sugar from the cell.
   *
   * @param amount the amount of sugar to remove
   * @return the actual amount of sugar removed (may be less than requested if not enough available)
   */
  public int removeSugar(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Cannot remove negative sugar amount");
    }
    int amountToRemove = Math.min(amount, sugar);
    sugar -= amountToRemove;
    return amountToRemove;
  }

  /**
   * Checks if this cell has more sugar than another cell.
   *
   * @param other the other cell to compare with
   * @return true if this cell has more sugar, false otherwise
   */
  public boolean hasMoreSugarThan(SugarCell other) {
    return this.sugar > other.sugar;
  }

  @Override
  public String toString() {
    return String.format("SugarCell[pos=(%d,%d), sugar=%d/%d, state=%s]",
        row, col, sugar, maxSugar, getCurrentState().getStateValue());
  }
}
