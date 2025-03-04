package cellsociety.view.gridview;

import cellsociety.model.StateInterface;
import cellsociety.model.state.PercolationState;
import cellsociety.view.ControlPanel;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import java.util.List;
import javafx.scene.layout.Pane;
import static org.junit.jupiter.api.Assertions.*;

import cellsociety.controller.SimulationConfig;
import cellsociety.controller.SimulationController;
import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.simulations.GameOfLife;
import cellsociety.model.state.GameOfLifeState;
import cellsociety.view.SimulationView;
import cellsociety.view.UserController;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

class GridViewTest extends DukeApplicationTest {
  // [UnitOfWork_StateUnderTest_ExpectedBehavior]
  int WINDOW_WIDTH = 1000;
  int WINDOW_HEIGHT = 800;

  private Map<String, Double> myParameters = new HashMap<>();
  private int[] myInitialStates = new int[]{0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1};
  private SimulationConfig mySimulationConfig;
  private ResourceBundle DEFAULT_LANGUAGE_BUNDLE = ResourceBundle.getBundle("cellsociety.controller.English");
  private Map<String, String> mySimulationResourceMap = SimulationController.retrieveImmutableConfigResourceBundle();
  private SimulationController myController;
  private Simulation mySimulation;
  private UserController myUserController;
  private SimulationView mySimulationView;
  private Grid myGrid;

  private BorderPane myRoot;
  private Scene myScene;
  private Stage myStage;

  @Override
  public void start (Stage stage) {
    mySimulationConfig = new SimulationConfig("Game of Life", "title", "Alana Zinkin", "Description",
        5, 5, myInitialStates, myParameters,"Default");
    myGrid = new Grid(5, 5, GameOfLifeState.ALIVE);
    mySimulation = new GameOfLife(mySimulationConfig, myGrid);
    myController = new SimulationController();
    myUserController = new UserController(DEFAULT_LANGUAGE_BUNDLE, myController);
    mySimulationView = new SimulationView(mySimulationConfig, myController, DEFAULT_LANGUAGE_BUNDLE);
    mySimulationView.createSimulationWindow(stage);
    myRoot = mySimulationView.getRoot();
    myScene = mySimulationView.getScene();
  }

  @Test
  public void makeGridLinesToggleButton_makeButton_ButtonDisplayed() {
    GridView gridView = new DefaultGridView(myController, mySimulationConfig, myGrid);
    Button gridLinesToggleButton = myUserController.makeGridLinesToggleButton("Gridlines");
    myUserController.setGridLinesButtonAction(gridView, gridLinesToggleButton);
    assertTrue(gridLinesToggleButton.isVisible());
  }

  @Test
  public void makeGridLinesToggleButton_clickButtonOnce_GridLinesRemoved() {
    SimulationConfig simulationConfig = new SimulationConfig("Game of Life", "title", "Alana Zinkin", "Description",
        5, 5, myInitialStates, myParameters,"Default");
    myController = new SimulationController();
    GridView gridView = new DefaultGridView(myController, simulationConfig, myGrid);
    Button gridLinesToggleButton = myUserController.makeGridLinesToggleButton("Gridlines");
    myUserController.setGridLinesButtonAction(gridView, gridLinesToggleButton);
    interact(() -> {
      myRoot.getChildren().add(gridLinesToggleButton);
      try {
        gridView.createGridDisplay(myRoot, mySimulation.getColorMap(), simulationConfig);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
        throw new RuntimeException(e);
      } catch (NoSuchMethodException e) {
        throw new RuntimeException(e);
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
      clickOn(gridLinesToggleButton);
    });
    for (Shape cell: gridView.getImmutableCellsList()) {
      assertEquals(0, cell.getStrokeWidth());
    }
  }


  @Test
  public void makeGridLinesToggleButton_clickButtonTwice_GridLinesPresent() {
    GridView gridView = new DefaultGridView(myController, mySimulationConfig, myGrid);
    Button gridLinesToggleButton = myUserController.makeGridLinesToggleButton("Gridlines");
    myUserController.setGridLinesButtonAction(gridView, gridLinesToggleButton);
    interact(() -> {
      myRoot.getChildren().add(gridLinesToggleButton);
      try {
        gridView.createGridDisplay(myRoot, mySimulation.getColorMap(), mySimulationConfig);
      } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
               InstantiationException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
      clickOn(gridLinesToggleButton);
      clickOn(gridLinesToggleButton);
    });
    for (Shape cell: gridView.getImmutableCellsList()) {
      assertEquals(1, cell.getStrokeWidth());
    }
  }


  @Test
  public void createGridView_HexagonViewSelected_GridNotNull()
      throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

    int[] initialStates = new int[]{2, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 1};
    Map<String, Double> parameter = new HashMap<>();
    parameter.put("percolationProb", 0.7);
    mySimulationConfig = new SimulationConfig("Percolation", "Basic Percolation", "Alana", "Description",
        6, 6, initialStates, parameter, "Hexagon");
    myGrid = new Grid(6, 6, PercolationState.PERCOLATED);
    int numCols = myGrid.getCols();
    int numRows = myGrid.getRows();
    double expectedCellWidth = (0.7 * 800 / numCols);
    double expectedCellHeight = (0.7 * 600 / numRows);
    runAsJFXAction(() -> {
      GridView gridView = new HexagonGridView(myController, mySimulationConfig, myGrid);
      try {
        gridView.createGridDisplay(myRoot, mySimulation.getColorMap(), mySimulationConfig);
        assertEquals(expectedCellWidth, gridView.getCellWidth());
        assertEquals(expectedCellHeight, gridView.getCellHeight());
        List<Shape> cells =  gridView.getImmutableCellsList();
        assertEquals(36, cells.size());
        Pane pane = gridView.getGridPane();
        for (Shape shape : cells) {
          assert(pane.getChildren().contains(shape));
        }
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
        throw new RuntimeException(e);
      } catch (NoSuchMethodException e) {
        throw new RuntimeException(e);
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Test
  public void createGridView_ParallelogramViewSelected_GridNotNull()
      throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    int[] initialStates = new int[]{2, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 1};
    Map<String, Double> parameter = new HashMap<>();
    parameter.put("percolationProb", 0.7);
    mySimulationConfig = new SimulationConfig("Percolation", "Basic Percolation", "Alana", "Description",
        6, 6, initialStates, parameter, "Parallelogram");
    myGrid = new Grid(6, 6, PercolationState.PERCOLATED);
    int numCols = myGrid.getCols();
    int numRows = myGrid.getRows();
    double expectedCellWidth = (0.7 * 800 / numCols);
    double expectedCellHeight = (0.7 * 600 / numRows);
    runAsJFXAction(() -> {
      GridView gridView = new ParallelogramGridView(myController, mySimulationConfig, myGrid);
      try {
        gridView.createGridDisplay(myRoot, mySimulation.getColorMap(), mySimulationConfig);
        assertEquals(expectedCellWidth, gridView.getCellWidth());
        assertEquals(expectedCellHeight, gridView.getCellHeight());
        List<Shape> cells =  gridView.getImmutableCellsList();
        assertEquals(36, cells.size());
        Pane pane = gridView.getGridPane();
        for (Shape shape : cells) {
          assert(pane.getChildren().contains(shape));
        }
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
        throw new RuntimeException(e);
      } catch (NoSuchMethodException e) {
        throw new RuntimeException(e);
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Test
  public void createGridView_TriangleViewSelected_GridNotNull()
      throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    int[] initialStates = new int[]{2, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 1};
    Map<String, Double> parameter = new HashMap<>();
    parameter.put("percolationProb", 0.7);
    mySimulationConfig = new SimulationConfig("Percolation", "Basic Percolation", "Alana", "Description",
        6, 6, initialStates, parameter, "Triangle");
    myGrid = new Grid(6, 6, PercolationState.PERCOLATED);
    int numCols = myGrid.getCols();
    int numRows = myGrid.getRows();
    double expectedCellWidth = ((double) 800 / numCols);
    double expectedCellHeight = ((double) 600 / numRows);
    runAsJFXAction(() -> {
      GridView gridView = new TriangleGridView(myController, mySimulationConfig, myGrid);
      try {
        gridView.createGridDisplay(myRoot, mySimulation.getColorMap(), mySimulationConfig);
        assertEquals(expectedCellWidth, gridView.getCellWidth());
        assertEquals(expectedCellHeight, gridView.getCellHeight());
        List<Shape> cells =  gridView.getImmutableCellsList();
        assertEquals(36, cells.size());
        Pane pane = gridView.getGridPane();
        for (Shape shape : cells) {
          assert(pane.getChildren().contains(shape));
        }
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
        throw new RuntimeException(e);
      } catch (NoSuchMethodException e) {
        throw new RuntimeException(e);
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    });
  }

}