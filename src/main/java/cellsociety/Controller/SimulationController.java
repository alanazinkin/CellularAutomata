package cellsociety.Controller;

import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.Simulations.GameOfLife;
import cellsociety.Model.State.GameOfLifeState;
import cellsociety.View.SimulationView;
import java.util.concurrent.ExecutionException;
import javafx.stage.Stage;

public class SimulationController {
  private static final String FILE_PATH = "data/GameOfLife/GOL1.xml";
  private SimulationConfig mySimulationConfig;
  private Simulation mySimulation;
  private SimulationView mySimView;

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
    mySimulation = new GameOfLife(
        new Grid(mySimulationConfig.getWidth(),
            mySimulationConfig.getHeight(),
            GameOfLifeState.ALIVE)
    );
    mySimView = new SimulationView();
    mySimView.initView(primaryStage, mySimulationConfig, mySimulation, mySimView, mySimulation.getStateMap());
  }

  public void startSimulation() {System.out.println("Starting Simulation");}

  public void pauseSimulation() {
    System.out.println("Pausing Simulation");
  }

  public void resetSimulation() {
    System.out.println("Resetting Simulation");
  }

  public void saveSimulation() {
    System.out.println("Saving Simulation");
  }

  public void selectSimulation() {
    System.out.println("Selecting Simulation");
  }
}
