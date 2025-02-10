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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
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
  private VBox myLowerBar;
  private HBox myLabelBar;
  private HBox myCustomizationBar;
  private FileRetriever myFileRetriever;
  private ResourceBundle myResources;
  private SimulationView mySimView;

  /**
   * construct a new Control Panel. Initializes the controller object by default.
   * This prevents a possible exception from occuring.
   */
  public ControlPanel(String language, SimulationController controller, SimulationView simulationView) {
    myController = controller;
    myResources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + language);
    mySimView = simulationView;
    initializeFileRetriever();
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
    //TODO add "one step"
    makeButton(myResources.getString("Step"), e -> myController.stepSimulation(1));
    makeButton(myResources.getString("Reset"), e -> myController.resetGrid());
    makeButton(myResources.getString("AddSimulation"), e -> {
      try {
        SimulationMaker maker = new SimulationMaker();
        maker.makeNewSimulation();
      } catch (Exception ex) {
        myController.displayAlert(myResources.getString("Error"), myResources.getString("CantMakeNewSimulation"));
      }
    });
    makeButton(myResources.getString("Save"), e -> myController.saveSimulation());
    List<String> simulationTypes = myFileRetriever.getSimulationTypes();
    makeComboBox(myResources.getString("SelectSim"), myResources.getString("SelectConfig") , e -> myController.selectSimulation(),
        simulationTypes);
  }

  //TODO
  public void makeLowerBar(BorderPane root) {
    myLowerBar = new VBox(10);
    myLowerBar.setPadding(new Insets(0, 0, 10, 0));
    myLowerBar.setPrefHeight(CONTROL_BAR_HEIGHT * .3);
    myLowerBar.setAlignment(Pos.CENTER);
    myLowerBar.setPrefWidth(Double.MAX_VALUE);
    root.setBottom(myLowerBar);
  }

  public void makeLabelBar(){
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

  public void makeCustomizationBar() {
    myCustomizationBar = new HBox(200);
    myCustomizationBar.setAlignment(Pos.CENTER);
    addCustomizationBarToLowerBar();
  }

  private void addCustomizationBarToLowerBar() {
    myLowerBar.getChildren().add(myCustomizationBar);
  }
  /**
   * This method initializes and adds the lower VBox panel containing the speed slider to the scene
   */
  //TODO
  public void makeSliderComponent() throws Exception {
      Slider slider = makeSlider();
      addElementToCustomizationBar(slider);
  }

  /**
   * make a new slider with Text label centered above it
   */
  private Slider makeSlider() {
    Slider slider = new Slider(0.1, 5, 1);
    slider.setPrefWidth(SimulationView.SIMULATION_WIDTH * .5);
    slider.setSnapToTicks(true);
    slider.setShowTickLabels(true);
    slider.setShowTickMarks(true);
    slider.setMajorTickUnit(1.0);
    slider.setMinorTickCount(9);
    slider.setBlockIncrement(0.1);
    slider.setMaxWidth(SimulationView.SIMULATION_WIDTH * .5);
    makeSliderAdjustToSpeed(slider);
    return slider;
  }

  private void makeSliderAdjustToSpeed(Slider slider) {
    slider.valueProperty().addListener((observable, oldValue, newValue) -> {
      myController.setSimulationSpeed(newValue.doubleValue());
    });
  }
  //TODO
  public void makeThemeComponent() throws Exception {
      ComboBox themeSelector = makeThemeComboBox();
      themeSelector.setPromptText(myResources.getString("SelectTheme"));
      addElementToCustomizationBar(themeSelector);
  }

  private ComboBox makeThemeComboBox() {
    ComboBox<String> themeSelector = new ComboBox<>();
    themeSelector.getItems().addAll("Dark", "Light");
    themeSelector.setOnAction(e -> {
      String selectedThemeColor = themeSelector.getValue();
      if (selectedThemeColor != null) {
        mySimView.setTheme(selectedThemeColor);
      } else {
        myController.displayAlert(myResources.getString("Error"), myResources.getString("NoThemeSelected"));
      }
    });
    return themeSelector;
  }

  private void addElementToCustomizationBar(Control element) throws Exception {
    if (myCustomizationBar != null){
      myCustomizationBar.getChildren().add(element);
    }
    else {
      throw new NullPointerException("CustomizationBar is null");
    }
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

  private void makeComboBox(String label, String secondBoxLabel, EventHandler<ActionEvent> handler, List<String> simulationTypeOptions) {
    ComboBox<String> simulationTypes = new ComboBox<>();
    simulationTypes.setPromptText(label);
    simulationTypes.getItems().addAll(simulationTypeOptions);
    simulationTypes.setOnAction(handler);
    ComboBox<String> configFileComboBox = new ComboBox<>();
    configFileComboBox.setPromptText(secondBoxLabel);
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
          myController.displayAlert(myResources.getString("Error"), myResources.getString("NoFilesToRun") + " " + simulationType + ". " + myResources.getString("SelectDifSim"));
          configFileComboBox.getItems().clear();
          configFileComboBox.setDisable(true);
        }
      }
    });
  }

}
