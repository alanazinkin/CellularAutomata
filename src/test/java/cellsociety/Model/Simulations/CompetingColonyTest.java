package cellsociety.Model.Simulations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.Cell;
import cellsociety.Model.ColonyState;
import cellsociety.Model.Grid;
import cellsociety.Model.StateInterface;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests for {@link CompetingColony} cellular automaton rules and configuration.
 * Verifies proper state transitions, parameter validation, and exception handling for invalid inputs.
 *
 * @author Your Name
 * @see CompetingColony
 */
class CompetingColonyTest {

  private static final int VALID_STATES = 3;
  private static final double VALID_THRESHOLD = 30.0;
  private SimulationConfig validConfig;
  private Grid mockGrid;
  private Map<String, Number> validParams;

  @BeforeEach
  void setUp() {
    // Mock configuration setup
    validConfig = mock(SimulationConfig.class);
    mockGrid = mock(Grid.class);
    validParams = new HashMap<>();

    // Valid parameters
    validParams.put("numStates", 3);
    validParams.put("threshold", 30.0);

    // Configure mock returns
    when(validConfig.getParameters()).thenReturn(validParams);
    when(mockGrid.getRows()).thenReturn(5);
    when(mockGrid.getCols()).thenReturn(5);
  }

  @Test
  void constructor_WithValidParameters_DoesNotThrowException() {
    assertDoesNotThrow(() -> new CompetingColony(validConfig, mockGrid));
  }

  @Test
  void constructor_InvalidNumStates_ThrowsIllegalArgumentException() {
    SimulationConfig invalidConfig = mock(SimulationConfig.class);
    Map<String, Number> invalidParams = new HashMap<>();
    invalidParams.put("numStates", 1);
    invalidParams.put("threshold", VALID_THRESHOLD);
    when(invalidConfig.getParameters()).thenReturn(invalidParams);

    assertThrows(IllegalArgumentException.class,
        () -> new CompetingColony(invalidConfig, mockGrid));
  }

  @Test
  void applyRules_CellWithDominantNeighborsAboveThreshold_ChangesState() {
    // Arrange
    Cell testCell = new Cell(0, 0, new ColonyState(0));
    List<Cell> neighbors = Arrays.asList(
        new Cell(0, 0, new ColonyState(1)),
        new Cell(0, 0, new ColonyState(1)), // 2/8 neighbors = 25% (below 30% threshold)
        new Cell(0, 0, new ColonyState(2))
    );
    when(mockGrid.getCell(0, 0)).thenReturn(testCell);
    when(mockGrid.getNeighbors(0, 0)).thenReturn(neighbors);

    // Act
    CompetingColony sim = new CompetingColony(validConfig, mockGrid);
    sim.applyRules();

    // Assert
    assertEquals(new ColonyState(1), testCell.getNextState());
  }

  @Test
  void applyRules_CellWithDominantNeighborsBelowThreshold_KeepsOriginalState() {
    // Arrange
    Cell testCell = new Cell(0, 0, new ColonyState(1));
    List<Cell> neighbors = Arrays.asList(
        new Cell(0, 0, new ColonyState(2)),
        new Cell(0, 0, new ColonyState(0)) // 1/8 neighbors = 12.5%
    );
    when(mockGrid.getCell(0, 0)).thenReturn(testCell);
    when(mockGrid.getNeighbors(0, 0)).thenReturn(neighbors);

    // Act
    CompetingColony sim = new CompetingColony(validConfig, mockGrid);
    sim.applyRules();

    // Assert
    assertEquals(new ColonyState(1), testCell.getNextState());
  }

  @Test
  void applyRules_TopStateCellWithWrappingDominance_ConvertsToBaseState() {
    // Arrange
    SimulationConfig config = mock(SimulationConfig.class);
    Map<String, Number> params = new HashMap<>();
    params.put("numStates", 3);
    params.put("threshold", 34.0);
    when(config.getParameters()).thenReturn(params);

    Cell testCell = new Cell(0, 0, new ColonyState(2));
    List<Cell> neighbors = Arrays.asList(
        new Cell(0, 0, new ColonyState(0)),
        new Cell(0, 0, new ColonyState(0)), // 2/6 neighbors = 33.3%
        new Cell(0, 0, new ColonyState(0))  // 3/6 neighbors = 50%
    );
    when(mockGrid.getCell(0, 0)).thenReturn(testCell);
    when(mockGrid.getNeighbors(0, 0)).thenReturn(neighbors);

    // Act
    CompetingColony sim = new CompetingColony(config, mockGrid);
    sim.applyRules();

    // Assert
    assertEquals(new ColonyState(0), testCell.getNextState());
  }

  @Test
  void initializeStateMap_CreatesCorrectNumberOfStates() {
    CompetingColony sim = new CompetingColony(validConfig, mockGrid);
    Map<Integer, StateInterface> states = sim.initializeStateMap();
    assertEquals(VALID_STATES, states.size());
  }
}