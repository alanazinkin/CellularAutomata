package cellsociety.view.gridview;

import cellsociety.controller.SimulationConfig;

import cellsociety.view.shapestrategy.DefaultStrategy;
import cellsociety.view.shapestrategy.ShapeStrategy;
import cellsociety.view.shapestrategy.ShapeStrategyContext;
import static java.lang.Double.parseDouble;
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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import javafx.stage.Popup;
import javafx.util.Duration;

/**
 * abstract class for creating a GridView, which holds all teh cells and updates the visual display
 * of their states
 */
public abstract class GridView {

  private boolean hasGridLines = true;

  private Grid myGrid;
  private List<Shape> myCells;
  private Pane gridPane;
  private Pane zoomPane;
  private SimulationController myController;
  private Map<String, String> myConfigResourceMap;
  private ResourceBundle myInfoDisplayBundle = ResourceBundle.getBundle(
      SimulationInfoDisplay.class.getPackageName() + ".InfoDisplay");
  private PauseTransition delay = new PauseTransition(Duration.seconds(0));
  private ShapeStrategyContext addStrategy = new ShapeStrategyContext(new DefaultStrategy());

  private int numRows;
  private int numCols;
  private double cellWidth;
  private double cellHeight;
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
    zoomPane = new StackPane();
    numRows = simulationConfig.getWidth();
    numCols = simulationConfig.getHeight();
  }

  private void addGridZoom() {
    setZoomInstanceVariables();
    Scale scale = new Scale(zoomFactor, zoomFactor, 0, 0);
    getGridPane().getTransforms().add(scale);
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
    zoomFactor = parseDouble(myConfigResourceMap.getOrDefault("zoom.factor", "1"));
    zoomStep = parseDouble(myConfigResourceMap.getOrDefault("zoom.step", "0.1"));
    minZoom = parseDouble(myConfigResourceMap.getOrDefault("min.zoom", "0.5"));
    maxZoom = parseDouble(myConfigResourceMap.getOrDefault("max.zoom", "3"));
  }

  /**
   * creates a new pane to hold the grid and instantiates myCells and myGrid
   *
   * @param myRoot
   * @param colorMap
   */
  public void createGridDisplay(BorderPane myRoot, Map<StateInterface, String> colorMap,
      SimulationConfig simulationConfig)
      throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    addGridZoom();
    myRoot.setLeft(zoomPane);
    getGridPane().setMaxWidth(
        parseInt(myConfigResourceMap.getOrDefault("grid.width", "400")) - parseInt(
            myInfoDisplayBundle.getString("sim.info.display.width")));
    getGridPane().setMaxHeight(
        parseInt(myConfigResourceMap.getOrDefault("grid.height", "400")) - parseInt(
            myConfigResourceMap.getOrDefault("lower.bar.height", "150")));
    //gridPane.setStyle("bacteria-state-rock: #f542dd;");
    myCells = new ArrayList<>();
    renderGrid(colorMap, simulationConfig);
  }

  /**
   * Abstract method for creating the visual display of grid cells
   * based on the Grid object and organizes them in the view
   *
   * @param colorMap         mapping of state interfaces to colors to visually render each cell
   *                         according to its state value
   * @param simulationConfig the object representation of the simulation configuration
   */
  public abstract void renderGrid(Map<StateInterface, String> colorMap, SimulationConfig simulationConfig)
      throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

  /**
   * adds the cell shape to the grid view
   *
   * @param colorMap         map of state interface to CSS identifier
   * @param simulationConfig simulation configuration object
   * @param cell             cell
   * @param j                column index
   * @param i                row index
   * @param isUpward         true if cell is in normal orientation, false if its upside down
   * @throws ClassNotFoundException    There is no class for the specific cell type
   * @throws NoSuchMethodException     no constructor exists to create the factory
   * @throws InvocationTargetException if a new cell cannot be made
   * @throws InstantiationException    if a new cell cannot be instantiated
   * @throws IllegalAccessException    if user attempts to access a method that should not be
   *                                   accessed
   */
  protected void addCellShapeToGridView(Map<StateInterface, String> colorMap,
      SimulationConfig simulationConfig, Cell cell, double j, double i, boolean isUpward)
      throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    Shape shape = initializeShape(colorMap, simulationConfig, cell, isUpward);
    addStrategy.executeStrategy(getGridPane(), j, i, shape, getCellWidth(), getCellHeight());
  }

  /**
   * wrapper method that creates a new shape, styles it, adds it to the myCells list and creates the
   * cell popup holding relevant cell information
   *
   * @param colorMap         map of state interface to CSS identifier
   * @param simulationConfig simulation configuration object
   * @param cell             cell
   * @param isUpward         true if cell is in normal orientation, false if its upside down
   * @throws ClassNotFoundException    There is no class for the specific cell type
   * @throws NoSuchMethodException     no constructor exists to create the factory
   * @throws InvocationTargetException if a new cell cannot be made
   * @throws InstantiationException    if a new cell cannot be instantiated
   * @throws IllegalAccessException    if user attempts to access a method that should not be
   *                                   accessed
   */
  protected Shape initializeShape(Map<StateInterface, String> colorMap,
      SimulationConfig simulationConfig,
      Cell cell, boolean isUpward)
      throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    StateInterface cellState = cell.getCurrentState();
    Shape shape = makeCellShape(cellState, simulationConfig, isUpward);
    styleTheShape(colorMap, shape, cellState);
    myCells.add(shape);
    makeCellPopUp(cellState, shape);
    return shape;
  }

  /**
   * adds CSS styles to the shape
   *
   * @param colorMap  maps states to CSS ids
   * @param shape     the shape to be styled
   * @param cellState the state of the cell being represented by the shape
   */
  protected void styleTheShape(Map<StateInterface, String> colorMap, Shape shape,
      StateInterface cellState) {
    shape.setId(colorMap.get(cellState));
    shape.setStroke(Color.BLACK);
    if (hasGridLines) {
      shape.setStrokeWidth(1);
    } else {
      shape.setStrokeWidth(0);
    }
  }

  /**
   * makes the visual pop up when hovering over a given cell
   *
   * @param cellState cell's state
   * @param shape     cell's visual shape
   */
  protected void makeCellPopUp(StateInterface cellState, Shape shape) {
    Popup popup = new Popup();
    Label popupLabel = new Label(cellState.toString());
    popupLabel.setId("popupLabel");
    popup.getContent().add(popupLabel);

    shape.setOnMouseEntered(event -> {
      delay.setOnFinished(e -> {
        if (!popup.isShowing()) {
          popup.show(shape, event.getScreenX() +
                  parseInt(myConfigResourceMap.getOrDefault("pop.up.offset", "10")),
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

  protected Shape makeCellShape(StateInterface cellState,
      SimulationConfig simulationConfig, boolean isUpward)
      throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    String factoryClassName;
    if (simulationConfig.getTiling().equals("Default")) {
      factoryClassName = getCellFactoryNameFromState(cellState, simulationConfig);
    } else {
      factoryClassName = getCellFactoryNameFromTiling(simulationConfig);
    }
    String fullFactoryClassName = "cellsociety.view.shapefactory." + factoryClassName;

    Class<?> factoryClass = Class.forName(fullFactoryClassName);
    CellShapeFactory factory = (CellShapeFactory) factoryClass.getDeclaredConstructor()
        .newInstance();

    CellShape cellShape = factory.createCellShape(cellWidth, cellHeight, isUpward);

    return cellShape.getShape();
  }

  private String getCellFactoryNameFromState(StateInterface cellState,
      SimulationConfig simulationConfig) {
    int cellStateNumericValue = cellState.getNumericValue();
    Map<Integer, String> cellShapeMap = simulationConfig.getCellShapeMap();
    if (cellShapeMap == null) {
      throw new NullPointerException("Cell shape map is null");
    }
    // Get the shape type from the map (e.g., "Rectangle" or "Triangle")
    String shapeType = cellShapeMap.getOrDefault(cellStateNumericValue, "Rectangle");
    String factoryClassName = shapeType + "CellFactory";
    return factoryClassName;
  }


  private String getCellFactoryNameFromTiling(SimulationConfig simulationConfig) {
    String tilingShape = simulationConfig.getTiling();
    if (tilingShape == null) {
      throw new NullPointerException("Tiling value is null, may not have been initialized");
    }
    return tilingShape + "CellFactory";
  }

  /**
   * sets the action call of the grid lines toggle button
   *
   * @param toggleButton the toggle button
   */
  public void setGridLinesToggleButtonAction(Button toggleButton) {
    toggleButton.setOnAction(e -> {
      hasGridLines = !hasGridLines;
      if (hasGridLines) {
        addGridLines();
      } else {
        removeGridLines();
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
   * if the grid is not flipped, it flips it over the X-axis and flips it back otherwise
   */
  public void renderGridFlippedVertically(Map<StateInterface, String> colorMap)
      throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    flipped = !flipped;
    renderGrid(myController.getSimulation().getColorMap(), myController.getSimulationConfig());
  }

  /**
   * adds columns constraints for each column in the simulation GridPane
   *
   * @param gridPane the GridPane that the column constraints are applied to
   */
  protected void addColumnConstraints(GridPane gridPane) {
    for (int i = 0; i < numCols; i++) {
      gridPane.getColumnConstraints()
          .add(new ColumnConstraints(getCellWidth() * 0.5)); // column 0 is 100 wide
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

  /**
   * used to get the gridPane for classes that extend the GridView
   *
   * @return new GridPane
   */
  protected Pane getGridPane() {
    return gridPane;
  }

  /**
   * Sets the width of each cell in the grid.
   *
   * @param cellWidth the width of an individual cell
   */
  protected void setCellWidth(double cellWidth) {
    this.cellWidth = cellWidth;
  }

  /**
   * Sets the height of each cell in the grid.
   *
   * @param cellHeight the height of an individual cell
   */
  protected void setCellHeight(double cellHeight) {
    this.cellHeight = cellHeight;
  }

  /**
   * Retrieves the current width of a cell in the grid.
   *
   * @return the width of an individual cell
   */
  protected double getCellWidth() {
    return cellWidth;
  }

  /**
   * Retrieves the current height of a cell in the grid.
   *
   * @return the height of an individual cell
   */
  protected double getCellHeight() {
    return cellHeight;
  }

  /**
   * Checks if the grid is flipped vertically.
   *
   * @return true if the grid is flipped, false otherwise
   */
  protected boolean getFlipped() {
    return flipped;
  }

  /**
   * Sets the grid pane that displays the simulation grid. Also clears and re-adds the pane to the
   * zoom container for interactive zooming.
   *
   * @param gridPane the {@link Pane} that represents the simulation grid
   */
  protected void setGridPane(Pane gridPane) {
    this.gridPane = gridPane;
    zoomPane.getChildren().clear();
    zoomPane.getChildren().add(gridPane);
    addGridZoom();
  }

  protected void setAddStrategy(ShapeStrategy addStrategy) {
    this.addStrategy.setStrategy(addStrategy);
  }
}
