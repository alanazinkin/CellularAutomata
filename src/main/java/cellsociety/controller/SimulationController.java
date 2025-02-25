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
  private Stage stage;
  private SimulationController myController;
  /**
   * Constructs a SimulationController and initializes key components.
   */
  public SimulationController() {
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