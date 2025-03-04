package cellsociety.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple neighborhoods combined - allows custom combinations of neighborhood patterns. This class
 * enables flexible neighborhood definitions by merging multiple strategies. For example, it can
 * combine Moore and Von Neumann neighborhoods.
 * <p>
 * Example usage:
 * <pre>
 * List<NeighborhoodStrategy> strategies = List.of(new MooreNeighborhood(), new VonNeumannNeighborhood());
 * MultipleNeighborhoods multiple = new MultipleNeighborhoods(strategies);
 * </pre>
 *
 * @author Tatum McKinnis
 */
public class MultipleNeighborhoods implements NeighborhoodStrategy {

  private final List<NeighborhoodStrategy> neighborhoods;

  /**
   * Creates a composite neighborhood from multiple different neighborhood strategies.
   *
   * @param neighborhoods The list of neighborhood strategies to combine
   */
  public MultipleNeighborhoods(List<NeighborhoodStrategy> neighborhoods) {
    this.neighborhoods = new ArrayList<>(neighborhoods);
  }

  /**
   * Gets all unique neighbor coordinates by combining the results of each neighborhood strategy.
   *
   * @param row The row index of the central cell
   * @param col The column index of the central cell
   * @return A list of integer arrays representing neighbor coordinates
   */
  @Override
  public List<int[]> getNeighborCoordinates(int row, int col) {
    List<int[]> allNeighbors = new ArrayList<>();
    for (NeighborhoodStrategy neighborhood : neighborhoods) {
      allNeighbors.addAll(neighborhood.getNeighborCoordinates(row, col));
    }

    return allNeighbors;
  }

  /**
   * Returns the type identifier for this neighborhood strategy.
   *
   * @return The string "MULTIPLE"
   */
  @Override
  public String getType() {
    return "MULTIPLE";
  }
}
