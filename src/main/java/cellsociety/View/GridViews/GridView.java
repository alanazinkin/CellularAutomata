package cellsociety.View.GridViews;

import static cellsociety.View.SimulationView.SIMULATION_HEIGHT;
import static cellsociety.View.SimulationView.SIMULATION_WIDTH;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.StateInterface;
import java.util.List;
import java.util.Map;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public abstract class GridView {
  protected static final int SLIDER_BAR_HEIGHT = 150;

  protected Grid myGrid;
  protected List<Shape> myCells;
  protected GridPane gridPane;
  int numRows;
  int numCols;
  int cellWidth;
  int cellHeight;

  public GridView(SimulationConfig simulationConfig, Grid grid) {
    myGrid = grid;
    numRows = simulationConfig.getWidth();
    numCols = simulationConfig.getHeight();
    cellWidth = SIMULATION_WIDTH / numCols;
    cellHeight = (SIMULATION_HEIGHT - SLIDER_BAR_HEIGHT) / numRows;
    gridPane = new GridPane();
  }

  public abstract void createGridDisplay(BorderPane myRoot, SimulationConfig simulationConfig, Map<StateInterface, Color> stateMap);

  public void renderGrid(Map<StateInterface, Color> stateMap) {
    for(int i = 0; i < numRows; i ++) {
      for (int j = 0; j < numCols; j ++) {
        Cell cell = myGrid.getCell(i, j);
        StateInterface cellState = cell.getState();
        Rectangle rectCell = new Rectangle(cellWidth, cellHeight, stateMap.get(cellState));
        rectCell.setStroke(Color.BLACK);
        rectCell.setStrokeWidth(1);
        myCells.add(rectCell);
        gridPane.add(rectCell, i * cellWidth, j * cellHeight);
      }
    }
  }
}
