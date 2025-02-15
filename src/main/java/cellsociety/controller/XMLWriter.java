package cellsociety.controller;

import cellsociety.model.Grid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * The XMLWriter class is responsible for saving the current simulation state
 * to an XML file. It constructs the XML structure based on the provided
 * simulation configuration and grid state.
 * @author Angela Predolac
 */
public class XMLWriter {

    private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
    private static final String SIMULATION_TAG = "simulation";
    private static final String TYPE_TAG = "type";
    private static final String TITLE_TAG = "title";
    private static final String AUTHOR_TAG = "author";
    private static final String DESCRIPTION_TAG = "description";
    private static final String WIDTH_TAG = "width";
    private static final String HEIGHT_TAG = "height";
    private static final String GRID_TAG = "grid";
    private static final String PARAMETERS_TAG = "parameters";
    private static final String PARAMETER_TAG = "parameter";

    /**
     * Saves the current simulation state to an XML file
     * @param config The current simulation configuration
     * @param grid The current grid state
     * @param filePath The path where the XML file should be saved
     * @throws IOException If there's an error writing the file
     */
    public void saveToXML(SimulationConfig config, Grid grid, String filePath) throws IOException {
        StringBuilder xmlContent = new StringBuilder();
        xmlContent.append(XML_DECLARATION);

        // Start simulation tag
        xmlContent.append(String.format("<%s>\n", SIMULATION_TAG));

        // Add basic configuration
        appendTag(xmlContent, TYPE_TAG, config.getType());
        appendTag(xmlContent, TITLE_TAG, config.getTitle());
        appendTag(xmlContent, AUTHOR_TAG, config.getAuthor());
        appendTag(xmlContent, DESCRIPTION_TAG, config.getDescription());
        appendTag(xmlContent, WIDTH_TAG, String.valueOf(config.getWidth()));
        appendTag(xmlContent, HEIGHT_TAG, String.valueOf(config.getHeight()));

        // Add parameters
        xmlContent.append(String.format("  <%s>\n", PARAMETERS_TAG));
        for (Map.Entry<String, Double> parameter : config.getParameters().entrySet()) {
            xmlContent.append(String.format("    <%s name=\"%s\" value=\"%f\"/>\n",
                    PARAMETER_TAG, parameter.getKey(), parameter.getValue()));
        }
        xmlContent.append(String.format("  </%s>\n", PARAMETERS_TAG));

        // Add grid state
        xmlContent.append(String.format("  <%s>\n", GRID_TAG));
        for (int row = 0; row < grid.getRows(); row++) {
            StringBuilder rowContent = new StringBuilder("    ");
            for (int col = 0; col < grid.getCols(); col++) {
                rowContent.append(grid.getCell(row, col).getCurrentState().toString());
                if (col < grid.getCols() - 1) {
                    rowContent.append(",");
                }
            }
            xmlContent.append(rowContent).append("\n");
        }
        xmlContent.append(String.format("  </%s>\n", GRID_TAG));

        // Close simulation tag
        xmlContent.append(String.format("</%s>", SIMULATION_TAG));

        // Write to file
        Files.write(Paths.get(filePath), xmlContent.toString().getBytes());
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
