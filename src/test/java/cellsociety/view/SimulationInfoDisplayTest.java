package cellsociety.view;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.StateInterface;
import cellsociety.model.simulations.GameOfLife;
import cellsociety.model.state.GameOfLifeState;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

class SimulationInfoDisplayTest extends DukeApplicationTest {
  private SimulationController myController;
  private ResourceBundle myResources;
  private Grid myGrid;
  private Simulation mySimulation;
  private SimulationConfig mySimulationConfig;
  private SimulationView mySimulationView;

  @Override
  public void start(Stage stage) {
    myController = new SimulationController();
    myResources = ResourceBundle.getBundle("cellsociety.controller.English");
    myGrid = new Grid(5, 5, GameOfLifeState.ALIVE);
    Map<String, Double> myParameters = new HashMap<>();
    int[] myInitialStates = new int[]{0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1};
    mySimulationConfig = new SimulationConfig("Game of Life", "title", "Alana Zinkin", "Description",
        5, 5, myInitialStates, myParameters,"Default");
    mySimulation = new GameOfLife(mySimulationConfig, myGrid);
    mySimulationView = new SimulationView(mySimulationConfig, myController, myResources);
    mySimulationView.createSimulationWindow(stage);
  }

  @Test
  void createDisplayBox_BasicTest_AllElementsAdded() {
    SimulationInfoDisplay mySimInfoDisplay = new SimulationInfoDisplay(mySimulationConfig.getType(),
        mySimulationConfig.getTitle(),
        mySimulationConfig.getAuthor(), mySimulationConfig.getDescription(), mySimulationConfig.getParameters(),
        mySimulation.getColorMap(),
        myResources
    );
    runAsJFXAction(() -> {
      Pane vbox;
      try {
        vbox = mySimInfoDisplay.createDisplayBox("Dark", mySimulationView);
        mySimulationView.getRoot().getChildren().add(vbox);
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
      Text EXPECTED_TYPE = lookup("#TypeGameofLife").query();
    Text EXPECTED_TITLE = lookup("#Titletitle").query();
    Text EXPECTED_AUTHOR = lookup("#AuthorAlanaZinkin").query();
    Text EXPECTED_DESCRIPTION = lookup("#DescriptionDescription").query();
    Text EXPECTED_PARAMETERS = lookup("#Parameters").query();
    Text EXPECTED_STATES = lookup("#StateColors").query();
    Text EXPECTED_NONE = lookup("#None").query();
    Text EXPECTED_DEAD = lookup("#gameoflife-state-dead").query();
    Text EXPECTED_ALIVE = lookup("#gameoflife-state-alive").query();
    List<Text> EXPECTED_TEXTS = List.of(EXPECTED_TYPE, EXPECTED_TITLE, EXPECTED_AUTHOR, EXPECTED_DESCRIPTION,
        EXPECTED_PARAMETERS, EXPECTED_STATES, EXPECTED_NONE, EXPECTED_DEAD, EXPECTED_ALIVE);
    for (Text text : EXPECTED_TEXTS) {
      assert(vbox.getChildren().contains(text));
    }
    });

  }
}