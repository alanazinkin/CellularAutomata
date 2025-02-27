package cellsociety.model;

import cellsociety.model.state.SugarScapeState;
import cellsociety.model.simulations.SugarScape;

/**
 * GrowthManager is responsible for handling the regrowth of sugar on the grid at specified intervals.
 * @author Tatum McKinnis
 */
public class GrowthManager {

  private final SugarScape simulation;

  /**
   * Constructs a GrowthManager for the given SugarScape simulation.
   *
   * @param simulation the SugarScape simulation instance
   */
  public GrowthManager(SugarScape simulation) {
    this.simulation = simulation;
  }

  /**
   * Applies the sugar regrowth process based on the current tick, interval, and growth rate.
   *
   * @param currentTick the current simulation tick
   * @param interval the interval at which sugar regrows
   * @param growRate the rate at which sugar regrows
   * @throws IllegalArgumentException if the interval or grow rate is non-positive
   */
  public void applyGrowBack(int currentTick, int interval, int growRate) {
    if (interval <= 0) {
      throw new IllegalArgumentException("Interval must be positive");
    }
    if (growRate <= 0) {
      throw new IllegalArgumentException("Growth rate must be positive");
    }

    if (currentTick % interval == 0) {
      var grid = simulation.getGrid();
      for (int r = 0; r < grid.getRows(); r++) {
        for (int c = 0; c < grid.getCols(); c++) {
          Cell cell = grid.getCell(r, c);
          if (cell instanceof SugarCell sugarCell) {
            if (cell.getCurrentState() == SugarScapeState.EMPTY) {
              int newSugar = Math.min(sugarCell.getSugar() + growRate, sugarCell.getMaxSugar());
              sugarCell.setSugar(newSugar);
              if (newSugar > 0) {
                sugarCell.setNextState(SugarScapeState.SUGAR);
              }
            }
          }
        }
      }
      grid.applyNextStates();
    }
  }
}
