package cellsociety.model;

/**
 * Factory class for creating edge strategies from configuration strings.
 * @author Tatum McKinnis
 */
public class EdgeStrategyFactory {

  /**
   * Creates an appropriate edge strategy based on a type string.
   *
   * @param type The type identifier for the edge strategy
   * @return A new instance of the requested edge strategy
   * @throws IllegalArgumentException if the type is not recognized
   */
  public static EdgeStrategy createEdgeStrategy(String type) {
    switch (type.toUpperCase()) {
      case "BOUNDED":
        return new BoundedEdge();
      case "TOROIDAL":
        return new ToroidalEdge();
      case "MIRROR":
        return new MirrorEdge();
      case "INFINITE":
        return new InfiniteEdge();
      default:
        throw new IllegalArgumentException("Unknown edge type: " + type);
    }
  }
}
