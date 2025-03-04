package cellsociety.model.state;

import cellsociety.model.StateInterface;

/**
 * Represents a special state for a cell that allows it to step back to a previous state
 * in a simulation. This state is primarily used in simulations that support undoing
 * previous steps.
 *
 * <p>Implements the {@link StateInterface} to ensure compatibility with the simulation model.</p>
 *
 * @author Tatum McKinnis
 */
public enum StepBackState implements StateInterface {

  /** Enum constant representing the step-back state with a default numeric value of 0. */
  STEP_BACK_STATE("Step Back State", 0);

  private final String stateValue;
  private final int numericValue;

  /**
   * Constructs a StepBackState with the given string representation and numeric value.
   *
   * @param stateValue   A string representing the name of the state.
   * @param numericValue An integer representing the numeric value of the state.
   */
  StepBackState(String stateValue, int numericValue) {
    this.stateValue = stateValue;
    this.numericValue = numericValue;
  }

  /**
   * Returns the string representation of this state.
   *
   * @return The name of the state.
   */
  @Override
  public String getStateValue() {
    return stateValue;
  }

  /**
   * Returns the numeric representation of this state.
   *
   * @return The numeric value of the state.
   */
  @Override
  public int getNumericValue() {
    return numericValue;
  }
}
