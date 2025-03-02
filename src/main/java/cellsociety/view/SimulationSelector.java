package cellsociety.view;

import cellsociety.controller.FileRetriever;
import cellsociety.controller.SimulationController;
import cellsociety.controller.SimulationUI;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

/**
 * the SimulationSelector class creates the necessary buttons, and ComboBoxes for selecting a new
 * simulation from a list of simulation type options and simulation configuration files
 *
 * @author Alana Zinkin
 */
public class SimulationSelector {

  private ResourceBundle myResources;
  private SimulationController myController;

  /**
   * Constructor for making a new SimulationSelector
   *
   * @param resources            ResourceBundle for user-selected language
   * @param simulationController SimulationController object for managing the simulation
   */
  public SimulationSelector(ResourceBundle resources, SimulationController simulationController) {
    myResources = resources;
    myController = simulationController;
  }

  /**
   * creates combo boxes for selecting a new simulation
   *
   * @param firstLabel            label for first combo box
   * @param secondBoxLabel        label for second combo box
   * @param simulationTypeOptions list of the different types of simulations to choose from
   * @return a list of two comboBoxes, the first is the TypeSelector, the second is the
   * ConfigFileSelector
   * @throws Exception
   */
  public List<ComboBox<String>> makeSimSelectorComboBoxes(String firstLabel, String secondBoxLabel,
      List<String> simulationTypeOptions) throws Exception {
    ComboBox<String> simulationTypes = new ComboBox<>();
    simulationTypes.setId("simulationTypesComboBox");
    simulationTypes.setPromptText(firstLabel);
    simulationTypes.getItems().addAll(simulationTypeOptions);
    ComboBox<String> configFileComboBox = new ComboBox<>();
    configFileComboBox.setId("configFileComboBox");
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
          if (myResources != null) {
            SimulationUI.displayAlert(myResources.getString("Error"),
                myResources.getString("NoFilesToRun") + " " + simulationType + ". "
                    + myResources.getString("SelectDifSim"));
          } else {
            SimulationUI.displayAlert("Error", "No Files To Run" + " " + simulationType + ". "
                + "Select a different simulation type");
          }
          configFileComboBox.getItems().clear();
          configFileComboBox.setDisable(true);
        }
      }
    });
  }

  /**
   * sets the action of the second combobox to selecting a simulation
   *
   * @param simulationTypes    combobox holding the different simulation types
   * @param configFileComboBox combobox holding the specific files for the selected simulation type
   * @param stage              current stage for holding the new simulation
   * @param resources          ResourceBundle for user-selected language
   */
  public void respondToFileSelection(ComboBox<String> simulationTypes,
      ComboBox<String> configFileComboBox, Stage stage, ResourceBundle resources) {
    configFileComboBox.setOnAction(e -> {
      String fileName = configFileComboBox.getValue();
      String simulationType = simulationTypes.getValue();
      if (simulationType != null && fileName != null) {
        try {
          myController.selectSimulation(simulationType, fileName, stage, myController);
        } catch (Exception ex) {
          SimulationUI.displayAlert(resources.getString("Error"),
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
