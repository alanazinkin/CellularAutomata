package cellsociety.controller;

import cellsociety.model.Grid;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

public class SimulationFileManager {
    private String configFilePath;
    private FileRetriever fileRetriever;
    private XMLParser xmlParser;
    private SimulationWriter xmlWriter;
    private ResourceBundle resources;

    /**
     * initializes a file manager
     */
    public SimulationFileManager() {
        this.xmlParser = new XMLParser();
        this.xmlWriter = new XMLWriter();
    }

    /**
     * loads a file given a simulation type and a file name
     * @param simulationType type of simulation selected
     * @param fileName specific simulation config file
     * @throws FileNotFoundException if the file does not exist in the path
     */
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

        xmlWriter.save(config, grid, metadata.saveLocation().getAbsolutePath());
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

    /**
     * Saves the current style to a file.
     *
     * @param stage the primary stage for the application
     * @param resources the resource bundle for internationalization
     * @param style the current simulation style
     */
    public void saveStyle(Stage stage, ResourceBundle resources, SimulationStyle style) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resources.getString("SaveStyleTitle"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML Files", "*.xml"));

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            saveStyleToFile(file, style);
        }
    }

    /**
     * Saves a style to a file.
     *
     * @param file the file to save to
     * @param style the style to save
     */
    private void saveStyleToFile(File file, SimulationStyle style) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("style");
            doc.appendChild(rootElement);

            Element idElement = doc.createElement("id");
            idElement.setTextContent("custom-style-" + System.currentTimeMillis());
            rootElement.appendChild(idElement);

            Element cellStatesElement = doc.createElement("cell-states");
            rootElement.appendChild(cellStatesElement);

            for (Map.Entry<String, CellAppearance> entry : style.getCellAppearances().entrySet()) {
                Element stateElement = doc.createElement("state");
                stateElement.setAttribute("name", entry.getKey());

                CellAppearance appearance = entry.getValue();
                if (appearance.getColor() != null) {
                    stateElement.setAttribute("color", appearance.getColor());
                }

                if (appearance.usesImage()) {
                    Element imageElement = doc.createElement("image");
                    imageElement.setTextContent(appearance.getImagePath());
                    stateElement.appendChild(imageElement);
                }

                cellStatesElement.appendChild(stateElement);
            }

            Element gridElement = doc.createElement("grid");
            rootElement.appendChild(gridElement);

            Element edgePolicyElement = doc.createElement("edge-policy");
            edgePolicyElement.setTextContent(style.getEdgePolicy().toString());
            gridElement.appendChild(edgePolicyElement);

            Element cellShapeElement = doc.createElement("cell-shape");
            cellShapeElement.setTextContent(style.getCellShape().toString());
            gridElement.appendChild(cellShapeElement);

            Element neighborElement = doc.createElement("neighbor-arrangement");
            neighborElement.setTextContent(style.getNeighborArrangement().toString());
            gridElement.appendChild(neighborElement);

            Element displayElement = doc.createElement("display");
            rootElement.appendChild(displayElement);

            Element gridOutlineElement = doc.createElement("grid-outline");
            gridOutlineElement.setTextContent(String.valueOf(style.isShowGridOutline()));
            displayElement.appendChild(gridOutlineElement);

            Element colorThemeElement = doc.createElement("color-theme");
            colorThemeElement.setTextContent(style.getColorTheme().toString());
            displayElement.appendChild(colorThemeElement);

            Element speedElement = doc.createElement("animation-speed");
            speedElement.setTextContent(String.valueOf(style.getAnimationSpeed()));
            displayElement.appendChild(speedElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
