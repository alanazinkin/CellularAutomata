package cellsociety.view.gridview;
import cellsociety.controller.SimulationConfig;

import static java.lang.Integer.parseInt;
import cellsociety.controller.SimulationController;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.StateInterface;
import cellsociety.view.SimulationInfoDisplay;
import cellsociety.view.shapefactory.CellShape;
import cellsociety.view.shapefactory.CellShapeFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import javafx.stage.Popup;
import javafx.util.Duration;

public abstract class GridView {
  private boolean hasGridLines = true;

  private Grid myGrid;
  private List<Shape> myCells;
  private GridPane gridPane;
  private Pane zoomPane;
  private SimulationController myController;
  private Map<String, String> myConfigResourceMap;
  private ResourceBundle myInfoDisplayBundle = ResourceBundle.getBundle(
      SimulationInfoDisplay.class.getPackageName() + ".InfoDisplay");;
  private PauseTransition delay = new PauseTransition(Duration.seconds(0));


  private int numRows;
  private int numCols;
  private int cellWidth;
  private int cellHeight;
  private boolean flipped = false;

  private double zoomFactor;
  private double zoomStep;
  private double minZoom;
  private double maxZoom;

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
    cellWidth = parseInt(myConfigResourceMap.getOrDefault("window.width", "900"))
         / numCols;
    cellHeight =
        (parseInt(myConfigResourceMap.getOrDefault("grid.height", "750"))
            - parseInt(myConfigResourceMap.getOrDefault("lower.bar.height", "150")))
            / numRows;
    gridPane = new GridPane();
    zoomPane = new StackPane(gridPane);
    addGridZoom();
  }

  private void addGridZoom() {
    setZoomInstanceVariables();
    Scale scale = new Scale(zoomFactor, zoomFactor, 0, 0);
    gridPane.getTransforms().add(scale);
    setupZoomPaneScrollEvent(scale);
  }

  private void setupZoomPaneScrollEvent(Scale scale) {
    zoomPane.setOnScroll((ScrollEvent event) -> {
      scale.setPivotX(event.getX());
      scale.setPivotY(event.getY());
      if (event.getDeltaY() > 0) {
        zoomFactor = Math.min(zoomFactor + zoomStep, maxZoom);
      } else {
        zoomFactor = Math.max(zoomFactor - zoomStep, minZoom);
      }
      scale.setX(zoomFactor);
      scale.setY(zoomFactor);
    });
  }

  private void setZoomInstanceVariables() {
    zoomFactor = Double.parseDouble(myConfigResourceMap.getOrDefault("zoom.factor", "1"));
    zoomStep = Double.parseDouble(myConfigResourceMap.getOrDefault("zoom.step", "0.1"));
    minZoom = Double.parseDouble(myConfigResourceMap.getOrDefault("min.zoom", "0.5"));
    maxZoom = Double.parseDouble(myConfigResourceMap.getOrDefault("max.zoom", "3"));
    ;
  }

  /**
   * creates a new pane to hold the grid and instantiates myCells and myGrid
   *
   * @param myRoot
   * @param colorMap
   */
  public void createGridDisplay(BorderPane myRoot, Map<StateInterface, String> colorMap, SimulationConfig simulationConfig)
      throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    myRoot.setLeft(zoomPane);
    gridPane.setMaxWidth(parseInt(myConfigResourceMap.getOrDefault("window.width", "1000")) - parseInt(myInfoDisplayBundle.getString("sim.info.display.width")));
    gridPane.setMaxHeight(
        parseInt(myConfigResourceMap.getOrDefault("window.height", "800")) - parseInt(myConfigResourceMap.getOrDefault("lower.bar.height", "150")));
    gridPane.setGridLinesVisible(true);
    //gridPane.setStyle("bacteria-state-rock: #f542dd;");
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
        int col = j;
        int row = i;
        if (flipped) {
          col = j;
          row = numRows - i - 1;
        }
        addCellShapeToGridView(colorMap, simulationConfig, cell, col, row);
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
    addShapeToGridPaneAtIndex(j, i, shape);
    makeCellPopUp(cellState, shape);
  }

  private void addShapeToGridPaneAtIndex(int col, int row, Shape shape) {
    gridPane.add(shape, col, row);
  }

  private void makeCellPopUp(StateInterface cellState, Shape shape) {
    Popup popup = new Popup();
    Label popupLabel = new Label(cellState.toString());
    popupLabel.setId("popupLabel");
    popup.getContent().add(popupLabel);

    shape.setOnMouseEntered(event -> {
      delay.setOnFinished(e -> {
        if (!popup.isShowing()) {
          popup.show(shape, event.getScreenX() +
              parseInt(myConfigResourceMap.getOrDefault("pop.up.offset", "10")) ,
              event.getScreenY() +
                  parseInt(myConfigResourceMap.getOrDefault("pop.up.offset", "10")));
        }
      });
      delay.playFromStart();
    });
    shape.setOnMouseExited(event -> {
      delay.stop();
      popup.hide();
    });
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
    flipped = !flipped;
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
