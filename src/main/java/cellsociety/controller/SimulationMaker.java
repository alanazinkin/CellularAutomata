package cellsociety.controller;

import javafx.stage.Stage;

public class SimulationMaker {

  /**
   * method is responsible for creating an entire new simulation instance
   *
   * @throws Exception
   */
  public void makeNewSimulation() throws Exception {
    SimulationController simulationController = new SimulationController();
    SimulationUI simulationUI = simulationController.getUI();
    simulationUI.initializeSplashScreen(new Stage(), simulationController);
  }
}
