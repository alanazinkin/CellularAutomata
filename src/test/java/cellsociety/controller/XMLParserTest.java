package cellsociety.Controller;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class XMLParserTest {

    String filepath = "data/GameOfLife/GliderPattern.xml";
    XMLParser xmlParser = new XMLParser();
    SimulationConfig simulationConfig = xmlParser.parseXMLFile(filepath);
    int[] initialStates = {0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1};
    XMLParserTest() throws Exception {
    }

    @Test
    void parseXMLFile() {
        assertEquals("Game of Life", simulationConfig.getType());
        assertEquals("Game of Life Simulation", simulationConfig.getTitle());
        assertEquals("A classic cellular automaton where cells on a grid live, die, or reproduce based on their neighbors", simulationConfig.getDescription());
        assertEquals(5, simulationConfig.getWidth());
        assertEquals(5, simulationConfig.getHeight());
        assertArrayEquals(initialStates, simulationConfig.getInitialStates());
        assertEquals(0, simulationConfig.getParameters().size());
    }

}