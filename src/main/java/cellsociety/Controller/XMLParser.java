package cellsociety.Controller;

import org.w3c.dom.Document;
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

        String type = getElementContent(document, "type");
        String title = getElementContent(document, "title");
        String author = getElementContent(document, "author");
        String description = getElementContent(document, "description");
        int width = Integer.parseInt(getElementContent(document, "width"));
        int height = Integer.parseInt(getElementContent(document, "height"));

        String[] statesStr = getElementContent(document, "initial_states").split("\\s+");
        int[] initialStates = new int[statesStr.length];
        for (int i = 0; i < statesStr.length; i++) {
            initialStates[i] = Integer.parseInt(statesStr[i]);
        }

        Map<String, String> parameters = parseParameters(document);

        return new SimulationConfig(type, title, author, description, width, height,
                initialStates, parameters);
    }

    /**
     * Extracts parameters from the XML file and stores them in a map.
     *
     * @param document The XML document being parsed
     * @return A map of parameter names to their values
     */
    private Map<String, String> parseParameters(Document document) {
        Map<String, String> parameters = new HashMap<>();
        NodeList paramNodes = document.getElementsByTagName("parameters");
        if (paramNodes.getLength() > 0) {
            Node parametersNode = paramNodes.item(0);
            NodeList paramList = parametersNode.getChildNodes();
            for (int i = 0; i < paramList.getLength(); i++) {
                Node param = paramList.item(i);
                if (param.getNodeType() == Node.ELEMENT_NODE) {
                    parameters.put(param.getNodeName(), param.getTextContent());
                }
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
