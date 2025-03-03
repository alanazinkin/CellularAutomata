package cellsociety.view;

import cellsociety.model.StateInterface;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 * Class for creating a bar graph that displays changes in cell state populations per step. The
 * X-axis represents different cell states, while the Y-axis represents the change in population
 * since the last step (positive or negative).
 *
 * @author Alana Zinkin
 */
public class CellStateBarGraph extends CellChart {

  private XYChart<String, Number> barChart;
  private Map<StateInterface, XYChart.Series<String, Number>> stateSeriesMap;
  private Map<StateInterface, Double> previousStateCounts;

  /**
   * Constructor for creating a bar graph.
   *
   * @param xAxisLabel label for the x-axis
   * @param yAxisLabel label for the y-axis
   * @param title      label for the title of the graph
   */
  public CellStateBarGraph(String xAxisLabel, String yAxisLabel, String title) {
    stateSeriesMap = new HashMap<>();
    previousStateCounts = new HashMap<>();
    initChart(xAxisLabel, yAxisLabel, title);
  }

  /**
   * Initializes a new Bar Chart with two axes.
   *
   * @param xAxisLabel x-axis label
   * @param yAxisLabel y-axis label
   * @param title      title of the graph
   */
  @Override
  public void initChart(String xAxisLabel, String yAxisLabel, String title) {
    CategoryAxis xAxis = new CategoryAxis();
    xAxis.setLabel(xAxisLabel);

    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel(yAxisLabel);
    yAxis.setAutoRanging(true); // Allow bars to extend both directions

    barChart = new BarChart<>(xAxis, yAxis);
    barChart.setTitle(title);
    barChart.setLegendVisible(false);
    barChart.setAnimated(false);
  }


  /**
   * Updates the bar chart according to the change in state populations per step.
   *
   * @param stateCounts a map between the state interface value and the number of cells in that
   *                    state at the given iteration
   * @param colorMap    a map between the state interface and its associated color
   */
  @Override
  public void updateChart(Map<StateInterface, Double> stateCounts,
      Map<StateInterface, String> colorMap) {
    for (StateInterface state : stateCounts.keySet()) {
      double currentCount = stateCounts.get(state);
      double previousCount = previousStateCounts.getOrDefault(state, currentCount);
      double delta = currentCount - previousCount;
      // If state is not already in the chart, create a new series
      if (!stateSeriesMap.containsKey(state)) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        stateSeriesMap.put(state, series);
        barChart.getData().add(series);
      }
      XYChart.Series<String, Number> series = stateSeriesMap.get(state);
      series.getData().clear(); // Clear previous step data
      XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(state.toString(), delta);
      series.getData().add(dataPoint);
      dataPoint.getNode().setStyle("-fx-background-color: " + colorMap.get(state));
    }
    // Store the new state counts for next step comparison
    previousStateCounts = new HashMap<>(stateCounts);
  }

  /**
   * @return barchart instance variable
   */
  public XYChart<String, Number> getGraph() {
    return barChart;
  }
}