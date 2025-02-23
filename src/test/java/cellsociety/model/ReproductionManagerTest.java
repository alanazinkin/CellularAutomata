package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cellsociety.model.simulations.SugarScape;
import cellsociety.model.state.SugarScapeState;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for ReproductionManager.
 * Tests the agent reproduction functionality in the SugarScape simulation.
 */
public class ReproductionManagerTest {

  private ReproductionManager reproductionManager;
  private SugarScape mockSimulation;
  private Grid mockGrid;
  private Agent mockParent1;
  private Agent mockParent2;
  private Cell mockEmptyCell;
  private List<Agent> agents;
  private List<Agent> simulationAgents;

  @BeforeEach
  void setUp() {
    mockSimulation = mock(SugarScape.class);
    mockGrid = mock(Grid.class);
    mockParent1 = mock(Agent.class);
    mockParent2 = mock(Agent.class);
    mockEmptyCell = mock(Cell.class);

    when(mockSimulation.getGrid()).thenReturn(mockGrid);
    when(mockSimulation.getRandom()).thenReturn(new Random(42));

    agents = new ArrayList<>();
    agents.add(mockParent1);
    agents.add(mockParent2);

    simulationAgents = new ArrayList<>();
    when(mockSimulation.getAgents()).thenReturn(simulationAgents);

    Cell mockParentCell = mock(Cell.class);
    when(mockParent1.getPosition()).thenReturn(mockParentCell);

    reproductionManager = new ReproductionManager(mockSimulation);
  }

  /**
   * Tests successful reproduction between two compatible agents.
   */
  @Test
  void applyReproduction_CompatibleAgents_ReproduceSuccessfully() {
    when(mockParent1.isFertile()).thenReturn(true);
    List<Agent> neighbors = new ArrayList<>();
    neighbors.add(mockParent2);

    try (var mockedStatic = mockStatic(GridOperations.class)) {
      mockedStatic.when(() -> GridOperations.getAgentNeighbors(mockParent1, mockSimulation))
          .thenReturn(neighbors);
      mockedStatic.when(() -> GridOperations.findAdjacentEmptyCell(mockParent1, mockSimulation))
          .thenReturn(mockEmptyCell);

      try (var mockedRulesOps = mockStatic(RulesOperations.class)) {
        mockedRulesOps.when(() -> RulesOperations.canReproduce(mockParent1, mockParent2))
            .thenReturn(true);
        Agent mockChild = mock(Agent.class);
        mockedRulesOps.when(() -> RulesOperations.reproduce(eq(mockParent1), eq(mockParent2),
            eq(mockEmptyCell), any(Random.class))).thenReturn(mockChild);

        reproductionManager.applyReproduction(agents);

        verify(mockEmptyCell).setNextState(SugarScapeState.AGENT);
        assertTrue(simulationAgents.contains(mockChild));
        verify(mockGrid).applyNextStates();
      }
    }
  }

  /**
   * Tests that no reproduction occurs when agents are not fertile.
   */
  @Test
  void applyReproduction_InfertileAgent_NoReproduction() {
    when(mockParent1.isFertile()).thenReturn(false);

    reproductionManager.applyReproduction(agents);

    assertEquals(0, simulationAgents.size());
    verify(mockGrid).applyNextStates();
  }

  /**
   * Tests that no reproduction occurs when there are no empty adjacent cells.
   */
  @Test
  void applyReproduction_NoEmptyCells_NoReproduction() {
    when(mockParent1.isFertile()).thenReturn(true);
    List<Agent> neighbors = new ArrayList<>();
    neighbors.add(mockParent2);

    try (var mockedStatic = mockStatic(GridOperations.class)) {
      mockedStatic.when(() -> GridOperations.getAgentNeighbors(mockParent1, mockSimulation))
          .thenReturn(neighbors);
      mockedStatic.when(() -> GridOperations.findAdjacentEmptyCell(mockParent1, mockSimulation))
          .thenReturn(null);

      try (var mockedRulesOps = mockStatic(RulesOperations.class)) {
        mockedRulesOps.when(() -> RulesOperations.canReproduce(mockParent1, mockParent2))
            .thenReturn(true);

        reproductionManager.applyReproduction(agents);

        assertEquals(0, simulationAgents.size());
        verify(mockGrid).applyNextStates();
      }
    }
  }

  /**
   * Tests that reproduction is skipped when agents are incompatible.
   */
  @Test
  void applyReproduction_IncompatibleAgents_NoReproduction() {
    when(mockParent1.isFertile()).thenReturn(true);
    List<Agent> neighbors = new ArrayList<>();
    neighbors.add(mockParent2);

    try (var mockedStatic = mockStatic(GridOperations.class)) {
      mockedStatic.when(() -> GridOperations.getAgentNeighbors(mockParent1, mockSimulation))
          .thenReturn(neighbors);

      try (var mockedRulesOps = mockStatic(RulesOperations.class)) {
        mockedRulesOps.when(() -> RulesOperations.canReproduce(mockParent1, mockParent2))
            .thenReturn(false);

        reproductionManager.applyReproduction(agents);

        assertEquals(0, simulationAgents.size());
        verify(mockGrid).applyNextStates();
      }
    }
  }

  /**
   * Tests that a NullPointerException is thrown when agent list is null.
   */
  @Test
  void applyReproduction_NullAgentList_ThrowsException() {
    NullPointerException thrown = assertThrows(NullPointerException.class, () ->
        reproductionManager.applyReproduction(null));
    assertNotNull(thrown.getMessage());
  }

  /**
   * Tests that a NullPointerException is thrown when an agent has null position.
   */
  @Test
  void applyReproduction_AgentWithNullPosition_ThrowsException() {
    when(mockParent1.isFertile()).thenReturn(true);
    when(mockParent1.getPosition()).thenReturn(null);

    List<Agent> neighbors = new ArrayList<>();
    neighbors.add(mockParent2);
    try (var mockedStatic = mockStatic(GridOperations.class)) {
      mockedStatic.when(() -> GridOperations.getAgentNeighbors(any(), any()))
          .thenThrow(new NullPointerException("Cannot get neighbors of agent with null position"));

      assertThrows(NullPointerException.class, () ->
          reproductionManager.applyReproduction(agents));
    }
  }

  /**
   * Tests that child agent is initialized with correct properties.
   */
  @Test
  void applyReproduction_SuccessfulReproduction_ChildPropertiesSet() {
    when(mockParent1.isFertile()).thenReturn(true);
    List<Agent> neighbors = new ArrayList<>();
    neighbors.add(mockParent2);

    Agent mockChild = mock(Agent.class);
    when(mockChild.getPosition()).thenReturn(mockEmptyCell);

    try (var mockedStatic = mockStatic(GridOperations.class)) {
      mockedStatic.when(() -> GridOperations.getAgentNeighbors(mockParent1, mockSimulation))
          .thenReturn(neighbors);
      mockedStatic.when(() -> GridOperations.findAdjacentEmptyCell(mockParent1, mockSimulation))
          .thenReturn(mockEmptyCell);

      try (var mockedRulesOps = mockStatic(RulesOperations.class)) {
        mockedRulesOps.when(() -> RulesOperations.canReproduce(mockParent1, mockParent2))
            .thenReturn(true);
        mockedRulesOps.when(() -> RulesOperations.reproduce(eq(mockParent1), eq(mockParent2),
            eq(mockEmptyCell), any(Random.class))).thenAnswer(invocation -> {
          if (mockChild != null) {
            mockChild.setFertile(false);
          }
          return mockChild;
        });

        reproductionManager.applyReproduction(agents);

        verify(mockChild).setFertile(false);
        verify(mockEmptyCell).setNextState(SugarScapeState.AGENT);
        verify(mockGrid).applyNextStates();
      }
    }
  }
}