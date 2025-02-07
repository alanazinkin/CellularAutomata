package cellsociety;

import cellsociety.Controller.SimulationController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

  /**
   * entry point of simulation
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    SimulationController myController = new SimulationController();
    myController.init(primaryStage);
  }

  /**
   * Main method that launches the program application
   * @param args
   */
  public static void main(String[] args) {
    launch(args);
  }
}
