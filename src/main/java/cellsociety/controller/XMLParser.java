package cellsociety.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

/**
 * Parser for reading and validating Game of Life simulation configuration files in XML format. This
 * class handles parsing of simulation parameters, grid dimensions, and initial states from an XML
 * file and provides validation of the configuration.
 *
 * @author Angela Predolac
 */
public class XMLParser {

  private final ResourceBundle defaultProperties;
  private static final String DEFAULT_PROPERTIES_PATH = "cellsociety.controller.simulation";
  private static final String PROB_PREFIX = "default.";
  private static final String PROB_SUFFIX = ".prob";

    public XMLParser() {
      this.defaultProperties = ResourceBundle.getBundle(DEFAULT_PROPERTIES_PATH);
    }

    /**
   * Parses an XML file containing Game of Life simulation configuration.
   *
   * @param filePath Path to the XML configuration file to be parsed
   * @return SimulationConfig object containing the parsed configuration
   * @throws Exception if there are errors reading the file, parsing the XML, or converting values
   *                   to the expected types
   */
  public SimulationConfig parseXMLFile(String filePath) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new File(filePath));
    document.getDocumentElement().normalize();

    SimulationConfig config = new SimulationConfig();
    validateRequiredFields(document);

    config.setSimulationType(getElementContent(document, "type"));
    config.setTitle(getElementContent(document, "title"));
    config.setAuthor(getElementContent(document, "author"));
    config.setDescription(getElementContent(document, "description"));

    String initialStatesStr = getElementContent(document, "initial_states");
    if (initialStatesStr == null || initialStatesStr.isEmpty()) {
      throw new IllegalArgumentException("Initial states must be specified in the configuration");
    }
    int[] initialStates = parseInitialStates(initialStatesStr);
    config.setInitialStates(initialStates);

    String widthStr = getElementContent(document, "width");
    String heightStr = getElementContent(document, "height");

    if ((widthStr == null || widthStr.isEmpty()) && (heightStr == null || heightStr.isEmpty())) {
      calculateGridDimensions(initialStates, config);
    } else {
      setProvidedDimensions(widthStr, heightStr, config);
    }

    config.setParameters(parseParametersWithDefaults(document, config.getType()));

    return config;
  }

  private void calculateGridDimensions(int[] initialStates, SimulationConfig config) {
    int totalCells = initialStates.length;
    int gridSize = (int) Math.sqrt(totalCells);
    if (gridSize * gridSize == totalCells) {
      config.setWidth(gridSize);
      config.setHeight(gridSize);
    } else {
      throw new IllegalArgumentException("Cannot determine grid dimensions from non-square initial states array");
    }
  }

  private void setProvidedDimensions(String widthStr, String heightStr, SimulationConfig config) {
    if (widthStr == null || heightStr == null || widthStr.isEmpty() || heightStr.isEmpty()) {
      throw new IllegalArgumentException("Both width and height must be specified if either is provided");
    }
    config.setWidth(Integer.parseInt(widthStr.trim()));
    config.setHeight(Integer.parseInt(heightStr.trim()));
  }

  private Map<String, Double> parseParametersWithDefaults(Document doc, String simulationType) {
    Map<String, Double> parameters = new HashMap<>();

    loadDefaultParameters(parameters, simulationType);

    NodeList paramNodes = doc.getElementsByTagName("parameter");
    for (int i = 0; i < paramNodes.getLength(); i++) {
      Element paramElement = (Element) paramNodes.item(i);
      String name = paramElement.getAttribute("name");
      String valueStr = paramElement.getAttribute("value");

      try {
        double value = Double.parseDouble(valueStr);
        parameters.put(name, value);
      } catch (NumberFormatException e) {
        System.out.printf("Warning: Invalid value for parameter '%s', using default value from properties%n", name);
      }
    }

    return parameters;
  }

  private void loadDefaultParameters(Map<String, Double> parameters, String simulationType) {
    String simTypeKey = simulationType.toLowerCase().replaceAll("\\s+", ".");

    Map<String, String[]> simParamKeys = new HashMap<>();
    simParamKeys.put("spreadingfire", new String[]{"fire", "tree"});
    simParamKeys.put("schelling", new String[]{"satisfaction"});
    simParamKeys.put("percolation", new String[]{"percolation"});

    String[] paramKeys = simParamKeys.getOrDefault(simTypeKey, new String[0]);
    for (String key : paramKeys) {
      String propertyKey = PROB_PREFIX + key + PROB_SUFFIX;
      try {
        String defaultValue = defaultProperties.getString(propertyKey);
        parameters.put(key + "Prob", Double.parseDouble(defaultValue));
      } catch (MissingResourceException | NumberFormatException e) {
        System.out.printf("Warning: Could not load default value for %s from properties file%n", propertyKey);
      }
    }
  }

  private void validateRequiredFields(Document doc) throws Exception {
    List<String> missingFields = new ArrayList<>();
    String[] requiredFields = {"type", "title", "author", "description"};

    for (String field : requiredFields) {
      Node node = doc.getElementsByTagName(field).item(0);
      if (node == null || node.getTextContent().trim().isEmpty()) {
        missingFields.add(field);
      }
    }

    if (!missingFields.isEmpty()) {
      throw new ConfigurationException("Missing required fields: " +
              String.join(", ", missingFields));
    }
  }

  private String getElementContent(Document doc, String tagName) {
    NodeList nodes = doc.getElementsByTagName(tagName);
    if (nodes.getLength() > 0) {
      return nodes.item(0).getTextContent();
    }
    return null;
  }

  /**
   * Parses the initial states string into an array of integers
   *
   * @param statesStr The string containing space-separated state values
   * @return Array of integer state values
   */
  private int[] parseInitialStates(String statesStr) {
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
   * Validates a simulation configuration to ensure it meets the requirements for a Game of Life
   * simulation.
   * <p>
   * Performs the following validations: - Ensures all cell states are either 0 (DEAD) or 1 (ALIVE)
   * - Verifies the simulation type is "Game of Life" - Confirms grid dimensions are positive
   * numbers
   *
   * @param config The SimulationConfig object to validate
   * @throws IllegalArgumentException if any validation check fails, with a message describing the
   *                                  specific validation error
   */
  public void validateConfig(SimulationConfig config) throws IllegalArgumentException {

    for (int state : config.getInitialStates()) {
      if (state != 0 && state != 1) {
        throw new IllegalArgumentException("Invalid state value found: " + state +
            ". Game of Life only accepts 0 (DEAD) or 1 (ALIVE)");
      }
    }

    if (!"Game of Life".equals(config.getType())) {
      throw new IllegalArgumentException(
          "Invalid simulation type. Expected 'Game of Life', found: " +
              config.getType());
    }

    if (config.getWidth() <= 0 || config.getHeight() <= 0) {
      throw new IllegalArgumentException("Grid dimensions must be positive");
    }
  }

  static class ConfigurationException extends Exception {
    public ConfigurationException(String message) {
      super(message);
    }
  }
}
