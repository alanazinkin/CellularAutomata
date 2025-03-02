package cellsociety.view.shapefactory;

/**
 * Factory class for creating instances of {@link HexagonCell}. This class extends
 * {@link CellShapeFactory} and implements the factory method for creating hexagon-shaped cells.
 * @author Alana Zinkin
 */
public class HexagonCellFactory extends CellShapeFactory {

  /**
   * Creates a new instance of {@link HexagonCell} with the specified dimensions and orientation.
   *
   * @param width    the width of the hexagon cell
   * @param height   the height of the hexagon cell
   * @param isUpward a boolean indicating the orientation of the hexagon; {@code true} if the
   *                 hexagon is aligned with a flat top, {@code false} otherwise
   * @return a new {@link HexagonCell} instance with the given dimensions and orientation
   */
  @Override
  public CellShape createCellShape(double width, double height, boolean isUpward) {
    return new HexagonCell(width, height, isUpward);
  }
}

