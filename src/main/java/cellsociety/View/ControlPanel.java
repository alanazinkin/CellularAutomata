package cellsociety.View;

import cellsociety.Controller.FileRetriever;
import cellsociety.Controller.SimulationMaker;
import cellsociety.Controller.SimulationController;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Slider;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This class creates the buttons and components of the control panel,
 * which allows the user to control the type, speed, start, and stop of the simulation. It also creates
 * a button to save an XML file of the current state of the simulation.
 */
public class ControlPanel {
  private static final int CONTROL_BAR_HEIGHT = 60;
  public static final String DEFAULT_RESOURCE_PACKAGE = "cellsociety.View.";

  private Stage myStage;
  private HBox myControlBar;
  private SimulationController myController;
  private VBox myLowerBar;
  private HBox myLabelBar;
  private HBox myCustomizationBar;
  private FileRetriever myFileRetriever;
  private ResourceBundle myResources;
  private SimulationView mySimView;
  private UserController myUserControl;

  /**
   * construct a new Control Panel. Initializes the controller object by default.
   * This prevents a possible exception from occuring.
   */
  public ControlPanel(Stage stage, String language, SimulationController controller, SimulationView simulationView) {
    myStage = stage;
    myController = controller;
    myResources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + language);
    mySimView = simulationView;
    initializeFileRetriever();
    initializeUserControl();
  }

  /**
   * create control bar GUI to allow users to start, pause, save, and select the type of simulation
   * @param root of the scene
   */
  public void setupControlBar(BorderPane root) {
    // make a new HBox and set it in the top of the border pane
    makeControlBar(root);
    // add buttons to Control Bar
    Button startButton = myUserControl.makeButton(myResources.getString("Start"), e -> myController.startSimulation());
    Button pauseButton = myUserControl.makeButton(myResources.getString("Pause"), e -> myController.pauseSimulation());
    Button stepForwardButton = myUserControl.makeButton(myResources.getString("Step"), e -> myController.stepSimulation(1));
    Button resetButton = myUserControl.makeButton(myResources.getString("Reset"), e -> myController.resetGrid());
    Button saveButton = myUserControl.makeButton(myResources.getString("Save"), e -> myController.saveSimulation());
    Button addSimButton = myUserControl.makeButton(myResources.getString("AddSimulation"), e -> {
      try {
        SimulationMaker maker = new SimulationMaker();
        maker.makeNewSimulation();
      } catch (Exception ex) {
        myController.displayAlert(myResources.getString("Error"), myResources.getString("CantMakeNewSimulation"));
      }
    });
    List<Button> buttons = List.of(startButton, pauseButton, stepForwardButton, resetButton, saveButton, addSimButton);
    try {
      for (Button button : buttons) {
        myUserControl.addElementToPane(button, myControlBar);
      }
    }
    catch (Exception e) {
      myController.displayAlert(myResources.getString("Error"), myResources.getString("CantMakeNewSimulation"));
    }
    //TODO add "one step back"
    List<String> simulationTypes = myFileRetriever.getSimulationTypes();
    try {
      myUserControl.makeSimSelectorComboBoxes(myResources.getString("SelectSim"), myResources.getString("SelectConfig"), simulationTypes, myStage, myControlBar);
    }
    catch (Exception e) {
      myController.displayAlert(myResources.getString("Error"), myResources.getString("CantMakeSimSelector"));
    }
  }

  /**
   * creates the lower control bar and adds all elements to the pane, including speed slider, customization buttons, and labels
   * @param root main BorderPane that holds scene elements
   * @throws Exception if myCustomizationBar is null and elements cannot be added to pane
   */
  public void setUpLowerBar(BorderPane root) throws Exception {
    makeLowerBar(root);
    makeLabelBar();
    makeCustomizationBar();

    Slider speedSlider = myUserControl.makeSpeedSlider();
    ComboBox<String> themeSelector = myUserControl.makeThemeComboBox(mySimView);

    myUserControl.addElementToPane(speedSlider, myCustomizationBar);
    myUserControl.addElementToPane(themeSelector, myCustomizationBar);
  }

  /**
   * Initializes lower control bar as a vertical box and adds it to the root borderpane
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

  private void makeLabelBar(){
    myLabelBar = new HBox(400);
    myLabelBar.setAlignment(Pos.CENTER);
    addLabelBarToLowerBar();
    makeLabelsAndAddToLabelBar();
  }

  private void addLabelBarToLowerBar() {
    myLowerBar.getChildren().add(myLabelBar);
  }

  private void makeLabelsAndAddToLabelBar() {
    Text myLabel = new Text(myResources.getString("Speed"));
    Text myCustomizationLabel = new Text(myResources.getString("Settings"));
    myLabelBar.getChildren().addAll(myLabel, myCustomizationLabel);
    addCSSStyleIDs(List.of(myLabel, myCustomizationLabel));
  }

  private void addCSSStyleIDs(List<Text> myTexts) {
    for (Text myText : myTexts) {
      myText.getStyleClass().add("custom-text");
    }
  }

  private void makeCustomizationBar() {
    myCustomizationBar = new HBox(200);
    myCustomizationBar.setAlignment(Pos.CENTER);
    addCustomizationBarToLowerBar();
  }

  private void addCustomizationBarToLowerBar() {
    myLowerBar.getChildren().add(myCustomizationBar);
  }


}
