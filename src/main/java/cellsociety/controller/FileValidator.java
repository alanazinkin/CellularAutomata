package cellsociety.controller;

import java.io.File;

/**
 * A utility class for validating configuration files.
 * Ensures that the file exists, is readable, is not empty, and has the correct XML format.
 *
 * @author angelapredolac
 */
class FileValidator {

    /**
     * Validates if a file exists, is readable, and has the correct format.
     *
     * @param filePath The path to the file to be validated.
     * @throws ConfigurationException If the file does not exist, is unreadable, empty, or not an XML file.
     */
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
}
