package cellsociety.Model.State;
import cellsociety.Model.StateInterface;

/**
 * Enum representing the states for Conway's Game of Life simulation.
 * <p>
 * Conway's Game of Life has two possible states: ALIVE and DEAD. A cell can either be alive
 * or dead based on the number of neighbors it has in each generation.
 * </p>
 * <p>
 * This enum implements the {@link cellsociety.Model.StateInterface} interface to maintain consistency across
 * various state enums used in grid-based simulations.
 * </p>
 */
public enum GameOfLifeState implements StateInterface {

  /**
   * Represents a cell that is alive in Conway's Game of Life.
   */
  ALIVE("Alive"),

  /**
   * Represents a cell that is dead in Conway's Game of Life.
   */
  DEAD("Dead");

  private final String stateValue;

  /**
   * Constructor for the GameOfLifeState enum, assigning a string value to each state.
   *
   * @param stateValue the string value representing the state (e.g., "Alive", "Dead")
   */
  GameOfLifeState(String stateValue) {
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

