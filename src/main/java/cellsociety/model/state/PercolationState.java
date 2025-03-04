package cellsociety.model.state;

import cellsociety.model.StateInterface;

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
 * @author Tatum McKinnis
 */
public enum PercolationState implements StateInterface {

  /**
   * Represents an open cell in the Percolation simulation, allowing fluid to flow through it.
   */
  OPEN("Open", 0),

  /**
   * Represents a blocked cell in the Percolation simulation, which blocks the flow of fluid.
   */
  BLOCKED("Blocked", 1),

  /**
   * Represents a cell that has been percolated. This cell was open and now indicates that fluid has
   * percolated through it.
   */
  PERCOLATED("Percolated", 2);

  private final String stateValue;
  private final int numericValue;

  /**
   * Constructor for the PercolationState enum, assigning a string value to each state.
   *
   * @param stateValue the string value representing the state (e.g., "Open", "Blocked",
   *                   "Percolated")
   */
  PercolationState(String stateValue, int numericValue) {
    this.stateValue = stateValue;
    this.numericValue = numericValue;
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

  @Override
  public int getNumericValue() {
    return numericValue;
  }
}


