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
          "Percolation", Set.of(0, 1, 2),
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
    public SimulationConfig parseXMLFile(String filePath) throws ConfigurationException {
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

        String widthStr = getElementContent(document, "width");
        String heightStr = getElementContent(document, "height");
        setGridDimensions(widthStr, heightStr, config);

        NodeList cellNodes = document.getElementsByTagName("cell");
        if (cellNodes.getLength() > 0) {
          validateAndSetCellLocations(cellNodes, config);
        } else {
          String initialStatesStr = getElementContent(document, "initial_states");
          validateInitialStates(initialStatesStr);
          int[] initialStates = parseInitialStates(initialStatesStr);
          validateCellStates(initialStates, simType);
          validateGridSize(config, initialStates.length);
          config.setInitialStates(initialStates);
        }

        config.setParameters(parseParametersWithValidation(document, config.getType()));

      } catch (ParserConfigurationException e) {
        throw new ConfigurationException("XML parser configuration error: " + e.getMessage());
      } catch (SAXException e) {
        throw new ConfigurationException("Invalid XML format: " + e.getMessage());
      } catch (Exception e) {
        throw new ConfigurationException("Error reading file: " + e.getMessage());
      }

      return config;
    }

  private void setGridDimensions(String widthStr, String heightStr, SimulationConfig config)
          throws ConfigurationException {
    if (widthStr == null || heightStr == null || widthStr.isEmpty() || heightStr.isEmpty()) {
      throw new ConfigurationException("Width and height must be specified in the configuration");
    }

    try {
      int width = Integer.parseInt(widthStr.trim());
      int height = Integer.parseInt(heightStr.trim());

      if (width <= 0 || height <= 0) {
        throw new ConfigurationException(
                String.format("Invalid grid dimensions: width=%d, height=%d. Must be positive values.",
                        width, height));
      }

      config.setWidth(width);
      config.setHeight(height);
    } catch (NumberFormatException e) {
      throw new ConfigurationException("Grid dimensions must be valid integers");
    }
  }

  private void validateAndSetCellLocations(NodeList cellNodes, SimulationConfig config)
          throws ConfigurationException {
    int width = config.getWidth();
    int height = config.getHeight();
    int[] states = new int[width * height];
    Set<String> usedLocations = new HashSet<>();
    List<String> outOfBoundsCells = new ArrayList<>();
    List<String> duplicateLocations = new ArrayList<>();

    Arrays.fill(states, 0);

    for (int i = 0; i < cellNodes.getLength(); i++) {
      Element cellElement = (Element) cellNodes.item(i);

      try {
        int row = Integer.parseInt(cellElement.getAttribute("row"));
        int col = Integer.parseInt(cellElement.getAttribute("col"));
        int state = Integer.parseInt(cellElement.getAttribute("state"));

        if (row < 0 || row >= height || col < 0 || col >= width) {
          outOfBoundsCells.add(String.format("(row=%d, col=%d)", row, col));
          continue;
        }

        String location = row + "," + col;
        if (!usedLocations.add(location)) {
          duplicateLocations.add(String.format("(row=%d, col=%d)", row, col));
          continue;
        }

        states[row * width + col] = state;

      } catch (NumberFormatException e) {
        throw new ConfigurationException(
                "Invalid number format in cell definition at index " + i);
      }
    }

    if (!outOfBoundsCells.isEmpty() || !duplicateLocations.isEmpty()) {
      StringBuilder errorMsg = new StringBuilder();
      if (!outOfBoundsCells.isEmpty()) {
        errorMsg.append(String.format("Cells outside grid bounds %dx%d: %s\n",
                width, height, String.join(", ", outOfBoundsCells)));
      }
      if (!duplicateLocations.isEmpty()) {
        errorMsg.append("Duplicate cell locations: ")
                .append(String.join(", ", duplicateLocations));
      }
      throw new ConfigurationException(errorMsg.toString());
    }

    validateCellStates(states, config.getType());
    config.setInitialStates(states);
  }

  private void validateGridSize(SimulationConfig config, int statesLength)
          throws ConfigurationException {
    int expectedCells = config.getWidth() * config.getHeight();
    if (statesLength != expectedCells) {
      throw new ConfigurationException(String.format(
              "Number of states (%d) does not match grid size (%dx%d = %d cells)",
              statesLength, config.getWidth(), config.getHeight(), expectedCells));
    }
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

  private Map<String, Double> parseParametersWithValidation(Document doc, String simulationType)
          throws ConfigurationException {
    Map<String, Double> parameters = new HashMap<>();
    //loadDefaultParameters(parameters, simulationType);

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

  static class ConfigurationException extends Exception {
    public ConfigurationException(String message) {
      super(message);
    }
  }
}
