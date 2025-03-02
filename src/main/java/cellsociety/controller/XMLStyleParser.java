package cellsociety.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XMLStyleParser {

    private final FileValidator fileValidator;
    private final XMLStructureValidator structureValidator;

    /**
     * Constructs an XMLStyleParser with validators.
     */
    public XMLStyleParser() {
        this.fileValidator = new FileValidator();
        this.structureValidator = new XMLStructureValidator();
    }

    /**
     * Parses an XML style file and returns a SimulationStyle object.
     *
     * @param stylePath The path to the XML style file.
     * @return A populated SimulationStyle object.
     * @throws ConfigurationException If an error occurs during parsing or validation.
     */
    public SimulationStyle parseStyleFile(String stylePath) throws ConfigurationException {
        fileValidator.validateFile(stylePath);
        try {
            Document document = loadXMLDocument(stylePath);
            return parseStyleDocument(document);
        } catch (ParserConfigurationException e) {
            throw new ConfigurationException("XML parser configuration error: " + e.getMessage());
        } catch (SAXException e) {
            throw new ConfigurationException("Invalid XML format: " + e.getMessage());
        } catch (Exception e) {
            throw new ConfigurationException("Error reading style file: " + e.getMessage());
        }
    }

    /**
     * Loads an XML document from a file path.
     *
     * @param filePath The path to the XML file.
     * @return The loaded Document object.
     * @throws ParserConfigurationException If there are parser configuration issues.
     * @throws SAXException If there are XML parsing issues.
     * @throws IOException If there are file reading issues.
     */
    private Document loadXMLDocument(String filePath)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(filePath));
        document.getDocumentElement().normalize();
        return document;
    }

    /**
     * Parses the XML document to create a SimulationStyle object.
     *
     * @param document The XML document to parse.
     * @return A populated SimulationStyle object.
     * @throws ConfigurationException If validation or parsing errors occur.
     */
    private SimulationStyle parseStyleDocument(Document document) throws ConfigurationException {
        structureValidator.validateStyleXMLStructure(document);

        SimulationStyle style = new SimulationStyle();

        parseCellStateAppearances(document, style);

        parseGridProperties(document, style);

        parseDisplayOptions(document, style);

        return style;
    }

    /**
     * Parses cell state appearance definitions from the XML document.
     *
     * @param document The XML document to parse.
     * @param style The SimulationStyle object to populate.
     * @throws ConfigurationException If parsing errors occur.
     */
    private void parseCellStateAppearances(Document document, SimulationStyle style) throws ConfigurationException {
        Element cellStatesElement = (Element) document.getElementsByTagName("cell-states").item(0);
        if (cellStatesElement == null) {
            return; 
        }

        NodeList stateNodes = cellStatesElement.getElementsByTagName("state");
        Map<String, CellAppearance> appearances = new HashMap<>();

        for (int i = 0; i < stateNodes.getLength(); i++) {
            Element stateElement = (Element) stateNodes.item(i);
            String stateName = stateElement.getAttribute("name");

            if (stateName.isEmpty()) {
                throw new ConfigurationException("Cell state must have a name attribute");
            }

            CellAppearance appearance = new CellAppearance();

            if (stateElement.hasAttribute("color")) {
                appearance.setColor(stateElement.getAttribute("color"));
            }

            NodeList imageNodes = stateElement.getElementsByTagName("image");
            if (imageNodes.getLength() > 0) {
                Element imageElement = (Element) imageNodes.item(0);
                appearance.setImagePath(imageElement.getTextContent());
            }

            appearances.put(stateName, appearance);
        }

        style.setCellAppearances(appearances);
    }

    /**
     * Parses grid property definitions from the XML document.
     *
     * @param document The XML document to parse.
     * @param style The SimulationStyle object to populate.
     * @throws ConfigurationException If parsing errors occur.
     */
    private void parseGridProperties(Document document, SimulationStyle style) throws ConfigurationException {
        Element gridElement = (Element) document.getElementsByTagName("grid").item(0);
        if (gridElement == null) {
            return;
        }

        NodeList edgePolicyNodes = gridElement.getElementsByTagName("edge-policy");
        if (edgePolicyNodes.getLength() > 0) {
            String edgePolicy = edgePolicyNodes.item(0).getTextContent();
            style.setEdgePolicy(EdgePolicy.valueOf(edgePolicy.toUpperCase()));
        }

        NodeList cellShapeNodes = gridElement.getElementsByTagName("cell-shape");
        if (cellShapeNodes.getLength() > 0) {
            String cellShape = cellShapeNodes.item(0).getTextContent();
            style.setCellShape(CellShape.valueOf(cellShape.toUpperCase()));
        }

        NodeList neighborNodes = gridElement.getElementsByTagName("neighbor-arrangement");
        if (neighborNodes.getLength() > 0) {
            String neighborArrangement = neighborNodes.item(0).getTextContent();
            style.setNeighborArrangement(NeighborArrangement.valueOf(neighborArrangement.toUpperCase()));
        }
    }

    /**
     * Parses display option definitions from the XML document.
     *
     * @param document The XML document to parse.
     * @param style The SimulationStyle object to populate.
     * @throws ConfigurationException If parsing errors occur.
     */
    private void parseDisplayOptions(Document document, SimulationStyle style) throws ConfigurationException {
        Element displayElement = (Element) document.getElementsByTagName("display").item(0);
        if (displayElement == null) {
            return;
        }

        NodeList gridOutlineNodes = displayElement.getElementsByTagName("grid-outline");
        if (gridOutlineNodes.getLength() > 0) {
            boolean showGridOutline = Boolean.parseBoolean(gridOutlineNodes.item(0).getTextContent());
            style.setShowGridOutline(showGridOutline);
        }

        NodeList colorThemeNodes = displayElement.getElementsByTagName("color-theme");
        if (colorThemeNodes.getLength() > 0) {
            String colorTheme = colorThemeNodes.item(0).getTextContent();
            style.setColorTheme(ColorTheme.valueOf(colorTheme.toUpperCase()));
        }

        NodeList animationSpeedNodes = displayElement.getElementsByTagName("animation-speed");
        if (animationSpeedNodes.getLength() > 0) {
            double animationSpeed = Double.parseDouble(animationSpeedNodes.item(0).getTextContent());
            style.setAnimationSpeed(animationSpeed);
        }
    }
}
