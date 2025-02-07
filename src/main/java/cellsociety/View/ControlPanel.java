package cellsociety.View;

import cellsociety.Controller.FileRetriever;
import cellsociety.Controller.SimulationController;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * This class creates the buttons and components of the control panel,
 * which allows the user to control the type, speed, start, and stop of the simulation. It also creates
 * a button to save an XML file of the current state of the simulation.
 */
public class ControlPanel {
  private static final int CONTROL_BAR_HEIGHT = 60;
  public static final String DEFAULT_RESOURCE_PACKAGE = "cellsociety.View.";

  private HBox myControlBar;
  private SimulationController myController;
  private VBox mySliderBar;
  private FileRetriever myFileRetriever;
  private ResourceBundle myResources;

  /**
   * construct a new Control Panel. Initializes the controller object by default.
   * This prevents a possible exception from occuring.
   */
  public ControlPanel(String language) {
    myResources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + language);
    initializeControls();
    initializeFileRetriever();
  }

  private void initializeControls() {
    myController = new SimulationController();
  }

  private void initializeFileRetriever() {
    myFileRetriever = new FileRetriever();
  }

  /**
   * create control bar GUI to allow users to start, pause, save, and select the type of simulation
   * @param root of the scene
   */
  public void makeControlBar(BorderPane root) {
    // make a new HBox and set it in the top of the border pane
    myControlBar = new HBox();
    root.setTop(myControlBar);
    myControlBar.setAlignment(Pos.CENTER);
    myControlBar.setPrefHeight(CONTROL_BAR_HEIGHT);
    // add buttons to Control Bar
    makeButton(myResources.getString("Start"), e -> myController.startSimulation());
    makeButton(myResources.getString("Pause"), e -> myController.pauseSimulation());
    makeButton(myResources.getString("Reset"), e -> myController.resetSimulation());
    makeButton(myResources.getString("Save"), e -> myController.saveSimulation());
    List<String> simulationTypes = myFileRetriever.getSimulationTypes();
    makeComboBox(myResources.getString("SelectSim"), e -> myController.selectSimulation(), simulationTypes);
  }

  public void makeSliderBar(BorderPane root) {
    // instantiate mySliderBar
    mySliderBar = new VBox(5);
    root.setBottom(mySliderBar);
    mySliderBar.setAlignment(Pos.CENTER);
    mySliderBar.setPrefHeight(CONTROL_BAR_HEIGHT * .3);
    // make speed slider
    makeSlider(myResources.getString("Speed"));
  }

  /**
   * make a new slider with Text label centered above it
   * @param label text label displayed to user describing slider
   */
  private void makeSlider(String label) {
    Text myLabel = new Text(label);
    mySliderBar.getChildren().add(myLabel);
    // make slider
    Slider slider = new Slider(0.1, 5, 1);
    slider.setPrefWidth(100);
    slider.setSnapToTicks(true);
    slider.setShowTickLabels(true);
    slider.setShowTickMarks(true);
    slider.setMajorTickUnit(1.0);
    slider.setMinorTickCount(9);
    slider.setBlockIncrement(0.1);
    slider.setMaxWidth(SimulationView.SIMULATION_WIDTH * .75);
    makeSliderAdjustToSpeed(slider);
    // add slider to slider bar
    mySliderBar.getChildren().add(slider);
  }

  private void makeSliderAdjustToSpeed(Slider slider) {
    slider.valueProperty().addListener((observable, oldValue, newValue) -> {
      myController.setSimulationSpeed(newValue.doubleValue());
    });
  }

  /**
   * create and initialize a new button and add it to the Control Bar
   * @param label of the button that is displayed to user
   * @param handler is the action that occurs upon clicking button
   */
  private void makeButton(String label, EventHandler<ActionEvent> handler) {
    Button button = new Button(label);
    button.setOnAction(handler);
    myControlBar.getChildren().add(button);
  }

  private void makeComboBox(String label, EventHandler<ActionEvent> handler, List<String> simulationTypeOptions) {
    ComboBox<String> simulationTypes = new ComboBox<>();
    simulationTypes.setPromptText(label);
    simulationTypes.getItems().addAll(simulationTypeOptions);
    simulationTypes.setOnAction(handler);

    ComboBox<String> configFileComboBox = new ComboBox<>();
    configFileComboBox.setPromptText(myResources.getString("ConfigFile"));

    // Update available files when simulation type is selected
    makeSimulationFileComboBox(simulationTypes, configFileComboBox);

    myControlBar.getChildren().addAll(simulationTypes, configFileComboBox);
  }

  private void makeSimulationFileComboBox(ComboBox<String> simulationTypes, ComboBox<String> configFileComboBox) {
    simulationTypes.valueProperty().addListener((obs, oldValue, simulationType) -> {
      if (simulationType == null) {
        configFileComboBox.getItems().clear();
        configFileComboBox.setDisable(true);
      }
      else {
        try {
          Collection<String> fileNames = myFileRetriever.getFileNames(simulationType);
          configFileComboBox.getItems().setAll(fileNames);
          configFileComboBox.setDisable(false);
        } catch (FileNotFoundException e) {
          displayAlert(simulationType);
          configFileComboBox.getItems().clear();
          configFileComboBox.setDisable(true);
        }
      }
    });
  }

  private void displayAlert(String simulationType) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(myResources.getString("Error"));
    alert.setContentText(myResources.getString("NoFilesToRun") + " " + simulationType + ". " + myResources.getString("SelectDifSim"));
    alert.showAndWait();
  }
}
