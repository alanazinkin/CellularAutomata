package cellsociety.model.state;

import cellsociety.model.StateInterface;

/**
 * Enum representing the states for Conway's Game of Life simulation.
 * <p>
 * Conway's Game of Life has two possible states: ALIVE and DEAD. A cell can either be alive or dead
 * based on the number of neighbors it has in each generation.
 * </p>
 * <p>
 * This enum implements the {@link cellsociety.model.StateInterface} interface to maintain
 * consistency across various state enums used in grid-based simulations.
 * </p>
 */
public enum GameOfLifeState implements StateInterface {

  /**
   * Represents a cell that is alive in Conway's Game of Life.
   */
  ALIVE("Alive", 1),

  /**
   * Represents a cell that is dead in Conway's Game of Life.
   */
  DEAD("Dead", 0);

  private final String stateValue;
  private final int numericValue;

  /**
   * Constructor for the GameOfLifeState enum, assigning a string value and a numeric value to each
   * state.
   *
   * @param stateValue   the string value representing the state (e.g., "Alive", "Dead")
   * @param numericValue the numeric value representing the state (e.g., 1 for alive, 0 for dead)
   */
  GameOfLifeState(String stateValue, int numericValue) {
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

  /**
   * Returns the numeric value associated with the state.
   *
   * @return the numeric value of the state
   */
  public int getNumericValue() {
    return numericValue;
  }

  /**
   * Returns the GameOfLifeState corresponding to the given numeric value.
   *
   * @param value the numeric value (e.g., 0 or 1) parsed from the XML file
   * @return the corresponding GameOfLifeState
   * @throws IllegalArgumentException if no matching state is found
   */
  public static GameOfLifeState fromValue(int value) {
    for (GameOfLifeState state : GameOfLifeState.values()) {
      if (state.getNumericValue() == value) {
        return state;
      }
    }
    throw new IllegalArgumentException("Invalid state value: " + value);
  }
}
