package cellsociety.view;

import static java.lang.Integer.parseInt;

import cellsociety.controller.FileRetriever;
import cellsociety.controller.SimulationController;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class UserController {

  private ResourceBundle myResources;
  private SimulationController myController;
  private Map<String, String> mySimulationResourceMap;

  public UserController(ResourceBundle resources, SimulationController simulationController) {
    myResources = resources;
    myController = simulationController;
    mySimulationResourceMap = SimulationController.retrieveImmutableConfigResourceBundle();
  }

  //TODO write better error message
  public void addElementToPane(Control element, Pane pane) throws Exception {
    if (pane != null) {
      pane.getChildren().add(element);
    } else {
      throw new NullPointerException("Pane is null");
    }
  }

  /**
   * create and initialize a new button and add it to the Control Bar
   *
   * @param label   of the button that is displayed to user
   * @param handler is the action that occurs upon clicking button
   */
  public Button makeButton(String label, EventHandler<ActionEvent> handler) {
    Button button = new Button(label);
    button.setOnAction(handler);
    return button;
  }

  /**
   * make a new slider with Text label centered above it
   */
  public Slider makeSpeedSlider() {
    Slider slider = new Slider(0.1, 5, 1);
    slider.setPrefWidth(
        parseInt(mySimulationResourceMap.getOrDefault("window.width", "1000")) * .5);
    slider.setSnapToTicks(true);
    slider.setShowTickLabels(true);
    slider.setShowTickMarks(true);
    slider.setMajorTickUnit(1.0);
    slider.setMinorTickCount(9);
    slider.setBlockIncrement(0.1);
    slider.setMaxWidth(parseInt(mySimulationResourceMap.getOrDefault("window.width", "1000")) * .5);
    makeSliderAdjustToSpeed(slider);
    return slider;
  }

  private void makeSliderAdjustToSpeed(Slider slider) {
    slider.valueProperty().addListener((observable, oldValue, newValue) -> {
      myController.setSimulationSpeed(newValue.doubleValue());
    });
  }

  public ComboBox<String> makeThemeComboBox(SimulationView simulationView) {
    ComboBox<String> themeSelector = new ComboBox<>();
    themeSelector.setPromptText(myResources.getString("SelectTheme"));
    themeSelector.getItems().addAll("Dark", "Light");
    themeSelector.setOnAction(e -> {
      String selectedThemeColor = themeSelector.getValue();
      if (selectedThemeColor != null) {
        try {
          simulationView.setTheme(selectedThemeColor);
        } catch (FileNotFoundException ex) {
          throw new RuntimeException(ex);
        }
      } else {
        myController.displayAlert(myResources.getString("Error"),
            myResources.getString("NoThemeSelected"));
      }
    });
    return themeSelector;
  }

  public List<ComboBox<String>> makeSimSelectorComboBoxes(String label, String secondBoxLabel,
      List<String> simulationTypeOptions, Stage stage) throws Exception {
    ComboBox<String> simulationTypes = new ComboBox<>();
    simulationTypes.setPromptText(label);
    simulationTypes.getItems().addAll(simulationTypeOptions);
    ComboBox<String> configFileComboBox = new ComboBox<>();
    configFileComboBox.setPromptText(secondBoxLabel);
    // Update available files when simulation type is selected
    updateFileOptions(simulationTypes, configFileComboBox, stage);
    respondToFileSelection(simulationTypes, configFileComboBox, stage);
    return List.of(simulationTypes, configFileComboBox);
  }

  private void updateFileOptions(ComboBox<String> simulationTypes,
      ComboBox<String> configFileComboBox, Stage stage) {
    simulationTypes.valueProperty().addListener((obs, oldValue, simulationType) -> {
      if (simulationType == null) {
        configFileComboBox.getItems().clear();
        configFileComboBox.setDisable(true);
      } else {
        try {
          displayNewFileOptions(configFileComboBox, simulationType);
        } catch (FileNotFoundException e) {
          myController.displayAlert(myResources.getString("Error"),
              myResources.getString("NoFilesToRun") + " " + simulationType + ". "
                  + myResources.getString("SelectDifSim"));
          configFileComboBox.getItems().clear();
          configFileComboBox.setDisable(true);
        }
      }
    });
  }

  public void respondToFileSelection(ComboBox<String> simulationTypes,
      ComboBox<String> configFileComboBox, Stage stage) {
    configFileComboBox.setOnAction(e -> {
      String fileName = configFileComboBox.getValue();
      String simulationType = simulationTypes.getValue();
      if (simulationType != null && fileName != null) {
        try {
          myController.selectSimulation(simulationType, fileName, stage, myController);
        } catch (Exception ex) {
          myController.displayAlert(myResources.getString("Error"), myResources.getString("SimOrFileNOtSelected"));
        }
      }
    });
  }

  private static void displayNewFileOptions(ComboBox<String> configFileComboBox,
      String simulationType) throws FileNotFoundException {
    FileRetriever fileRetriever = new FileRetriever();
    Collection<String> fileNames = fileRetriever.getFileNames(simulationType);
    configFileComboBox.getItems().setAll(fileNames);
    configFileComboBox.setDisable(false);
  }
}
