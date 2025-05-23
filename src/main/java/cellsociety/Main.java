package cellsociety;

import cellsociety.controller.SimulationMaker;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * main class where application is run from
 * @author Alana Zinkin, Tatum Mckinnis, Angela Predolac
 */
public class Main extends Application {

  /**
   * entry point of simulation
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    SimulationMaker simulationMaker = new SimulationMaker();
    simulationMaker.makeNewSimulation();
  }

  /**
   * Main method that launches the program application
   *
   * @param args
   */
  public static void main(String[] args) {
    launch(args);
  }
}
