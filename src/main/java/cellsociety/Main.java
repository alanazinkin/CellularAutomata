package cellsociety;

import cellsociety.Controller.SimulationController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application {

  /**
   * entry point of simulation
   */
  @Override
  public void start(Stage primaryStage) {
    try {
      SimulationController myController = new SimulationController();
      myController.init(primaryStage);

      primaryStage.setOnCloseRequest(event -> {
        myController.cleanup();
        Platform.exit();
      });

      primaryStage.show();
    } catch (Exception e) {
      System.err.println("Error initializing simulation: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Main method that launches the program application
   * @param args
   */
  public static void main(String[] args) {
    launch(args);
  }
}
