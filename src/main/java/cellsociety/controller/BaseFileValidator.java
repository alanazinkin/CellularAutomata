package cellsociety.controller;

import java.io.File;

/**
 * A base implementation of a file validator that checks for the existence, readability,
 * and validity of a configuration file.
 *
 * @author angelapredolac
 */
public class BaseFileValidator extends FileValidator {

    /**
     * Validates the given file path by checking if the file exists, is readable, is not empty,
     * and has a valid file extension.
     *
     * @param filePath The path to the configuration file.
     * @throws ConfigurationException If the file does not exist, is unreadable, is empty,
     *                                or has an invalid file extension.
     */
    @Override
    public void validateFile(String filePath) throws ConfigurationException {
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
        if (!isValidFileExtension(extension)) {
            throw new ConfigurationException("Invalid file type. Expected " +
                    getExpectedFileExtension() + " file, got: " + extension);
        }
    }

    /**
     * Extracts the file extension from the given file path.
     *
     * @param filePath The file path to extract the extension from.
     * @return The file extension as a string, or an empty string if none is found.
     */
    protected String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1) {
            return filePath.substring(lastDotIndex + 1);
        }
        return "";
    }

    /**
     * Determines whether the given file extension is valid.
     * This method can be overridden in subclasses to enforce specific file types.
     *
     * @param extension The file extension to validate.
     * @return {@code true} if the extension is valid, {@code false} otherwise.
     */
    protected boolean isValidFileExtension(String extension) {
        return true;
    }

    /**
     * Returns the expected file extension.
     * This method can be overridden in subclasses to specify a required file type.
     *
     * @return The expected file extension as a string.
     */
    protected String getExpectedFileExtension() {
        return "";
    }
}
