package cellsociety.model.state;

import cellsociety.model.StateInterface;

public enum StepBackState implements StateInterface {
  STEP_BACK_STATE("Step Back State", 0);

  private final String stateValue;
  private final int numericValue;

  StepBackState(String stateValue, int numericValue) {
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
}
