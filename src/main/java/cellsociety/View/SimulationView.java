package cellsociety.View;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.Simulation;
import cellsociety.View.GridViews.GameOfLifeGridView;
import java.util.ArrayList;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SimulationView {
  public static final int SIMULATION_WIDTH = 1000;
  public static final int SIMULATION_HEIGHT = 800;

  private Scene myScene;
  private BorderPane myRoot;

  public void initView(Stage primaryStage, SimulationConfig simConfig, Simulation simulation, SimulationView simView) {
    // make simulation information pop-up window
    SimulationInfoDisplay mySimInfoDisplay = new SimulationInfoDisplay(
        simConfig.getType(),
        simConfig.getTitle(),
        simConfig.getAuthor(),
        simConfig.getDescription(),
        //TODO: change to the real parameters from XML file
        new ArrayList<>(),
        simulation.getStateMap()
    );
    mySimInfoDisplay.createDisplayBox(new Stage(), "Simulation Information");
    createSimulationWindow(primaryStage);
    // make control panel
    ControlPanel myControlPanel = new ControlPanel();
    myControlPanel.makeControlBar(simView.getRoot());
    myControlPanel.makeSliderBar(simView.getRoot());
    // create Grid
    GameOfLifeGridView myGridView = new GameOfLifeGridView();
    myGridView.createGridDisplay(simView.getRoot());
  }

  /**
   * Creates a new main pane to hold the grid view and control bar.
   * @param primaryStage holds all main panes and views for simulation except the Simulation Information
   * @return myScene
   */
  public Scene createSimulationWindow(Stage primaryStage) {
    myRoot = new BorderPane();
    // add relevant text to scene
    // create and set the scene
    myScene = new Scene(myRoot, SIMULATION_WIDTH, SIMULATION_HEIGHT);
    primaryStage.setScene(myScene);
    primaryStage.show();
    // add CSS files
    myScene.getStylesheets().add(getClass().getResource("/cellsociety/CSS/ControlPanel.css").toExternalForm());
    myScene.getStylesheets().add(getClass().getResource("/cellsociety/CSS/ControlPanel.css").toExternalForm());
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