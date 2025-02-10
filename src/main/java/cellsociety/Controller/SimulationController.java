package cellsociety.Controller;

import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.Simulations.*;
import cellsociety.Model.State.GameOfLifeState;
import cellsociety.View.GridViews.GridView;
import cellsociety.View.SimulationView;
import cellsociety.View.SplashScreen;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SimulationController {
  private static final String FILE_PATH = "data/SpreadingFire/BasicCenterFireSpread.xml";
  public static final String DEFAULT_RESOURCE_PACKAGE = "cellsociety.View.";

  private SimulationController myController;
  private SimulationConfig mySimulationConfig;
  private Simulation mySimulation;
  private SimulationView mySimView;
  private Grid myGrid;
  private GridView myGridView;
  private Scene myScene;
  private boolean isPaused = false;
  private Timeline myTimeline;
  private boolean isRunning = false;
  private double simulationSpeed = 1.0;
  private Stage myStage;
  private ResourceBundle myResources;

  private static final double DEFAULT_FIRE_PROB = 0.5;
  private static final double DEFAULT_TREE_PROB = 0.4;
  private static final double DEFAULT_SATISFACTION = 0.3;
  private static final double DEFAULT_PERCOLATION_PROB = 0.5;
  public double FRAMES_PER_SECOND = 5;
  public double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;

  public SimulationController(){}

  /**
   * Initializes the simulation by parsing the configuration file, setting up the model,
   * and initializing the view.
   *
   * @param stage The primary stage for the JavaFX application.
   * @throws Exception If there is an error during initialization.
   */
  public void init(Stage stage, SimulationController controller) throws Exception {
    myController = controller;
    myStage = stage;
    // initialize the simulation configuration
    XMLParser xmlParser = new XMLParser();
    mySimulationConfig = xmlParser.parseXMLFile(FILE_PATH);// make initial splash screen window
    if (mySimulationConfig == null) {
      throw new IllegalStateException("Failed to parse simulation configuration.");
    }
    myGrid = new Grid(mySimulationConfig.getWidth(), mySimulationConfig.getHeight(), GameOfLifeState.ALIVE);
    mySimulation = initializeSimulationType();
    initializeTimeline();
    SplashScreen initialScreen = new SplashScreen();
    Stage splashStage = initialScreen.showSplashScreen(new Stage(), mySimulationConfig, "Cell Society", 1000, 800);
    ComboBox<String> languageSelector = initialScreen.makeComboBox("Select Language", List.of("English", "Spanish", "Italian"));
    ComboBox<String> themeColorSelector = initialScreen.makeComboBox("Select Theme Color", List.of("Dark", "Light"));
    // Wait for language selection
    selectSettingsToStartSimulation(initialScreen, languageSelector, themeColorSelector, splashStage);
  }

  private void initializeTimeline() {
    myTimeline = new Timeline();
    myTimeline.setCycleCount(Timeline.INDEFINITE);
    myTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(SECOND_DELAY), e -> stepSimulation(SECOND_DELAY)));
  }

  private void selectSettingsToStartSimulation(SplashScreen initialScreen, ComboBox<String> languageSelector, ComboBox<String> themeColorSelector, Stage splashStage) {
    Button enterButton = initialScreen.makeEnterButton();
    enterButton.setOnAction(e -> {
      String selectedLanguage = languageSelector.getValue();
      String selectedThemeColor = themeColorSelector.getValue();
      if (selectedLanguage != null && selectedThemeColor != null) {
        myResources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + selectedLanguage);
        // close splash screen
        splashStage.close();
        //myScene = mySimulationConfig.initializeStage(myStage);
        String themeFile = getThemeFolderOrFile(mySimulationConfig.getType());
        List<String> cssFiles = List.of(selectedThemeColor, getSimulationFile(themeFile, selectedThemeColor));
        try {
          setupSimulation(myStage, selectedLanguage, cssFiles);
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }
      }
    });
  }

  private String getThemeFolderOrFile(String simulationType) {
    switch (simulationType) {
      case "Game of Life": return "GameOfLife";
      case "Spreading of Fire": return "Fire";
      case "Percolation": return "Percolation";
      case "Schelling Segregation": return "Schelling";
      case "Wa-Tor World": return "WaTorWorld";
      default: displayAlert(myResources.getString("Error"), myResources.getString("InvalidSimulationType"));
        return "";
    }
  }

  private String getSimulationFile(String themeFile, String selectedThemeColor) {
    return themeFile + "/" + themeFile + selectedThemeColor;
  }

  private void setupSimulation(Stage stage, String language, List<String> cssFiles) throws Exception {
    mySimView = new SimulationView(mySimulationConfig,myController, myResources);
    initializeView(stage, language, cssFiles);
  }

  /**
   * method that allows access to resource bundle for a specific language
   * @return Resource Bundle for a user-selected language
   */
  public ResourceBundle getResources() {return myResources;}


  /**
   * Initializes the simulation based on the type specified in the configuration.
   * Uses a switch statement to determine which simulation to create.
   */
  private Simulation initializeSimulationType() {
    String simulationType = mySimulationConfig.getType();
    mySimulation = createSimulation(simulationType);
    if (mySimulation == null) {
      displayAlert(myResources.getString("Error"), myResources.getString("InvalidSimType") + " " + simulationType);
    }
    return mySimulation;
  }

  /**
   * Creates and returns a specific simulation instance based on the given type.
   *
   * @param simulationType The type of simulation to create
   * @return A new instance of the specified simulation type
   */
  private Simulation createSimulation(String simulationType) {
    switch (simulationType) {
      case "Game of Life":
        return new GameOfLife(mySimulationConfig, myGrid);
      case "Spreading of Fire":
        double fireProb = mySimulationConfig.getParameters().getOrDefault("fireProb", DEFAULT_FIRE_PROB);
        double treeProb = mySimulationConfig.getParameters().getOrDefault("treeProb", DEFAULT_TREE_PROB);
        return new Fire(mySimulationConfig, myGrid, fireProb, treeProb);
      case "Percolation":
        double percolationProb = mySimulationConfig.getParameters().getOrDefault("percolationProb", DEFAULT_PERCOLATION_PROB);
        return new Percolation(mySimulationConfig, myGrid, percolationProb);
      case "Schelling Segregation":
        double satisfaction = mySimulationConfig.getParameters().getOrDefault("satisfaction", DEFAULT_SATISFACTION);
        return new Schelling(mySimulationConfig, myGrid, satisfaction);
        default:
          return null;
    }
  }

  /**
   * Initializes the simulation view with current configuration and model state.
   *
   * @param primaryStage The primary stage for the JavaFX application.
   */
  private void initializeView(Stage primaryStage, String language, List<String> cssFiles) {
    mySimView.initView(
        primaryStage,
        mySimulation,
        mySimView,
        mySimulation.getColorMap(),
        myGrid,
        language,
        cssFiles
    );
  }


  /**
   * Starts or resumes the simulation.
   */
  public void startSimulation() {
    myTimeline.play();
  }

  /**
   * Pauses the simulation.
   */
  public void pauseSimulation() {
    myTimeline.pause();
  }

  /**
   * myTimeline executes this method for each frame, which updates the grid and the view
   * @param elapsedTime (in seconds) of time elapsed in the simulation
   */
  public void stepSimulation(double elapsedTime) {
    mySimulation.step();
    updateView();
  }

  /**
   * re-initializes the Grid States to their original
   * configuration based on the Simulation Configuration
   */
  public void resetGrid() {
    mySimulation.reinitializeGridStates(mySimulationConfig);
    updateView();
  }

  public void saveSimulation() {
    try {
      SaveSimulationDescription dialog = new SaveSimulationDescription(myStage, myResources, mySimulationConfig);
      Optional<SaveSimulationDescription.SimulationMetadata> result = dialog.showAndWait();
      if (result.isPresent()) {
        SaveSimulationDescription.SimulationMetadata metadata = result.get();
        // Update the configuration with new metadata
        mySimulationConfig.setTitle(metadata.title());
        mySimulationConfig.setAuthor(metadata.author());
        mySimulationConfig.setDescription(metadata.description());
        // Save the file
        XMLWriter xmlWriter = new XMLWriter();
        xmlWriter.saveToXML(mySimulationConfig, myGrid, metadata.saveLocation().getAbsolutePath());
        // Show success message
        displayAlert(myResources.getString("Success"), metadata.saveLocation().getName() + " " + myResources.getString("Saved"));
      }
    } catch (IOException e) {
      displayAlert(myResources.getString("Error"), myResources.getString("Error") + ": " + e.getMessage());
    }
    System.out.println("Saving Simulation");
  }

  /**
   * Updates the simulation speed.
   *
   * @param speed The new speed multiplier (1.0 is normal speed)
   */
  public void setSimulationSpeed(double speed) {
    myTimeline.setRate(Math.max(0.1, Math.min(5, speed)));
  }

  public void selectSimulation() {
    System.out.println("Selecting Simulation");
  }

  /**
   * Updates the view to reflect the current simulation state.
   */
  private void updateView() {
    if (mySimView == null || mySimulation == null || myGrid == null) {
      displayAlert(myResources.getString("Error"), myResources.getString("Error") + ":" + " " + myResources.getString("ViewSimOrGridNull"));
      return;
    }
    mySimView.updateGrid(mySimulation.getColorMap());
  }

  private void displayAlert(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setContentText(content);
    alert.showAndWait();
  }

  /**
   * Cleans up resources when the simulation is closed.
   */
  public void cleanup() {
    System.out.println("Cleaning up");
  }

}
