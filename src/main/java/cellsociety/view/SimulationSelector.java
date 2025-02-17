package cellsociety.view;

import cellsociety.controller.FileRetriever;
import cellsociety.controller.SimulationController;
import cellsociety.controller.SimulationUI;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class SimulationSelector {

  private ResourceBundle myResources;
  private SimulationController myController;
  private SimulationUI myUI;

  public SimulationSelector(ResourceBundle resources, SimulationController simulationController) {
    myResources = resources;
    myController = simulationController;
    myUI = myController.getUI();
  }
  public List<ComboBox<String>> makeSimSelectorComboBoxes(String label, String secondBoxLabel,
      List<String> simulationTypeOptions) throws Exception {
    ComboBox<String> simulationTypes = new ComboBox<>();
    simulationTypes.setPromptText(label);
    simulationTypes.getItems().addAll(simulationTypeOptions);
    ComboBox<String> configFileComboBox = new ComboBox<>();
    configFileComboBox.setPromptText(secondBoxLabel);
    // Update available files when simulation type is selected
    updateFileOptions(simulationTypes, configFileComboBox);
    return List.of(simulationTypes, configFileComboBox);
  }

  private void updateFileOptions(ComboBox<String> simulationTypes,
      ComboBox<String> configFileComboBox) {
    simulationTypes.valueProperty().addListener((obs, oldValue, simulationType) -> {
      if (simulationType == null) {
        configFileComboBox.getItems().clear();
        configFileComboBox.setDisable(true);
      } else {
        try {
          displayNewFileOptions(configFileComboBox, simulationType);
        } catch (FileNotFoundException e) {
          myUI.displayAlert(myResources.getString("Error"),
              myResources.getString("NoFilesToRun") + " " + simulationType + ". "
                  + myResources.getString("SelectDifSim"));
          configFileComboBox.getItems().clear();
          configFileComboBox.setDisable(true);
        }
      }
    });
  }

  public void respondToFileSelection(ComboBox<String> simulationTypes,
      ComboBox<String> configFileComboBox, Stage stage, ResourceBundle resources) {
    configFileComboBox.setOnAction(e -> {
      String fileName = configFileComboBox.getValue();
      String simulationType = simulationTypes.getValue();
      if (simulationType != null && fileName != null) {
        try {
          myController.selectSimulation(simulationType, fileName, stage, myController);
        } catch (Exception ex) {
          myUI.displayAlert(resources.getString("Error"),
              resources.getString("SimOrFileNOtSelected"));
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
