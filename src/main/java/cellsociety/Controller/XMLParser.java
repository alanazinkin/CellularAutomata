package cellsociety.Controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Parser for reading and validating Game of Life simulation configuration files in XML format.
 * This class handles parsing of simulation parameters, grid dimensions, and initial states
 * from an XML file and provides validation of the configuration.
 */
public class XMLParser {

    /**
     * Parses an XML file containing Game of Life simulation configuration.
     *
     * @param filePath Path to the XML configuration file to be parsed
     * @return SimulationConfig object containing the parsed configuration
     * @throws Exception if there are errors reading the file, parsing the XML,
     *         or converting values to the expected types
     */
    public SimulationConfig parseXMLFile(String filePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(filePath));
        document.getDocumentElement().normalize();

        SimulationConfig config = new SimulationConfig();

        config.setSimulationType(getElementContent(document, "type"));
        config.setTitle(getElementContent(document, "title"));
        config.setAuthor(getElementContent(document, "author"));
        config.setDescription(getElementContent(document, "description"));

        String widthStr = getElementContent(document, "width");
        String heightStr = getElementContent(document, "height");
        if (widthStr == null || heightStr == null || widthStr.isEmpty() || heightStr.isEmpty()) {
            throw new IllegalArgumentException("Width and height must be specified in the configuration");
        }
        config.setWidth(Integer.parseInt(widthStr.trim()));
        config.setHeight(Integer.parseInt(heightStr.trim()));

        config.setParameters(parseParameters(document));

        String initialStatesStr = getElementContent(document, "initial_states");
        if (initialStatesStr == null || initialStatesStr.isEmpty()) {
            throw new IllegalArgumentException("Initial states must be specified in the configuration");
        }

        int[] initialStates = parseInitialStates(initialStatesStr);
        config.setInitialStates(initialStates);

        return config;
    }

    /**
     * Parses the initial states string into an array of integers
     *
     * @param statesStr The string containing space-separated state values
     * @return Array of integer state values
     */
    private int[] parseInitialStates(String statesStr) {
        // Remove leading/trailing whitespace and split on any whitespace (space or newline)
        String[] values = statesStr.trim().split("\\s+");
        int[] states = new int[values.length];

        for (int i = 0; i < values.length; i++) {
            if (!values[i].isEmpty()) {
                try {
                    states[i] = Integer.parseInt(values[i].trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid state value: " + values[i]);
                }
            } else {
                throw new IllegalArgumentException("Empty state value found in position " + i);
            }
        }

        return states;
    }


    /**
     * Parses parameter values from the XML configuration
     *
     * @param doc The XML document
     * @return Map of parameter names to their double values
     */
    private Map<String, Double> parseParameters(Document doc) {
        Map<String, Double> parameters = new HashMap<>();
        NodeList paramNodes = doc.getElementsByTagName("parameter");

        for (int i = 0; i < paramNodes.getLength(); i++) {
            Element paramElement = (Element) paramNodes.item(i);
            String name = paramElement.getAttribute("name");
            String valueStr = paramElement.getAttribute("value");

            try {
                double value = Double.parseDouble(valueStr);
                parameters.put(name, value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        String.format("Parameter '%s' has invalid numerical value: %s", name, valueStr)
                );
            }
        }

        return parameters;
    }

    /**
     * Helper method to extract the content of an XML element by its tag name.
     *
     * @param doc The XML document being parsed
     * @param tagName The name of the XML tag whose content should be retrieved
     * @return The text content of the first element matching the tag name
     */
    private String getElementContent(Document doc, String tagName) {
        return doc.getElementsByTagName(tagName).item(0).getTextContent();
    }

    /**
     * Validates a simulation configuration to ensure it meets the requirements
     * for a Game of Life simulation.
     *
     * Performs the following validations:
     * - Ensures all cell states are either 0 (DEAD) or 1 (ALIVE)
     * - Verifies the simulation type is "Game of Life"
     * - Confirms grid dimensions are positive numbers
     *
     * @param config The SimulationConfig object to validate
     * @throws IllegalArgumentException if any validation check fails, with a message
     *         describing the specific validation error
     */
    public void validateConfig(SimulationConfig config) throws IllegalArgumentException {

        for (int state : config.getInitialStates()) {
            if (state != 0 && state != 1) {
                throw new IllegalArgumentException("Invalid state value found: " + state +
                        ". Game of Life only accepts 0 (DEAD) or 1 (ALIVE)");
            }
        }

        if (!"Game of Life".equals(config.getType())) {
            throw new IllegalArgumentException("Invalid simulation type. Expected 'Game of Life', found: " +
                    config.getType());
        }

        if (config.getWidth() <= 0 || config.getHeight() <= 0) {
            throw new IllegalArgumentException("Grid dimensions must be positive");
        }
    }

}
