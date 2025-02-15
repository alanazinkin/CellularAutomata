package cellsociety.controller;

import cellsociety.model.StateInterface;
import java.util.Map;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Represents the configuration for a simulation, including metadata,
 * grid dimensions, initial states, and simulation parameters.
 * @author Angela Predolac
 */
public class SimulationConfig {
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private static final String TITLE = "Cell Society";

    private Scene myScene;
    private String type;
    private String title;
    private String author;
    private String description;
    private int width;
    private int height;
    private int[] initialStates;
    private Map<String, Double> parameters;
    private Map<StateInterface, Color> colorMap;

    /**
     * Default constructor for XML parsing
     */
    public SimulationConfig() {
        // Default constructor needed for XML parsing
    }

    /**
     * Simulation Configuration constructor method
     * @param type type of simulation (ex: Fire, GameofLife, etc.)
     * @param title title full name of the simulation (ex: Spreading Fire, Game of Life, etc.)
     * @param author creator of the XML file
     * @param description description explanation of the simulation type
     * @param width number of rows of cells
     * @param height number of columns of cells
     * @param initialStates starting cell states
     * @param parameters inputs to the simulation (ex: probCatch in Spreading Fire)
     */
    public SimulationConfig(String type, String title, String author, String description,
                            int width, int height, int[] initialStates, Map<String, Double> parameters) {
        this.type = type;
        this.title = title;
        this.author = author;
        this.description = description;
        this.width = width;
        this.height = height;
        this.initialStates = initialStates;
        this.parameters = parameters;
    }

    /**
     * Initializes the JavaFX stage for the simulation.
     *
     * @param stage The main JavaFX stage to display the simulation.
     * @return The initialized JavaFX scene.
     */
    public Scene initializeStage(Stage stage) {
        Group root = new Group();
        myScene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.WHITE);
        stage.setScene(myScene);
        stage.setTitle(TITLE);
        stage.show();
        return myScene;
    }

    // Getters

    /** @return The type of simulation. */
    public String getType() { return type; }
    /** @return The title of the simulation. */
    public String getTitle() { return title; }
    /** @return The author of the simulation configuration. */
    public String getAuthor() { return author; }
    /** @return The description of the simulation. */
    public String getDescription() { return description; }
    /** @return The width of the simulation grid. */
    public int getWidth() { return width; }
    /** @return The height of the simulation grid. */
    public int getHeight() { return height; }
    /** @return The initial states of cells in the simulation. */
    public int[] getInitialStates() { return initialStates; }
    /** @return The parameters for the simulation. */
    public Map<String, Double> getParameters() { return parameters; }

    // Setters

    /**
     * Sets the type of simulation.
     * @param type The new simulation type.
     */
    public void setSimulationType(String type) {
        this.type = type;
    }

    /**
     * Sets the title of the simulation.
     * @param title The new title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the author of the simulation configuration.
     * @param author The author's name.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Sets the description of the simulation.
     * @param description The new description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the width of the simulation grid.
     * @param i The new width.
     */
    public void setWidth(int i) {
        width = i;
    }

    /**
     * Sets the height of the simulation grid.
     * @param i The new height.
     */
    public void setHeight(int i) {
        height = i;
    }

    /**
     * Sets the simulation parameters.
     * @param stringDoubleMap A map of parameter names to values.
     */
    public void setParameters(Map<String, Double> stringDoubleMap) {
        parameters = stringDoubleMap;
    }

    /**
     * Sets the initial states of cells in the simulation.
     * @param initialStates The new array of initial states.
     */
    public void setInitialStates(int[] initialStates) {
        this.initialStates = initialStates;
    }
}
