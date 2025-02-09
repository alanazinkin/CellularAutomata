package cellsociety.Controller;

import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.Simulations.*;
import cellsociety.Model.State.GameOfLifeState;
import cellsociety.View.SimulationView;
import cellsociety.View.SplashScreen;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Timer;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SimulationController {
  private static final String FILE_PATH = "data/SpreadingFire/BasicCenterFireSpread.xml";
  // use Java's dot notation, like with import, for properties files
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
   * @param primaryStage The primary stage for the JavaFX application.
   * @throws Exception If there is an error during initialization.
   */
  public void init(Stage primaryStage, SimulationController controller) throws Exception {
    myController = controller;
    myStage = primaryStage;
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
    ComboBox<String> languageSelector = initialScreen.makeLanguageComboBox();
    // Wait for language selection
    selectLanguageToStartSimulation(primaryStage, initialScreen, languageSelector, splashStage);
  }

  private void initializeTimeline() {
    myTimeline = new Timeline();
    myTimeline.setCycleCount(Timeline.INDEFINITE);
    myTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(SECOND_DELAY), e -> stepSimulation(SECOND_DELAY)));
  }

  private void selectLanguageToStartSimulation(Stage primaryStage, SplashScreen initialScreen, ComboBox<String> languageSelector, Stage splashStage) {
    Button enterButton = initialScreen.makeEnterButton();
    enterButton.setOnAction(e -> {
      String selectedLanguage = languageSelector.getValue();
      if (selectedLanguage != null) {
        myResources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + selectedLanguage);
        splashStage.close();
        myScene = mySimulationConfig.initializeStage(primaryStage);
        try {
          setupSimulation(primaryStage, selectedLanguage);
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }
      }
    });
  }

  /**
   * method that allows access to resource bundle for a specific language
   * @return Resource Bundle for a user-selected language
   */
  public ResourceBundle getResources() {return myResources;}

  private void setupSimulation(Stage primaryStage, String language) throws Exception {
    mySimView = new SimulationView();
    initializeView(primaryStage, language);
  }


  /**
   * Initializes the simulation based on the type specified in the configuration.
   * Uses a switch statement to determine which simulation to create.
   */
  private Simulation initializeSimulationType() {
    String simulationType = mySimulationConfig.getType();
    mySimulation = createSimulation(simulationType);
    if (mySimulation == null) {
      displayAlert(myResources.getString("InvalidSimType") + " " + simulationType);
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
      case "Schelling":
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
  private void initializeView(Stage primaryStage, String language) {
    mySimView.initView(
        primaryStage,
        mySimulationConfig,
        mySimulation,
        mySimView,
        mySimulation.getColorMap(),
        myGrid,
        language,
        myController
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
   * Resets the simulation to its initial state.
   */
  public void resetSimulation() throws Exception {
    myTimeline.stop();
    myStage.close();
    init(new Stage(), myController);
  }

  public void saveSimulation() {
    //TODO save a simulation config file based on the current state liek a snapshot
    System.out.println("Saving Simulation");
  }

  /**
   * Updates the simulation speed.
   *
   * @param speed The new speed multiplier (1.0 is normal speed)
   */
  public void setSimulationSpeed(double speed) {
    FRAMES_PER_SECOND = Math.max(1, Math.min(5, speed));
  }

  public void selectSimulation() {
    System.out.println("Selecting Simulation");
  }

  /**
   * Updates the view to reflect the current simulation state.
   */
  private void updateView() {
    if (mySimView == null || mySimulation == null || myGrid == null) {
      displayAlert(myResources.getString("Error") + ":" + " " + myResources.getString("ViewSimOrGridNull"));
      return;
    }
  }

  private void displayAlert(String content) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(myResources.getString("Error"));
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
