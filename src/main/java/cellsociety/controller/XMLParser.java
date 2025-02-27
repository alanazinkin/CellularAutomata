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
 * Parser for reading and validating configuration files in XML format.
 * This class handles parsing of simulation parameters, grid dimensions, and initial states from an XML
 * file and provides validation of the configuration.
 *
 * @author angelapredolac
 */
public class XMLParser extends BaseConfigParser{

  private static final String DEFAULT_PROPERTIES_PATH = "cellsociety.controller.simulation";
  private static final String SIMULATION_TAG = "simulation";
  private static final Set<String> VALID_SHAPES = Set.of("Rectangle", "Triangle");
  private static final Set<String> VALID_TILING = Set.of("Default", "Triangle");
  private static final Set<String> VALID_ROOT_CHILDREN = Set.of(
          "type", "title", "author", "description", "width", "height",
          "cell", "initial_states", "parameter", "random_states", "random_proportions", "cell_state",
      "tiling"
  );

  /**
   * Constructs an XMLParser with a default XMLFileValidator and properties path.
   */
  public XMLParser() {
    super(new XMLFileValidator(), DEFAULT_PROPERTIES_PATH);
  }

  /**
   * Parses an XML configuration file and converts it into a SimulationConfig object.
   *
   * @param filePath The path to the XML file containing simulation configuration.
   * @return A SimulationConfig object populated with parsed values.
   * @throws ConfigurationException If there are issues with XML parsing or validation.
   */
  @Override
  protected SimulationConfig parseConfig(String filePath) throws ConfigurationException {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(new File(filePath));
      document.getDocumentElement().normalize();

      validateXMLStructure(document);
      validateRequiredFields(document);

      SimulationConfig config = new SimulationConfig();

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

      config.setCellShapeValues(parseCellShapesWithValidation(document));

      validateAndSetTiling(document, config);

      return config;
    } catch (ParserConfigurationException e) {
      throw new ConfigurationException("XML parser configuration error: " + e.getMessage());
    } catch (SAXException e) {
      throw new ConfigurationException("Invalid XML format: " + e.getMessage());
    } catch (IOException e) {
      throw new ConfigurationException("Error reading file: " + e.getMessage());
    }
  }

  /**
   * Parses an XML file and returns a SimulationConfig object with configuration details.
   *
   * @param filePath The path to the XML configuration file.
   * @return A populated SimulationConfig object.
   * @throws ConfigurationException If an error occurs during parsing or validation.
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

        config.setCellShapeValues(parseCellShapesWithValidation(document));

        validateAndSetTiling(document, config);

      } catch (ParserConfigurationException e) {
        throw new ConfigurationException("XML parser configuration error: " + e.getMessage());
      } catch (SAXException e) {
        throw new ConfigurationException("Invalid XML format: " + e.getMessage());
      } catch (Exception e) {
        throw new ConfigurationException("Error reading file: " + e.getMessage());
      }

      return config;
    }

  private void validateAndSetTiling(Document document, SimulationConfig config)
      throws ConfigurationException {
    String tiling = getElementContent(document, "tiling");
    // set a default tiling
    if (tiling == null) {
      tiling = "Default";
    }
    if (!VALID_TILING.contains(tiling)) {
      throw new ConfigurationException(tiling + "is not a valid tiling.");
    }
    config.setTiling(tiling);
  }

  /**
   * Parses and validates random state assignments for cells in the simulation grid.
   *
   * @param doc The XML Document containing simulation configuration.
   * @param config The SimulationConfig object to store parsed data.
   * @throws ConfigurationException If an error occurs during parsing or validation.
   */
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

  /**
   * Validates and sets the initial states for the simulation based on the XML configuration.
   * Ensures only one initialization method is used and applies appropriate validations.
   *
   * @param doc    The XML document containing the simulation configuration.
   * @param config The simulation configuration object to be updated.
   * @throws ConfigurationException If multiple initialization methods are specified or the configuration is invalid.
   */
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

  /**
   * Parses and applies random state proportions for initialization.
   * Ensures the proportions sum up to at most 1.0 and assigns states accordingly.
   *
   * @param doc    The XML document containing the configuration.
   * @param config The simulation configuration object to be updated.
   * @throws ConfigurationException If state values or proportions are invalid.
   */
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

  /**
   * Validates if a file exists, is readable, and has the correct format.
   *
   * @param filePath The path to the file to be validated.
   * @throws ConfigurationException If the file does not exist, is unreadable, empty, or not an XML file.
   */
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

  /**
   * Extracts and returns the file extension from a given file path.
   *
   * @param filePath The file path from which to extract the extension.
   * @return The file extension as a string.
   */
  private String getFileExtension(String filePath) {
    int lastDotIndex = filePath.lastIndexOf('.');
    if (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1) {
      return filePath.substring(lastDotIndex + 1);
    }
    return "";
  }

  /**
   * Parses and sets the grid dimensions from the provided configuration values.
   *
   * @param widthStr  The width value as a string.
   * @param heightStr The height value as a string.
   * @param config    The simulation configuration object to be updated.
   * @throws ConfigurationException If dimensions are missing, invalid, or non-positive.
   */
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

  /**
   * Validates and sets specific cell locations and their states based on XML input.
   * Ensures locations are within bounds and do not overlap.
   *
   * @param cellNodes The list of cell nodes from the XML document.
   * @param config    The simulation configuration object to be updated.
   * @throws ConfigurationException If locations are out of bounds, duplicated, or states are invalid.
   */
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

  /**
   * Validates that the number of provided states matches the expected grid size.
   *
   * @param config       The simulation configuration containing grid dimensions.
   * @param statesLength The number of states provided.
   * @throws ConfigurationException If the number of states does not match the grid size.
   */
  private void validateGridSize(SimulationConfig config, int statesLength)
          throws ConfigurationException {
    int expectedCells = config.getWidth() * config.getHeight();
    if (statesLength != expectedCells) {
      throw new ConfigurationException(String.format(
              "Number of states (%d) does not match grid size (%dx%d = %d cells)",
              statesLength, config.getWidth(), config.getHeight(), expectedCells));
    }
  }

  /**
   * Validates that the given cell states are valid for the specified simulation type.
   *
   * @param states         An array of cell states.
   * @param simulationType The type of simulation being run.
   * @throws ConfigurationException If any state is not valid for the simulation type.
   */
  @Override
  protected void validateCellStates(int[] states, String simulationType) throws ConfigurationException {
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

  /**
   * Validates the structure of the provided XML document.
   * Ensures it contains the expected root element and structure.
   *
   * @param doc The XML document to be validated.
   * @throws ConfigurationException If the document is empty, malformed, or missing required elements.
   */
  private void validateXMLStructure(Document doc) throws ConfigurationException {
    if (doc.getDocumentElement() == null) {
      throw new ConfigurationException("Empty or malformed XML document");
    }

    if (!SIMULATION_TAG.equals(doc.getDocumentElement().getTagName())) {
      throw new ConfigurationException("Root element must be 'simulation', found: " +
              doc.getDocumentElement().getTagName());
    }

    NodeList rootChildren = doc.getDocumentElement().getChildNodes();
    for (int i = 0; i < rootChildren.getLength(); i++) {
      Node child = rootChildren.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE &&
              !VALID_ROOT_CHILDREN.contains(child.getNodeName())) {
        throw new ConfigurationException(
                "Unexpected element in simulation configuration: " + child.getNodeName());
      }
    }
  }

  /**
   * Validates the initial states string to ensure it is non-empty and contains only numbers and whitespace.
   *
   * @param states The string representing initial states.
   * @throws ConfigurationException if the states string is empty or contains invalid characters.
   */
  private void validateInitialStates(String states) throws ConfigurationException {
    if (states == null || states.trim().isEmpty()) {
      throw new ConfigurationException("Initial states cannot be empty");
    }

    if (!states.trim().matches("^[0-9\\s]+$")) {
      throw new ConfigurationException("Initial states can only contain numbers and whitespace");
    }
  }

  /**
   * Parses parameters from the given XML document and validates them.
   *
   * @param doc The XML document containing parameter elements.
   * @return A map of parameter names to their corresponding double values.
   * @throws ConfigurationException if a parameter is missing, has an empty name, or contains an invalid value.
   */
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

  /**
   * Validates that required fields exist and are non-empty in the given XML document.
   *
   * @param doc The XML document to validate.
   * @throws ConfigurationException if any required field is missing or empty.
   */
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

  /**
   * Retrieves the text content of the first occurrence of the specified tag from the XML document.
   *
   * @param doc The XML document to search.
   * @param tagName The name of the tag to retrieve content from.
   * @return The text content of the tag, or {@code null} if the tag does not exist.
   */
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
   * Parses cell shapes from the given XML document and validates them.
   *
   * @param doc The XML document containing cell shape elements.
   * @return A map of cell shapes to their corresponding shape values as strings.
   * @throws ConfigurationException if a state is missing, or it has an empty name.
   */
  private Map<Integer, String> parseCellShapesWithValidation(Document doc)
      throws ConfigurationException {
    Map<Integer, String> cellShapes = new HashMap<>();

    NodeList cellStateNodes = doc.getElementsByTagName("cell_state");
    for (int i = 0; i < cellStateNodes.getLength(); i++) {
      Element stateElement = (Element) cellStateNodes.item(i);
      String state = stateElement.getAttribute("state");
      String shape = stateElement.getAttribute("shape");

      if (state.isEmpty()) {
        throw ne