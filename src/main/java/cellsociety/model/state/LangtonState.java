package cellsociety.model.state;

import cellsociety.model.StateInterface;

/**
 * Represents the possible states in Langton's Loop simulation. Each state represents different
 * components and stages of the self-replicating loop structure.
 */
public enum LangtonState implements StateInterface {
  EMPTY("Empty", 0),           // State 0: Background state
  SHEATH("Sheath", 1),        // State 1: Core/sheath of the loop
  CORE("Core", 2),            // State 2: Internal structure
  TEMP("Temporary", 3),       // State 3: Temporary marker state
  TURN("Turn", 4),            // State 4: Corner/turning point
  EXTEND("Extend", 5),        // State 5: Extension signal
  INIT("Initialize", 6),      // State 6: Initialization signal
  ADVANCE("Advance", 7);      // State 7: Advance signal

  private final String stateValue;
  private final int numericValue;

  LangtonState(String stateValue, int numericValue) {
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