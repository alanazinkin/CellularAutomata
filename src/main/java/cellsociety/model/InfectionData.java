package cellsociety.model;

import cellsociety.model.StateInterface;

/**
 * Data class for infection status.
 */
public class InfectionData {
  final StateInterface originalState;
  int remainingSteps;
  boolean wasInfectedPreviously;

  InfectionData(StateInterface originalState, int steps, boolean wasInfectedPreviously) {
    this.originalState = originalState;
    this.remainingSteps = steps;
    this.wasInfectedPreviously = wasInfectedPreviously;
  }
}