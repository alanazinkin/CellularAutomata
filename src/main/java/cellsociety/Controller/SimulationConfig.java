package cellsociety.Controller;

import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.Simulations.GameOfLife;
import cellsociety.Model.State.GameOfLifeState;
import cellsociety.Model.StateInterface;
import cellsociety.View.SimulationView;
import java.util.Map;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SimulationConfig {
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private static final String TITLE = "Cell Society";

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
     * set the JavaFX stage for the application
     * @param primaryStage main stage onto which all elements are added
     */
    public Scene initializeStage(Stage primaryStage) {
        Group root = new Group();
        Scene myScene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.WHITE);
        primaryStage.setScene(myScene);
        primaryStage.setTitle(TITLE);
        primaryStage.show();
        return myScene;
    }

    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int[] getInitialStates() { return initialStates; }
    public Map<String, Double> getParameters() { return parameters; }

    public void setSimulationType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWidth(int i) {
        width = i;
    }

    public void setHeight(int i) {
        height = i;
    }

    public void setParameters(Map<String, Double> stringDoubleMap) {
        parameters = stringDoubleMap;
    }

    public void setInitialStates(int[] initialStates) {
        this.initialStates = initialStates;
    }
}
