package cellsociety.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for creating neighborhood strategies from configuration.
 * This simplified version supports Moore, Von Neumann, Extended Moore, and Multiple neighborhoods.
 * @author Tatum McKinnis
 */
public class NeighborhoodFactory {
  /**
   * Creates an appropriate neighborhood strategy based on a type string.
   *
   * @param type The type identifier for the neighborhood strategy
   * @return A new instance of the requested neighborhood strategy
   * @throws IllegalArgumentException if the type is not recognized
   */
  public static NeighborhoodStrategy createNeighborhoodStrategy(String type) {
    if (type.equals("MOORE")) {
      return new MooreNeighborhood();
    } else if (type.equals("VON_NEUMANN")) {
      return new VonNeumannNeighborhood();
    } else if (type.startsWith("EXTENDED_MOORE_")) {
      int radius = Integer.parseInt(type.substring("EXTENDED_MOORE_".length()));
      return new ExtendedMooreNeighborhood(radius);
    } else if (type.equals("MULTIPLE")) {
      return new MultipleNeighborhoods(new ArrayList<>());
    }

    throw new IllegalArgumentException("Unknown neighborhood type: " + type);
  }

  /**
   * Creates a multiple neighborhood strategy by combining multiple types.
   *
   * @param types Array of type strings for neighborhood strategies to combine
   * @return A new MultipleNeighborhoods instance containing all requested strategies
   */
  public static NeighborhoodStrategy createMultipleNeighborhoods(String[] types) {
    List<NeighborhoodStrategy> strategies = new ArrayList<>();
    for (String type : types) {
      strategies.add(createNeighborhoodStrategy(type));
    }
    return new MultipleNeighborhoods(strategies);
  }
}