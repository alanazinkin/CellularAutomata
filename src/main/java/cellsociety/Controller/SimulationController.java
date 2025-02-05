package cellsociety.Controller;

import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.Simulations.GameOfLife;
import cellsociety.Model.State.GameOfLifeState;
import cellsociety.View.SimulationView;
import javafx.stage.Stage;

public class SimulationController {
  private static final String FILE_PATH = "data/GameOfLife/GOL1.xml";
  private SimulationConfig mySimulationConfig;
  private Simulation mySimulation;
  private SimulationView mySimView;
  private Grid myGrid;
  private boolean isPaused = false;

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
    mySimulation = new GameOfLife(myGrid);
    mySimView = new SimulationView();
    mySimView.initView(primaryStage, mySimulationConfig, mySimulation, mySimView, mySimulation.getStateMap(), myGrid);
  }

  public void startSimulation() {
    if (isPaused) {
      isPaused = false;
    }
    System.out.println("Starting Simulation");
    runSimulationLoop();
  }

  private void runSimulationLoop() {
    new Thread(() -> {
      while (!isPaused) {
        mySimulation.step();
        updateView();
        try {
          Thread.sleep(200); // Adjust the delay to control simulation speed
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  public void pauseSimulation() {
    if (!isPaused) {
      isPaused = true;
    }
    System.out.println("Pausing Simulation");
  }

  public void resetSimulation() {
    mySimulation = new GameOfLife(
            new Grid(mySimulationConfig.getWidth(),
                    mySimulationConfig.getHeight(),
                    GameOfLifeState.ALIVE)
    );
    updateView();
    System.out.println("Resetting Simulation");
  }

  public void saveSimulation() {
    System.out.println("Saving Simulation");
  }

  public void selectSimulation() {
    System.out.println("Selecting Simulation");
  }

  public void updateView() {
    mySimView.getRoot().getChildren().clear(); // Clear the current view
    mySimView.initView(new Stage(), mySimulationConfig, mySimulation, mySimView, mySimulation.getStateMap(), myGrid);
  }

}
