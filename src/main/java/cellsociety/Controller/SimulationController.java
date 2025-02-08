package cellsociety.Controller;

import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.Simulations.*;
import cellsociety.Model.State.GameOfLifeState;
import cellsociety.View.GridViews.GridView;
import cellsociety.View.SimulationView;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.stage.Stage;

public class SimulationController {
  private static final String FILE_PATH = "data/SpreadingFire/BasicCenterFireSpread.xml";
  private static final long FRAME_INTERVAL = 200_000_000L;

  private SimulationConfig mySimulationConfig;
  private Simulation mySimulation;
  private SimulationView mySimView;
  private Grid myGrid;
  private GridView myGridView;
  private boolean isPaused = false;
  private AnimationTimer simulationTimer;
  private long lastUpdate = 0;
  private double simulationSpeed = 1.0;

  private static final double DEFAULT_FIRE_PROB = 0.5;
  private static final double DEFAULT_TREE_PROB = 0.4;
  private static final double DEFAULT_SATISFACTION = 0.3;
  private static final double DEFAULT_PERCOLATION_PROB = 0.5;

  public SimulationController() {}

  /**
   * Initializes the simulation by parsing the configuration file, setting up the model,
   * and initializing the view.
   *
   * @param primaryStage The primary stage for the JavaFX application.
   * @throws Exception If there is an error during initialization.
   */
  public void init(Stage primaryStage) throws Exception {
    XMLParser xmlParser = new XMLParser();
    mySimulationConfig = xmlParser.parseXMLFile(FILE_PATH);
    mySimulationConfig.initializeStage(primaryStage);
    myGrid = new Grid(mySimulationConfig.getWidth(), mySimulationConfig.getHeight(), GameOfLifeState.ALIVE);
    initializeSimulationType();
    mySimView = new SimulationView();
    initializeView(primaryStage);
    setupSimulationTimer();
  }

  /**
   * Initializes the simulation based on the type specified in the configuration.
   * Uses a switch statement to determine which simulation to create.
   */
  private void initializeSimulationType() {
    String simulationType = mySimulationConfig.getType();
    mySimulation = createSimulation(simulationType);

    if (mySimulation == null) {
      throw new IllegalArgumentException("Invalid simulation type: " + simulationType);
    }
  }

  /**
   * Creates and returns a specific simulation instance based on the given type.
   *
   * @param simulationType The type of simulation to create
   * @return A new instance of the specified simulation type
   */
  private Simulation createSimulation(String simulationType) {
    switch (simulationType.toLowerCase()) {
      case "game of life":
        return new GameOfLife(mySimulationConfig, myGrid);
      case "spreading of fire":
        double fireProb = mySimulationConfig.getParameters().getOrDefault("fireProb", DEFAULT_FIRE_PROB);
        double treeProb = mySimulationConfig.getParameters().getOrDefault("treeProb", DEFAULT_TREE_PROB);
        return new Fire(mySimulationConfig, myGrid, fireProb, treeProb);
      case "percolation":
        double percolationProb = mySimulationConfig.getParameters().getOrDefault("percolationProb", DEFAULT_PERCOLATION_PROB);
        return new Percolation(mySimulationConfig, myGrid, percolationProb);
      case "schelling":
        double satisfaction = mySimulationConfig.getParameters().getOrDefault("satisfaction", DEFAULT_SATISFACTION);
        return new Schelling(mySimulationConfig, myGrid, satisfaction);
        /*
      case "watoworld":
        return new WaTorWorld(mySimulationConfig, myGrid, mySimulationConfig.getParameters().get(0), mySimulationConfig.getParameters().get(1), mySimulationConfig.getParameters().get(2), mySimulationConfig.getParameters().get(3));
      */
      default:
        return null;
    }
  }

  /**
   * Starts or resumes the simulation.
   */
  public void startSimulation() {
    if (isPaused) {
      isPaused = false;
    }
    simulationTimer.start();
    System.out.println("Starting Simulation");
  }

  /**
   * Pauses the simulation.
   */
  public void pauseSimulation() {
    if (!isPaused) {
      isPaused = true;
    }
    System.out.println("Pausing Simulation");
  }

  /**
   * Resets the simulation to its initial state.
   */
  public void resetSimulation() {
    pauseSimulation();
    myGrid = new Grid(mySimulationConfig.getWidth(), mySimulationConfig.getHeight(), GameOfLifeState.ALIVE);
    mySimulation = new GameOfLife(mySimulationConfig, myGrid);
    updateView();
    System.out.println("Resetting Simulation");
  }

  public void saveSimulation() {
    System.out.println("Saving Simulation");
  }

  /**
   * Updates the simulation speed.
   *
   * @param speed The new speed multiplier (1.0 is normal speed)
   */
  public void setSimulationSpeed(double speed) {
    this.simulationSpeed = Math.max(0.1, Math.min(5.0, speed));
  }

  public void selectSimulation() {
    System.out.println("Selecting Simulation");
  }

  /**
   * Updates the view to reflect the current simulation state.
   */
  private void updateView() {
    Stage currentStage = (Stage) mySimView.getRoot().getScene().getWindow();
    mySimView.getRoot().getChildren().clear();
    mySimView.initView(currentStage, mySimulationConfig, mySimulation, mySimView, mySimulation.getColorMap(), myGrid, "English");
  }

  /**
   * Cleans up resources when the simulation is closed.
   */
  public void cleanup() {
    if (simulationTimer != null) {
      simulationTimer.stop();
    }
  }

  /**
   * Sets up the animation timer for running the simulation loop.
   * Uses JavaFX AnimationTimer for smooth frame updates.
   */
  private void setupSimulationTimer() {
    simulationTimer = new AnimationTimer() {
      @Override
      public void handle(long now) {
        if (now - lastUpdate >= (long)(FRAME_INTERVAL / simulationSpeed)) {
          if (!isPaused) {
            step();
          }
          lastUpdate = now;
        }
      }
    };
  }

  /**
   * Performs a single step of the simulation and updates the view.
   */
  private void step() {
    mySimulation.step();
    Platform.runLater(this::updateView);
  }

  /**
   * Initializes the simulation view with current configuration and model state.
   *
   * @param primaryStage The primary stage for the JavaFX application.
   */
  private void initializeView(Stage primaryStage) {
    mySimView.initView(
            primaryStage,
            mySimulationConfig,
            mySimulation,
            mySimView,
            mySimulation.getColorMap(),
            myGrid
    );
  }

}
