package cellsociety.model.state;

import cellsociety.model.StateInterface;

/**
 * Represents the possible states in Langton's Loop simulation. Each state represents different
 * components and stages of the self-replicating loop structure.
 *
 * <p>States include:
 * <ul>
 *   <li>{@code EMPTY} - Background state</li>
 *   <li>{@code SHEATH} - Core/sheath of the loop</li>
 *   <li>{@code CORE} - Internal structure</li>
 *   <li>{@code TEMP} - Temporary marker state</li>
 *   <li>{@code TURN} - Corner/turning point</li>
 *   <li>{@code EXTEND} - Extension signal</li>
 *   <li>{@code INIT} - Initialization signal</li>
 *   <li>{@code ADVANCE} - Advance signal</li>
 * </ul>
 * </p>
 *
 * @author Tatum McKinnis
 */
public enum LangtonState implements StateInterface {
  EMPTY("Empty", 0),           // State 0: Background state
  SHEATH("Sheath", 1),         // State 1: Core/sheath of the loop
  CORE("Core", 2),             // State 2: Internal structure
  TEMP("Temporary", 3),        // State 3: Temporary marker state
  TURN("Turn", 4),             // State 4: Corner/turning point
  EXTEND("Extend", 5),         // State 5: Extension signal
  INIT("Initialize", 6),       // State 6: Initialization signal
  ADVANCE("Advance", 7);       // State 7: Advance signal

  private final String stateValue;
  private final int numericValue;

  /**
   * Constructs a new LangtonState with the specified string representation and numeric value.
   *
   * @param stateValue the string representation of the state.
   * @param numericValue the numeric value identifying the state.
   */
  LangtonState(String stateValue, int numericValue) {
    this.stateValue = stateValue;
    this.numericValue = numericValue;
  }

  /**
   * Retrieves the string representation of this state.
   *
   * @return the state value as a String.
   */
  @Override
  public String getStateValue() {
    return stateValue;
  }

  /**
   * Retrieves the numeric value associated with this state.
   *
   * @return the numeric identifier of the state.
   */
  @Override
  public int getNumericValue() {
    return numericValue;
  }
}