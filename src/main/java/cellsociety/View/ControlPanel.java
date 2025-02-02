package cellsociety.View;

import cellsociety.Controller.SimulationController;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
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
  private HBox myControlBar;
  private SimulationController myController;
  private VBox mySliderBar;

  /**
   * construct a new Control Panel. Initializes the controller object by default.
   * This prevents a possible exception from occuring.
   */
  public ControlPanel() {
    initializeControls();
  }

  /**
   * instantiate myController object
   */
  public void initializeControls() {
    myController = new SimulationController();
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
    makeButton("Start", e -> myController.startSimulation());
    makeButton("Pause", e -> myController.pauseSimulation());
    makeButton("Reset", e -> myController.resetSimulation());
    makeButton("Save", e -> myController.saveSimulation());
    List<String> simulationTypes = getSimulationTypes();
    makeComboBox("Select Simulation Type", e -> myController.selectSimulation(), simulationTypes);
  }

  public void makeSliderBar(BorderPane root) {
    // instantiate mySliderBar
    mySliderBar = new VBox(5);
    root.setBottom(mySliderBar);
    mySliderBar.setAlignment(Pos.CENTER);
    mySliderBar.setPrefHeight(CONTROL_BAR_HEIGHT * .3);
    // make speed slider
    makeSlider("Speed");
  }

  /**
   * make a new slider with Text label centered above it
   * @param label text label displayed to user describing slider
   */
  private void makeSlider(String label) {
    Text myLabel = new Text(label);
    mySliderBar.getChildren().add(myLabel);
    // make slider
    Slider slider = new Slider(0, 100, 50);
    slider.setPrefWidth(100);
    slider.setSnapToTicks(true);
    slider.setShowTickLabels(true);
    slider.setShowTickMarks(true);
    slider.setBlockIncrement(25);
    slider.setMaxWidth(SimulationView.SIMULATION_WIDTH * .75);
    // add slider to slider bar
    mySliderBar.getChildren().add(slider);
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

  /**
   * create and initialize a new combo box and add it to the Control Bar
   * @param label combo nox button label
   * @param handler action event to occur
   * @param simulationTypeOptions combo box drop down options to select
   */
  private void makeComboBox(String label, EventHandler<ActionEvent> handler, List<String> simulationTypeOptions) {
    ComboBox<String> simulationTypes = new ComboBox<>();
    simulationTypes.setPromptText(label);
    for (String option : simulationTypeOptions) {
      simulationTypes.getItems().add(option);
    }
    simulationTypes.setOnAction(handler);
    myControlBar.getChildren().add(simulationTypes);
  }

  //TODO: remove method once real one is created
  /**
   * method for testing
   * @return
   */
  private List<String> getSimulationTypes(){
    List<String> simulationTypes = new ArrayList<>();
    simulationTypes.add("Game of Life");
    simulationTypes.add("Wa-Tor World");
    simulationTypes.add("Spreading of Fire");
    simulationTypes.add("Percolation");
    simulationTypes.add("Schelling State");
    return simulationTypes;
  }

}
