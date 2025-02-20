package cellsociety.controller;

import java.util.*;

public abstract class BaseConfigParser implements SimulationConfigParser {
    protected static final Set<String> VALID_SIMULATION_TYPES = Set.of(
            "Game of Life", "Spreading of Fire", "Schelling Segregation",
            "Percolation", "Wa-Tor World", "Langton Loop", "Sugar Scape", "Bacteria", "Foraging Ants"
    );

    protected static final Map<String, Set<Integer>> VALID_STATES = Map.of(
            "Game of Life", Set.of(0, 1),
            "Spreading of Fire", Set.of(0, 1, 2),
            "Schelling Segregation", Set.of(0, 1, 2),
            "Percolation", Set.of(0, 1, 2),
            "Wa-Tor World", Set.of(0, 1, 2),
            "Langton Loop", Set.of(0, 1, 2, 3, 4, 5, 6, 7),
            "Sugar Scape", Set.of(0, 1, 2),
            "Bacteria", Set.of(0, 1, 2),
        "Foraging Ants", Set.of(0, 1, 2, 3)
    );

    protected final FileValidator fileValidator;
    protected final ResourceBundle defaultProperties;

    protected BaseConfigParser(FileValidator fileValidator, String propertiesPath) {
        this.fileValidator = fileValidator;
        this.defaultProperties = ResourceBundle.getBundle(propertiesPath);
    }

    @Override
    public SimulationConfig parse(String filePath) throws ConfigurationException {
        fileValidator.validateFile(filePath);
        return parseConfig(filePath);
    }

    protected abstract SimulationConfig parseConfig(String filePath) throws ConfigurationException;

    protected void validateSimulationType(String type) throws ConfigurationException {
        if (!VALID_SIMULATION_TYPES.contains(type)) {
            throw new ConfigurationException("Invalid simulation type: " + type +
                    ". Valid types are: " + String.join(", ", VALID_SIMULATION_TYPES));
        }
    }

    protected void validateCellStates(int[] states, String simulationType)
            throws ConfigurationException {
        Set<Integer> validStates = VALID_STATES.get(simulationType);
        if (validStates == null) {
            throw new ConfigurationException(
                    "No valid states defined for simulation type: " + simulationType);
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
                    "Invalid cell states found at positions %s. Found invalid values: %s. " +
                            "Valid states for %s are: %s",
                    invalidStates, foundInvalidStates, simulationType, validStates
            );
            throw new ConfigurationException(errorMsg);
        }
    }

    protected void validateParameterValue(String name, double value) throws ConfigurationException {
        if (name.toLowerCase().contains("prob") && (value < 0 || value > 1)) {
            throw new ConfigurationException(
                    String.format("Probability parameter '%s' must be between 0 and 1, got: %f",
                            name, value));
        }

        if (value < 0) {
            throw new ConfigurationException(
                    String.format("Parameter '%s' cannot be negative, got: %f", name, value));
        }
    }
}
