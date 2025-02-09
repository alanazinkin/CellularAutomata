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
  public void start(Stage primaryStage) throws Exception {
    try {
      SimulationController myController = new SimulationController();
      myController.init(primaryStage);

      primaryStage.setOnCloseRequest(event -> {
        if (myController != null) {
          myController.cleanup();
        }
        Platform.exit();
      });
    } catch (Exception e) {
      System.err.println("Error initializing simulation: " + e.getMessage());
      e.printStackTrace();
      throw e;
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
