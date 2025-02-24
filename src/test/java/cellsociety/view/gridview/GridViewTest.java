package cellsociety.view.gridview;

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

  private Grid myGrid = new Grid(5, 5, GameOfLifeState.ALIVE);
  private Map<String, Double> myParameters = new HashMap<>();
  private int[] myInitialStates = new int[]{0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1};
  private SimulationConfig mySimulationConfig = new SimulationConfig("Game of Life", "title", "Alana Zinkin", "Description",
      5, 5, myInitialStates, myParameters);
  private ResourceBundle DEFAULT_LANGUAGE_BUNDLE = ResourceBundle.getBundle("cellsociety.controller.English");
  private Map<String, String> mySimulationResourceMap;
  private SimulationController myController;
  private UserController myUserController = new UserController(DEFAULT_LANGUAGE_BUNDLE, new SimulationController());
  Simulation mySimulation = new GameOfLife(mySimulationConfig, myGrid);
  private SimulationView mySimulationView = new SimulationView(mySimulationConfig, myController, DEFAULT_LANGUAGE_BUNDLE);
  private BorderPane myRoot;
  private Scene myScene;
  private Stage myStage;

  @Override
  public void start (Stage stage) {
    mySimulationView.createSimulationWindow(stage);
    myRoot = mySimulationView.getRoot();
    myScene = mySimulationView.getScene();
  }

  @Test
  public void makeGridLinesToggleButton_makeButton_ButtonDisplayed() {
    GridView gridView = new DefaultGridView(myController, mySimulationConfig, myGrid);
    Button gridLinesToggleButton = myUserController.makeGridLinesToggleButton("Gridlines", gridView);
    assertTrue(gridLinesToggleButton.isVisible());
  }

  @Test
  public void makeGridLinesToggleButton_clickButtonOnce_GridLinesRemoved() {
    GridView gridView = new DefaultGridView(myController, mySimulationConfig, myGrid);
    Button gridLinesToggleButton = myUserController.makeGridLinesToggleButton("Gridlines", gridView);
    interact(() -> {
      myRoot.getChildren().add(gridLinesToggleButton);
      try {
        gridView.createGridDisplay(myRoot, mySimulation.getColorMap());
      } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
               InstantiationException | IllegalAccessException e) {
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
    Button gridLinesToggleButton = myUserController.makeGridLinesToggleButton("Gridlines", gridView);
    interact(() -> {
      myRoot.getChildren().add(gridLinesToggleButton);
      try {
        gridView.createGridDisplay(myRoot, mySimulation.getColorMap());
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
  void createGridDisplay() {
  }

  @Test
  void renderGrid() {
  }

  @Test
  void updateCellColors() {
  }

  @Test
  void setGridLinesToggleButtonAction() {
  }

  @Test
  void renderGridFlippedVertically_ClickButtonTwice_GridFlipsVerticallyAndBack() {
    GridView gridView = new DefaultGridView(myController, mySimulationConfig, myGrid);
    runAsJFXAction(() -> {
      try {
        gridView.createGridDisplay(myRoot, mySimulation.getColorMap());
      } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
               InstantiationException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    });
    Button flipGridButton = myUserController.makeFlipGridButton("Flip Grid", gridView);
    runAsJFXAction(() -> myUserController.addElementToPane(flipGridButton, myRoot));
    runAsJFXAction(() -> clickOn(flipGridButton));
    // Capture flipped positions
    boolean isFlippedCorrectly = true;
    int numRows = myGrid.getRows();
    int numCols = myGrid.getCols();
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        int expectedRow = numRows - i - 1;
        Shape cell = gridView.getImmutableCellsList().get(i * numCols + j);
        Integer actualRow = GridPane.getRowIndex(cell);
        Integer actualCol = GridPane.getColumnIndex(cell);
        if (actualRow == null || actualCol == null || actualRow != expectedRow || actualCol != j) {
          isFlippedCorrectly = false;
          break;
        }
      }
    }
    assertTrue(isFlippedCorrectly, "Grid should be flipped vertically");
    runAsJFXAction(() -> clickOn(flipGridButton));
    boolean isFlippedBack = true;
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        Shape cell = gridView.getImmutableCellsList().get(i * numCols + j);
        Integer actualRow = GridPane.getRowIndex(cell);
        Integer actualCol = GridPane.getColumnIndex(cell);
        if (actualRow == null || actualCol == null || actualRow != i || actualCol != j) {
          isFlippedBack = false;
          break;
        }
      }
    }
    assertTrue(isFlippedBack, "Grid should be flipped back to its original position");
  }

}