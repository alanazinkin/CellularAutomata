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

  /**
   * Creates a new main pane to hold the grid view and control bar.
   * @param primaryStage holds all main panes and views for simulation except the Simulation Information
   * @param title name of the new stage that is displayed to the user
   * @return myScene
   */
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

  /**
   * Retrieves the root of the scene. Primarily used to add/ remove objects later on with root.getChildren().add()
   * or root.getChildren().remove()
   * @return BorderPane myRoot
   */
  public BorderPane getRoot() {
    return myRoot;
  }
}