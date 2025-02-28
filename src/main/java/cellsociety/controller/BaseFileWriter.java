package cellsociety.controller;

import cellsociety.model.Grid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * An abstract base class for writing simulation data to a file.
 * Provides a utility method for writing content to a file and
 * defines an abstract method for formatting simulation content.
 *
 * @author angelapredolac
 */
public abstract class BaseFileWriter implements SimulationWriter {

    /**
     * Writes the given content to the specified file path.
     *
     * @param filePath The path to the file where content should be written.
     * @param content  The content to write to the file.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    protected void writeToFile(String filePath, String content) throws IOException {
        Files.write(Paths.get(filePath), content.getBytes());
    }

    /**
     * Formats the simulation configuration and grid data into a writable content format.
     * This method must be implemented by subclasses to specify the output format.
     *
     * @param config The simulation configuration to be written.
     * @param grid   The grid representing the simulation state.
     * @return A formatted string representation of the simulation data.
     */
    protected abstract String formatContent(SimulationConfig config, Grid grid);
}
