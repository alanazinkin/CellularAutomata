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
    private Map<String, String> parameters;
    private Map<StateInterface, Color> colorMap;

    public SimulationConfig(String type, String title, String author, String description,
                            int width, int height, int[] initialStates, Map<String, String> parameters) {
        this.type = type;
        this.title = title;
        this.author = author;
        this.description = description;
        this.width = width;
        this.height = height;
        this.initialStates = initialStates;
        this.parameters = parameters;
    }

    public void initializeStage(Stage primaryStage) {
        Group root = new Group();
        Scene myScene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.WHITE);
        primaryStage.setScene(myScene);
        primaryStage.setTitle(TITLE);
        primaryStage.show();
    }

    /**
     * wrapper method is the starting point of the simulation
     */
    public void init(Stage primaryStage, SimulationConfig simulationConfig) {
        initializeStage(primaryStage);
        Simulation simulation = new GameOfLife(new Grid(width, height, GameOfLifeState.ALIVE));
        SimulationView mySimView = new SimulationView();
        mySimView.initView(primaryStage, simulationConfig, simulation, mySimView);
    }

    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int[] getInitialStates() { return initialStates; }
    public Map<String, String> getParameter() { return parameters; }

}
