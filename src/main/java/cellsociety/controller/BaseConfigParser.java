package cellsociety.controller;

import java.util.*;

/**
 * An abstract base class for parsing simulation configuration files.
 * Provides common validation logic and utilities for ensuring
 * configurations adhere to expected formats and constraints.
 *
 * @author angelapredolac
 */
public abstract class BaseConfigParser implements SimulationConfigParser {
    protected static final Set<String> VALID_SIMULATION_TYPES = Set.of(
            "Game of Life", "Spreading of Fire", "Schelling Segregation",
            "Percolation", "Wa-Tor World", "Langton Loop", "Sugar Scape", "Bacteria", "Foraging Ants",
        "Tempesti Loop"
    );

    /**
     * Mapping of valid states for each simulation type.
     */
    protected static final Map<String, Set<Integer>> VALID_STATES = Map.of(
            "Game of Life", Set.of(0, 1),
            "Spreading of Fire", Set.of(0, 1, 2),
            "Schelling Segregation", Set.of(0, 1, 2),
            "Percolation", Set.of(0, 1, 2),
            "Wa-Tor World", Set.of(0, 1, 2),
            "Langton Loop", Set.of(0, 1, 2, 3, 4, 5, 6, 7),
            "Sugar Scape", Set.of(0, 1, 2),
            "Bacteria", Set.of(0, 1, 2),
        "Foraging Ants", Set.of(0, 1, 2, 3),
        "Tempesti Loop", Set.of(0, 1, 2, 3, 4, 5, 6, 7)
    );

    protected final FileValidator fileValidator;
    protected final ResourceBundle defaultProperties;

    /**
     * Constructs a BaseConfigParser with the given file validator and properties file.
     *
     * @param fileValidator  the validator to ensure file integrity
     * @param propertiesPath the path to the resource bundle containing default properties
     */
    protected BaseConfigParser(FileValidator fileValidator, String propertiesPath) {
        this.fileValidator = fileValidator;
        this.defaultProperties = ResourceBundle.getBundle(propertiesPath);
    }

    /**
     * Parses a simulation configuration file.
     *
     * @param filePath the path to the configuration file
     * @return the parsed simulation configuration
     * @throws ConfigurationException if the configuration is invalid
     */
    @Override
    public SimulationConfig parse(String filePath) throws ConfigurationException {
        fileValidator.validateFile(filePath);
        return parseConfig(filePath);
    }

    /**
     * Abstract method for parsing a specific configuration file.
     *
     * @param filePath the path to the configuration file
     * @return the parsed simulation configuration
     * @throws ConfigurationException if the configuration is invalid
     */
    protected abstract SimulationConfig parseConfig(String filePath) throws ConfigurationException;

    /**
     * Validates whether a given simulation type is supported.
     *
     * @param type the simulation type to validate
     * @throws ConfigurationException if the simulation type is not recognized
     */
    protected void validateSimulationType(String type) throws ConfigurationException {
        if (!VALID_SIMULATION_TYPES.contains(type)) {
            throw new ConfigurationException("Invalid simulation type: " + type +
                    ". Valid types are: " + String.join(", ", VALID_SIMULATION_TYPES));
        }
    }
}
