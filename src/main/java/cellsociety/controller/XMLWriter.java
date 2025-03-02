package cellsociety.controller;

import cellsociety.model.Grid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * The XMLWriter class is responsible for saving the current simulation state to an XML file. It
 * constructs the XML structure based on the provided simulation configuration and grid state.
 * This class extends {@code BaseFileWriter} and overrides the {@code save} and {@code formatContent}
 * methods to generate XML-formatted content representing the simulation details and grid layout.
 *
 * @author angelapredolac
 */
public class XMLWriter extends BaseFileWriter {

  private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
  private static final String SIMULATION_TAG = "simulation";
  private static final String TYPE_TAG = "type";
  private static final String TITLE_TAG = "title";
  private static final String AUTHOR_TAG = "author";
  private static final String DESCRIPTION_TAG = "description";
  private static final String WIDTH_TAG = "width";
  private static final String HEIGHT_TAG = "height";
  private static final String GRID_TAG = "initial_states";
  private static final String PARAMETER_TAG = "parameter";

  /**
   * Saves the simulation configuration and grid state to an XML file at the specified file path.
   *
   * @param config   The simulation configuration containing metadata and parameters.
   * @param grid     The grid representing the current simulation state.
   * @param filePath The path to save the XML file.
   * @throws IOException If an error occurs while writing to the file.
   */
  @Override
  public void save(SimulationConfig config, Grid grid, String filePath) throws IOException {
    String content = formatContent(config, grid);
    writeToFile(filePath, content);
  }

  /**
   * Formats the simulation configuration and grid state into an XML string representation.
   *
   * @param config The simulation configuration containing metadata and parameters.
   * @param grid   The grid representing the current simulation state.
   * @return A string containing the XML representation of the simulation.
   */
  @Override
  protected String formatContent(SimulationConfig config, Grid grid) {
    StringBuilder xmlContent = new StringBuilder();
    xmlContent.append(XML_DECLARATION);

    xmlContent.append(String.format("<%s>\n", SIMULATION_TAG));

    appendTag(xmlContent, TYPE_TAG, config.getType());
    appendTag(xmlContent, TITLE_TAG, config.getTitle());
    appendTag(xmlContent, AUTHOR_TAG, config.getAuthor());
    appendTag(xmlContent, DESCRIPTION_TAG, config.getDescription());
    appendTag(xmlContent, WIDTH_TAG, String.valueOf(config.getWidth()));
    appendTag(xmlContent, HEIGHT_TAG, String.valueOf(config.getHeight()));

    for (Map.Entry<String, Double> parameter : config.getParameters().entrySet()) {
      xmlContent.append(String.format("    <%s name=\"%s\" value=\"%f\"/>\n",
              PARAMETER_TAG, parameter.getKey(), parameter.getValue()));
    }

    xmlContent.append(String.format("  <%s>\n", GRID_TAG));
    for (int row = 0; row < grid.getRows(); row++) {
      StringBuilder rowContent = new StringBuilder("    ");
      for (int col = 0; col < grid.getCols(); col++) {
        rowContent.append(grid.getCell(row, col).getCurrentState().getNumericValue());
        if (col < grid.getCols() - 1) {
          rowContent.append(" ");
        }
      }
      xmlContent.append(rowContent).append("\n");
    }
    xmlContent.append(String.format("  </%s>\n", GRID_TAG));

    xmlContent.append(String.format("</%s>", SIMULATION_TAG));

    return xmlContent.toString();
  }

  /**
   * Appends an XML tag with a value to the provided StringBuilder.
   *
   * @param builder The StringBuilder to append the tag to.
   * @param tag     The XML tag name.
   * @param value   The value to enclose within the tag.
   */
  private void appendTag(StringBuilder builder, String tag, String value) {
    builder.append(String.format("  <%s>%s</%s>\n", tag, value, tag));
  }
}
