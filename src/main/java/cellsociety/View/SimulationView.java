package cellsociety.View;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SimulationView {
  public static final int SIMULATION_WIDTH = 1000;
  public static final int SIMULATION_HEIGHT = 800;

  private Scene myScene;
  private BorderPane myRoot;

  public Scene createSimulationWindow(Stage primaryStage, String title) {
    primaryStage.setTitle(title);
    myRoot = new BorderPane();
    // add relevant text to scene
    // create and set the scene
    myScene = new Scene(myRoot, SIMULATION_WIDTH, SIMULATION_HEIGHT);
    primaryStage.setScene(myScene);
    primaryStage.show();
    // add CSS files
    myScene.getStylesheets().add(getClass().getResource("SimulationView.css").toExternalForm());
    myScene.getStylesheets().add(getClass().getResource("ControlPanel.css").toExternalForm());
    return myScene;
  }

  public BorderPane getRoot() {
    return myRoot;
  }
}