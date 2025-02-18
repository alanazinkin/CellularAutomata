package cellsociety.view;

import static java.lang.Integer.parseInt;

import cellsociety.controller.FileRetriever;
import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.controller.SimulationUI;
import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.StateInterface;
import cellsociety.view.gridview.FireGridView;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import cellsociety.view.gridview.DefaultGridView;
import cellsociety.view.gridview.GridView;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SimulationView {

  private SimulationController myController;
  private Scene myScene;
  private BorderPane myRoot;
  private ResourceBundle myResources;
  private GridView myGridView;
  private SimulationConfig myConfig;
  private String myThemeColor;
  private Map<String, String> mySimulationResourceMap;
  private SimulationUI myUI;

  /**
   * @param simulationConfig  the simulation configuration containing all information about the
   *                          exact simulation file
   * @param controller        simulation controller responsible for handling events
   * @param languageResources the language file for user-selected language
   */
  public SimulationView(SimulationConfig simulationConfig, SimulationController controller,
      ResourceBundle languageResources) {
    myConfig = simulationConfig;
    myController = controller;
    myUI = controller.getUI();
    myResources = languageResources;
    mySimulationResourceMap = controller.retrieveImmutableConfigResourceBundle();
  }

  /**
   * entry point for adding all views to application
   *
   * @param primaryStage main stage onto which all elements are added
   * @param simulation   the simulation model
   * @param simView      the simulation view object
   * @param colorMap     data structure mapping cell states to visual colors in the simulation grid
   */
  public void initView(Stage primaryStage, Simulation simulation, SimulationView simView,
      Map<StateInterface, String> colorMap, Grid grid, String language, String themeColor)
      throws FileNotFoundException {
    createSimulationWindow(primaryStage);
    setTheme(themeColor, myScene);
    // create Grid
    myGridView = new DefaultGridView(myController, myConfig, grid);
    myGridView.createGridDisplay(simView.getRoot(), colorMap);
    // make control panel
    ControlPanel myControlPanel = new ControlPanel(primaryStage, myScene, myController,
        simView, myResources, myGridView);
    myControlPanel.setupControlBar(simView.getRoot());
    myControlPanel.makeLowerBar(simView.getRoot());
    try {
      myControlPanel.setUpLowerBar(simView.getRoot());
    } catch (Exception e) {
      myUI.displayAlert(myResources.getString("Error"),
          myResources.getString("CustomizationBarError"));
      throw new NullPointerException(e.getMessage());
    }
    // make simulation information pop-up window
    SimulationInfoDisplay mySimInfoDisplay = new SimulationInfoDisplay(myConfig.getType(),
        myConfig.getTitle(),
        myConfig.getAuthor(), myConfig.getDescription(), myConfig.getParameters(),
        simulation.getColorMap(), language,
        myResources
    );
    mySimInfoDisplay.createDisplayBox(new Stage(), myResources.getString("SimInfo"), themeColor,
        simView);
  }

  /**
   * Creates a new main pane to hold the grid view and control bar.
   *
   * @param stage holds all main panes and views for simulation except the Simulation Information
   * @return myScene
   */
  public Scene createSimulationWindow(Stage stage) {
    myRoot = new BorderPane();
    myScene = new Scene(myRoot, parseInt(mySimulationResourceMap.get("window.width")),
        parseInt(mySimulationResourceMap.get("window.height")));
    stage.setScene(myScene);
    stage.show();
    return myScene;
  }

  public void setTheme(String themeColor, Scene scene) throws FileNotFoundException {
    myThemeColor = themeColor;
    updateTheme(scene);
  }

  /**
   * Called by set theme.
   * <p>
   * Ony way to update the theme is to call setTheme()
   * </p>
   */
  private void updateTheme(Scene scene) throws FileNotFoundException {
    scene.getStylesheets().clear();
    FileRetriever retriever = new FileRetriever();
    String themeFile = retriever.getSimulationTypeFolderExtension(myConfig.getType());
    List<String> cssFiles = List.of(myThemeColor, getSimulationFile(themeFile, myThemeColor));
    addCSSFiles(cssFiles, scene);
  }

  private String getSimulationFile(String themeFile, String selectedThemeColor) {
    return themeFile + "/" + themeFile + selectedThemeColor;
  }

  private void addCSSFiles(List<String> files, Scene scene) {
    String basePath = "/cellsociety/CSS/";
    for (String file : files) {
      String completePath = basePath + file + ".css";
      System.out.println(completePath);
      scene.getStylesheets().add(getClass().getResource(completePath).toExternalForm());
    }
  }


  private GridView createAppropriateGridView(Grid grid) {
    return switch (myConfig.getType().toLowerCase()) {
      case "spreading of fire" -> new FireGridView(myController, myConfig, grid);
      case "game of life" -> new DefaultGridView(myController, myConfig, grid);
      default -> null;
    };
  }

  /**
   * Retrieves the root of the scene. Primarily used to add/ remove objects later on with
   * root.getChildren().add() or root.getChildren().remove()
   *
   * @return BorderPane myRoot
   */
  public BorderPane getRoot() {
    return myRoot;
  }

  /**
   * updates the cell colors of the grid
   *
   * @param stateMap holds the mapping for retrieving the CSS ids for a given cell state
   */
  public void updateGrid(Map<StateInterface, String> stateMap) {
    if (myGridView != null) {
      myGridView.updateCellColors(stateMap);
    } else {
      System.err.println("Error: GridView is not initialized.");
    }
  }

}