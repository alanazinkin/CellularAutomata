package cellsociety.View;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Controller.SimulationController;
import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.StateInterface;
import cellsociety.View.GridViews.FireGridView;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import cellsociety.View.GridViews.GameOfLifeGridView;
import cellsociety.View.GridViews.GridView;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SimulationView {
  public static final int SIMULATION_WIDTH = 1000;
  public static final int SIMULATION_HEIGHT = 800;

  private SimulationController myController;
  private List<String> myCSSFiles;
  private Scene myScene;
  private BorderPane myRoot;
  private ResourceBundle myResources;
  private GridView myGridView;
  private SimulationConfig myConfig;

  /**
   *
   * @param simulationConfig the simulation configuration containing all information about the exact simulation file
   * @param controller
   * @param resources
   */
  public SimulationView(SimulationConfig simulationConfig, SimulationController controller, ResourceBundle resources) {
    myConfig = simulationConfig;
    myController = controller;
    myResources = resources;
  }
  /**
   * entry point for adding all views to application
   *
   * @param primaryStage main stage onto which all elements are added
   * @param simulation the simulation model
   * @param simView the simulation view object
   * @param stateMap data structure mapping cell states to visual colors in the simulation grid
   */
  public void initView(Stage primaryStage, Simulation simulation, SimulationView simView, Map<StateInterface, Color> stateMap, Grid grid, String language, List<String> cssFiles) {
    myCSSFiles = cssFiles;
    createSimulationWindow(primaryStage);
    // make control panel
    ControlPanel myControlPanel = new ControlPanel(language, myController);
    myControlPanel.makeControlBar(simView.getRoot());
    myControlPanel.makeSliderBar(simView.getRoot());
    // create Grid
    myGridView = new FireGridView(myConfig, grid);
    myGridView.createGridDisplay(simView.getRoot(), stateMap);
    // make simulation information pop-up window
    SimulationInfoDisplay mySimInfoDisplay = new SimulationInfoDisplay(
        myConfig.getType(),
        myConfig.getTitle(),
        myConfig.getAuthor(),
        myConfig.getDescription(),
        myConfig.getParameters(),
        simulation.getColorMap(),
        language
    );
    mySimInfoDisplay.createDisplayBox(new Stage(), myResources.getString("SimInfo"));
  }

  /**
   * Creates a new main pane to hold the grid view and control bar.
   *
   * @param primaryStage holds all main panes and views for simulation except the Simulation
   *                     Information
   * @return myScene
   */
  public Scene createSimulationWindow(Stage primaryStage) {
    myRoot = new BorderPane();
    myScene = new Scene(myRoot, SIMULATION_WIDTH, SIMULATION_HEIGHT);
    addCSSFiles(myCSSFiles);
    primaryStage.setScene(myScene);
    primaryStage.show();
    return myScene;
  }

  private void addCSSFiles(List<String> files) {
    String basePath = "/cellsociety/CSS/";
    for (String file : files) {
      String completePath = basePath + file + ".css";
      System.out.println(completePath);
      myScene.getStylesheets().add(getClass().getResource(completePath).toExternalForm());
    }
  }


  private GridView createAppropriateGridView(Grid grid) {
      return switch (myConfig.getType().toLowerCase()) {
          case "spreading of fire" -> new FireGridView(myConfig, grid);
          case "game of life" -> new GameOfLifeGridView(myConfig, grid);
          default -> null;
      };
  }

  /**
   * Retrieves the root of the scene. Primarily used to add/ remove objects later on with root.getChildren().add()
   * or root.getChildren().remove()
   * @return BorderPane myRoot
   */
  public BorderPane getRoot() {
    return myRoot;
  }

  public void updateGrid(Map<StateInterface, Color> stateMap) {
    if (myGridView != null) {
      myGridView.updateCellColors(stateMap);
    } else {
      System.err.println("Error: GridView is not initialized.");
    }
  }

}