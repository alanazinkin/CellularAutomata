package cellsociety.Controller;

import cellsociety.View.ControlPanel;
import cellsociety.View.GridViews.GameOfLifeGridView;
import cellsociety.View.SimulationInfoPanel;
import cellsociety.View.SimulationView;
import java.util.ArrayList;
import java.util.List;
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
    private SimulationParameter parameter;

    public SimulationConfig(String type, String title, String author, String description,
                            int width, int height, int[] initialStates, SimulationParameter parameter) {
        this.type = type;
        this.title = title;
        this.author = author;
        this.description = description;
        this.width = width;
        this.height = height;
        this.initialStates = initialStates;
        this.parameter = parameter;
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
    public void initSimulation(Stage primaryStage) {
        //TODO: get rid of dummy data and pull from XML file
        List<List<String>> parameters = new ArrayList<>();
        ArrayList<String> firstParameter = new ArrayList<>();
        firstParameter.add("probCatch");
        firstParameter.add("0.5");
        parameters.add(firstParameter);
        List<List<String>> stateColors = new ArrayList<>();
        List<String> firstState = new ArrayList<>();
        firstState.add("dead");
        firstState.add("blue");
        stateColors.add(firstState);
        SimulationInfoPanel mySimInfoPanel = new SimulationInfoPanel("Fire", "Catching Fire", "alana", "this sim...", parameters, stateColors);
        Stage window = new Stage();
        mySimInfoPanel.createDisplayBox(window, "myInfoBox");
        SimulationView mySimView = new SimulationView();
        mySimView.createSimulationWindow(primaryStage);
        ControlPanel myControlPanel = new ControlPanel();
        myControlPanel.makeControlBar(mySimView.getRoot());
        myControlPanel.makeSliderBar(mySimView.getRoot());
        // create Grid
        GameOfLifeGridView myGridView = new GameOfLifeGridView();
        myGridView.createGridDisplay(mySimView.getRoot());
    }

    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int[] getInitialStates() { return initialStates; }
    public SimulationParameter getParameter() { return parameter; }

}
