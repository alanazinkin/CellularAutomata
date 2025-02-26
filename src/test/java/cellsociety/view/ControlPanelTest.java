package cellsociety.view;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

import cellsociety.controller.FileRetriever;
import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Grid;
import cellsociety.model.state.GameOfLifeState;
import cellsociety.view.gridview.DefaultGridView;
import cellsociety.view.gridview.GridView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

class ControlPanelTest extends DukeApplicationTest {
  private static final int SCREEN_WIDTH = 500;
  private static final int SCREEN_HEIGHT = 500;
  private Stage myStage;
  private Scene myScene;
  private BorderPane myRoot;
  private SimulationController myController;
  private ResourceBundle myResources;
  private SimulationView mySimView;
  private GridView myGridView;
  private Grid myGrid;
  private UserController myUserController;
  private FileRetriever myFileRetriever;


  @Override
  public void start(Stage stage) {
    myController = new SimulationController();
    myResources = ResourceBundle.getBundle("cellsociety.controller.English");
    myGrid = new Grid(5, 5, GameOfLifeState.ALIVE);
    Map<String, Double> myParameters = new HashMap<>();
    int[] myInitialStates = new int[]{0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1};
    SimulationConfig mySimulationConfig = new SimulationConfig("Game of Life", "title", "Alana Zinkin", "Description",
        5, 5, myInitialStates, myParameters);
    mySimView = new SimulationView(mySimulationConfig, myController, myResources);
    myStage = new Stage();
    mySimView.createSimulationWindow(myStage);
    myGridView = new DefaultGridView(myController, mySimulationConfig, myGrid);
  }
  
  @Test
  void setupControlBar_ControlBarExists_ControlsAreShown() {
    ControlPanel testControlPanel = new ControlPanel(myStage, myScene, myController, mySimView, myResources, myGridView);
    runAsJFXAction(() -> {
      testControlPanel.setupControlBar(mySimView.getRoot());
      HBox myControlBar = lookup("#myRoot #controlBar").query();

      Button startButton = lookup("#startButton").query();
      Button pauseButton = lookup("#pauseButton").query();
      Button stepForwardButton = lookup("#stepForwardButton").query();
      Button stepBackwardButton = lookup("#stepBackButton").query();
      Button resetButton = lookup("#resetButton").query();
      Button saveButton = lookup("#saveButton").query();
      Button addSimButton = lookup("#addSimButton").query();
      ComboBox<String> simTypeComboBox = lookup("#simulationTypesComboBox").query();
      ComboBox<String> configFileComboBox = lookup("#configFileComboBox").query();
      List<Button> buttons = List.of(startButton, pauseButton, stepForwardButton, stepBackwardButton, resetButton, saveButton, addSimButton);
      assertTrue(myControlBar.getChildren().containsAll(buttons), "Not all buttons are in the control bar.");
      assertTrue(myControlBar.getChildren().contains(simTypeComboBox), "Doesnt contain sim type combo box.");
      assertTrue(myControlBar.getChildren().contains(configFileComboBox), "Doesnt contain config file combo box.");
    });
  }


  @Test
  void setUpLowerBar_BasicTest_AllButtonsAreShown() {
    ControlPanel controlPanel = new ControlPanel(myStage, myScene, myController, mySimView, myResources, myGridView);
    runAsJFXAction(() -> {
      try {
        controlPanel.setUpLowerBar(mySimView.getRoot());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      Slider speedSlider = lookup("#speedSlider").query();
      ComboBox<String> themeSelector = lookup("#themeSelector").query();
      Button gridLinesToggleButton = lookup("#gridLinesToggle").query();
      Button flipGridButton = lookup("#flipGridButton").query();
      List<Control> elements = List.of(speedSlider, themeSelector, gridLinesToggleButton, flipGridButton);
      VBox myLowerBar = lookup("#lowerBar").query();
      HBox myCustomizationBar = lookup("#customizationBar").query();
      HBox myTextBar = lookup("#textBar").query();
      Text speedText = lookup("#speedText").query();
      Text settingsText = lookup("#settingsText").query();
      assertTrue(myTextBar.getChildren().containsAll(List.of(speedText, settingsText)), "Not all text are in the text bar.");
      assertTrue(myLowerBar.getChildren().contains(myCustomizationBar));
      assertTrue(myLowerBar.getChildren().contains(myTextBar));
      assertTrue(myCustomizationBar.getChildren().containsAll(elements));
    });
  }

  @Test
  void makeLowerBar() {
  }
}