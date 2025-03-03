package cellsociety.view;

import cellsociety.model.StateInterface;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * class for creating a new CellStateLineGraph, which is used to display changes in cell populations
 * over time
 *
 * @author Alana Zinkin
 */
public class CellStateLineGraph {

  private LineChart<Number, Number> lineChart;
  private Map<StateInterface, XYChart.Series<Number, Number>> stateSeriesMap;
  private int timeTracker;

  /**
   * constructor for creating a new line graph
   *
   * @param xAxisLabel label for the x-axis
   * @param yAxisLabel label for the y-axis
   * @param title      label for the title of the chart
   */
  public CellStateLineGraph(String xAxisLabel, String yAxisLabel, String title) {
    stateSeriesMap = new HashMap<>();
    timeTracker = 0;
    initLineChart(xAxisLabel, yAxisLabel, title);
  }

  /**
   * initializes a new Line Graph with two axes
   *
   * @param xAxisLabel x-axis label
   * @param yAxisLabel y-axis label
   * @param title      title of the graph
   */
  public void initLineChart(String xAxisLabel, String yAxisLabel, String title) {
    NumberAxis xAxis = new NumberAxis();
    xAxis.setLabel(xAxisLabel);
    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel(yAxisLabel);
    lineChart = new LineChart<>(xAxis, yAxis);
    lineChart.setTitle(title);
  }

  /**
   * create the cell State Line Graph Display Box
   *
   * @param themeColor theme color of the simulation
   * @return new Pane containing the line chart
   */
  public Pane createDisplayBox(String themeColor) {
    VBox vbox = new VBox();
    vbox.setAlignment(Pos.CENTER);
    vbox.getChildren().add(lineChart);
    lineChart.getStyleClass().add(themeColor);
    return vbox;
  }

  /**
   * updates the line chart according to the counts of each of the states and the iteration the
   * simulation is currently on
   *
   * @param stateCounts a map between the state interface value and the number of cells in that
   *                    state at the given iteration
   */
  public void updateLineChart(Map<StateInterface, Double> stateCounts,
      Map<StateInterface, String> colorMap) {

    for (Map.Entry<StateInterface, Double> entry : stateCounts.entrySet()) {
      StateInterface state = entry.getKey();
      double count = entry.getValue();

      // If state is not already in the chart, create a new series
      if (!stateSeriesMap.containsKey(state)) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        //style the graph
        series.setName(state.toString());
        stateSeriesMap.put(state, series);
        lineChart.getData().add(series);
      }
      // Add the new data point to the corresponding series
      XYChart.Series<Number, Number> series = stateSeriesMap.get(state);
      series.nodeProperty().get().setStyle("-fx-stroke: " + colorMap.get(state));
      series.getData().add(new XYChart.Data<>(timeTracker, count));
    }
    timeTracker++;
  }
}
