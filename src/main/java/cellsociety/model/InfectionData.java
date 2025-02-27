package cellsociety.model;

/**
 * Data class for infection status.
 * <p>
 * This class encapsulates the metadata associated with an infection event. It stores the original state of the cell
 * before infection, the number of steps remaining for the infection, and a flag indicating whether the cell was
 * previously infected.
 * @author Tatum McKinnis
 * </p>
 */
public class InfectionData {

  /**
   * The original state of the cell before it became infected.
   */
  final StateInterface originalState;

  /**
   * The number of steps remaining for which the cell remains infected.
   */
  int remainingSteps;

  /**
   * Flag indicating whether the cell was infected previously.
   */
  boolean wasInfectedPreviously;

  /**
   * Constructs a new {@code InfectionData} instance with the specified original state, remaining steps,
   * and previous infection status.
   *
   * @param originalState         the state of the cell prior to infection
   * @param steps                 the number of steps remaining for the infection
   * @param wasInfectedPreviously {@code true} if the cell was infected before, {@code false} otherwise
   */
  InfectionData(StateInterface originalState, int steps, boolean wasInfectedPreviously) {
    this.originalState = originalState;
    this.remainingSteps = steps;
    this.wasInfectedPreviously = wasInfectedPreviously;
  }
}
