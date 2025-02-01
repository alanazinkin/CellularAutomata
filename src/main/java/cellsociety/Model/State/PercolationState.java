package cellsociety.Model.State;

import cellsociety.Model.StateInterface;

/**
 * Enum representing the states for the Percolation simulation.
 * <p>
 * The Percolation simulation has three possible states:
 * <ul>
 *   <li>{@code OPEN} – the cell is open and may allow fluid to flow through it.</li>
 *   <li>{@code BLOCKED} – the cell is blocked, preventing fluid from flowing through.</li>
 *   <li>{@code PERCOLATED} – the cell was open and is now percolated, indicating that fluid has passed through it.</li>
 * </ul>
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
  BLOCKED("Blocked"),

  /**
   * Represents a cell that has been percolated. This cell was open and now indicates that
   * fluid has percolated through it.
   */
  PERCOLATED("Percolated");

  private final String stateValue;

  /**
   * Constructor for the PercolationState enum, assigning a string value to each state.
   *
   * @param stateValue the string value representing the state (e.g., "Open", "Blocked", "Percolated")
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


