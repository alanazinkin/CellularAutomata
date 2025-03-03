package cellsociety.view;

import static java.lang.Integer.parseInt;

import cellsociety.controller.FileRetriever;
import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.controller.SimulationUI;
import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.StateInterface;
import cellsociety.view.gridview.GridViewFactory;
import cellsociety.view.shapefactory.CellShapeFactory;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import cellsociety.view.gridview.GridView;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * SimulationView class is the high level class for creating a new simulation view with the relative
 * control panels, buttons, grid views etc.
 *
 * @author Alana Zinkin
 */
public class SimulationView {

  private SimulationController myController;
  private Scene myScene;
  private BorderPane myRoot;
  private ResourceBundle myResources;
  private GridView myGridView;
  private SimulationConfig myConfig;
  private String myThemeColor;
  private Map<String, String> mySimulationResourceMap;
  private Text iterationCounter;
  private CellStateLineGraph myCellStateLineGraph;

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
      throws FileNotFoundException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    createSimulationWindow(primaryStage);
    setTheme(themeColor, myScene);
    createGridView(simView, colorMap, grid);
    createControlPanel(primaryStage, simView);
    createRightSideDisplayBox(simulation, simView, colorMap, themeColor);
  }

  private void createRightSideDisplayBox(Simulation simulation, SimulationView simView,
      Map<StateInterface, String> colorMap, String themeColor) throws FileNotFoundException {
    VBox rightSideBox = new VBox();
    rightSideBox.setPadding(new Insets(50, 50, 50, 0));  // Adds 20px padding inside the VBox
    simView.getRoot().setRight(rightSideBox);
    Pane simInfoDisplay = createSimulationInfoDisplay(simulation, simView, themeColor);
    Pane cellPopChart = createCellPopulationsOverTimeChart(simView, themeColor, simulation, colorMap);
    rightSideBox.getChildren().addAll(simInfoDisplay, cellPopChart);
  }

  /**
   * initializes the cell populations over time chart
   *
   * @param simView the simulation view object
   * @param themeColor CSS theme style color
   * @param simulation simulation currently running
   * @param colorMap map of state interface values to the CSS identifier for that given state
   */
  public Pane createCellPopulationsOverTimeChart(SimulationView simView, String themeColor, Simulation simulation
  , Map<StateInterface, String> colorMap) {
    myCellStateLineGraph = new CellStateLineGraph(myResources.getString("Time"), myResources.getString("PopulationCount"),
        myResources.getString("CellStatesOverTime"));
    Pane vbox = myCellStateLineGraph.createDisplayBox(themeColor, simView);
    myCellStateLineGraph.updateLineChart(simulation.getStateCounts(), colorMap, simulation.retrieveIterationCount());
    return vbox;
  }

  private Pane createSimulationInfoDisplay(Simulation simulation, SimulationView simView,
      String themeColor)
      throws FileNotFoundException {
    SimulationInfoDisplay mySimInfoDisplay = new SimulationInfoDisplay(myConfig.getType(),
        myConfig.getTitle(),
        myConfig.getAuthor(), myConfig.getDescription(), myConfig.getParameters(),
        simulation.getColorMap(),
        myResources
    );
    return mySimInfoDisplay.createDisplayBox(themeColor, simView);
  }

  private void createGridView(SimulationView simView, Map<StateInterface, String> colorMap,
      Grid grid)
      throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

    myGridView = makeGridViewFromTiling(myConfig, grid);
    myGridView.createGridDisplay(simView.getRoot(), colorMap, myConfig);
  }

  private void createControlPanel(Stage primaryStage, SimulationView simView) {
    ControlPanel myControlPanel = new ControlPanel(primaryStage, myScene, myController,
        simView, myResources, myGridView);
    myControlPanel.setupControlBar(simView.getRoot());
    myControlPanel.makeLowerBar(simView.getRoot());
    try {
      myControlPanel.setUpLowerBar(simView.getRoot());
      makeIterationCounter();
      myControlPanel.addTextToTextBar(iterationCounter);
    } catch (Exception e) {
      SimulationUI.displayAlert(myResources.getString("Error"),
          myResources.getString("CustomizationBarError"));
      throw new NullPointerException(e.getMessage());
    }
  }

  /**
   * Creates a new main pane to hold the grid view and control bar.
   *
   * @param stage holds all main panes and views for simulation except the Simulation Information
   * @return myScene
   */
  public Scene createSimulationWindow(Stage stage) {
    myRoot = new BorderPane();
    myRoot.setId("myRoot");
    myScene = new Scene(myRoot, parseInt(mySimulationResourceMap.get("window.width")),
        parseInt(mySimulationResourceMap.get("window.height")));
    stage.setScene(myScene);
    stage.setMaximized(true);
    stage.show();
    return myScene;
  }

  /**
   * sets the themeColor instance variable to the selected themeColor string and updates the theme
   * to reflect the change
   *
   * @param themeColor selected theme color (Dark or Light)
   * @param scene      scene to be updated
   * @throws FileNotFoundException if there is no CSS file for the themeColor
   */
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
      scene.getStylesheets().add(getClass().getResource(completePath).toExternalForm());
    }
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
  public void updateGrid(Map<StateInterface, String> stateMap)
      throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
    if (myGridView != null) {
      myGridView.renderGrid(stateMap, myConfig);
    } else {
      SimulationUI.displayAlert(myResources.getString("Error"),
          myResources.getString("GridViewNull"));
    }
  }

  /**
   * updates the cell population chart according to the number of cells in a given state
   *
   * @param stateCounts a map between cell states and the number of cells in that given state
   * @param colorMap    map of cell states to colors for styling purposes
   */
  public void updateCellPopulationChart(Map<StateInterface, Double> stateCounts,
      Map<StateInterface, String> colorMap) {
    myCellStateLineGraph.updateLineChart(stateCounts, colorMap, myController.getIterationCount());
  }

  /**
   * retrieves the scene instance variable for the simulation view
   *
   * @return myScene instance variable
   */
  public Scene getScene() {
    return this.myScene;
  }

  /**
   * updates the iterationCounter instance variable text according to the number of iterations of
   * the simulation
   */
  public void updateIterationCounter() {
    iterationCounter.setText("Iteration Count: " + myController.getIterationCount());
  }

  private void makeIterationCounter() {
    iterationCounter = new Text("Iteration Count: " + myController.getIterationCount());
  }

  private GridView makeGridViewFromTiling(SimulationConfig simulationConfig, Grid grid)
      throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    String gridFactoryClassName = getGridFactoryName(simulationConfig);
    String fullFactoryClassName = "cellsociety.view.gridview." + gridFactoryClassName;

    // Create factory instance using reflection
    Class<?> factoryClass = Class.forName(fullFactoryClassName);
    GridViewFactory factory = (GridViewFactory) factoryClass.getDeclaredConstructor()
        .newInstance();

    // Create the cell shape using the factory
    GridView gridView = factory.createGridView(myController, simulationConfig, grid);

    return gridView;
  }

  private String getGridFactoryName(SimulationConfig simulationConfig) {
    String tiling = simulationConfig.getTiling();
    if (tiling == null) {
      throw new NullPointerException("Tiling cannot be null");
    }
    // Construct the factory class name
    return tiling + "GridViewFactory";
  }
}