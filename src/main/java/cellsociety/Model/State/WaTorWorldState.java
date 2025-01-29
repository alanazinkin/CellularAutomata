/**
 * Enum representing the states for Schelling's Model of Segregation.
 * <p>
 * Schelling's Model of Segregation simulation has two states: AGENT and EMPTY_CELL. AGENT represents
 * an individual agent in the simulation, and EMPTY_CELL represents an empty space where no agent exists.
 * </p>
 */
public enum SchellingState {

  /**
   * Represents an agent in the Schelling's Model of Segregation simulation.
   */
  AGENT,

  /**
   * Represents an empty cell in the Schelling's Model of Segregation simulation.
   */
  EMPTY_CELL
}

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