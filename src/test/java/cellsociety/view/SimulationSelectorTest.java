package cellsociety.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.controller.SimulationUI;
import java.util.List;
import org.testfx.util.WaitForAsyncUtils;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Grid;
import cellsociety.model.state.GameOfLifeState;
import cellsociety.view.gridview.GridView;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import util.DukeApplicationTest;

class SimulationSelectorTest extends DukeApplicationTest {
  Stage myStage;
  Scene myScene;
  BorderPane myRoot;
  SimulationController myController;
  ResourceBundle myResources;
  SimulationView mySimView;
  GridView myGridView;
  Grid myGrid;

  @Override
  public void start(Stage primaryStage) {
    myController = new SimulationController();
    myResources = ResourceBundle.getBundle("cellsociety.controller.English");
    myGrid = new Grid(5, 5, GameOfLifeState.ALIVE);
    Map<String, Double> myParameters = new HashMap<>();
    int[] myInitialStates = new int[]{0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1};
    SimulationConfig mySimulationConfig = new SimulationConfig("Game of Life", "title", "Alana Zinkin", "Description",
        5, 5, myInitialStates, myParameters,"Default");
    mySimView = new SimulationView(mySimulationConfig, myController, myResources);
    myStage = new Stage();
    mySimView.createSimulationWindow(myStage);
  }

  @Test
  void makeSimSelectorComboBoxes_Basic_ComboBoxesContainCorrectInfo() throws Exception {
    List<String> SIMULATION_TYPES = List.of(
        "Bacteria",
        "Foraging Ants",
        "Game of Life",
        "Rules-Based Game of Life",
        "Langton Loop",
        "Percolation",
        "Schelling Segregation",
        "Spreading of Fire",
        "Sugar Scape",
        "Tempesti Loop",
        "Wa-Tor World"
    );
    String fakeSim = "FakeSim";
    SimulationSelector simulationSelector = new SimulationSelector(myResources, myController);

    List<ComboBox<String>> comboBoxes = simulationSelector.makeSimSelectorComboBoxes(
        "Select Simulation", "Select Configuration", SIMULATION_TYPES);

    ComboBox<String> simTypeComboBox = comboBoxes.get(0);
    ComboBox<String> configFileComboBox = comboBoxes.get(1);

    assertTrue(simTypeComboBox.getItems().containsAll(SIMULATION_TYPES), "simulationTypesComboBox does not contain expected items!");
    assertFalse(simTypeComboBox.getItems().contains(fakeSim), "simulationTypesComboBox contains a fake simulation!");
    assertTrue(configFileComboBox.getItems().isEmpty(), "configFileComboBox should start empty!");
  }

}