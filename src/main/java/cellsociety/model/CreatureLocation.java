package cellsociety.model;

/**
 * Represents a creature's location in the grid.
 */
public class CreatureLocation {
  private final int row;
  private final int col;
  private final Cell cell;

  /**
   * Constructs a new CreatureLocation.
   *
   * @param row the row coordinate
   * @param col the column coordinate
   * @param cell the cell reference
   */
  public CreatureLocation(int row, int col, Cell cell) {
    this.row = row;
    this.col = col;
    this.cell = cell;
  }

  public int getRow() {
    return row;
  }

  public int getCol() {
    return col;
  }

  public Cell getCell() {
    return cell;
  }
}
