package cellsociety.controller;

import cellsociety.model.Grid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class BaseFileWriter implements SimulationWriter {

    protected void writeToFile(String filePath, String content) throws IOException {
        Files.write(Paths.get(filePath), content.getBytes());
    }

    protected abstract String formatContent(SimulationConfig config, Grid grid);
}
