package cellsociety.Controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimulationConfigTest {

    int[] initialStates = {0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1};
    SimulationConfig simulationConfig = new SimulationConfig("Game of Life", "Game of Life Simulation", "Alana Zinkin", "A classic cellular automaton where cells on a grid live, die, or reproduce based on their neighbors", 5, 5, initialStates, new SimulationParameter());

    @Test
    void getType() {
        assertEquals("Game of Life", simulationConfig.getType());
    }

    @Test
    void getTitle() {
        assertEquals("Game of Life Simulation", simulationConfig.getTitle());
    }

    @Test
    void getAuthor() {
        assertEquals("Alana Zinkin", simulationConfig.getAuthor());
    }

    @Test
    void getDescription() {
        assertEquals("A classic cellular automaton where cells on a grid live, die, or reproduce based on their neighbors", simulationConfig.getDescription());
    }

    @Test
    void getWidth() {
        assertEquals(5, simulationConfig.getWidth());
    }

    @Test
    void getHeight() {
        assertEquals(5, simulationConfig.getHeight());
    }

    @Test
    void getInitialStates() {
        assertEquals(initialStates, simulationConfig.getInitialStates());
    }

}