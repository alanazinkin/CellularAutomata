package cellsociety.controller;

import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.simulations.*;
import cellsociety.model.state.GameOfLifeState;
import cellsociety.view.SimulationView;
import cellsociety.view.SplashScreen;

import cellsociety.view.UserController;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SimulationController {

  private static final ResourceBundle CONFIG = ResourceBundle.getBundle("simulation");
  private static final String DEFAULT_FILE_PATH = CONFIG.getString("default.file.path");
  public static final String DEFAULT_RESOURCE_PACKAGE = CONFIG.getString("default.resource.package");

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
    SCHELLING("Schelling Segregation");

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

  private String completeConfigFilePath;
  private Stage myStage;
  private final Timeline myTimeline;
  private ResourceBundle myResources;
  private Simulation mySimulation;
  private SimulationView mySimView;
  private Grid myGrid;
  private SimulationConfig mySimulationConfig;
  private final SimulationParameters parameters;
  private FileRetriever myFileRetriever;
  private SimulationController myController;

  /**
   * Constructs a SimulationController and initializes key components.
   */
  public SimulationController() {
    this.myStage = new Stage();
    this.myTimeline = initializeTimeline();
    this.parameters = SimulationParameters.fromConfig();
  }

  public void selectSimulation(String simulationType, String fileName, Stage stage, SimulationController simulationController) throws Exception {
    if (myTimeline != null) {
      pauseSimulation();
    }
    loadFile(simulationType, fileName);
    init(stage, simulationController);
  }

  private void loadFile(String simulationType, String fileName) throws FileNotFoundException {
    myFileRetriever = new FileRetriever();
    try {
      String myFilePath = myFileRetriever.getSimulationTypeFilePath(simulationType);
      completeConfigFilePath = myFilePath + "/" + fileName;
    }
    catch (FileNotFoundException e) {
      displayAlert("Error", myResources.getString("InvalidSimType"));
      throw e;
    }
  }

  /**
   * Initializes the simulation controller by loading configuration, setting up the simulation,
   * and displaying the splash screen.
   * @param stage the primary stage for the application.
   * @param controller the main simulation controller instance.
   */
  public void init(Stage stage, SimulationController controller) throws Exception {
    myController = controller;
    myStage = stage;
    // initialize the simulation configuration
    try {
      XMLParser xmlParser = new XMLParser();
      mySimulationConfig = xmlParser.parseXMLFile(completeConfigFilePath);
      initializeSimulation(mySimulationConfig);
      initializeSplashScreen(stage, controller);
    }
    catch (Exception e) {
      displayAlert(myResources.getString("Error"), myResources.getString("SimNotSupported"));
    }
  }


  /**
   * Initializes the simulation timeline to control the simulation's execution speed.
   * @return a configured Timeline instance.
   */
  private Timeline initializeTimeline() {
    double framesPerSecond = Double.parseDouble(CONFIG.getString("frames.per.second"));
    double secondDelay = 1.0 / framesPerSecond;

    Timeline timeline = new Timeline();
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.getKeyFrames().add(
            new KeyFrame(Duration.seconds(secondDelay),
                    e -> stepSimulation(secondDelay))
    );
    return timeline;
  }

  /**
   * Initializes the simulation based on the provided configuration.
   * @param config the simulation configuration.
   */
  private void initializeSimulation(SimulationConfig config) {
    try {
      myGrid = new Grid(config.getWidth(), config.getHeight(), GameOfLifeState.ALIVE);
      mySimulation = createSimulation(config.getType());
    } catch (Exception e) {
      handleError(myResources.getString("SimulationInitError"), e);
    }
  }

  /**
   * Creates a new simulation instance based on the provided type.
   *
   * @param type the type of simulation to create
   * @return the created Simulation instance
   * @throws IllegalArgumentException if the simulation type is invalid
   */
  private Simulation createSimulation(String type) {
    return SimulationType.fromString(type)
            .map(simType -> {
              Grid grid = myGrid;
              if (grid == null) {
                throw new IllegalStateException(myResources.getString("GridNull"));
              }
              SimulationConfig config = mySimulationConfig;
              if (config == null) {
                displayAlert(myResources.getString("Error"), myResources.getString("ConfigNull"));
                throw new IllegalStateException(myResources.getString("ViewSimOrGridNull"));
              }
              return switch (simType) {
                case GAME_OF_LIFE -> new GameOfLife(config, grid);
                case SPREADING_FIRE -> new Fire(config, grid, parameters.fireProb(), parameters.treeProb());
                case PERCOLATION -> new Percolation(config, grid, parameters.percolationProb());
                case SCHELLING -> new Schelling(config, grid, parameters.satisfaction());
              };
            })
            .orElseThrow(() -> new IllegalArgumentException("Invalid simulation type: " + type));
  }

  /**
   * Initializes and displays the splash screen.
   *
   * @param stage the primary stage of the application
   * @param controller the simulation controller instance
   */
  private void initializeSplashScreen(Stage stage, SimulationController controller) {
    try {
      SplashScreen initialScreen = new SplashScreen();
      Stage splashStage = initialScreen.showSplashScreen(
              new Stage(),
              CONFIG.getString("splash.title"),
              Integer.parseInt(CONFIG.getString("window.width")),
              Integer.parseInt(CONFIG.getString("window.height"))
      );

      ComboBox<String> languageSelector = initialScreen.makeComboBox(
              CONFIG.getString("language.selector"),
              Arrays.asList(CONFIG.getString("available.languages").split(","))
      );
      ComboBox<String> themeSelector = initialScreen.makeComboBox(
              CONFIG.getString("theme.selector"),
              Arrays.asList(CONFIG.getString("available.themes").split(","))
      );
      List<String> simulationTypes = myFileRetriever.getSimulationTypes();
      UserController myUserController = new UserController(myResources, controller);
      myUserController.makeSimSelectorComboBoxes(CONFIG.getString("select.sim"), CONFIG.getString("select.config"), simulationTypes, stage);
      setupSplashScreenCallbacks(initialScreen, languageSelector, themeSelector, splashStage);
    } catch (Exception e) {
      handleError("SplashScreenError", e);
    }
  }

  /**
   * Sets up event handlers for the splash screen controls.
   *
   * @param screen the splash screen instance
   * @param languageSelector the language selection combo box
   * @param themeSelector the theme selection combo box
   * @param splashStage the splash screen stage
   */
  private void setupSplashScreenCallbacks(
          SplashScreen screen,
          ComboBox<String> languageSelector,
          ComboBox<String> themeSelector,
          Stage splashStage) {
    Button enterButton = screen.makeEnterButton();
    enterButton.setOnAction(e -> {
      try {
        String selectedLanguage = Optional.ofNullable(languageSelector.getValue())
                .orElseThrow(() -> new IllegalStateException("Language not selected"));
        String selectedTheme = Optional.ofNullable(themeSelector.getValue())
                .orElseThrow(() -> new IllegalStateException("Theme not selected"));
        myResources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + selectedLanguage);
        splashStage.close();
        setupSimulation(myStage, selectedLanguage, selectedTheme);
      } catch (Exception ex) {
        handleError("SetupError", ex);
      }
    });
  }

  /**
   * Sets up the simulation with the given stage, language, and theme.
   *
   * @param stage the primary application stage
   * @param language the selected language
   * @param themeColor the selected theme color
   */
  private void setupSimulation(Stage stage, String language, String themeColor) {
    try {
      SimulationConfig config = mySimulationConfig;
      Simulation simulation = createSimulation(config.getType());
      Grid grid = myGrid;

      mySimView = new SimulationView(config, this, myResources);
      mySimView.initView(stage, simulation, mySimView, simulation.getColorMap(), grid, language, themeColor);
    } catch (Exception e) {
      handleError("SetupError", e);
    }
  }

  /**
   * Advances the simulation by a single step.
   *
   * @param elapsedTime the time elapsed since the last step
   */
  public void stepSimulation(double elapsedTime) {
    try {
      mySimulation.step();
      updateView();
    } catch (Exception e) {
      handleError("StepError", e);
      pauseSimulation();
    }
  }

  /**
   * Updates the simulation view.
   */
  private void updateView() {
    try {
      mySimView.updateGrid(mySimulation.getColorMap());
    } catch (Exception e) {
      handleError("ViewUpdateError", e);
    }
  }

  /**
   * Handles errors by displaying an alert message.
   *
   * @param key the error message key
   * @param e the exception that occurred
   */
  private void handleError(String key, Exception e) {
    Platform.runLater(() -> {
      String title = getResourceString("Error");
      String message = String.format("%s: %s",
              getResourceString(key),
              e.getMessage());
      displayAlert(title, message);
    });
  }

  /**
   * Retrieves a localized resource string.
   *
   * @param key the key for the resource string
   * @return the corresponding resource string
   */
  private String getResourceString(String key) {
    return Optional.ofNullable(myResources)
            .map(r -> r.getString(key))
            .orElse(key);
  }

  /**
   * Starts the simulation.
   */
  public void startSimulation() {
    try {
      myTimeline.play();
    } catch (Exception e) {
      handleError("StartError", e);
    }
  }

  /**
   * Pauses the simulation by stopping the timeline.
   * If an error occurs, it is handled appropriately.
   */
  public void pauseSimulation() {
    try {
      myTimeline.pause();
    } catch (Exception e) {
      handleError("PauseError", e);
    }
  }

  /**
   * Resets the grid to its initial state.
   * If the simulation and configuration exist, their grid states are reinitialized.
   * The view is updated after resetting.
   */
  public void resetGrid() {
    try {
      mySimulation.reinitializeGridStates(mySimulationConfig);
      updateView();
    } catch (Exception e) {
      handleError("ResetError", e);
    }
  }

  /**
   * Sets the simulation speed within defined bounds.
   * @param speed the desired simulation speed
   */
  public void setSimulationSpeed(double speed) {
    try {
      double minSpeed = Double.parseDouble(CONFIG.getString("min.speed"));
      double maxSpeed = Double.parseDouble(CONFIG.getString("max.speed"));
      double clampedSpeed = Math.max(minSpeed, Math.min(maxSpeed, speed));
      myTimeline.setRate(clampedSpeed);
    } catch (Exception e) {
      handleError("SpeedError", e);
    }
  }

  /**
   * Displays an alert dialog with a given title and content.
   * @param title the title of the alert
   * @param content the content/message of the alert
   */
  public void displayAlert(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setContentText(content);
    alert.showAndWait();
  }

  /**
   * Saves the current simulation configuration and grid state to an XML file.
   * If an error occurs during saving, it is handled appropriately.
   */
  public void saveSimulation() {
    try {
      SimulationConfig config = mySimulationConfig;
          //.orElseThrow(() -> new IllegalStateException("No simulation configuration available"));
      SaveSimulationDescription dialog = new SaveSimulationDescription(myStage, myResources, config);

      dialog.showAndWait().ifPresent(metadata -> {
        try {
          updateConfigurationWithMetadata(config, metadata);
          saveConfigurationToFile(config, metadata);
          displaySuccessMessage(metadata.saveLocation().getName());
        } catch (IOException e) {
          handleError("SaveError", e);
        }
      });
    } catch (Exception e) {
      handleError("SaveError", e);
    }
  }

  /**
   * Updates the simulation configuration with user-provided metadata.
   * @param config the current simulation configuration
   * @param metadata the metadata provided by the user
   */
  private void updateConfigurationWithMetadata(SimulationConfig config, SaveSimulationDescription.SimulationMetadata metadata) {
    config.setTitle(metadata.title());
    config.setAuthor(metadata.author());
    config.setDescription(metadata.description());
  }

  /**
   * Saves the simulation configuration and grid state to a file.
   * @param config the simulation configuration
   * @param metadata metadata containing file save location
   * @throws IOException if an error occurs during file writing
   */
  private void saveConfigurationToFile(
          SimulationConfig config,
          SaveSimulationDescription.SimulationMetadata metadata) throws IOException {
    XMLWriter xmlWriter = new XMLWriter();
    xmlWriter.saveToXML(config, myGrid, metadata.saveLocation().getAbsolutePath());
  }

  /**
   * Displays a success message upon successful saving of a simulation.
   * @param fileName the name of the saved file
   */
  private void displaySuccessMessage(String fileName) {
    String title = getResourceString("Success");
    String message = String.format("%s %s",
            fileName,
            getResourceString("Saved"));

    Platform.runLater(() -> {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle(title);
      alert.setContentText(message);
      alert.showAndWait();
    });
  }

  /**
   * Opens a file chooser for the user to select a simulation file.
   * If a valid file is selected, a new simulation is loaded.
   */
  /*
  public void selectSimulation(String simulationType, String fileName, Stage stage, SimulationController simulationController) {
    try {
      FileChooser fileChooser = createSimulationFileChooser();
      File selectedFile = fileChooser.showOpenDialog(myStage);

      if (selectedFile != null) {
        loadNewSimulation(selectedFile);
      }
    } catch (Exception e) {
      handleError("SelectSimulationError", e);
    }
  }
   */

  /**
   * Creates and configures a file chooser for selecting simulation files.
   * @return a configured FileChooser instance
   */
  private FileChooser createSimulationFileChooser() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(getResourceString("SelectSimulation"));
    fileChooser.setInitialDirectory(new File(CONFIG.getString("default.simulation.directory")));
    fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("XML Files", "*.xml")
    );
    return fileChooser;
  }

  /**
   * Loads a new simulation from a selected XML configuration file.
   * @param configFile the selected simulation configuration file
   */
  private void loadNewSimulation(String configFile) {
    try {
      pauseSimulation();

      XMLParser xmlParser = new XMLParser();
      SimulationConfig newConfig = xmlParser.parseXMLFile(configFile);

      if (newConfig != null) {
        mySimulationConfig = newConfig;
        myGrid = new Grid(newConfig.getWidth(), newConfig.getHeight(), GameOfLifeState.ALIVE);
        mySimulation = createSimulation(newConfig.getType());
        mySimView = new SimulationView(newConfig, this, myResources);
        mySimView.initView(myStage, mySimulation, mySimView, mySimulation.getColorMap(), myGrid, myResources.getLocale().getLanguage(), "Light");
      } else {
        throw new IllegalStateException("Failed to parse new simulation configuration");
      }
    } catch (Exception e) {
      handleError("LoadSimulationError", e);
    }
  }

  /**
   * Sets the simulation controller for managing interactions.
   * @param controller the simulation controller instance
   */
  public void setController(SimulationController controller) {
    this.myController = controller;
  }
}