package cellsociety.view;

import cellsociety.controller.FileRetriever;
import cellsociety.controller.SimulationMaker;
import cellsociety.controller.SimulationController;
import cellsociety.controller.SimulationUI;
import cellsociety.view.gridview.GridView;
import java.util.List;
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
 */
public class ControlPanel {

  private static final int CONTROL_BAR_HEIGHT = 60;

  private final Stage myStage;
  private final Scene myScene;
  private final SimulationController myController;
  private final ResourceBundle myResources;
  private final SimulationView mySimView;
  private HBox myControlBar;
  private VBox mySideBar;
  private VBox myLowerBar;
  private HBox myTextBar;
  private HBox myCustomizationBar;
  private FileRetriever myFileRetriever;
  private UserController myUserControl;
  private GridView myGridView;
  private Text iterationCounter;

  /**
   * construct a new Control Panel. Initializes the controller object by default. This prevents a
   * possible exception from occuring.
   */
  public ControlPanel(Stage stage, Scene scene, SimulationController controller,
      SimulationView simulationView, ResourceBundle resources, GridView gridView) {
    myStage = stage;
    myScene = scene;
    myController = controller;
    myResources = resources;
    mySimView = simulationView;
    myGridView = gridView;
    initializeFileRetriever();
    initializeUserControl();
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
    Button pauseButton = myUserControl.makeButton(myResources.getString("Pause"),
        e -> myController.pauseSimulation());
    Button stepForwardButton = myUserControl.makeButton(myResources.getString("StepForward"),
        e -> myController.stepSimulation());
    Button stepBackwardButton = myUserControl.makeButton(myResources.getString("StepBack"),
        e -> myController.stepBackSimulation());
    Button resetButton = myUserControl.makeButton(myResources.getString("Reset"),
        e -> myController.resetGrid());
    Button saveButton = myUserControl.makeButton(myResources.getString("Save"),
        e -> myController.saveSimulation());
    Button addSimButton = myUserControl.makeButton(myResources.getString("AddSimulation"), e -> {
      makeANewSim();
    });
    List<Button> buttons = List.of(startButton, pauseButton, stepForwardButton, stepBackwardButton, resetButton,
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
    ComboBox<String> themeSelector = myUserControl.makeThemeComboBox(mySimView, myScene);
    myUserControl.selectTheme(mySimView, myScene, themeSelector);
    Button gridLinesToggle = myUserControl.makeGridLinesToggleButton(
        myResources.getString("ToggleGrid"), myGridView);
    Button flipGridButton = myUserControl.makeFlipGridButton(myResources.getString("FlipGrid"), myGridView);

    List<Control> elements = List.of(speedSlider, themeSelector, gridLinesToggle, flipGridButton);
    for (Control element : elements) {
      myUserControl.addElementToPane(element, myCustomizationBar);
    }
  }

  /**
   * Initializes lower control bar as a vertical box and adds it to the root borderpane
   *
   * @param root main BorderPane that holds scene elements
   */
  public void makeLowerBar(BorderPane root) {
    myLowerBar = new VBox(10);
    myLowerBar.setPadding(new Insets(0, 0, 10, 0));
    myLowerBar.setPrefHeight(CONTROL_BAR_HEIGHT * .3);
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

  private void makeControlBar(BorderPane root) {
    myControlBar = new HBox();
    root.setTop(myControlBar);
    myControlBar.setAlignment(Pos.CENTER);
    myControlBar.setPrefHeight(CONTROL_BAR_HEIGHT);
  }

  private void makeTextBar() {
    myTextBar = new HBox(400);
    myTextBar.setAlignment(Pos.CENTER);
    addTextBarToLowerBar();
    makeTextAndAddToTextBar();
  }

  private void addTextBarToLowerBar() {
    myLowerBar.getChildren().add(myTextBar);
  }

  private void makeTextAndAddToTextBar() {
    Text speedText = new Text(myResources.getString("Speed"));
    Text settingsText = new Text(myResources.getString("Settings"));
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
    myCustomizationBar = new HBox(100);
    myCustomizationBar.setAlignment(Pos.CENTER);
    addCustomizationBarToLowerBar();
  }

  private void addCustomizationBarToLowerBar() {
    myLowerBar.getChildren().add(myCustomizationBar);
  }

}