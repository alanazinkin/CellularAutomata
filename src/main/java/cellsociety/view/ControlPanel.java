package cellsociety.view;

import static java.lang.Integer.parseInt;

import cellsociety.controller.FileRetriever;
import cellsociety.controller.SimulationMaker;
import cellsociety.controller.SimulationController;
import cellsociety.controller.SimulationUI;
import cellsociety.view.gridview.GridView;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Slider;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This class creates the buttons and components of the control panel, which allows the user to
 * control the type, speed, start, and stop of the simulation. It also creates a button to save an
 * XML file of the current state of the simulation.
 *
 * @author Alana Zinkin
 */
public class ControlPanel {

  private final Stage myStage;
  private final Scene myScene;
  private final SimulationController myController;
  private final ResourceBundle myResources;
  private final Map<String, String> myConfigBundle;
  private final SimulationView mySimView;
  private HBox myControlBar;
  private VBox myLowerBar;
  private HBox myTextBar;
  private HBox myCustomizationBar;
  private FileRetriever myFileRetriever;
  private UserController myUserControl;
  private GridView myGridView;
  private GridSettingsDisplay myGridSettingsDisplay;
  private Button gridLinesToggle;
  private Button flipGridButton;

  /**
   * construct a new Control Panel. Initializes the controller object by default. This prevents a
   * possible exception from occuring.
   */
  public ControlPanel(Stage stage, Scene scene, SimulationController controller,
      SimulationView simulationView, ResourceBundle resources, GridView gridView) {
    myStage = stage;
    myScene = scene;
    myController = controller;
    myConfigBundle = controller.retrieveImmutableConfigResourceBundle();
    myResources = resources;
    mySimView = simulationView;
    myGridView = gridView;
    initializeFileRetriever();
    initializeUserControl();
    initalizeGridSettingsDisplay();
  }

  public Button getGridLinesToggleButton() {
    return gridLinesToggle;
  }

  public Button getGridFlipButton() {
    return flipGridButton;
  }
  /**
   * create control bar GUI to allow users to start, pause, save, and select the type of simulation
   *
   * @param root of the scene
   */
  public void setupControlBar(BorderPane root) {
    // make a new HBox and set it in the top of the border pane
    makeControlBar(root);
    // add buttons to Control Bar
    Button startButton = myUserControl.makeButton(myResources.getString("Start"),
        e -> myController.startSimulation());
    startButton.setId("startButton");
    Button pauseButton = myUserControl.makeButton(myResources.getString("Pause"),
        e -> myController.pauseSimulation());
    pauseButton.setId("pauseButton");
    Button stepForwardButton = myUserControl.makeButton(myResources.getString("StepForward"),
        e -> myController.stepSimulation());
    stepForwardButton.setId("stepForwardButton");
    Button stepBackwardButton = myUserControl.makeButton(myResources.getString("StepBack"),
        e -> myController.stepBackSimulation());
    stepBackwardButton.setId("stepBackButton");
    Button resetButton = myUserControl.makeButton(myResources.getString("Reset"),
        e -> myController.resetGrid());
    resetButton.setId("resetButton");
    Button saveButton = myUserControl.makeButton(myResources.getString("Save"),
        e -> myController.saveSimulation());
    saveButton.setId("saveButton");
    Button addSimButton = myUserControl.makeButton(myResources.getString("AddSimulation"), e -> {
      makeANewSim();
    });
    addSimButton.setId("addSimButton");
    List<Button> buttons = List.of(startButton, pauseButton, stepForwardButton, stepBackwardButton,
        resetButton,
        saveButton, addSimButton);
    addButtonsToPane(buttons);
    List<String> simulationTypes = myFileRetriever.getSimulationTypes();
    updateSelectorWidgetsAndRespond(simulationTypes);
  }

  private void updateSelectorWidgetsAndRespond(List<String> simulationTypes) {
    try {
      SimulationSelector simulationSelector = new SimulationSelector(myResources, myController);
      List<ComboBox<String>> dropDownBoxes = simulationSelector.makeSimSelectorComboBoxes(
          myResources.getString("SelectSim"), myResources.getString("SelectConfig"),
          simulationTypes);
      for (ComboBox<String> dropDownBox : dropDownBoxes) {
        myUserControl.addElementToPane(dropDownBox, myControlBar);
      }
      simulationSelector.respondToFileSelection(dropDownBoxes.get(0), dropDownBoxes.get(1), myStage,
          myResources);
    } catch (Exception e) {
      SimulationUI.displayAlert(myResources.getString("Error"),
          myResources.getString("CantMakeSimSelector"));
    }
  }

  private void addButtonsToPane(List<Button> buttons) {
    try {
      for (Button button : buttons) {
        myUserControl.addElementToPane(button, myControlBar);
      }
    } catch (Exception e) {
      SimulationUI.displayAlert(myResources.getString("Error"),
          myResources.getString("CantMakeNewSimulation"));
    }
  }

  private void makeANewSim() {
    try {
      SimulationMaker maker = new SimulationMaker();
      maker.makeNewSimulation();
    } catch (Exception ex) {
      SimulationUI.displayAlert(myResources.getString("Error"),
          myResources.getString("CantMakeNewSimulation"));
    }
  }

  /**
   * creates the lower control bar and adds all elements to the pane, including speed slider,
   * customization buttons, and labels
   *
   * @param root main BorderPane that holds scene elements
   * @throws Exception if myCustomizationBar is null and elements cannot be added to pane
   */
  public void setUpLowerBar(BorderPane root) throws Exception {
    makeLowerBar(root);
    makeTextBar();
    makeCustomizationBar();

    Slider speedSlider = myUserControl.makeSpeedSlider();
    speedSlider.setId("speedSlider");
    ComboBox<String> themeSelector = myUserControl.makeThemeComboBox(mySimView, myScene);
    themeSelector.setId("themeSelector");
    myUserControl.selectTheme(mySimView, myScene, themeSelector);
    gridLinesToggle = myUserControl.makeGridLinesToggleButton(
        myResources.getString("ToggleGrid"));
    myUserControl.setGridLinesButtonAction(myGridView, gridLinesToggle);
    gridLinesToggle.setId("gridLinesToggle");
    flipGridButton = myUserControl.makeFlipGridButton(myResources.getString("FlipGrid"),
        myGridView);
    myUserControl.setGridFlipButtonAction(myGridView, flipGridButton);
    flipGridButton.setId("flipGridButton");
    Button gridSettings = myUserControl.makeButton(myResources.getString("GridSettings"),
        e -> myGridSettingsDisplay.openWindow());
    List<Control> elements = List.of(speedSlider, themeSelector, gridLinesToggle, flipGridButton,
        gridSettings);
    myCustomizationBar.getChildren().addAll(elements);
  }


  /**
   * Initializes lower control bar as a vertical box and adds it to the root borderpane
   *
   * @param root main BorderPane that holds scene elements
   */
  public void makeLowerBar(BorderPane root) {
    myLowerBar = new VBox(parseInt(myConfigBundle.getOrDefault("lower.bar.spacing", "10")));
    myLowerBar.setId("lowerBar");
    myLowerBar.setPadding(
        new Insets(0, 0, parseInt(myConfigBundle.getOrDefault("lower.bar.bottom.padding", "10")),
            0));
    myLowerBar.setPrefHeight(
        parseInt(myConfigBundle.getOrDefault("control.bar.height", "60")) * .3);
    myLowerBar.setAlignment(Pos.CENTER);
    myLowerBar.setPrefWidth(Double.MAX_VALUE);
    root.setBottom(myLowerBar);
  }

  private void initializeFileRetriever() {
    myFileRetriever = new FileRetriever();
  }

  private void initializeUserControl() {
    myUserControl = new UserController(myResources, myController);
  }

  private void initalizeGridSettingsDisplay() {
    myGridSettingsDisplay = new GridSettingsDisplay(myResources, myController, this);
    myGridSettingsDisplay.makeGridSettingsDisplay();
  }

  private void makeControlBar(BorderPane root) {
    myControlBar = new HBox();
    root.setTop(myControlBar);
    myControlBar.setAlignment(Pos.CENTER);
    myControlBar.setPrefHeight(parseInt(myConfigBundle.getOrDefault("control.bar.height", "400")));
    myControlBar.setId("controlBar");
  }

  private void makeTextBar() {
    myTextBar = new HBox(parseInt(myConfigBundle.getOrDefault("text.bar.spacing", "400")));
    myTextBar.setId("textBar");
    myTextBar.setAlignment(Pos.CENTER);
    addTextBarToLowerBar();
    makeTextAndAddToTextBar();
  }

  private void addTextBarToLowerBar() {
    myLowerBar.getChildren().add(myTextBar);
  }

  private void makeTextAndAddToTextBar() {
    Text speedText = new Text(myResources.getString("Speed"));
    speedText.setId("speedText");
    Text settingsText = new Text(myResources.getString("Settings"));
    settingsText.setId("settingsText");
    myTextBar.getChildren().addAll(speedText, settingsText);
    addCSSStyleIDs(List.of(speedText, settingsText));
  }

  /**
   * add text to the textBar
   *
   * @param newText text to add to the bar
   */
  public void addTextToTextBar(Text newText) {
    myTextBar.getChildren().add(newText);
    addCSSStyleIDs(List.of(newText));
  }

  private void addCSSStyleIDs(List<Shape> myTexts) {
    for (Shape myText : myTexts) {
      myText.getStyleClass().add("custom-text");
    }
  }

  private void makeCustomizationBar() {
    myCustomizationBar = new HBox(
        parseInt(myConfigBundle.getOrDefault("customization.bar.spacing", "100")));
    myCustomizationBar.setId("customizationBar");
    myCustomizationBar.setAlignment(Pos.CENTER);
    addCustomizationBarToLowerBar();
  }

  private void addCustomizationBarToLowerBar() {
    myLowerBar.getChildren().add(myCustomizationBar);
  }

}
