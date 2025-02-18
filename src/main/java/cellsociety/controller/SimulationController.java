package cellsociety.controller;

import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
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

  /**
   * Record representing various parameters required for simulations.
   */
  public record SimulationParameters(
      double fireProb,
      double treeProb,
      double satisfaction,
      double percolationProb
  ) {
    /**
     * Creates a SimulationParameters instance using default values from the configuration file.
     * @return a new SimulationParameters object populated with default values.
     */
    public static SimulationParameters fromConfig() {
      return new SimulationParameters(
          Double.parseDouble(CONFIG.getString("default.fire.prob")),
          Double.parseDouble(CONFIG.getString("default.tree.prob")),
          Double.parseDouble(CONFIG.getString("default.satisfaction")),
          Double.parseDouble(CONFIG.getString("default.percolation.prob"))
      );
    }
  }

  /**
   * Enum representing the different types of simulations available.
   */
  public enum SimulationType {
    GAME_OF_LIFE("Game of Life"),
    SPREADING_FIRE("Spreading of Fire"),
    PERCOLATION("Percolation"),
    SCHELLING("Schelling Segregation"),
    WATOR_WORLD("Wa-Tor World"),
    SAND("Sand"),
    LANGTON_LOOP("Langton Loop");

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
  private final cellsociety.controller.SimulationParameters parameters;
  private Stage stage;
  private ResourceBundle resources;
  private SimulationController myController;

  /**
   * Constructs a SimulationController and initializes key components.
   */
  public SimulationController() {
    this.parameters = cellsociety.controller.SimulationParameters.fromConfig();
    this.engine = new SimulationEngine(CONFIG);
    this.ui = new SimulationUI(CONFIG);
    this.fileManager = new SimulationFileManager();
    this.myController = this;
  }

  public void selectSimulation(String simulationType, String fileName, Stage stage,
      SimulationController controller) throws Exception {
    this.stage = stage;
    engine.pause();
    fileManager.loadFile(simulationType, fileName);
    init(stage, controller);
  }

  public void init(Stage stage, SimulationController controller) throws Exception {
    SimulationConfig config = fileManager.parseConfiguration();
    engine.initializeSimulation(config, controller);
    ui.initialize(stage, this);
  }

  public void stepSimulation(double elapsedTime) {
    try {
      engine.step();
    } catch (Exception e) {
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

  public static ResourceBundle getResourceConfig() {
    return CONFIG;
  }
}