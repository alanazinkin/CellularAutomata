package cellsociety.controller;

public interface SimulationConfigParser {
    SimulationConfig parse(String filePath) throws ConfigurationException;
}
