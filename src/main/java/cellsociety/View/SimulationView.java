package cellsociety.View;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.StateInterface;
import cellsociety.View.GridViews.FireGridView;
import java.util.Map;
import java.util.ResourceBundle;

import cellsociety.View.GridViews.GridView;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SimulationView {
  public static final int SIMULATION_WIDTH = 1000;
  public static final int SIMULATION_HEIGHT = 800;
  // use Java's dot notation, like with import, for properties files
  public static final String DEFAULT_RESOURCE_PACKAGE = "cellsociety.View.";

  private Scene myScene;
  private BorderPane myRoot;
  private ResourceBundle myResources;
  private GridView myGridView;

  /**
   * entry point for adding all views to application
   *
   * @param primaryStage main stage onto which all elements are added
   * @param simulationConfig the simulation configuration containing all information about the exact simulation file
   * @param simulation the simulation model
   * @param simView the simulation view object
   * @param stateMap data structure mapping cell states to visual colors in the simulation grid
   */
  public void initView(Stage primaryStage, SimulationConfig simulationConfig, Simulation simulation, SimulationView simView, Map<StateInterface, Color> stateMap, Grid grid, String language) {
    // get resource bundle
    myResources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + language);
    // make initial splash screen window
    SplashScreen initialScreen = new SplashScreen();
    Scene splashScreen = initialScreen.showSplashScreen(new Stage(), "Cell Society", 1000, 800);
    // make simulation information pop-up window
    SimulationInfoDisplay mySimInfoDisplay = new SimulationInfoDisplay(
        simulationConfig.getType(),
        simulationConfig.getTitle(),
        simulationConfig.getAuthor(),
        simulationConfig.getDescription(),
        simulationConfig.getParameters(),
        simulation.getColorMap(),
        language
    );
    mySimInfoDisplay.createDisplayBox(new Stage(), myResources.getString("SimInfo"));
    createSimulationWindow(primaryStage);
    // make control panel
    ControlPanel myControlPanel = new ControlPanel(language);
    myControlPanel.makeControlBar(simView.getRoot());
    myControlPanel.makeSliderBar(simView.getRoot());
    // create Grid
    myGridView = new FireGridView(simulationConfig, grid);
    myGridView.createGridDisplay(simView.getRoot(), stateMap);
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

  /**
   * method that allows access to the simulation view class' resource bundle for a specific language
   * @return Resource Bundle for a user-selected language
   */
  public ResourceBundle getResources() {return myResources;}

  public void updateGrid(Map<StateInterface, Color> stateMap) {
    if (myGridView != null) {
      myGridView.updateCellColors(stateMap);
    } else {
      System.err.println("Error: GridView is not initialized.");
    }
  }

}