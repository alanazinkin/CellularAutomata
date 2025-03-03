package cellsociety.view;

import cellsociety.model.StateInterface;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 * class for creating a new CellStateLineGraph, which is used to display changes in cell populations
 * over time
 *
 * @author Alana Zinkin
 */
public class CellStateLineGraph extends CellChart {

  private XYChart<Number, Number> lineChart;
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
    initChart(xAxisLabel, yAxisLabel, title);
  }

  /**
   * initializes a new Line Graph with two axes
   *
   * @param xAxisLabel x-axis label
   * @param yAxisLabel y-axis label
   * @param title      title of the graph
   */
  @Override
  public void initChart(String xAxisLabel, String yAxisLabel, String title) {
    NumberAxis xAxis = new NumberAxis();
    xAxis.setLabel(xAxisLabel);
    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel(yAxisLabel);
    lineChart = new LineChart<>(xAxis, yAxis);
    lineChart.setTitle(title);
  }

  /**
   * updates the line chart according to the counts of each of the states and the iteration the
   * simulation is currently on
   *
   * @param stateCounts a map between the state interface value and the number of cells in that
   *                    state at the given iteration
   */
  @Override
  public void updateChart(Map<StateInterface, Double> stateCounts,
      Map<StateInterface, String> colorMap) {
    for (StateInterface state : stateCounts.keySet()) {
      double populationCount = stateCounts.get(state);
      // If state is not already in the chart, create a new series
      if (!stateSeriesMap.containsKey(state)) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(state.toString());
        stateSeriesMap.put(state, series);
        lineChart.getData().add(series);
      }
      // Add the new data point to the corresponding series
      XYChart.Series<Number, Number> series = stateSeriesMap.get(state);
      series.nodeProperty().get().setStyle("-fx-stroke: " + colorMap.get(state));
      series.getData().add(new XYChart.Data<>(timeTracker, populationCount));
    }
    timeTracker++;
  }

  /**
   * @return linechart instance variable
   */
  public XYChart<Number, Number> getGraph() {
    return lineChart;
  }
}
