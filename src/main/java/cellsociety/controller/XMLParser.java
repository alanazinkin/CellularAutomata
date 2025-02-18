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
          "Game of Life", "Spreading of Fire", "Schelling Segregation", "Percolation", "Wa-Tor World",
      "Langton Loop", "Sugar Scape", "Bacteria"
  );
  private static final Map<String, Set<Integer>> VALID_STATES = Map.of(
          "Game of Life", Set.of(0, 1),
          "Spreading of Fire", Set.of(0, 1, 2), // 0: empty, 1: tree, 2: burning
          "Schelling Segregation", Set.of(0, 1, 2),    // 0: empty, 1: agent A, 2: agent B
          "Percolation", Set.of(0, 1, 2),
          "Wa-Tor World", Set.of(0, 1, 2),
      "Langton Loop", Set.of(0, 1, 2, 3, 4, 5, 6, 7),
      "Sugar Scape", Set.of(0, 1, 2),
      "Bacteria", Set.of(0, 1, 2)
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
      validateFile(filePath);

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

        validateAndSetInitialStates(document, config);

        config.setParameters(parseParametersWithValidation(document));

      } catch (ParserConfigurationException e) {
        throw new ConfigurationException("XML parser configuration error: " + e.getMessage());
      } catch (SAXException e) {
        throw new ConfigurationException("Invalid XML format: " + e.getMessage());
      } catch (Exception e) {
        throw new ConfigurationException("Error reading file: " + e.getMessage());
      }

      return config;
    }

  private void parseRandomStates(Document doc, SimulationConfig config) throws ConfigurationException {
    NodeList randomStateNodes = doc.getElementsByTagName("random_states");
    if (randomStateNodes.getLength() == 0) {
      return;
    }

    if (doc.getElementsByTagName("cell").getLength() > 0 ||
            getElementContent(doc, "initial_states") != null) {
      throw new ConfigurationException(
              "Cannot specify both random states and explicit initial states");
    }

    Element randomStatesElement = (Element) randomStateNodes.item(0);
    NodeList stateNodes = randomStatesElement.getElementsByTagName("state");

    Map<Integer, Integer> stateCounts = new HashMap<>();
    int totalSpecifiedCells = 0;
    int gridSize = config.getWidth() * config.getHeight();

    for (int i = 0; i < stateNodes.getLength(); i++) {
      Element stateElement = (Element) stateNodes.item(i);

      int stateValue = Integer.parseInt(stateElement.getAttribute("value"));
      int count = Integer.parseInt(stateElement.getAttribute("count"));

      Set<Integer> validStates = VALID_STATES.get(config.getType());
      if (!validStates.contains(stateValue)) {
        throw new ConfigurationException(
                String.format("Invalid state value %d for %s simulation",
                        stateValue, config.getType()));
      }

      if (count < 0) {
        throw new ConfigurationException(
                String.format("State count cannot be negative: %d", count));
      }

      totalSpecifiedCells += count;
      stateCounts.put(stateValue, count);
    }

    if (totalSpecifiedCells > gridSize) {
      throw new ConfigurationException(
              String.format("Total specified cells (%d) exceeds grid size (%d)",
                      totalSpecifiedCells, gridSize));
    }

    int[] states = generateRandomStates(config.getWidth(), config.getHeight(),
            stateCounts);
    config.setInitialStates(states);
  }

  /**
   * Generates random state assignments based on specified counts
   *
   * @param width       Grid width
   * @param height      Grid height
   * @param stateCounts Map of state values to their desired counts
   * @return Array of randomly assigned states
   */
  private int[] generateRandomStates(int width, int height,
                                     Map<Integer, Integer> stateCounts) {
    int gridSize = width * height;
    int[] states = new int[gridSize];
    Random random = new Random();

    Arrays.fill(states, 0);

    for (Map.Entry<Integer, Integer> entry : stateCounts.entrySet()) {
      int stateValue = entry.getKey();
      int count = entry.getValue();

      for (int i = 0; i < count; i++) {
        int position;
        do {
          position = random.nextInt(gridSize);
        } while (states[position] != 0);
        states[position] = stateValue;
      }
    }

    return states;
  }

  private void validateAndSetInitialStates(Document doc, SimulationConfig config)
          throws ConfigurationException {
    NodeList cellNodes = doc.getElementsByTagName("cell");
    String initialStatesStr = getElementContent(doc, "initial_states");
    NodeList randomStateNodes = doc.getElementsByTagName("random_states");
    NodeList randomProportionNodes = doc.getElementsByTagName("random_proportions");

    int initializationMethods = 0;
    if (cellNodes.getLength() > 0) initializationMethods++;
    if (initialStatesStr != null) initializationMethods++;
    if (randomStateNodes.getLength() > 0) initializationMethods++;
    if (randomProportionNodes.getLength() > 0) initializationMethods++;

    if (initializationMethods > 1) {
      throw new ConfigurationException(
              "Only one initialization method (cells, initial_states, random_states, or random_proportions) can be specified");
    }

    if (cellNodes.getLength() > 0) {
      validateAndSetCellLocations(cellNodes, config);
    } else if (initialStatesStr != null) {
      validateInitialStates(initialStatesStr);
      int[] initialStates = parseInitialStates(initialStatesStr);
      validateCellStates(initialStates, config.getType());
      validateGridSize(config, initialStates.length);
      config.setInitialStates(initialStates);
    } else if (randomStateNodes.getLength() > 0) {
      parseRandomStates(doc, config);
    } else if (randomProportionNodes.getLength() > 0) {
      parseRandomProportions(doc, config);
    } else {
      int[] defaultStates = new int[config.getWidth() * config.getHeight()];
      Arrays.fill(defaultStates, 0);
      config.setInitialStates(defaultStates);
    }
  }

  private void parseRandomProportions(Document doc, SimulationConfig config) throws ConfigurationException {
    Element randomPropsElement = (Element) doc.getElementsByTagName("random_proportions").item(0);
    NodeList stateNodes = randomPropsElement.getElementsByTagName("state");

    Map<Integer, Double> stateProportions = new HashMap<>();
    double totalProportion = 0.0;
    int gridSize = config.getWidth() * config.getHeight();

    for (int i = 0; i < stateNodes.getLength(); i++) {
      Element stateElement = (Element) stateNodes.item(i);

      try {
        int stateValue = Integer.parseInt(stateElement.getAttribute("value"));
        double proportion = Double.parseDouble(stateElement.getAttribute("proportion"));

        Set<Integer> validStates = VALID_STATES.get(config.getType());
        if (!validStates.contains(stateValue)) {
          throw new ConfigurationException(
                  String.format("Invalid state value %d for %s simulation",
                          stateValue, config.getType()));
        }

        if (proportion < 0.0 || proportion > 1.0) {
          throw new ConfigurationException(
                  String.format("Proportion must be between 0 and 1, got: %.2f", proportion));
        }

        totalProportion += proportion;
        stateProportions.put(stateValue, proportion);

      } catch (NumberFormatException e) {
        throw new ConfigurationException(
                "Invalid number format in random proportion definition: " + e.getMessage());
      }
    }

    if (totalProportion > 1.0) {
      throw new ConfigurationException(
              String.format("Total proportion (%.2f) exceeds 1.0", totalProportion));
    }

    Map<Integer, Integer> stateCounts = new HashMap<>();
    for (Map.Entry<Integer, Double> entry : stateProportions.entrySet()) {
      int cellCount = (int) Math.round(entry.getValue() * gridSize);
      stateCounts.put(entry.getKey(), cellCount);
    }

    int[] states = generateRandomStates(config.getWidth(), config.getHeight(),
            stateCounts);
    config.setInitialStates(states);
  }

  private void validateFile(String filePath) throws ConfigurationException {
    File file = new File(filePath);

    if (!file.exists()) {
      throw new ConfigurationException("Configuration file not found: " + filePath);
    }

    if (!file.canRead()) {
      throw new ConfigurationException("Cannot read configuration file: " + filePath);
    }

    if (file.length() == 0) {
      throw new ConfigurationException("Configuration file is empty: " + filePath);
    }

    String extension = getFileExtension(filePath);
    if (!extension.equalsIgnoreCase("xml")) {
      throw new ConfigurationException("Invalid file type. Expected XML file, got: " + extension);
    }
  }

  private String getFileExtension(String filePath) {
    int lastDotIndex = filePath.lastIndexOf('.');
    if (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1) {
      return filePath.substring(lastDotIndex + 1);
    }
    return "";
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
    if (doc.getDocumentElement() == null) {
      throw new ConfigurationException("Empty or malformed XML document");
    }

    if (!"simulation".equals(doc.getDocumentElement().getTagName())) {
      throw new ConfigurationException("Root element must be 'simulation', found: " +
              doc.getDocumentElement().getTagName());
    }

    NodeList rootChildren = doc.getDocumentElement().getChildNodes();
    for (int i = 0; i < rootChildren.getLength(); i++) {
      Node child = rootChildren.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        String nodeName = child.getNodeName();
        if (!isValidRootChild(nodeName)) {
          throw new ConfigurationException("Unexpected element in simulation configuration: " + nodeName);
        }
      }
    }
  }

  private boolean isValidRootChild(String nodeName) {
    Set<String> validElements = Set.of("type", "title", "author", "description",
            "width", "height", "cell", "initial_states", "parameter", "random_states", "random_proportions");
    return validElements.contains(nodeName);
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

  private Map<String, Double> parseParametersWithValidation(Document doc)
          throws ConfigurationException {
    Map<String, Double> parameters = new HashMap<>();

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

  private void validateRequiredFields(Document doc) throws ConfigurationException {
    Map<String, String> missingOrEmptyFields = new HashMap<>();
    String[] requiredFields = {"type", "title", "author", "description"};

    for (String field : requiredFields) {
      NodeList nodes = doc.getElementsByTagName(field);
      if (nodes.getLength() == 0) {
        missingOrEmptyFields.put(field, "Missing field");
      } else {
        String content = nodes.item(0).getTextContent().trim();
        if (content.isEmpty()) {
          missingOrEmptyFields.put(field, "Empty field");
        }
      }
    }

    if (!missingOrEmptyFields.isEmpty()) {
      StringBuilder errorMsg = new StringBuilder("Configuration errors found:");
      for (Map.Entry<String, String> entry : missingOrEmptyFields.entrySet()) {
        errorMsg.append("\n- ").append(entry.getKey()).append(": ").append(entry.getValue());
      }
      throw new ConfigurationException(errorMsg.toString());
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
