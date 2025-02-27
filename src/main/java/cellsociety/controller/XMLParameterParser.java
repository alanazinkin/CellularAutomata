package cellsociety.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

class XMLParameterParser {
    /**
     * Parses parameters from the given XML document and validates them.
     *
     * @param doc The XML document containing parameter elements.
     * @return A map of parameter names to their corresponding double values.
     * @throws ConfigurationException if a parameter is missing, has an empty name, or contains an invalid value.
     */
    public Map<String, Double> parseParametersWithValidation(Document doc)
            throws ConfigurationException {
        Map<String, Double> parameters = new HashMap<>();

        NodeList paramNodes = doc.getElementsByTagName("parameter");
        for (int i = 0; i < paramNodes.getLength(); i++) {
            Element paramElement = (Element) paramNodes.item(i);
            String name = paramElement.getAttribute("name");
            String valueStr = paramElement.getAttribute("value");

            if (name.isEmpty()) {
                throw new ConfigurationException("Parameter name cannot be empty");
            }

            try {
                double value = Double.parseDouble(valueStr);
                validateParameterValue(name, value);
                parameters.put(name, value);
            } catch (NumberFormatException e) {
                throw new ConfigurationException(
                        String.format("Invalid numerical value for parameter '%s': %s", name, valueStr));
            }
        }
        return parameters;
    }

    /**
     * Validates the value of a parameter based on parameter-specific rules.
     * This method should be implemented with specific validation logic.
     *
     * @param name The name of the parameter.
     * @param value The value to validate.
     * @throws ConfigurationException If the parameter value is invalid.
     */
    private void validateParameterValue(String name, double value) throws ConfigurationException {
        validateParamHelper(name, value);
    }

    static void validateParamHelper(String name, double value) throws ConfigurationException {
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
