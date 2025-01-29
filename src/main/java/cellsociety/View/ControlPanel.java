package cellsociety.View;

import cellsociety.Controller.SimulationController;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * This class creates the buttons and components of the control panel,
 * which allows the user to control the type, speed, start, and stop of the simulation. It also creates
 * a button to save an XML file of the current state of the simulation.
 */
public class ControlPanel {

  private Pane myControlBar;
  private SimulationController myController;

  /**
   * create control bar GUI to allow users to start, pause, save, and select the type of simulation
   * @param root of the scene
   */
  public void makeControlBar(BorderPane root) {
    // make a new HBox and set it in the top of the border pane
    myControlBar = new HBox();
    root.setTop(myControlBar);
    // instantiate myController
    myController = new SimulationController();
    // add buttons to Control Bar
    makeButton("Start", e -> myController.startSimulation());
    makeButton("Pause", e -> myController.pauseSimulation());
    makeButton("Reset", e -> myController.resetSimulation());
    makeButton("Save", e -> myController.saveSimulation());
    List<String> simulationTypes = getSimulationTypes();
    makeComboBox("Select Simulation Type", e -> myController.selectSimulation(), simulationTypes);
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
    for (String option : simulationTypeOptions) {
      simulationTypes.getItems().add(option);
    }
    simulationTypes.setOnAction(handler);
    myControlBar.getChildren().add(simulationTypes);
  }

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
