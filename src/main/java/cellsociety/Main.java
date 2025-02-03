package cellsociety;

import static javafx.application.Application.launch;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Controller.SimulationController;
import cellsociety.Controller.XMLParser;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

  /**
   * entry point of simulation
   * @param primaryStage the primary stage for this application, onto which
   * the application scene can be set.
   * Applications may create other stages, if needed, but they will not be
   * primary stages.
   * @throws Exception
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    SimulationController myController = new SimulationController();
    myController.init(primaryStage);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
