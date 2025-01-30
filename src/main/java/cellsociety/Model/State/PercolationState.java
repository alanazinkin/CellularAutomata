package cellsociety.Model.State;
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
