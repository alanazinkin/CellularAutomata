package cellsociety.Model.State;
import cellsociety.Model.StateInterface;

/**
 * Enum representing the states for the Percolation simulation.
 * <p>
 * The Percolation simulation has two possible states: OPEN and BLOCKED. A cell can be open (allowing
 * fluid to flow through it) or blocked (impeding the flow of fluid).
 * </p>
 * <p>
 * This enum implements the {@link StateInterface} interface to maintain consistency across
 * various state enums used in grid-based simulations.
 * </p>
 */
public enum PercolationState implements StateInterface {

  /**
   * Represents an open cell in the Percolation simulation, allowing fluid to flow through it.
   */
  OPEN("Open"),

  /**
   * Represents a blocked cell in the Percolation simulation, which blocks the flow of fluid.
   */
  BLOCKED("Blocked");

  private final String stateValue;

  /**
   * Constructor for the PercolationState enum, assigning a string value to each state.
   *
   * @param stateValue the string value representing the state (e.g., "Open", "Blocked")
   */
  PercolationState(String stateValue) {
    this.stateValue = stateValue;
  }

  /**
   * Returns the string value associated with the state.
   *
   * @return the string value of the state
   */
  @Override
  public String getStateValue() {
    return stateValue;
  }

}

