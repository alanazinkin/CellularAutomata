package cellsociety.controller;

import cellsociety.model.Grid;

import java.io.IOException;

public interface SimulationWriter {
    void save(SimulationConfig config, Grid grid, String filePath) throws IOException;
}
