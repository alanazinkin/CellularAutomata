package cellsociety.controller;

import cellsociety.model.EdgeStrategyFactory;
import cellsociety.model.Grid;
import cellsociety.model.NeighborhoodFactory;
import cellsociety.model.Simulation;
import cellsociety.model.state.MockState;
import java.lang.reflect.InvocationTargetException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ResourceBundle;

/**
 * The {@code SimulationEngine} class manages the execution and control of a simulation.
 * It handles the simulation's timeline, step progression, and interactions with the UI.
 *
 * @author angelapredolac
 */
public class SimulationEngine {
    private final Timeline timeline;
    private final ResourceBundle config;
    private Simulation simulation;
    private Grid grid;
    private SimulationConfig simulationConfig;
    private Stage stage;
    private SimulationController simulationController;
    private SimulationUI simulationUI;

    /**
     * Constructs a {@code SimulationEngine} with the provided configuration.
     *
     * @param config The resource bundle containing simulation settings.
     */
    public SimulationEngine(ResourceBundle config) {
        this.config = config;
        this.timeline = initializeTimeline();
    }

    /**
     * Initializes the simulation timeline based on the frame rate specified in the configuration.
     *
     * @return A configured {@code Timeline} object that updates the simulation at regular intervals.
     */
    private Timeline initializeTimeline() {
        double framesPerSecond = Double.parseDouble(config.getString("frames.per.second"));
        double secondDelay = 1.0 / framesPerSecond;

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(secondDelay),
                        e -> {
                          try {
                            step();
                          } catch (ClassNotFoundException | NoSuchMethodException |
                                   InvocationTargetException | InstantiationException |
                                   IllegalAccessException ex) {
                            SimulationUI.displayAlert("Error", ex.getMessage());
                          }
                        })
        );
        return timeline;
    }

    /**
     * Initializes the simulation with the provided configuration and controller.
     *
     * @param config               The simulation configuration.
     * @param simulationController The controller managing the simulation.
     */
    public void initializeSimulation(SimulationConfig config, SimulationController simulationController) {
        this.simulationConfig = config;
        this.grid = new Grid(config.getWidth(), config.getHeight(), MockState.STATE_TWO);
        this.simulation = SimulationFactory.createSimulation(config.getType(), config, grid);
        this.simulationController = simulationController;
        this.simulationUI = simulationController.getUI();
    }

    /**
     * Advances the simulation by one step and updates the UI.
     *
     * @throws ClassNotFoundException    If the simulation class cannot be found.
     * @throws NoSuchMethodException     If a required method is not found.
     * @throws InvocationTargetException If an invoked method throws an exception.
     * @throws InstantiationException    If the simulation instance cannot be created.
     * @throws IllegalAccessException    If access to a method is denied.
     */
    public void step()
        throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        simulation.step();
        simulationUI.updateView(getSimulation().getColorMap(), getSimulation().getStateCounts());
    }

    /**
     * Reverts the simulation by one step and updates the UI.
     *
     * @throws ClassNotFoundException    If the simulation class cannot be found.
     * @throws NoSuchMethodException     If a required method is not found.
     * @throws InvocationTargetException If an invoked method throws an exception.
     * @throws InstantiationException    If the simulation instance cannot be created.
     * @throws IllegalAccessException    If access to a method is denied.
     */
    public void stepBackOnce()
        throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        boolean applied = simulation.stepBackOnce();
        if (applied) {
            simulationUI.updateView(getSimulation().getColorMap(), getSimulation().getStateCounts());
        }
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

    /**
     * Adjusts the simulation speed, clamping it within the configured minimum and maximum values.
     *
     * @param speed The desired speed multiplier.
     */
    public void setSpeed(double speed) {
        double minSpeed = Double.parseDouble(config.getString("min.speed"));
        double maxSpeed = Double.parseDouble(config.getString("max.speed"));
        double clampedSpeed = Math.max(minSpeed, Math.min(maxSpeed, speed));
        timeline.setRate(clampedSpeed);
    }


    public void setEdgeStrategy(String type) {
        //no checking for validation here
        grid.setEdgeStrategy(EdgeStrategyFactory.createEdgeStrategy(type));
    }

    public void setNeighborhoodStrategy(String type) {
        //no checking for validation here
        grid.setNeighborhoodStrategy(NeighborhoodFactory.createNeighborhoodStrategy(type));
    }

    // Getters
    public SimulationConfig getConfig() { return simulationConfig; }
    public Grid getGrid() { return grid; }
    public Simulation getSimulation() { return simulation; }

    public void setGrid(Grid infiniteGrid) {
        grid = infiniteGrid;
    }
}