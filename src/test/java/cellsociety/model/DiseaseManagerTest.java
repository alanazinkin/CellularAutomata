package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cellsociety.model.simulations.SugarScape;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for DiseaseManager.
 * Tests the disease transmission and immune system interaction functionality.
 */
public class DiseaseManagerTest {

  private DiseaseManager diseaseManager;
  private SugarScape mockSimulation;
  private Agent mockAgent1;
  private Agent mockAgent2;
  private Disease mockDisease;
  private List<Agent> agents;
  private Grid mockGrid;
  private Cell mockCell1;
  private Cell mockCell2;

  @BeforeEach
  void setUp() {
    mockSimulation = mock(SugarScape.class);
    mockGrid = mock(Grid.class);
    mockCell1 = mock(Cell.class);
    mockCell2 = mock(Cell.class);
    mockAgent1 = mock(Agent.class);
    mockAgent2 = mock(Agent.class);
    mockDisease = new Disease("10101010");

    when(mockSimulation.getRandom()).thenReturn(new Random(42)); // Fixed seed
    when(mockSimulation.getGrid()).thenReturn(mockGrid);

    when(mockAgent1.getPosition()).thenReturn(mockCell1);
    when(mockAgent2.getPosition()).thenReturn(mockCell2);

    agents = new ArrayList<>();
    agents.add(mockAgent1);
    agents.add(mockAgent2);

    diseaseManager = new DiseaseManager(mockSimulation);
  }

  /**
   * Tests that diseases are properly transmitted between neighboring agents.
   */
  @Test
  void applyDiseaseRules_DiseaseTransmission_TransmitsToNeighbors() {
    List<Disease> diseases = new ArrayList<>();
    diseases.add(mockDisease);
    when(mockAgent1.getDiseases()).thenReturn(diseases);
    when(mockAgent2.getDiseases()).thenReturn(new ArrayList<>());
    when(mockAgent1.getRandomDisease()).thenReturn(mockDisease);

    List<Agent> neighbors = new ArrayList<>();
    neighbors.add(mockAgent2);

    // Mock the static GridOperations method
    try (var mockedStatic = mockStatic(GridOperations.class)) {
      mockedStatic.when(() -> GridOperations.getAgentNeighbors(eq(mockAgent1), any()))
          .thenReturn(neighbors);

      diseaseManager.applyDiseaseRules(agents);

      verify(mockAgent2).addDisease(any(Disease.class));
    }
  }

  /**
   * Tests that an agent's immune system is updated when infected with a disease.
   */
  @Test
  void applyDiseaseRules_ImmuneSystemUpdate_UpdatesImmuneSystem() {
    List<Disease> diseases = new ArrayList<>();
    diseases.add(mockDisease);
    when(mockAgent1.getDiseases()).thenReturn(diseases);
    when(mockAgent1.getRandomDisease()).thenReturn(mockDisease);

    when(mockGrid.getRows()).thenReturn(5);
    when(mockGrid.getCols()).thenReturn(5);

    try (var mockedStatic = mockStatic(GridOperations.class)) {
      mockedStatic.when(() -> GridOperations.getAgentNeighbors(eq(mockAgent1), any()))
          .thenReturn(new ArrayList<>());

      diseaseManager.applyDiseaseRules(agents);

      verify(mockAgent1).updateImmuneSystem(mockDisease);
      verify(mockAgent1).checkAndRemoveDiseases();
    }
  }

  /**
   * Tests that no transmission occurs when infected agent has no neighbors.
   */
  @Test
  void applyDiseaseRules_NoNeighbors_NoTransmission() {
    List<Disease> diseases = new ArrayList<>();
    diseases.add(mockDisease);
    when(mockAgent1.getDiseases()).thenReturn(diseases);

    try (var mockedStatic = mockStatic(GridOperations.class)) {
      mockedStatic.when(() -> GridOperations.getAgentNeighbors(eq(mockAgent1), any()))
          .thenReturn(new ArrayList<>());

      diseaseManager.applyDiseaseRules(agents);

      verify(mockAgent2, never()).addDisease(any(Disease.class));
    }
  }

  /**
   * Tests that no immune system update occurs for agents without diseases.
   */
  @Test
  void applyDiseaseRules_NoDisease_NoImmuneSystemUpdate() {
    when(mockAgent1.getDiseases()).thenReturn(new ArrayList<>());

    diseaseManager.applyDiseaseRules(agents);

    verify(mockAgent1, never()).updateImmuneSystem(any(Disease.class));
    verify(mockAgent1, never()).checkAndRemoveDiseases();
  }

  /**
   * Tests that a NullPointerException is thrown when agent list is null.
   */
  @Test
  void applyDiseaseRules_NullAgentList_ThrowsException() {
    NullPointerException thrown = assertThrows(NullPointerException.class, () ->
        diseaseManager.applyDiseaseRules(null));
    assertNotNull(thrown.getMessage());
  }

  /**
   * Tests that disease cloning works during transmission.
   */
  @Test
  void applyDiseaseRules_DiseaseTransmission_ClonesDiseases() {
    List<Disease> diseases = new ArrayList<>();
    diseases.add(mockDisease);
    when(mockAgent1.getDiseases()).thenReturn(diseases);
    when(mockAgent1.getRandomDisease()).thenReturn(mockDisease);

    List<Agent> neighbors = new ArrayList<>();
    neighbors.add(mockAgent2);

    try (var mockedStatic = mockStatic(GridOperations.class)) {
      mockedStatic.when(() -> GridOperations.getAgentNeighbors(eq(mockAgent1), any()))
          .thenReturn(neighbors);

      diseaseManager.applyDiseaseRules(agents);

      verify(mockAgent2).addDisease(argThat(disease ->
          disease != null && disease != mockDisease));
    }
  }
}