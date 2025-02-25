package cellsociety.view;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.controller.FileRetriever;
import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Grid;
import cellsociety.model.state.GameOfLifeState;
import cellsociety.view.gridview.GridView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

class ControlPanelTest extends DukeApplicationTest {
  private static final int SCREEN_WIDTH = 500;
  private static final int SCREEN_HEIGHT = 500;
  Stage myStage;
  Scene myScene;
  BorderPane myRoot;
  SimulationController myController;
  ResourceBundle myResources;
  SimulationView mySimView;
  GridView myGridView;
  Grid myGrid;
  UserController myUserController;
  FileRetriever myFileRetriever;


  @Override
  public void start(Stage stage) {
    myStage = new Stage();
    myRoot = new BorderPane();
    myScene = new Scene(myRoot, SCREEN_WIDTH, SCREEN_HEIGHT);
    myController = new SimulationController();
    myResources = ResourceBundle.getBundle("cellsociety.controller.English");
    myGrid = new Grid(5, 5, GameOfLifeState.ALIVE);
    Map<String, Double> myParameters = new HashMap<>();
    int[] myInitialStates = new int[]{0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1};
    SimulationConfig mySimulationConfig = new SimulationConfig("Game of Life", "title", "Alana Zinkin", "Description",
        5, 5, myInitialStates, myParameters);
    mySimView = new SimulationView(mySimulationConfig, myController, myResources);
  }
  
  @Test
  void setupControlBar_ControlBarExists_ButtonsAreShown() {
    ControlPanel testControlPanel = new ControlPanel(myStage, myScene, myController, mySimView, myResources, myGridView);
    testControlPanel.setupControlBar(myRoot);
    HBox controlBar = (HBox) myRoot.getTop();
    List<String> expectedButtons = List.of("Start", "Pause", "Step", "Step Back", "Reset", "Save", "Add Simulation");
    List<String> expectedComboBoxes = List.of("Select Simulation Type", "Select Config File");
    assertEquals(expectedButtons.size() + expectedComboBoxes.size(), controlBar.getChildren().size(), "Control bar should have the correct number of buttons");
    for (int i = 0; i < expectedButtons.size(); i++) {
      assertTrue(controlBar.getChildren().get(i) instanceof Button || controlBar.getChildren().get(i) instanceof ComboBox<?>, "Each control should be a button");
      Button button = (Button) controlBar.getChildren().get(i);
      assertEquals(expectedButtons.get(i), button.getText(), "Button should have the correct label");
    }
    for (int i = 0; i < expectedComboBoxes.size(); i++) {
      assertTrue(controlBar.getChildren().get(i + expectedButtons.size()) instanceof ComboBox<?>, "Each control should be a button");
      ComboBox<String> comboBox = (ComboBox<String>) controlBar.getChildren().get(i + expectedButtons.size());
      assertEquals(expectedComboBoxes.get(i), comboBox.getPromptText(), "ComboBox should have the correct label");
    }
  }



  @Test
  void setUpLowerBar() {
  }

  @Test
  void makeLowerBar() {
  }
}