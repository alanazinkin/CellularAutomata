package cellsociety.controller;

import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.view.SimulationView;
import cellsociety.view.SplashScreen;
import cellsociety.view.UserController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.*;

public class SimulationUI {
    private final ResourceBundle config;
    private Stage stage;
    private SimulationView simulationView;
    private ResourceBundle resources;
    private FileRetriever fileRetriever;
    private UserController userController;

    public SimulationUI(ResourceBundle config) {
        this.config = config;
    }

    public void initialize(Stage stage, SimController controller,
                           SimulationConfig config, Simulation simulation, Grid grid) {
        this.stage = stage;
        initializeSplashScreen(stage, controller);
    }

    private void initializeSplashScreen(Stage stage, SimulationController controller) {
        SplashScreen screen = new SplashScreen();
        Stage splashStage = screen.showSplashScreen(
                new Stage(),
                config.getString("splash.title"),
                Integer.parseInt(config.getString("window.width")),
                Integer.parseInt(config.getString("window.height"))
        );

        setupSplashScreenControls(screen, splashStage, stage, controller);
    }

    private void setupSplashScreenControls(SplashScreen screen, Stage splashStage,
                                           Stage mainStage, SimulationController controller) {
        ComboBox<String> languageSelector = screen.makeComboBox(
                config.getString("language.selector"),
                Arrays.asList(config.getString("available.languages").split(","))
        );

        ComboBox<String> themeSelector = screen.makeComboBox(
                config.getString("theme.selector"),
                Arrays.asList(config.getString("available.themes").split(","))
        );

        Button enterButton = screen.makeEnterButton();
        enterButton.setOnAction(e -> handleSplashScreenEnter(
                languageSelector, themeSelector, splashStage, mainStage, controller));
    }

    private void handleSplashScreenEnter(ComboBox<String> languageSelector,
                                         ComboBox<String> themeSelector,
                                         Stage splashStage, Stage mainStage,
                                         SimulationController controller) {
        try {
            String selectedLanguage = Optional.ofNullable(languageSelector.getValue())
                    .orElseThrow(() -> new IllegalStateException("Language not selected"));
            String selectedTheme = Optional.ofNullable(themeSelector.getValue())
                    .orElseThrow(() -> new IllegalStateException("Theme not selected"));

            resources = ResourceBundle.getBundle(
                    SimulationController.DEFAULT_RESOURCE_PACKAGE + selectedLanguage);
            splashStage.close();
            setupSimulation(mainStage, selectedLanguage, selectedTheme, controller);
        } catch (Exception ex) {
            handleError("SetupError", ex);
        }
    }

    private void setupSimulation(Stage stage, String language, String themeColor,
                                 SimulationController controller) {
        try {
            Simulation simulation = controller.getEngine().getSimulation();
            Grid grid = controller.getEngine().getGrid();
            SimulationConfig config = controller.getEngine().getConfig();

            simulationView = new SimulationView(config, controller, resources);
            simulationView.initView(stage, simulation, simulationView,
                    simulation.getColorMap(), grid, language, themeColor);
        } catch (Exception e) {
            handleError("SetupError", e);
        }
    }

    public void updateView(Map<Point2D, Color> colorMap) {
        simulationView.updateGrid(colorMap);
    }

    public void handleError(String key, Exception e) {
        Platform.runLater(() -> {
            String title = getResourceString("Error");
            String message = String.format("%s: %s",
                    getResourceString(key),
                    e.getMessage());
            displayAlert(title, message);
        });
    }

    public void displayAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String getResourceString(String key) {
        return Optional.ofNullable(resources)
                .map(r -> r.getString(key))
                .orElse(key);
    }

    // Getters
    public ResourceBundle getResources() { return resources; }
}
