package cellsociety.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class XMLCellShapeParser {
    private static final Set<String> VALID_SHAPES = Set.of("Rectangle", "Triangle", "Hexagon", "Parallelogram");

    /**
     * Parses cell shapes from the given XML document and validates them.
     *
     * @param doc The XML document containing cell shape elements.
     * @return A map of cell shapes to their corresponding shape values as strings.
     * @throws ConfigurationException if a state is missing, or it has an empty name.
     */
    public Map<Integer, String> parseCellShapesWithValidation(Document doc)
            throws ConfigurationException {
        Map<Integer, String> cellShapes = new HashMap<>();

        NodeList cellStateNodes = doc.getElementsByTagName("cell_state");
        for (int i = 0; i < cellStateNodes.getLength(); i++) {
            Element stateElement = (Element) cellStateNodes.item(i);
            String state = stateElement.getAttribute("state");
            String shape = stateElement.getAttribute("shape");

            if (state.isEmpty()) {
                throw new ConfigurationException("State name cannot be empty");
            }
            if (shape.isEmpty()) {
                throw new ConfigurationException("Shape name cannot be empty");
            }
            if (!VALID_SHAPES.contains(shape)) {
                throw new ConfigurationException(shape + " is not a valid cell shape");
            }
            try {
                int value = Integer.parseInt(state);
                cellShapes.put(value, shape);
            } catch (NumberFormatException e) {
                throw new ConfigurationException(
                        String.format("Invalid numerical value for state '%s': %s", state, shape));
            }
        }
        return cellShapes;
    }
}
