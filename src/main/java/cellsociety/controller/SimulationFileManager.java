package cellsociety.controller;

import cellsociety.model.Grid;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SimulationFileManager {
    private String configFilePath;
    private FileRetriever fileRetriever;
    private XMLParser xmlParser;
    private XMLWriter xmlWriter;

    public SimulationFileManager() {
        this.xmlParser = new XMLParser();
        this.xmlWriter = new XMLWriter();
    }

    public void loadFile(String simulationType, String fileName) throws FileNotFoundException {
        fileRetriever = new FileRetriever();
        String basePath = "data/" + fileRetriever.getSimulationTypeFolderExtension(simulationType);
        configFilePath = basePath + "/" + fileName;
    }

    public SimulationConfig parseConfiguration() throws Exception {
        XMLParser xmlParser = new XMLParser();
        return xmlParser.parseXMLFile(configFilePath);
    }

    public void saveSimulation(SimulationConfig config, Grid grid) {
        SaveSimulationDescription dialog = new SaveSimulationDescription(stage, resources, config);

        dialog.showAndWait().ifPresent(metadata -> {
            try {
                updateConfigurationWithMetadata(config, metadata);
                XMLWriter xmlWriter = new XMLWriter();
                xmlWriter.saveToXML(config, grid, metadata.saveLocation().getAbsolutePath());
                displaySuccessMessage(metadata.saveLocation().getName());
            } catch (IOException e) {
                // Handle error
            }
        });
    }
}
