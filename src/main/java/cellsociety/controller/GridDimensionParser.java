package cellsociety.controller;

class GridDimensionParser {
    /**
     * Parses and sets the grid dimensions from the provided configuration values.
     *
     * @param widthStr  The width value as a string.
     * @param heightStr The height value as a string.
     * @param config    The simulation configuration object to be updated.
     * @throws ConfigurationException If dimensions are missing, invalid, or non-positive.
     */
    public static void setGridDimensions(String widthStr, String heightStr, SimulationConfig config)
            throws ConfigurationException {
        validateGridDimensions(widthStr, heightStr, config);
    }

    static void validateGridDimensions(String widthStr, String heightStr, SimulationConfig config) throws ConfigurationException {
        if (widthStr == null || heightStr == null || widthStr.isEmpty() || heightStr.isEmpty()) {
            throw new ConfigurationException("Width and height must be specified in the configuration");
        }

        try {
            int width = Integer.parseInt(widthStr.trim());
            int height = Integer.parseInt(heightStr.trim());

            if (width <= 0 || height <= 0) {
                throw new ConfigurationException(
                        String.format("Invalid grid dimensions: width=%d, height=%d. Must be positive values.",
                                width, height));
            }

            config.setWidth(width);
            config.setHeight(height);
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Grid dimensions must be valid integers");
        }
    }
}
