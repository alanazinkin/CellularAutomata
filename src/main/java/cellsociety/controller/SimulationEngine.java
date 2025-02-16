package cellsociety.controller;

import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.state.GameOfLifeState;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ResourceBundle;

public class SimulationEngine {
    private final Timeline timeline;
    private final ResourceBundle config;
    private Simulation simulation;
    private Grid grid;
    private SimulationConfig simulationConfig;
    private Stage stage;

    public SimulationEngine(ResourceBundle config) {
        this.config = config;
        this.timeline = initializeTimeline();
    }

    private Timeline initializeTimeline() {
        double framesPerSecond = Double.parseDouble(config.getString("frames.per.second"));
        double secondDelay = 1.0 / framesPerSecond;

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(secondDelay),
                        e -> step())
        );
        return timeline;
    }

    public void initializeSimulation(SimulationConfig config, SimulationParameters params) {
        this.simulationConfig = config;
        this.grid = new Grid(config.getWidth(), config.getHeight(), GameOfLifeState.ALIVE);
        this.simulation = SimulationFactory.createSimulation(config.getType(), config, grid, params);
    }

    public void step() {
        simulation.step();
    }

    public void resetGrid() {
        simulation.reinitializeGridStates(simulationConfig);
    }

    public void start() {
        timeline.play();
    }

    public void pause() {
        timeline.pause();
    }

    public void setSpeed(double speed) {
        double minSpeed = Double.parseDouble(config.getString("min.speed"));
        double maxSpeed = Double.parseDouble(config.getString("max.speed"));
        double clampedSpeed = Math.max(minSpeed, Math.min(maxSpeed, speed));
        timeline.setRate(clampedSpeed);
    }

    // Getters
    public SimulationConfig getConfig() { return simulationConfig; }
    public Grid getGrid() { return grid; }
    public Simulation getSimulation() { return simulation; }
}