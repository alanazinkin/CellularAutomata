package cellsociety.controller;

import cellsociety.model.*;

import cellsociety.view.gridview.GridView;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Central controller for managing cellular automata simulations,
 * responsible for coordinating interactions between model, view, and configuration.
 *
 * <p>Design Goals:
 * - Provide a clean, decoupled interface for simulation management
 * - Support dynamic configuration and styling of simulations
 * - Enable flexible simulation control and state management
 *
 * <p>Key Responsibilities:
 * - Initialize and configure simulations
 * - Control simulation lifecycle (start, pause, step, reset)
 * - Apply styles and configuration dynamically
 * - Manage simulation state and resources
 *
 * @version 2.0
 * @since 2024.03
 * @author angelapredolac
 */
public class SimulationController {

  public static final String DEFAULT_RESOURCE_PACKAGE =
          SimulationController.class.getPackageName() + ".";

  private static final Logger LOG = LogManager.getLogger();

  /**
   * Configuration resource bundle for simulation constants.
   * Provides immutable access to core simulation parameters.
   */
  private static final ResourceBundle CONFIG = ResourceBundle.getBundle(
          SimulationController.class.getPackageName() + ".Simulation");

  // Core simulation components
  private final SimulationEngine engine;
  private final SimulationUI ui;
  private final SimulationFileManager fileManager;
  private final XMLStyleParser styleParser;

  // Current simulation state
  private Stage primaryStage;
  private SimulationStyle currentStyle;

  /**
   * Creates a new SimulationController with core dependencies.
   *
   * @param engine Simulation calculation and state management engine
   * @param ui User interface for rendering and interaction
   * @param fileManager Manages file loading and saving operations
   * @param styleParser Parses XML style configuration files
   */
  public SimulationController(
          SimulationEngine engine,
          SimulationUI ui,
          SimulationFileManager fileManager,
          XMLStyleParser styleParser) {
    this.engine = Objects.requireNonNull(engine, "SimulationEngine cannot be null");
    this.ui = Objects.requireNonNull(ui, "SimulationUI cannot be null");
    this.fileManager = Objects.requireNonNull(fileManager, "SimulationFileManager cannot be null");
    this.styleParser = Objects.requireNonNull(styleParser, "XMLStyleParser cannot be null");

    this.currentStyle = new SimulationStyle();
  }

  /**
   * Enum representing the different types of simulations available.
   */
  public enum SimulationType {
    GAME_OF_LIFE("Game of Life"), SPREADING_FIRE("Spreading of Fire"), PERCOLATION(
        "Percolation"), SCHELLING("Schelling Segregation"), WATOR_WORLD(
        "Wa-Tor World"), LANGTON_LOOP("Langton Loop"), SUGAR_SCAPE("Sugar Scape"), BACTERIA(
        "Bacteria"), ANT("Foraging Ants"), TEMPESTI_LOOP("Tempesti Loop"), RULES_GAME_OF_LIFE(
        "Rules-Based Game of Life");

    private final String displayName;

    /**
     * Constructs a SimulationType with a specified display name.
     *
     * @param displayName the display name of the simulation type.
     */
    SimulationType(String displayName) {
      this.displayName = displayName;
    }

    /**
     * Retrieves a SimulationType from a string representation.
     *
     * @param text the display name of the simulation type.
     * @return an Optional containing the corresponding SimulationType, if found.
     */
    public static Optional<SimulationType> fromString(String text) {

      return Arrays.stream(SimulationType.values())
              .filter(type -> type.displayName.equals(text))
              .findFirst();
    }
  }

  /**
   * Constructs a SimulationController and initializes key components.
   */
  public SimulationController() {
    this.engine = new SimulationEngine(CONFIG);
    this.ui = new SimulationUI(CONFIG);
    this.fileManager = new SimulationFileManager();
    this.styleParser = new XMLStyleParser();
    this.currentStyle = new SimulationStyle();
  }

  /**
   * Selects and loads a simulation with an optional style file.
   *
   * @param simulationType the type of simulation to load
   * @param fileName       the name of the simulation configuration file
   * @param styleFileName  the name of the style file (can be null for default style)
   * @param stage          the primary stage for the application
   * @param controller     the simulation controller
   * @throws Exception if there's an error loading the simulation or style
   */
  public void selectSimulation(String simulationType, String fileName, String styleFileName,
      Stage stage, SimulationController controller) throws Exception {
    this.primaryStage = stage;
    engine.pause();

    fileManager.loadFile(simulationType, fileName);

    if (styleFileName == null || styleFileName.isEmpty()) {
      try {
        String defaultStylePath = getDefaultStylePath(simulationType);

        LOG.warn("Attempting to load default style from: " + defaultStylePath);

        if (defaultStylePath != null) {
          try {
            String fileContents = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(defaultStylePath)));
            LOG.info("Default Style File Contents:\n" + fileContents);
          } catch (Exception e) {
            LOG.error("Could not read file contents: " + e.getMessage());
          }

          this.currentStyle = styleParser.parseStyleFile(defaultStylePath);
          LOG.info("Successfully loaded default style");
        } else {
          LOG.error("No default style file found");
          this.currentStyle = new SimulationStyle();
        }
      } catch (Exception e) {
        LOG.error("Error loading default style: " + e.getMessage());
        this.currentStyle = new SimulationStyle();
      }
    } else {
      try {
        this.currentStyle = styleParser.parseStyleFile(styleFileName);
      } catch (ConfigurationException e) {
        ui.handleError("StyleLoadError", e);
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
    String formattedType = simulationType.toLowerCase()
            .replace(" ", "")
            .replace("of", "");

    String[] possiblePaths = {
            "data/" + formattedType + "/" + formattedType + "_default.xml",
            "styles/" + formattedType + "_default.xml",
            formattedType + "_default.xml"
    };

    for (String path : possiblePaths) {
      File file = new File(path);
      LOG.info("Checking style file path: " + file.getAbsolutePath());
      if (file.exists()) {
        LOG.info("Found style file at: " + file.getAbsolutePath());
        return path;
      }
    }

    LOG.error("No default style file found for simulation type: " + simulationType);
    return null;

  }

  /**
   * Initializes the simulation with the current configuration and style.
   *
   * @param stage      the primary stage for the application
   * @param controller the simulation controller
   * @throws Exception if there's an error initializing the simulation
   */
  public void init(Stage stage, SimulationController controller) throws Exception {
    SimulationConfig config = fileManager.parseConfiguration();
    engine.initializeSimulation(config, controller);

    ui.initialize(stage, this);

    applyStyle(currentStyle);
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
    LOG.info("appearances: " + appearances);
    Map<StateInterface, String> colorMap = new HashMap<>();

    String simulationType = ui.getView().getSimulationConfig().getType().toLowerCase().replace(" ", "");
    String defaultColor = "#808080"; // Gray as a default

    for (Map.Entry<Integer, StateInterface> stateEntry : simulation.getStateMap().entrySet()) {
      StateInterface state = stateEntry.getValue();
      String stateName = state.getStateValue().toLowerCase();

      String cssSelector = simulationType + "-state-" + stateName;
      LOG.debug(cssSelector);

      if (appearances.containsKey(cssSelector)) {
        CellAppearance appearance = appearances.get(cssSelector);
        String color = appearance.getColor() != null ? appearance.getColor() : defaultColor;
        String cssStyle = cssSelector + ":" + color;

        ui.getView().getRoot().setStyle(cssStyle);
      } else {
        LOG.error("No style found for state: " + stateName + ". Using default color.");
      }
    }
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
    }

    if (style.getCellShape() != null) {
      updateGridCellShape(grid, style.getCellShape().toString());
    }

    if (style.getNeighborArrangement() != null) {
      updateGridNeighborArrangement(grid, style.getNeighborArrangement().toString());
    }
  }

  /**
   * Updates the grid's edge policy based on the style setting.
   *
   * @param grid           the grid to update
   * @param edgePolicyName the name of the edge policy to apply
   */
  private void updateGridEdgePolicy(Grid grid, String edgePolicyName) {
    try {
      if (edgePolicyName.equalsIgnoreCase("INFINITE")) {
        InfiniteGrid infiniteGrid = new InfiniteGrid(grid.getRows(), grid.getCols(), grid.getDefaultState());
        engine.setGrid(infiniteGrid);
        return;
      }
      Method configureEdgePolicy = grid.getClass().getMethod("configureEdgePolicy", String.class);
      configureEdgePolicy.invoke(grid, edgePolicyName);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      try {
        Method setEdgeMode = grid.getClass().getMethod("setEdgeMode", String.class);
        setEdgeMode.invoke(grid, edgePolicyName);
      } catch (Exception ex) {
        LOG.warn("Could not set edge policy: {}", ex.getMessage());
      }


      String edgeType = edgePolicyName.toUpperCase();
      EdgeStrategy strategy = EdgeStrategyFactory.createEdgeStrategy(edgeType);
      grid.setEdgeStrategy(strategy);

    } catch (Exception e) {
      LOG.warn("Error setting edge policy: " + e.getMessage());
      grid.setEdgeStrategy(EdgeStrategyFactory.createEdgeStrategy("BOUNDED"));
    }
  }

  /**
   * Converts style names (e.g., "Toroidal") to EdgeStrategyFactory-compatible types.
   */
  private String convertEdgePolicyNameToType(String edgePolicyName) {
    return edgePolicyName.toUpperCase();
  }

  /**
   * Updates the grid's cell shape based on the style setting
   *
   * @param grid          the grid to update
   * @param cellShapeName the name of the cell shape to apply
   */
  private void updateGridCellShape(Grid grid, String cellShapeName) {
    try {
      Method setCellShapeMethod = grid.getClass().getMethod("setCellShape", String.class);
      setCellShapeMethod.invoke(grid, cellShapeName);
    } catch (Exception e) {
      try {
        Method configureCellShapeMethod = grid.getClass()
            .getMethod("configureCellShape", String.class);
        configureCellShapeMethod.invoke(grid, cellShapeName);
      } catch (Exception ex) {
        LOG.error("Could not set cell shape: " + ex.getMessage());
      }
    }
  }

  /**
   * Updates the grid's neighbor arrangement based on the style setting.
   *
   * @param grid                    the grid to update
   * @param neighborArrangementName the name of the neighbor arrangement to apply
   */
  private void updateGridNeighborArrangement(Grid grid, String neighborArrangementName) {
    try {
      String neighborhoodType = convertNeighborArrangementNameToType(neighborArrangementName);
      NeighborhoodStrategy neighborhoodStrategy =
              NeighborhoodFactory.createNeighborhoodStrategy(neighborhoodType);
      grid.setNeighborhoodStrategy(neighborhoodStrategy);

    } catch (Exception e) {
      LOG.error("Error setting neighbor arrangement: " + e.getMessage());
      grid.setNeighborhoodStrategy(new MooreNeighborhood());
    }
  }

  /**
   * Converts style names (e.g., "Moore") to NeighborhoodFactory-compatible types.
   */
  private String convertNeighborArrangementNameToType(String neighborArrangementName) {
    return neighborArrangementName.toUpperCase();
  }

  /**
   * Applies display options from the style.
   *
   * @param style the style containing display option information
   */
  private void applyDisplayOptions(SimulationStyle style) {
      ui.setGridOutlineVisible(style.isShowGridOutline());
      ui.setColorTheme(style.getColorTheme().toString());
      engine.setSpeed(style.getAnimationSpeed());
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
    } catch (ConfigurationException | ClassNotFoundException | InvocationTargetException |
             InstantiationException | IllegalAccessException | NoSuchMethodException e) {
      ui.handleError("StyleLoadError", e);
    }
  }

  /**
   * Opens a file chooser dialog to select and load a style file.
   */
  public void chooseAndLoadStyle() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Style File");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));

    File selectedFile = fileChooser.showOpenDialog(primaryStage);
    if (selectedFile != null) {
      loadStyle(selectedFile.getAbsolutePath());
    }
  }

  /**
   * Saves the current style to a file.
   */
  public void saveStyle() {
    fileManager.saveStyle(primaryStage, ui.getResources(), currentStyle);
  }

  /**
   * advance the simulation one step
   */
  public void stepSimulation() {
    try {
      engine.step();
    } catch (Exception e) {
      ui.handleError("StepError", e);
      pauseSimulation();
    }
  }

  /**
   * allows user to go back one "step" in time to view previous cell states, but will not allow this
   * action if it is the zeroth iteration. Will also prevent user from stepping backward more than
   * once in a row
   */
  public void stepBackSimulation() {
    try {
      engine.stepBackOnce();
    } catch (Exception e) {
      ui.handleError("StepError", e);
      pauseSimulation();
    }
  }

  /**
   * start running the simulation
   */
  public void startSimulation() {
    engine.start();
  }

  /**
   * pause the simulation
   */
  public void pauseSimulation() {
    engine.pause();
  }

  /**
   * resets the grid to its initial configuration state
   */
  public void resetGrid() {
    try {
      engine.resetGrid();
      ui.updateView(engine.getSimulation().getColorMap(), engine.getSimulation().getStateCounts());
    } catch (Exception e) {
      ui.handleError("ResetError", e);
    }
  }

  /**
   * sets the simulation speed according to the slider adjusted value from the front-end display
   *
   * @param speed the new speed with which to update the timeline
   */
  public void setSimulationSpeed(double speed) {
    engine.setSpeed(speed);
    currentStyle.setAnimationSpeed(speed);
  }

  /**
   * saves the current simulation state as a new configuration file
   */
  public void saveSimulation() {
    fileManager.saveSimulation(primaryStage, ui.getResources(), engine.getConfig(), engine.getGrid());
  }

  /**
   * static method for retrieving an immutable version of the simulation configuration resource
   * bundle which holds constants, whcih are used for setting up the simulation
   *
   * @return Map<String, String> where keys are used to retrieve the string values
   */
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

  /**
   * @return the simulation object
   */
  public Simulation getSimulation() {
    return engine.getSimulation();
  }

  /**
   * @return the grid object that holds the cells of the simulation
   */
  public Grid getGrid() {
    return engine.getGrid();
  }

  /**
   * @return the simulation configuration object that controls the initialization of the simulation
   */
  public SimulationConfig getSimulationConfig() {
    return engine.getConfig();
  }

  /**
   * @return the UI object responsible for rendering the front-end of the simulation
   */
  public SimulationUI getUI() {
    return ui;
  }

  /**
   * @return number of iterations that have passed of the simulation
   */
  public int getIterationCount() {
    return engine.getSimulation().retrieveIterationCount();
  }

  /**
   * sets the grid tiling to the new tiling by updating the grid view
   *
   * @param newTiling the new tiling selected
   * @param colorMap  map of state interface values to CSS identifiers
   * @param grid      the grid holding the underlying cell objects
   * @throws ClassNotFoundException    if there is no factory class for creating the grid view
   * @throws InvocationTargetException if a new grid view cannot be made
   * @throws NoSuchMethodException     if there is no constructor for creating the gridview
   * @throws InstantiationException    if a new gridview cannot be instantiated
   * @throws IllegalAccessException    if user attempts to access a method that should not be
   *                                   accessed
   */
  public GridView setGridTiling(String newTiling, Map<StateInterface, String> colorMap, Grid grid)
      throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    getSimulationConfig().setTiling(newTiling);
    return ui.createGridViewFromTiling(colorMap, grid);
  }

  /**
   * sets the edge strategy of the simulation
   *
   * @param type the type of edge strategy selected
   */
  public void setEdgeStrategy(String type) {
    engine.setEdgeStrategy(type);
  }

  /**
   * sets the neighborhood strategy of the simulation
   *
   * @param type the type of neighborhood strategy selected
   */
  public void setNeighborhoodStrategy(String type) {
    engine.setNeighborhoodStrategy(type);
  }

}