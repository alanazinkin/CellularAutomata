package cellsociety.model;

/**
 * Interface representing a state in a grid-based simulation.
 * <p>
 * This interface defines methods that can be shared by different state enums in various
 * simulations. Each state enum, such as FireState or GameOfLifeState, should implement this
 * interface to ensure consistency and shared functionality.
 * </p>
 *
 * @author Tatum McKinnis
 */
public interface StateInterface {

  /**
   * Returns the state value as a string.
   * <p>
   * Each state enum should implement this method to return a string representation of the state,
   * such as "Alive", "Burning", or "Empty".
   * </p>
   *
   * @return a string representation of the state
   */
  String getStateValue();

  /**
   * Returns the numeric value associated with the state.
   *
   * @return the numeric value of the state
   */
  int getNumericValue();
}
