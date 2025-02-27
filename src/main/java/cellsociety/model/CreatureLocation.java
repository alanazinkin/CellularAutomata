package cellsociety.model;

/**
 * Represents a creature's location in the grid.
 * <p>
 * This class encapsulates the position of a creature by storing its row and column
 * coordinates, along with a reference to the corresponding {@code Cell}. The cell
 * reference provides context about the environment in which the creature exists.
 * @author Tatum McKinnis
 * </p>
 */
public class CreatureLocation {
  private final int row;
  private final int col;
  private final Cell cell;

  /**
   * Constructs a new {@code CreatureLocation}.
   *
   * @param row  the row coordinate of the creature in the grid
   * @param col  the column coordinate of the creature in the grid
   * @param cell the {@code Cell} reference where the creature is located
   */
  public CreatureLocation(int row, int col, Cell cell) {
    this.row = row;
    this.col = col;
    this.cell = cell;
  }

  /**
   * Returns the row coordinate of the creature's location.
   *
   * @return the row coordinate
   */
  public int getRow() {
    return row;
  }

  /**
   * Returns the column coordinate of the creature's location.
   *
   * @return the column coordinate
   */
  public int getCol() {
    return col;
  }

  /**
   * Returns the cell associated with this creature's location.
   *
   * @return the {@code Cell} reference
   */
  public Cell getCell() {
    return cell;
  }
}

