package cellsociety.controller;

import cellsociety.model.Grid;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;

public class SimulationFileManager {
    private String configFilePath;
    private FileRetriever fileRetriever;
    private XMLParser xmlParser;
    private XMLWriter xmlWriter;
    private ResourceBundle resources;

    public SimulationFileManager() {
        this.xmlParser = new XMLParser();
        this.xmlWriter = new XMLWriter();
    }

    public void loadFile(String simulationType, String fileName) throws FileNotFoundException {
        fileRetriever = new FileRetriever();
        try {
            String basePath = "data/" + fileRetriever.getSimulationTypeFolderExtension(simulationType);
            configFilePath = basePath + "/" + fileName;
        } catch (FileNotFoundException e) {
            displayAlert("Error", "Invalid simulation type");
            throw e;
        }
    }

    public SimulationConfig parseConfiguration() throws Exception {
        try {
            return xmlParser.parseXMLFile(configFilePath);
        } catch (Exception e) {
            displayAlert("Error", "Failed to parse configuration file");
            throw e;
        }
    }

    public void saveSimulation(Stage stage, ResourceBundle resources, SimulationConfig config, Grid grid) {
        this.resources = resources;
        try {
            if (config == null) {
                displayAlert(resources.getString("Error"), resources.getString("ConfigNull"));
                throw new IllegalStateException("Configuration is null");
            }

            SaveSimulationDescription dialog = new SaveSimulationDescription(stage, resources, config);

            dialog.showAndWait().ifPresent(metadata -> {
                try {
                    updateConfigurationWithMetadata(config, metadata);
                    saveConfigurationToFile(config, grid, metadata);
                    displaySuccessMessage(metadata.saveLocation().getName());
                } catch (IOException e) {
                    displayAlert(resources.getString("Error"), resources.getString("SaveError"));
                }
            });
        } catch (Exception e) {
            displayAlert(resources.getString("Error"), resources.getString("SaveError"));
        }
    }

    private void updateConfigurationWithMetadata(SimulationConfig config,
                                                 SaveSimulationDescription.SimulationMetadata metadata) {
        config.setTitle(metadata.title());
        config.setAuthor(metadata.author());
        config.setDescription(metadata.description());
    }

    private void saveConfigurationToFile(SimulationConfig config, Grid grid,
                                         SaveSimulationDescription.SimulationMetadata metadata) throws IOException {
        xmlWriter.saveToXML(config, grid, metadata.saveLocation().getAbsolutePath());
    }

    private void displaySuccessMessage(String fileName) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resources.getString("Success"));
            alert.setContentText(String.format("%s %s",
                    fileName,
                    resources.getString("Saved")));
            alert.showAndWait();
        });
    }

    private void displayAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public String getConfigFilePath() {
        return configFilePath;
    }
}
