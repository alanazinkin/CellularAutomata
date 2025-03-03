package cellsociety.controller;

import cellsociety.model.Grid;
import cellsociety.model.Simulation;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import cellsociety.model.StateInterface;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


/**
 * Class responsible for connecting frontend and backend to make the simulation work as a whole.
 *
 * @author Angela Predolac
 */
public class SimulationController {
  private static final ResourceBundle CONFIG = ResourceBundle.getBundle(
      SimulationController.class.getPackageName() + ".Simulation");
  public static final String DEFAULT_RESOURCE_PACKAGE =
      SimulationController.class.getPackageName() + ".";
  private static final String DEFAULT_STYLE_FILE = "default.xml";

  /**
   * Enum representing the different types of simulations available.
   */
  public enum SimulationType {
    GAME_OF_LIFE("Game of Life"),
    SPREADING_FIRE("Spreading of Fire"),
    PERCOLATION("Percolation"),
    SCHELLING("Schelling Segregation"),
    WATOR_WORLD("Wa-Tor World"),
    LANGTON_LOOP("Langton Loop"),
    SUGAR_SCAPE("Sugar Scape"),
    BACTERIA("Bacteria"),
    ANT("Foraging Ants"),
    TEMPESTI_LOOP("Tempesti Loop"),
    RULES_GAME_OF_LIFE("Rules-Based Game of Life");

    private final String displayName;

    /**
     * Constructs a SimulationType with a specified display name.
     * @param displayName the display name of the simulation type.
     */
    SimulationType(String displayName) {
      this.displayName = displayName;
    }

    /**
     * Retrieves a SimulationType from a string representation.
     * @param text the display name of the simulation type.
     * @return an Optional containing the corresponding SimulationType, if found.
     */
    public static Optional<SimulationType> fromString(String text) {
      return Arrays.stream(SimulationType.values())
          .filter(type -> type.displayName.equals(text))
          .findFirst();
    }
  }

  private final SimulationUI ui;
  private final SimulationEngine engine;
  private final SimulationFileManager fileManager;
  private final XMLStyleParser styleParser;
  private Stage stage;
  private SimulationController myController;
  private SimulationStyle currentStyle;

  /**
   * Constructs a SimulationController and initializes key components.
   */
  public SimulationController() {
    this.engine = new SimulationEngine(CONFIG);
    this.ui = new SimulationUI(CONFIG);
    this.fileManager = new SimulationFileManager();
    this.styleParser = new XMLStyleParser();
    this.myController = this;
    this.currentStyle = new SimulationStyle();
  }

  /**
   * Selects and loads a simulation with an optional style file.
   *
   * @param simulationType the type of simulation to load
   * @param fileName the name of the simulation configuration file
   * @param styleFileName the name of the style file (can be null for default style)
   * @param stage the primary stage for the application
   * @param controller the simulation controller
   * @throws Exception if there's an error loading the simulation or style
   */
  public void selectSimulation(String simulationType, String fileName, String styleFileName, Stage stage,
                               SimulationController controller) throws Exception {
    this.stage = stage;
    engine.pause();
    fileManager.loadFile(simulationType, fileName);

    if (styleFileName != null && !styleFileName.isEmpty()) {
      try {
        this.currentStyle = styleParser.parseStyleFile(styleFileName);
      } catch (ConfigurationException e) {
        ui.handleError("StyleLoadError", e);
        this.currentStyle = new SimulationStyle();
      }
    } else {
      try {
        String defaultStylePath = getDefaultStylePath(simulationType);
        this.currentStyle = styleParser.parseStyleFile(defaultStylePath);
      } catch (ConfigurationException e) {
        this.currentStyle = new SimulationStyle();
      }
    }
    init(stage, controller);
  }

  /**
   * Gets the default style path for a simulation type.
   *
   * @param simulationType the type of simulation
   * @return the path to the default style file for this simulation type
   */
  private String getDefaultStylePath(String simulationType) {
    return "styles/" + simulationType.toLowerCase().replace(" ", "_") + "_" + DEFAULT_STYLE_FILE;
  }

  /**
   * Initializes the simulation with the current configuration and style.
   *
   * @param stage the primary stage for the application
   * @param controller the simulation controller
   * @throws Exception if there's an error initializing the simulation
   */
  public void init(Stage stage, SimulationController controller) throws Exception {
    SimulationConfig config = fileManager.parseConfiguration();
    engine.initializeSimulation(config, controller);

    applyStyle(currentStyle);

    ui.initialize(stage, this);
  }

  /**
   * Applies a style to the current simulation.
   *
   * @param style the style to apply
   */
  public void applyStyle(SimulationStyle style) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
    applyCellStateAppearances(style);

    applyGridProperties(style);

    applyDisplayOptions(style);

    ui.updateView(engine.getSimulation().getColorMap());
  }

  /**
   * Applies cell state appearances from the style.
   *
   * @param style the style containing cell appearance information
   */
  private void applyCellStateAppearances(SimulationStyle style) {
    Simulation simulation = engine.getSimulation();
    Map<String, CellAppearance> appearances = style.getCellAppearances();
    Map<StateInterface, String> colorMap = new HashMap<>();

    for (Map.Entry<String, CellAppearance> entry : appearances.entrySet()) {
      String stateName = entry.getKey();
      CellAppearance appearance = entry.getValue();
      StateInterface stateInterface = getStateByName(simulation, stateName);

      if (stateInterface != null) {
        colorMap.put(stateInterface, appearance.usesImage() ? appearance.getImagePath() : appearance.getColor());
      }
    }

    simulation.setColorMap(colorMap);
  }

  private StateInterface getStateByName(Simulation simulation, String stateName) {
    Map<Integer, StateInterface> stateMap = simulation.getStateMap(); // Assuming there's a getter

    for (StateInterface state : stateMap.values()) {
      if (state.getStateValue().equals(stateName)) {
        return state;
      }
    }

    return null;
  }

  /**
   * Applies grid properties from the style.
   *
   * @param style the style containing grid property information
   */
  private void applyGridProperties(SimulationStyle style) {
    Grid grid = engine.getGrid();

    if (style.getEdgePolicy() != null) {
      updateGridEdgePolicy(grid, style.getEdgePolicy().toString());
    } else {
      updateGridEdgePolicy(grid, "Finite"); // Default
    }

    if (style.getCellShape() != null) {
      updateGridCellShape(grid, style.getCellShape().toString());
    } else {
      updateGridCellShape(grid, "Square"); // Default
    }

    if (style.getNeighborArrangement() != null) {
      updateGridNeighborArrangement(grid, style.getNeighborArrangement().toString());
    } else {
      updateGridNeighborArrangement(grid, "Moore"); // Default
    }
  }

  /**
   * Updates the grid's edge policy based on the style setting
   *
   * @param grid the grid to update
   * @param edgePolicyName the name of the edge policy to apply
   */
  private void updateGridEdgePolicy(Grid grid, String edgePolicyName) {
    try {
      Method configureEdgePolicy = grid.getClass().getMethod("configureEdgePolicy", String.class);
      configureEdgePolicy.invoke(grid, edgePolicyName);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      try {
        Method setEdgeMode = grid.getClass().getMethod("setEdgeMode", String.class);
        setEdgeMode.invoke(grid, edgePolicyName);
      } catch (Exception ex) {
        System.err.println("Could not set edge policy: " + ex.getMessage());
      }
    }
  }

  /**
   * Updates the grid's cell shape based on the style setting
   *
   * @param grid the grid to update
   * @param cellShapeName the name of the cell shape to apply
   */
  private void updateGridCellShape(Grid grid, String cellShapeName) {
    try {
      Method setCellShapeMethod = grid.getClass().getMethod("setCellShape", String.class);
      setCellShapeMethod.invoke(grid, cellShapeName);
    } catch (Exception e) {
      try {
        Method configureCellShapeMethod = grid.getClass().getMethod("configureCellShape", String.class);
        configureCellShapeMethod.invoke(grid, cellShapeName);
      } catch (Exception ex) {
        System.err.println("Could not set cell shape: " + ex.getMessage());
      }
    }
  }

  /**
   * Updates the grid's neighbor arrangement based on the style setting
   *
   * @param grid the grid to update
   * @param neighborArrangementName the name of the neighbor arrangement to apply
   */
  private void updateGridNeighborArrangement(Grid grid, String neighborArrangementName) {
    try {
      Method setNeighborArrangementMethod = grid.getClass().getMethod("setNeighborArrangement", String.class);
      setNeighborArrangementMethod.invoke(grid, neighborArrangementName);
    } catch (Exception e) {
      try {
        Method configureNeighborsMethod = grid.getClass().getMethod("configureNeighbors", String.class);
        configureNeighborsMethod.invoke(grid, neighborArrangementName);
      } catch (Exception ex) {
        System.err.println("Could not set neighbor arrangement: " + ex.getMessage());
      }
    }
  }


  /**
   * Applies display options from the style.
   *
   * @param style the style containing display option information
   */
  private void applyDisplayOptions(SimulationStyle style) {
    if (ui != null) {
      ui.setGridOutlineVisible(style.isShowGridOutline());

      String theme = (style.getColorTheme() != null) ? style.getColorTheme().toString() : "Light";
      ui.setColorTheme(theme);
    }

    if (engine != null) {
      engine.setSpeed(style.getAnimationSpeed() > 0 ? style.getAnimationSpeed() : 1.0);
    }
  }

  /**
   * Loads a style file and applies it to the current simulation.
   *
   * @param styleFilePath the path to the style file
   */
  public void loadStyle(String styleFilePath) {
    try {
      SimulationStyle style = styleParser.parseStyleFile(styleFilePath);
      this.currentStyle = style;
      applyStyle(style);
    } catch (ConfigurationException | ClassNotFoundException | InvocationTargetException | InstantiationException |
             IllegalAccessException | NoSuchMethodException e) {
      ui.handleError("StyleLoadError", e);
    }
  }

  /**
   * Opens a file chooser dialog to select and load a style file.
   */
  public void chooseAndLoadStyle() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Style File");
    fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("XML Files", "*.xml"));

    File selectedFile = fileChooser.showOpenDialog(stage);
    if (selectedFile != null) {
      loadStyle(selectedFile.getAbsolutePath());
    }
  }

  /**
   * Saves the current style to a file.
   */
  public void saveStyle() {
    fileManager.saveStyle(stage, ui.getResources(), currentStyle);
  }

  public void stepSimulation() {
    try {
      engine.step();
    } catch (Exception e) {
      ui.handleError("StepError", e);
      pauseSimulation();
    }
  }

  public void stepBackSimulation() {
    try {
      engine.stepBackOnce();
    }
    catch (Exception e) {
      ui.handleError("StepError", e);
      pauseSimulation();
    }
  }

  public void startSimulation() {
    engine.start();
  }

  public void pauseSimulation() {
    engine.pause();
  }

  public void resetGrid() {
    try {
      engine.resetGrid();
      ui.updateView(engine.getSimulation().getColorMap());
    } catch (Exception e) {
      ui.handleError("ResetError", e);
    }
  }

  public void setSimulationSpeed(double speed) {
    engine.setSpeed(speed);
    currentStyle.setAnimationSpeed(speed);
  }

  public void saveSimulation() {
    fileManager.saveSimulation(stage, ui.getResources(), engine.getConfig(), engine.getGrid());
  }

  public static Map<String, String> retrieveImmutableConfigResourceBundle() {
    return convertResourceBundletoImmutableMap(CONFIG);
  }

  private static Map<String, String> convertResourceBundletoImmutableMap(ResourceBundle bundle) {
    Map<String, String> copy = new HashMap<>();
    for (String key : bundle.keySet()) {
      copy.put(key, bundle.getString(key));
    }
    return Collections.unmodifiableMap(copy);
  }

  public Simulation getSimulation() {
    return engine.getSimulation();
  }

  public Grid getGrid() {
    return engine.getGrid();
  }

  public SimulationConfig getSimulationConfig() {
    return engine.getConfig();
  }

  public SimulationUI getUI() {
    return ui;
  }

  public int getIterationCount() {
    return engine.getSimulation().retrieveIterationCount();
  }

}