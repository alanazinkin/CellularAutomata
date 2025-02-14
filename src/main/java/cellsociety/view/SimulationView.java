package cellsociety.view;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.StateInterface;
import cellsociety.view.gridview.FireGridView;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import cellsociety.view.gridview.GameOfLifeGridView;
import cellsociety.view.gridview.GridView;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SimulationView {
  public static final int SIMULATION_WIDTH = 1000;
  public static final int SIMULATION_HEIGHT = 800;

  private SimulationController myController;
  private Scene myScene;
  private BorderPane myRoot;
  private ResourceBundle myResources;
  private GridView myGridView;
  private SimulationConfig myConfig;
  private String myThemeColor;

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
   * @param colorMap data structure mapping cell states to visual colors in the simulation grid
   */
  public void initView(Stage primaryStage, Simulation simulation, SimulationView simView, Map<StateInterface, String> colorMap, Grid grid, String language, String themeColor) {
    createSimulationWindow(primaryStage);
    setTheme(themeColor);
    // make control panel
    ControlPanel myControlPanel = new ControlPanel(primaryStage, language, myController, simView, myResources);
    myControlPanel.setupControlBar(simView.getRoot());
    myControlPanel.makeLowerBar(simView.getRoot());
    try {
      myControlPanel.setUpLowerBar(simView.getRoot());
    }
    catch (Exception e) {
      myController.displayAlert(myResources.getString("Error"), myResources.getString("CustomizationBarError"));
      throw new NullPointerException(e.getMessage());
    }
    // create Grid
    myGridView = new FireGridView(myConfig, grid);
    myGridView.createGridDisplay(simView.getRoot(), colorMap);
    // make simulation information pop-up window
    SimulationInfoDisplay mySimInfoDisplay = new SimulationInfoDisplay(
        myConfig.getType(),
        myConfig.getTitle(),
        myConfig.getAuthor(),
        myConfig.getDescription(),
        myConfig.getParameters(),
        simulation.getColorMap(),
        language,
        myResources
    );
    mySimInfoDisplay.createDisplayBox(new Stage(), myResources.getString("SimInfo"), themeColor);
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
    primaryStage.setScene(myScene);
    primaryStage.show();
    return myScene;
  }

  public void setTheme(String themeColor) {
    myThemeColor = themeColor;
    updateTheme();
  }

  /**
   * Called by set theme.
   * <p>
   *   Ony way to update the theme is to call setTheme()
   * </p>
   */
  private void updateTheme() {
    System.out.println(myThemeColor);
    myScene.getStylesheets().clear();
    String themeFile = getThemeFolderOrFile(myConfig.getType());
    List<String> cssFiles = List.of(myThemeColor, getSimulationFile(themeFile, myThemeColor));
    addCSSFiles(cssFiles);
  }

//TODO: get rid of thiss method
  private String getThemeFolderOrFile(String simulationType) {
    switch (simulationType) {
      case "Game of Life": return "GameOfLife";
      case "Spreading of Fire": return "Fire";
      case "Percolation": return "Percolation";
      case "Schelling Segregation": return "Schelling";
      case "Wa-Tor World": return "WaTorWorld";
      default: myController.displayAlert(myResources.getString("Error"), myResources.getString("InvalidSimulationType"));
        return "";
    }
  }

  private String getSimulationFile(String themeFile, String selectedThemeColor) {
    return themeFile + "/" + themeFile + selectedThemeColor;
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

  public void updateGrid(Map<StateInterface, String> stateMap) {
    if (myGridView != null) {
      myGridView.updateCellColors(stateMap);
    } else {
      System.err.println("Error: GridView is not initialized.");
    }
  }

}