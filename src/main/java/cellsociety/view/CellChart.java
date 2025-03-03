package cellsociety.view;

import cellsociety.model.StateInterface;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.chart.Chart;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public abstract class CellChart {

  /**
   * updates the chart according to the new state count values
   *
   * @param stateCounts map of state interface values to cell population counts
   * @param colorMap    map of state interface values to colors
   */
  public abstract void updateChart(Map<StateInterface, Double> stateCounts,
      Map<StateInterface, String> colorMap);

  /**
   * initializes a new chart
   *
   * @param xAxisLabel x-axis label
   * @param yAxisLabel y-axis label
   * @param title      title of graph
   */
  protected abstract void initChart(String xAxisLabel, String yAxisLabel, String title);

  /**
   * create a new Graph Display Box and centers it's alignment
   *
   * @param themeColor theme color of the simulation
   * @return new Pane containing the chart
   */
  public Pane createDisplayBox(String themeColor, Chart chart) {
    VBox vbox = new VBox();
    vbox.setAlignment(Pos.CENTER);
    vbox.getChildren().add(chart);
    chart.getStyleClass().add(themeColor);
    return vbox;
  }

}/**/