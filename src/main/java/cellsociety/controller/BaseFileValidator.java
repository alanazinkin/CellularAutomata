package cellsociety.controller;

import java.io.File;

public class BaseFileValidator implements FileValidator {
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

    protected String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1) {
            return filePath.substring(lastDotIndex + 1);
        }
        return "";
    }

    protected boolean isValidFileExtension(String extension) {
        return true;
    }

    protected String getExpectedFileExtension() {
        return "";
    }
}
