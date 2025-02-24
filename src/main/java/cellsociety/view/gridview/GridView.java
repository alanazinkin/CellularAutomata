package cellsociety.view.gridview;
import cellsociety.controller.SimulationConfig;

import static java.lang.Integer.parseInt;
import cellsociety.controller.SimulationController;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.StateInterface;
import cellsociety.view.shapefactory.CellShape;
import cellsociety.view.shapefactory.CellShapeFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public abstract class GridView {

  private static final int SLIDER_BAR_HEIGHT = 150;
  private boolean hasGridLines = true;

  private Grid myGrid;
  private List<Shape> myCells;
  private GridPane gridPane;
  private SimulationController myController;
  private Map<String, String> myConfigResourceMap;

  int numRows;
  int numCols;
  int cellWidth;
  int cellHeight;
  boolean flipped = false;

  /**
   * Constructor for creating a GridView object, which is responsible for creating and updating the
   * visuals of the grid of cells.
   *
   * @param simulationController simulation controller responsible for managing the simulation and
   *                             merging the back-end and front-end
   * @param simulationConfig     configuration object containing relevant simulation details such as
   *                             type, title, description etc.
   * @param grid                 the grid of cells of the simulation
   */
  public GridView(SimulationController simulationController, SimulationConfig simulationConfig,
      Grid grid) {
    myController = simulationController;
    myConfigResourceMap = SimulationController.retrieveImmutableConfigResourceBundle();
    myGrid = grid;
    numRows = simulationConfig.getWidth();
    numCols = simulationConfig.getHeight();
    cellWidth = parseInt(myConfigResourceMap.getOrDefault("window.width", "1000")) / numRows;
    cellHeight =
        (parseInt(myConfigResourceMap.getOrDefault("window.height", "800")) - SLIDER_BAR_HEIGHT)
            / numCols;
    gridPane = new GridPane();
  }

  /**
   * creates a new pane to hold the grid and instantiates myCells and myGrid
   *
   * @param myRoot
   * @param colorMap
   */
  public void createGridDisplay(BorderPane myRoot, Map<StateInterface, String> colorMap, SimulationConfig simulationConfig)
      throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    myRoot.setCenter(gridPane);
    gridPane.setMaxWidth(parseInt(myConfigResourceMap.getOrDefault("window.width", "1000")));
    gridPane.setMaxHeight(
        parseInt(myConfigResourceMap.getOrDefault("window.height", "800")) - SLIDER_BAR_HEIGHT);
    gridPane.setGridLinesVisible(true);
    myCells = new ArrayList<>();
    renderGrid(colorMap, simulationConfig);
  }

  /**
   * Creates the visual display of grid cells based on the Grid object and organizes them in the
   * view
   *
   * @param colorMap mapping of state interfaces to colors to visually render each cell according to
   *                 its state value
   */
  public void renderGrid(Map<StateInterface, String> colorMap, SimulationConfig simulationConfig)
      throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    gridPane.getChildren().clear();
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        Cell cell = myGrid.getCell(i, j);
        addCellShapeToGridView(colorMap, simulationConfig, cell, j, i);
      }
    }
  }

  private void addCellShapeToGridView(Map<StateInterface, String> colorMap, SimulationConfig simulationConfig, Cell cell, int j, int i)
      throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    StateInterface cellState = cell.getCurrentState();
    Shape shape = makeCellShapeFromState(cellState, simulationConfig);
    shape.setId(colorMap.get(cellState));
    shape.setStroke(Color.BLACK);
    shape.setStrokeWidth(1);
    myCells.add(i * numCols + j, shape);
    gridPane.add(shape, j, i);
  }


  private Shape makeCellShapeFromState(StateInterface cellState, SimulationConfig simulationConfigconfig)
      throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    String factoryClassName = getCellFactoryName(cellState, simulationConfigconfig);
    String fullFactoryClassName = "cellsociety.view.shapefactory." + factoryClassName;

    // Create factory instance using reflection
    Class<?> factoryClass = Class.forName(fullFactoryClassName);
    CellShapeFactory factory = (CellShapeFactory) factoryClass.getDeclaredConstructor().newInstance();

    // Create the cell shape using the factory
    CellShape cellShape = factory.createCellShape(cellWidth, cellHeight);

    Shape shape = cellShape.getShape();
    return shape;
  }

  private String getCellFactoryName(StateInterface cellState, SimulationConfig simulationConfig) {
    int cellStateNumericValue = cellState.getNumericValue();
    //use reflection here to grab cell Shape
    Map<Integer, String> cellShapeMap = simulationConfig.getCellShapeMap();
    if (cellShapeMap == null) {
      throw new NullPointerException("Cell shape map is null");
    }
    // Get the shape type from the map (e.g., "Rectangle" or "Triangle")
    String shapeType = cellShapeMap.getOrDefault(cellStateNumericValue, "Rectangle");
    // Construct the factory class name
    String factoryClassName = shapeType + "CellFactory";
    return factoryClassName;
  }

  /**
   * sets the action call of the grid lines toggle button
   * @param gridView the gridview object that will be changed by the button press
   * @param toggleButton the toggle button
   */
  public void setGridLinesToggleButtonAction(GridView gridView, Button toggleButton) {
    toggleButton.setOnAction(e -> {
      if (hasGridLines) {
        gridView.removeGridLines();
        hasGridLines = false;
      } else {
        gridView.addGridLines();
        hasGridLines = true;
      }
    });
  }

  /**
   * visually removes the grid lines y setting stroke outline of cells to 0 pixels
   */
  private void removeGridLines() {
    for (Shape shape : myCells) {
      shape.setStrokeWidth(0);
    }
  }

  /**
   * visually adds the grid lines by setting stroke outline of cells to 1 pixels
   */
  private void addGridLines() {
    for (Shape shape : myCells) {
      shape.setStrokeWidth(1);
    }
  }

  /**
   * if the grid is not flipped, it flips it over the X-axis
   * and flips it back otherwise
   *
   */
  public void renderGridFlippedVertically() {
    gridPane.getChildren().clear(); // Clear the GridPane but keep myCells
    if (!flipped) {
      applyGridFlip();
    } else {
      flipGridBack();
    }
    flipped = !flipped;
  }

  private void applyGridFlip() {
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        Shape rectCell = myCells.get(i * numCols + j);
        gridPane.add(rectCell, j, numRows - i - 1);
      }
    }
  }

  private void flipGridBack() {
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        Shape rectCell = myCells.get(i * numCols + j);
        gridPane.add(rectCell, j, i);
      }
    }
  }

  /**
   * retrieves myCells instance variable holding all the grid cells
   *
   * @return immutable myCells list
   */
  public List<Shape> getImmutableCellsList() {
    return Collections.unmodifiableList(myCells);
  }
}
