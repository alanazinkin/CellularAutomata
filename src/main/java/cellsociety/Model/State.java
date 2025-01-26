package Model;

/**
 * Enum representing the states for Conway's Game of Life simulation.
 * <p>
 * Conway's Game of Life has two possible states: ALIVE and DEAD. A cell can either be alive
 * or dead based on the number of neighbors it has in each generation.
 * </p>
 */
public enum GameOfLifeState {

  /**
   * Represents a cell that is alive in Conway's Game of Life.
   */
  ALIVE,

  /**
   * Represents a cell that is dead in Conway's Game of Life.
   */
  DEAD
}

/**
 * Enum representing the states for the Percolation simulation.
 * <p>
 * Percolation simulation has two possible states: OPEN and BLOCKED. A cell can be open (allowing
 * fluid to flow through it) or blocked (impeding the flow of fluid).
 * </p>
 */
public enum PercolationState {

  /**
   * Represents an open cell in the Percolation simulation, allowing fluid to flow through it.
   */
  OPEN,

  /**
   * Represents a blocked cell in the Percolation simulation, which blocks the flow of fluid.
   */
  BLOCKED
}

/**
 * Enum representing the states for the Spreading of Fire simulation.
 * <p>
 * The Spreading of Fire simulation includes four states: TREE, BURNING, BURNT, and EMPTY. A tree may
 * catch fire, burn, or remain as an empty cell after burning out.
 * </p>
 */
public enum FireState {

  /**
   * Represents a tree in the Spreading of Fire simulation, ready to catch fire.
   */
  TREE,

  /**
   * Represents a burning tree in the Spreading of Fire simulation.
   */
  BURNING,

  /**
   * Represents a burnt tree (empty cell) in the Spreading of Fire simulation.
   */
  BURNT,

  /**
   * Represents an empty cell in the Spreading of Fire simulation.
   */
  EMPTY
}

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