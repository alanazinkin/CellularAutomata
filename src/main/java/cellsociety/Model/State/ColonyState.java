package cellsociety.Model;

/**
 * Represents a discrete state in a competing colony cellular automaton simulation.
 * <p>
 * Each state corresponds to an integer value representing a specific bacterial colony type.
 * Implements {@link StateInterface} to integrate with the simulation framework and provide
 * standardized state comparisons via {@code equals} and {@code hashCode}.
 * </p>
 */
public class ColonyState implements StateInterface {
  private final int value;

  /**
   * Constructs a colony state with a specific integer identifier.
   *
   * @param value Unique identifier for this state (0 ≤ value < numStates in simulation)
   */
  public ColonyState(int value) {
    this.value = value;
  }

  /**
   * Returns the string representation of the state's integer value.
   *
   * @return State identifier as a string (e.g., "0", "1")
   */
  @Override
  public String getStateValue() {
    return Integer.toString(value);
  }

  /**
   * Compares states for equality based on their integer values.
   *
   * @param obj Object to compare
   * @return true if both objects are ColonyStates with the same value
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    ColonyState that = (ColonyState) obj;
    return value == that.value;
  }

  /**
   * Generates hash code based on the state's integer value.
   *
   * @return Hash code corresponding to the state value
   */
  @Override
  public int hashCode() {
    return value;
  }
}
