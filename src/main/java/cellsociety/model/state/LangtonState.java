package cellsociety.model.state;
import cellsociety.model.StateInterface;

/**
 * Represents the possible states in Langton's Loop simulation.
 * Each state represents different components and stages of the self-replicating loop structure.
 */
public enum LangtonState implements StateInterface {
  EMPTY("Empty"),           // State 0: Background state
  SHEATH("Sheath"),        // State 1: Core/sheath of the loop
  CORE("Core"),            // State 2: Internal structure
  TEMP("Temporary"),       // State 3: Temporary marker state
  TURN("Turn"),            // State 4: Corner/turning point
  EXTEND("Extend"),        // State 5: Extension signal
  INIT("Initialize"),      // State 6: Initialization signal
  ADVANCE("Advance");      // State 7: Advance signal

  private final String stateValue;

  LangtonState(String stateValue) {
    this.stateValue = stateValue;
  }

  @Override
  public String getStateValue() {
    return stateValue;
  }
}