package cellsociety.model.state;

import cellsociety.model.StateInterface;

/**
 * Represents the possible states in the Bacteria Colonies simulation. Each state represents a
 * different type of bacteria that competes in a rock-paper-scissors fashion.
 */
public enum BacteriaState implements StateInterface {
  ROCK("Rock"),
  PAPER("Paper"),
  SCISSORS("Scissors");

  private final String stateValue;

  BacteriaState(String stateValue) {
    this.stateValue = stateValue;
  }

  @Override
  public String getStateValue() {
    return stateValue;
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
