package cellsociety.model;

import cellsociety.model.state.SugarScapeState;
import cellsociety.model.simulations.SugarScape;

public class GrowthManager {

  private final SugarScape simulation;

  public GrowthManager(SugarScape simulation) {
    this.simulation = simulation;
  }

  public void applyGrowBack(int currentTick, int interval, int growRate) {
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

