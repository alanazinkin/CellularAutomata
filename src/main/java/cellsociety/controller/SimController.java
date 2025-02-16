package cellsociety.controller;

import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SimController {
    private static final ResourceBundle CONFIG = ResourceBundle.getBundle(
            SimController.class.getPackageName() + ".Simulation");
    public static final String DEFAULT_RESOURCE_PACKAGE =
            SimController.class.getPackageName() + ".";

    private final SimulationUI ui;
    private final SimulationEngine engine;
    private final SimulationFileManager fileManager;
    private final SimulationParameters parameters;
    private Stage stage;
    private ResourceBundle resources;
    private SimController myController;

    public SimController() {
        this.stage = new Stage();
        this.parameters = SimulationParameters.fromConfig();
        this.engine = new SimulationEngine(CONFIG);
        this.ui = new SimulationUI(CONFIG);
        this.fileManager = new SimulationFileManager();
        this.myController = this;
    }

    public void selectSimulation(String simulationType, String fileName, Stage stage,
                                 SimulationController controller) throws Exception {
        this.stage = stage;
        engine.pause();
        fileManager.loadFile(simulationType, fileName);
        init(stage, controller);
    }

    public void init(Stage stage, SimulationController controller) throws Exception {
        SimulationConfig config = fileManager.parseConfiguration();
        engine.initializeSimulation(config, parameters);
        ui.initialize(stage, this, config, engine.getSimulation(), engine.getGrid());
    }

    public void stepSimulation(double elapsedTime) {
        try {
            engine.step();
            ui.updateView(engine.getSimulation().getColorMap());
        } catch (Exception e) {
            ui.handleError("StepError", e);
            pauseSimulation();
        }
    }

    public void startSimulation() {
        engine.start();
    }

    public void pauseSimulation() {
        engine.pause();
    }

    public void resetGrid() {
        try {
            engine.resetGrid();
            ui.updateView(engine.getSimulation().getColorMap());
        } catch (Exception e) {
            ui.handleError("ResetError", e);
        }
    }

    public void setSimulationSpeed(double speed) {
        engine.setSpeed(speed);
    }

    public void saveSimulation() {
        fileManager.saveSimulation(stage, ui.getResources(), engine.getConfig(), engine.getGrid());
    }

    public static Map<String, String> retrieveImmutableConfigResourceBundle() {
        return convertResourceBundletoImmutableMap(CONFIG);
    }

    private static Map<String, String> convertResourceBundletoImmutableMap(ResourceBundle bundle) {
        Map<String, String> copy = new HashMap<>();
        for (String key : bundle.keySet()) {
            copy.put(key, bundle.getString(key));
        }
        return Collections.unmodifiableMap(copy);
    }

    public Simulation getSimulation() {
        return engine.getSimulation();
    }

    public Grid getGrid() {
        return engine.getGrid();
    }

    public SimulationConfig getSimulationConfig() {
        return engine.getConfig();
    }
}
