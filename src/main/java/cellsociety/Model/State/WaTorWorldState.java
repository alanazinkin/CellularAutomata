package cellsociety.Model.State;
/**
 * Enum representing the states for the Wa-Tor World simulation.
 * <p>
 * The Wa-Tor World simulation models a predator-prey ecosystem with two active states: FISH and SHARK.
 * A third state, EMPTY, represents an empty cell where neither a fish nor shark exists.
 * </p>
 */
public enum WaTorWorldState {

  /**
   * Represents a fish in the Wa-Tor World simulation.
   */
  FISH,

  /**
   * Represents a shark in the Wa-Tor World simulation.
   */
  SHARK,

  /**
   * Represents an empty cell in the Wa-Tor World simulation.
   */
  EMPTY
}