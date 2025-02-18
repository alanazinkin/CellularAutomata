package cellsociety.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
  private static final Set<String> VALID_SIMULATION_TYPES = Set.of(
          "Game of Life", "Spreading of Fire", "Schelling State", "Percolation", "Wa-Tor World"
  );
  private static final Map<String, Set<Integer>> VALID_STATES = Map.of(
          "Game of Life", Set.of(0, 1),
          "Spreading of Fire", Set.of(0, 1, 2), // 0: empty, 1: tree, 2: burning
          "Schelling State", Set.of(0, 1, 2),    // 0: empty, 1: agent A, 2: agent B
          "Percolation", Set.of(0, 1),
          "Wa-Tor World", Set.of(0, 1, 2)           // 0: empty, 1: fish, 2: shark
  );

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
    Document document;
    SimulationConfig config = new SimulationConfig();

    try {
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.parse(new File(filePath));
      document.getDocumentElement().normalize();

      validateXMLStructure(document);
      validateRequiredFields(document);

      String simType = getElementContent(document, "type");
      validateSimulationType(simType);
      config.setSimulationType(simType);

      config.setTitle(getElementContent(document, "title"));
      config.setAuthor(getElementContent(document, "author"));
      config.setDescription(getElementContent(document, "description"));

      String initialStatesStr = getElementContent(document, "initial_states");
      validateInitialStates(initialStatesStr);
      int[] initialStates = parseInitialStates(initialStatesStr);
      validateCellStates(initialStates, simType);
      config.setInitialStates(initialStates);

      String widthStr = getElementContent(document, "width");
      String heightStr = getElementContent(document, "height");

      if ((widthStr == null || widthStr.isEmpty()) && (heightStr == null || heightStr.isEmpty())) {
        calculateGridDimensions(initialStates, config);
      } else {
        setProvidedDimensions(widthStr, heightStr, config);
      }

      validateGridSize(config);
      config.setParameters(parseParametersWithValidation(document, config.getType()));

    } catch (ParserConfigurationException e) {
      throw new ConfigurationException("XML parser configuration error: " + e.getMessage());
    } catch (SAXException e) {
      throw new ConfigurationException("Invalid XML format: " + e.getMessage());
    } catch (IOException e) {
      throw new ConfigurationException("Error reading file: " + e.getMessage());
    }

    return config;
  }

  private void validateCellStates(int[] states, String simulationType) throws ConfigurationException {
    Set<Integer> validStates = VALID_STATES.get(simulationType);
    if (validStates == null) {
      throw new ConfigurationException("No valid states defined for simulation type: " + simulationType);
    }

    List<Integer> invalidStates = new ArrayList<>();
    Set<Integer> foundInvalidStates = new HashSet<>();

    for (int i = 0; i < states.length; i++) {
      if (!validStates.contains(states[i])) {
        foundInvalidStates.add(states[i]);
        invalidStates.add(i);
      }
    }

    if (!invalidStates.isEmpty()) {
      String errorMsg = String.format(
              "Invalid cell states found at positions %s. Found invalid values: %s. Valid states for %s are: %s",
              invalidStates,
              foundInvalidStates,
              simulationType,
              validStates
      );
      throw new ConfigurationException(errorMsg);
    }
  }

  private void validateXMLStructure(Document doc) throws ConfigurationException {
    if (!"simulation".equals(doc.getDocumentElement().getTagName())) {
      throw new ConfigurationException("Root element must be 'simulation'");
    }
  }

  private void validateSimulationType(String type) throws ConfigurationException {
    if (!VALID_SIMULATION_TYPES.contains(type)) {
      throw new ConfigurationException("Invalid simulation type: " + type +
              ". Valid types are: " + String.join(", ", VALID_SIMULATION_TYPES));
    }
  }

  private void validateInitialStates(String states) throws ConfigurationException {
    if (states == null || states.trim().isEmpty()) {
      throw new ConfigurationException("Initial states cannot be empty");
    }

    if (!states.trim().matches("^[0-9\\s]+$")) {
      throw new ConfigurationException("Initial states can only contain numbers and whitespace");
    }
  }

  private void validateGridSize(SimulationConfig config) throws ConfigurationException {
    int totalCells = config.getWidth() * config.getHeight();
    if (totalCells != config.getInitialStates().length) {
      throw new ConfigurationException(String.format(
              "Grid size (%dx%d = %d cells) does not match number of initial states (%d)",
              config.getWidth(), config.getHeight(), totalCells, config.getInitialStates().length
      ));
    }
  }

  private Map<String, Double> parseParametersWithValidation(Document doc, String simulationType)
          throws ConfigurationException {
    Map<String, Double> parameters = new HashMap<>();
    loadDefaultParameters(parameters, simulationType);

    NodeList paramNodes = doc.getElementsByTagName("parameter");
    for (int i = 0; i < paramNodes.getLength(); i++) {
      Element paramElement = (Element) paramNodes.item(i);
      String name = paramElement.getAttribute("name");
      String valueStr = paramElement.getAttribute("value");

      if (name.isEmpty()) {
        throw new ConfigurationException("Parameter name cannot be empty");
      }

      try {
        double value = Double.parseDouble(valueStr);
        validateParameterValue(name, value);
        parameters.put(name, value);
      } catch (NumberFormatException e) {
        throw new ConfigurationException(
                String.format("Invalid numerical value for parameter '%s': %s", name, valueStr));
      }
    }

    return parameters;
  }

  private void validateParameterValue(String name, double value) throws ConfigurationException {
    if (name.toLowerCase().contains("prob") && (value < 0 || value > 1)) {
      throw new ConfigurationException(
              String.format("Probability parameter '%s' must be between 0 and 1, got: %f", name, value));
    }

    if (value < 0) {
      throw new ConfigurationException(
              String.format("Parameter '%s' cannot be negative, got: %f", name, value));
    }
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
  private int[] parseInitialStates(String statesStr) throws ConfigurationException {
    try {
      String[] values = statesStr.trim().split("\\s+");
      int[] states = new int[values.length];

      for (int i = 0; i < values.length; i++) {
        if (!values[i].isEmpty()) {
          states[i] = Integer.parseInt(values[i].trim());
        } else {
          throw new ConfigurationException("Empty state value found at position " + i);
        }
      }

      return states;
    } catch (NumberFormatException e) {
      throw new ConfigurationException("Invalid state value format. All states must be integers.");
    }
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

  static class ConfigurationException extends Exception {
    public ConfigurationException(String message) {
