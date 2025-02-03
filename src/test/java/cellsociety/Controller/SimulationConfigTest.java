package cellsociety.Controller;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SimulationConfigTest {

    int[] initialStates = {0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1};
    SimulationConfig simulationConfig = new SimulationConfig("Game of Life", "Game of Life Simulation", "Alana Zinkin", "A classic cellular automaton where cells on a grid live, die, or reproduce based on their neighbors", 5, 5, initialStates, new Map<String, String>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public String get(Object key) {
            return "";
        }

        @Override
        public String put(String key, String value) {
            return "";
        }

        @Override
        public String remove(Object key) {
            return "";
        }

        @Override
        public void putAll(Map<? extends String, ? extends String> m) {

        }

        @Override
        public void clear() {

        }

        @Override
        public Set<String> keySet() {
            return Set.of();
        }

        @Override
        public Collection<String> values() {
            return List.of();
        }

        @Override
        public Set<Entry<String, String>> entrySet() {
            return Set.of();
        }
    });

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