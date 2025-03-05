package cellsociety.view;

import cellsociety.model.StateInterface;
import cellsociety.model.state.PercolationState;
import java.util.HashMap;
import java.util.Map;
import javafx.stage.Stage;
import static org.junit.jupiter.api.Assertions.*;
import javafx.scene.chart.XYChart;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class CellStateBarGraphTest extends ApplicationTest {


  CellChart testGraph;
  @Override
  public void start(Stage stage) {
    testGraph = new CellStateBarGraph("States", "Population Change", "Cell State Changes");
  }


  @Test
  void updateChart_StatCountsUpdated_StateChangeIsTracked() {
    Map<StateInterface, Double> stateCounts = new HashMap<>();
    Map<StateInterface, String> colorMap = new HashMap<>();
    StateInterface stateA =  PercolationState.OPEN;
    StateInterface stateB = PercolationState.PERCOLATED;
    stateCounts.put(stateA, 5.0);
    colorMap.put(stateA, "blue");

    testGraph.updateChart(stateCounts, colorMap);

    // Change state count in next step
    stateCounts.put(stateA, 8.0);
    testGraph.updateChart(stateCounts, colorMap);

    XYChart<String, Number> chart = ((CellStateBarGraph) testGraph).getGraph();
    assertEquals(1, chart.getData().size(), "Still one state tracked.");
    assertEquals(3.0, chart.getData().getFirst().getData().getFirst().getYValue(), "State A should show a change of +3.");
  }
  @Test
  void CellStateBarGraph_BasicTest_LabelsCorrect() {
    CellChart graph = new CellStateBarGraph("States", "Population Change", "Cell State Changes");

    XYChart<String, Number> chart = ((CellStateBarGraph) graph).getGraph();
    assertNotNull(chart, "The bar chart should be initialized.");
    assertEquals("Cell State Changes", chart.getTitle(), "Chart title should be correctly set.");
    assertEquals("States", chart.getXAxis().getLabel(), "X-axis label should be set.");
    assertEquals("Population Change", chart.getYAxis().getLabel(), "Y-axis label should be set.");
    assertTrue(chart.getData().isEmpty(), "Initially, the bar chart should have no data.");
  }
}
