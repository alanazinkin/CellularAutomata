package cellsociety.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.*;

import static cellsociety.controller.BaseConfigParser.VALID_STATES;

class XMLInitialStateParser {
    /**
     * Validates and sets the initial states for the simulation based on the XML configuration.
     * Ensures only one initialization method is used and applies appropriate validations.
     *
     * @param doc    The XML document containing the simulation configuration.
     * @param config The simulation configuration object to be updated.
     * @throws ConfigurationException If multiple initialization methods are specified or the configuration is invalid.
     */
    public void validateAndSetInitialStates(Document doc, SimulationConfig config)
            throws ConfigurationException {
        NodeList cellNodes = doc.getElementsByTagName("cell");
        String initialStatesStr = XMLDocumentUtil.getElementContent(doc, "initial_states");
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
     * Parses and validates specific cell locations and their states based on XML input.
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
                XMLDocumentUtil.getElementContent(doc, "initial_states") != null) {
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
        return getInts(width, height, stateCounts);
    }

    static int[] getInts(int width, int height, Map<Integer, Integer> stateCounts) {
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
     * Validates that the given cell states are valid for the specified simulation type.
     *
     * @param states         An array of cell states.
     * @param simulationType The type of simulation being run.
     * @throws ConfigurationException If any state is not valid for the simulation type.
     */
    protected void validateCellStates(int[] states, String simulationType) throws ConfigurationException {
        validateCellStatesHelper(states, simulationType);
    }

    static void validateCellStatesHelper(int[] states, String simulationType) throws ConfigurationException {
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
}
