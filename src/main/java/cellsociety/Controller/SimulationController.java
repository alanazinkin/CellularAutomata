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

  public SimulationController() {}

  /**
   * wrapper method is the starting point of the simulation
   */
  public void init(Stage primaryStage) throws Exception {
    XMLParser xmlParser = new XMLParser();
    mySimulationConfig = xmlParser.parseXMLFile(FILE_PATH);
    mySimulationConfig.initializeStage(primaryStage);
    Simulation simulation = new GameOfLife(
        new Grid(mySimulationConfig.getWidth(),
            mySimulationConfig.getHeight(),
            GameOfLifeState.ALIVE)
    );
    SimulationView mySimView = new SimulationView();
    mySimView.initView(primaryStage, mySimulationConfig, simulation, mySimView, simulation.getStateMap());
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
