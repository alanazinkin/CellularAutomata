package cellsociety.model.state;

import cellsociety.model.StateInterface;

/**
 * Represents the possible states in the Bacteria Colonies simulation. Each state represents a
 * different type of bacteria that competes in a rock-paper-scissors fashion.
 */
public enum BacteriaState implements StateInterface {
  ROCK("Rock", 0),
  PAPER("Paper", 1),
  SCISSORS("Scissors", 2);

  private final String stateValue;
  private final int numericValue;

  BacteriaState(String stateValue, int numericValue) {
    this.stateValue = stateValue;
    this.numericValue = numericValue;
  }

  @Override
  public String getStateValue() {
    return stateValue;
  }

  @Override
  public int getNumericValue() {
    return numericValue;
  }

  /**
   * Determines if this state beats another state in rock-paper-scissors rules.
   *
   * @param other The state to compare against
   * @return true if this state beats the other state
   */
  public boolean beats(BacteriaState other) {
    return (this == ROCK && other == SCISSORS) ||
        (this == PAPER && other == ROCK) ||
        (this == SCISSORS && other == PAPER);
  }
}
