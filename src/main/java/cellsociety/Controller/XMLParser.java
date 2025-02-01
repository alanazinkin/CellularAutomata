package cellsociety.Controller;

import org.w3c.dom.Document;
import javax.xml.parsers.*;
import java.io.*;


public class XMLParser {

    public SimulationConfig parseXMLFile(String filePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(filePath));
        document.getDocumentElement().normalize();

        String type = getElementContent(document, "type");
        String title = getElementContent(document, "title");
        String author = getElementContent(document, "author");
        String description = getElementContent(document, "description");
        int width = Integer.parseInt(getElementContent(document, "width"));
        int height = Integer.parseInt(getElementContent(document, "height"));

        String[] statesStr = getElementContent(document, "initial_states").split("\\s+");
        int[] initialStates = new int[statesStr.length];
        for (int i = 0; i < statesStr.length; i++) {
            initialStates[i] = Integer.parseInt(statesStr[i]);
        }

        SimulationParameter parameters = new SimulationParameter();

        return new SimulationConfig(type, title, author, description, width, height,
                initialStates, parameters);
    }

    private String getElementContent(Document doc, String tagName) {
        return doc.getElementsByTagName(tagName).item(0).getTextContent();
    }

    public void validateConfig(SimulationConfig config) throws IllegalArgumentException {

        for (int state : config.getInitialStates()) {
            if (state != 0 && state != 1) {
                throw new IllegalArgumentException("Invalid state value found: " + state +
                        ". Game of Life only accepts 0 (DEAD) or 1 (ALIVE)");
            }
        }

        if (!"Game of Life".equals(config.getType())) {
            throw new IllegalArgumentException("Invalid simulation type. Expected 'Game of Life', found: " +
                    config.getType());
        }
        
        if (config.getWidth() <= 0 || config.getHeight() <= 0) {
            throw new IllegalArgumentException("Grid dimensions must be positive");
        }
    }

}
