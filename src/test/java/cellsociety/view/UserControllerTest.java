package cellsociety.view;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.simulations.GameOfLife;
import cellsociety.model.state.GameOfLifeState;
import cellsociety.view.gridview.DefaultGridView;
import cellsociety.view.gridview.GridView;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import util.DukeApplicationTest;



class UserControllerTest extends DukeApplicationTest {
  // [UnitOfWork_StateUnderTest_ExpectedBehavior]
  int WINDOW_WIDTH = 1000;
  int WINDOW_HEIGHT = 800;


  private Grid myGrid = new Grid(5, 5, GameOfLifeState.ALIVE);
  private Map<String, Double> myParameters = new HashMap<>();
  private int[] myInitialStates = new int[]{0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1};
  private SimulationConfig mySimulationConfig = new SimulationConfig("Game of Life", "title", "Alana Zinkin", "Description",
  5, 5, myInitialStates, myParameters);
  private ResourceBundle DEFAULT_LANGUAGE_BUNDLE = ResourceBundle.getBundle("cellsociety.controller.English");
  private Map<String, String> mySimulationResourceMap;
  private SimulationController myController = new SimulationController();
  private UserController myUserController = new UserController(DEFAULT_LANGUAGE_BUNDLE, new SimulationController());
  Simulation mySimulation = new GameOfLife(mySimulationConfig, myGrid);
  private SimulationView mySimulationView = new SimulationView(mySimulationConfig, myController, DEFAULT_LANGUAGE_BUNDLE);

  Boolean startCalled = false;

  private BorderPane myRoot;
  private Scene myScene;
  private Stage myStage;

  // this method is automatically run BEFORE EACH test to set up application in a fresh state, like @BeforeEach
  @Override
  public void start (Stage stage) {
    mySimulationView.createSimulationWindow(stage);
    myRoot = mySimulationView.getRoot();
    myScene = mySimulationView.getScene();
  }

  @Test
  public void addElementToPane_PaneAndElementNotNull_PassTest() {
    Pane myPane = new GridPane();
    Button addButton = new Button("Add Element");
    myUserController.addElementToPane(addButton, myPane);
    assertTrue(myPane.getChildren().contains(addButton));
  }

  @Test
  public void addElementToPane_NullPane_ThrowsException() {
    Pane myPane = null;
    Button addButton = new Button("Add Element");
    assertThrows(NullPointerException.class, () -> myUserController.addElementToPane(addButton, myPane));
  }

  @Test
  public void addElementToPane_NullElement_ThrowsException() {
    Pane myPane = new GridPane(2, 2);
    Button addButton = null;
    assertThrows(NullPointerException.class, () -> myUserController.addElementToPane(addButton, myPane));
  }

  @Test
  public void makeButton_ClickOnButton_CallExecutedOne() {
    String expectedLabel = "Start";
    int[] callCount = new int[]{0};
    Button startButton = myUserController.makeButton(expectedLabel, e ->  callCount[0]++);
    interact(() -> {
      myRoot.getChildren().add(startButton);
    });
    assertTrue(myRoot.getChildren().contains(startButton), "Button should be in the pane's children");
  }

  @Test
  public void makeButton_ClickOnButtonTwice_CallExecutedTwice() {
    String expectedLabel = "Start";
    int[] callCount = new int[]{0};
    Button startButton = myUserController.makeButton(expectedLabel, e ->  callCount[0]++);
    interact(() -> {
      myRoot.getChildren().add(startButton);
      clickOn(startButton);
      clickOn(startButton);
    });
    assertEquals(2, callCount[0], "Method should have been called once");
  }

  @Test
  public void makeThemeComboBox_AddComboBoxNoThemeSelected_SceneEmpty() {
    ComboBox<String> themeComboBox = myUserController.makeThemeComboBox(mySimulationView, myScene);
    assertTrue(myScene.getStylesheets().isEmpty(), "Scene should start with no stylesheets.");
  }

  @Test
  public void selectTheme_SelectLightTheme_CSSFilesChange() {
    // GIVEN, app first starts up
    ComboBox<String> themeComboBox = myUserController.makeThemeComboBox(mySimulationView, myScene);

    // WHEN user selects light theme
    runAsJFXAction(() -> myRoot.getChildren().add(themeComboBox));
    runAsJFXAction(() -> myUserController.selectTheme(mySimulationView, myScene, themeComboBox));
    runAsJFXAction(() -> select(themeComboBox, "Light"));
    // THEN "Light" theme CSS files should be added to scene
    String expectedCss = getClass().getResource("/cellsociety/CSS/Light.css").toExternalForm();
    assertTrue(mySimulationView.getScene().getStylesheets().contains(expectedCss), "Light theme should be applied.");
    String expectedSimCss = getClass().getResource("/cellsociety/CSS/GameOfLife/GameOfLifeLight.css").toExternalForm();
    assertTrue(mySimulationView.getScene().getStylesheets().contains(expectedSimCss), "GameOfLife theme should be applied.");
  }

  @Test
  public void selectTheme_SelectLightThenSelectDark_CSSFilesChange() {
    // GIVEN, we make a themeComboBox
    ComboBox<String> themeComboBox = myUserController.makeThemeComboBox(mySimulationView, myScene);
    // WHEN user selects light theme then dark theme
    runAsJFXAction(() -> myRoot.getChildren().add(themeComboBox));
    runAsJFXAction(() -> myUserController.selectTheme(mySimulationView, myScene, themeComboBox));
    runAsJFXAction(() -> select(themeComboBox, "Light"));
    runAsJFXAction(() -> myUserController.selectTheme(mySimulationView, myScene, themeComboBox));
    runAsJFXAction(() -> select(themeComboBox, "Dark"));
    // THEN "Light" theme CSS files should be added to scene and dark one should be removed
    String darkCSS = getClass().getResource("/cellsociety/CSS/Dark.css").toExternalForm();
    assertTrue(mySimulationView.getScene().getStylesheets().contains(darkCSS), "Dark theme should be applied.");
    String expectedSimCss = getClass().getResource("/cellsociety/CSS/GameOfLife/GameOfLifeDark.css").toExternalForm();
    assertTrue(mySimulationView.getScene().getStylesheets().contains(expectedSimCss), "GameOfLife theme should be applied.");
    String lightCSS = getClass().getResource("/cellsociety/CSS/Light.css").toExternalForm();
    assertFalse(mySimulationView.getScene().getStylesheets().contains(lightCSS));
    String lightGameOfLife = getClass().getResource("/cellsociety/CSS/GameOfLife/GameOfLifeLight.css").toExternalForm();
    assertFalse(mySimulationView.getScene().getStylesheets().contains(lightGameOfLife));
  }

  @Test
  public void makeSpeedSlider_BasicTest_SliderHasValidProperties() {
    Slider slider = myUserController.makeSpeedSlider();
    assertNotNull(slider, "Slider should not be null");
    assertEquals(0.1, slider.getMin(), "Min value should be 0.1");
    assertEquals(5, slider.getMax(), "Max value should be 5");
    assertEquals(1, slider.getValue(), "Initial value should be 1");
    assertTrue(slider.isSnapToTicks(), "Slider should snap to ticks");
    assertTrue(slider.isShowTickLabels(), "Tick labels should be visible");
    assertTrue(slider.isShowTickMarks(), "Tick marks should be visible");
    assertEquals(1.0, slider.getMajorTickUnit(), "Major tick unit should be 1.0");
    assertEquals(9, slider.getMinorTickCount(), "Minor tick count should be 9");
    assertEquals(0.1, slider.getBlockIncrement(), "Block increment should be 0.1");
  }

}