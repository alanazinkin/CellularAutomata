package cellsociety;

import static javafx.application.Application.launch;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.Simulation;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
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
    //TODO: pull this information from XML file
    SimulationConfig mySimConfig = new SimulationConfig("GameofLife",
        "Game of Life",
        "Alana Zinkin",
        "Game of Life is a simulation created by Conway to simulate bacterial growth",
        800,
        1000,
        new int[]{0, 1},
        null
    );
    mySimConfig.initializeStage(primaryStage);
    mySimConfig.initSimulation(primaryStage);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
