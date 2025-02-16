package cellsociety.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;
import java.util.ResourceBundle;

public class SaveSimulationDescription {

    private final Dialog<SimulationMetadata> dialog;
    private final ResourceBundle myResources;
    private final TextField titleField;
    private final TextField authorField;
    private final TextArea descriptionArea;
    private final SimulationConfig currentConfig;

    public record SimulationMetadata(String title, String author, String description, File saveLocation) {}

    public SaveSimulationDescription(Stage owner, ResourceBundle resources, SimulationConfig config) {
        myResources = resources;
        currentConfig = config;
        dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle(myResources.getString("Save"));

        // Create input fields
        titleField = new TextField(config.getTitle());
        authorField = new TextField(config.getAuthor());
        descriptionArea = new TextArea(config.getDescription());
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(3);

        setupDialog();
    }

    private void setupDialog() {
        // Create the content grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Add fields to grid
        grid.add(new Label(myResources.getString("Title") + ":"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label(myResources.getString("Author") + ":"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label(myResources.getString("Description") + ":"), 0, 2);
        grid.add(descriptionArea, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Add buttons
        ButtonType saveButtonType = new ButtonType(myResources.getString("Save"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton != null && dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                return showFileChooserAndCreateMetadata();
            }
            return null;
        });

        // Enable the save button
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(false);
    }

    private SimulationMetadata showFileChooserAndCreateMetadata() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(myResources.getString("Save"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML Files", "*.xml"));

        // Set initial directory to the data folder
        File initialDirectory = new File("data/" + currentConfig.getType().replace(" ", ""));
        if (!initialDirectory.exists()) {
            initialDirectory.mkdirs();
        }
        fileChooser.setInitialDirectory(initialDirectory);

        // Show save dialog
        File file = fileChooser.showSaveDialog(dialog.getOwner());
        if (file != null) {
            return new SimulationMetadata(
                    titleField.getText(),
                    authorField.getText(),
                    descriptionArea.getText(),
                    file
            );
        }
        return null;
    }

    public Optional<SimulationMetadata> showAndWait() {
        return dialog.showAndWait();
    }

}
