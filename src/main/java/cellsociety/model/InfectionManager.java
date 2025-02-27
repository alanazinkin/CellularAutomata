package cellsociety.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages infection status of creatures in the simulation.
 * @author Tatum McKinnis
 */
public class InfectionManager {
  private final Map<Cell, InfectionData> infectionStatuses = new HashMap<>();

  /**
   * Infects a creature with a new state.
   *
   * @param cell the cell containing the creature
   * @param originalState the original state before infection
   * @param steps the number of steps the infection lasts
   */
  public void infectCreature(Cell cell, StateInterface originalState, int steps) {
    infectionStatuses.put(cell, new InfectionData(originalState, steps, false));
  }

  /**
   * Checks if a creature was infected in the current step.
   *
   * @param cell the cell containing the creature
   * @return true if newly infected, false otherwise
   */
  public boolean isNewlyInfected(Cell cell) {
  InfectionData data = infectionStatuses.get(cell);
    return data != null && !data.wasInfectedPreviously;
  }

  /**
   * Updates the infection status of all infected creatures.
   *
   * @param grid the grid containing creatures
   */
  public void updateInfections(Grid grid) {
    List<Cell> cellsToRemove = new ArrayList<>();

    for (Map.Entry<Cell, InfectionData> entry : infectionStatuses.entrySet()) {
      Cell cell = entry.getKey();
      InfectionData data = entry.getValue();

      data.remainingSteps--;
      data.wasInfectedPreviously = true;

      if (data.remainingSteps <= 0) {
        cell.setNextState(data.originalState);
        cellsToRemove.add(cell);
      }
    }
    for (Cell cell : cellsToRemove) {
      infectionStatuses.remove(cell);
    }
  }

  /**
   * Clears all infection data.
   */
  public void clear() {
    infectionStatuses.clear();
  }
}

