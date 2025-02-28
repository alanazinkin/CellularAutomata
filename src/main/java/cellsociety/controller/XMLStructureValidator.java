package cellsociety.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class XMLStructureValidator {
    private static final String SIMULATION_TAG = "simulation";
    private static final Set<String> VALID_ROOT_CHILDREN = Set.of(
            "type", "title", "author", "description", "width", "height",
            "cell", "initial_states", "parameter", "random_states", "random_proportions", "cell_state", "tiling"
    );

    /**
     * Validates the structure of the provided XML document.
     * Ensures it contains the expected root element and structure.
     *
     * @param doc The XML document to be validated.
     * @throws ConfigurationException If the document is empty, malformed, or missing required elements.
     */
    public void validateXMLStructure(Document doc) throws ConfigurationException {
        if (doc.getDocumentElement() == null) {
            throw new ConfigurationException("Empty or malformed XML document");
        }

        if (!SIMULATION_TAG.equals(doc.getDocumentElement().getTagName())) {
            throw new ConfigurationException("Root element must be 'simulation', found: " +
                    doc.getDocumentElement().getTagName());
        }

        NodeList rootChildren = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < rootChildren.getLength(); i++) {
            Node child = rootChildren.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE &&
                    !VALID_ROOT_CHILDREN.contains(child.getNodeName())) {
                throw new ConfigurationException(
                        "Unexpected element in simulation configuration: " + child.getNodeName());
            }
        }
    }

    /**
     * Validates that required fields exist and are non-empty in the given XML document.
     *
     * @param doc The XML document to validate.
     * @throws ConfigurationException if any required field is missing or empty.
     */
    public void validateRequiredFields(Document doc) throws ConfigurationException {
        Map<String, String> missingOrEmptyFields = new HashMap<>();
        String[] requiredFields = {"type", "title", "author", "description"};

        for (String field : requiredFields) {
            NodeList nodes = doc.getElementsByTagName(field);
            if (nodes.getLength() == 0) {
                missingOrEmptyFields.put(field, "Missing field");
            } else {
                String content = nodes.item(0).getTextContent().trim();
                if (content.isEmpty()) {
                    missingOrEmptyFields.put(field, "Empty field");
                }
            }
        }

        if (!missingOrEmptyFields.isEmpty()) {
            StringBuilder errorMsg = new StringBuilder("Configuration errors found:");
            for (Map.Entry<String, String> entry : missingOrEmptyFields.entrySet()) {
                errorMsg.append("\n- ").append(entry.getKey()).append(": ").append(entry.getValue());
            }
            throw new ConfigurationException(errorMsg.toString());
        }
    }
}
