package cellsociety.model.state;

import cellsociety.model.StateInterface;

/**
 * Represents the possible states in the Bacteria Colonies simulation.
 * <p>
 * Each state corresponds to a type of bacteria that competes in a rock-paper-scissors fashion.
 * The states defined are:
 * <ul>
 *   <li>{@code ROCK} – beats {@code SCISSORS}</li>
 *   <li>{@code PAPER} – beats {@code ROCK}</li>
 *   <li>{@code SCISSORS} – beats {@code PAPER}</li>
 * </ul>
 * The {@link #beats(BacteriaState)} method can be used to determine if one state wins over another.
 * </p>
 *
 * @author Tatum McKinnis
 */
public enum BacteriaState implements StateInterface {
  /**
   * Represents the Rock state.
   */
  ROCK("Rock", 0),

  /**
   * Represents the Paper state.
   */
  PAPER("Paper", 1),

  /**
   * Represents the Scissors state.
   */
  SCISSORS("Scissors", 2);

  /**
   * The human-readable name of this state.
   */
  private final String stateValue;

  /**
   * The numeric value associated with this state.
   */
  private final int numericValue;

  /**
   * Constructs a new {@code BacteriaState} with the specified state value and numeric value.
   *
   * @param stateValue   the human-readable name of the state
   * @param numericValue the numeric representation of the state
   */
  BacteriaState(String stateValue, int numericValue) {
    this.stateValue = stateValue;
    this.numericValue = numericValue;
  }

  /**
   * Returns the human-readable value of this state.
   *
   * @return the state value as a {@code String}
   */
  @Override
  public String getStateValue() {
    return stateValue;
  }

  /**
   * Returns the numeric representation of this state.
   *
   * @return the numeric value of the state
   */
  @Override
  public int getNumericValue() {
    return numericValue;
  }

  /**
   * Determines if this state beats another state based on rock-paper-scissors rules.
   *
   * @param other the state to compare against
   * @return {@code true} if this state beats the other state, {@code false} otherwise
   */
  public boolean beats(BacteriaState other) {
    return (this == ROCK && other == SCISSORS) ||
        (this == PAPER && other == ROCK) ||
        (this == SCISSORS && other == PAPER);
  }
}
