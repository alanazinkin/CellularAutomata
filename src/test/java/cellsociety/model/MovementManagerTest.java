package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cellsociety.model.simulations.SugarScape;
import cellsociety.model.state.SugarScapeState;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for MovementManager. Tests the agent movement functionality in the SugarScape
 * simulation including valid moves, sugar collection, and edge cases.
 *
 * @author Tatum McKinnis
 */
public class MovementManagerTest {

  private MovementManager movementManager;
  private SugarScape mockSimulation;
  private Grid mockGrid;
  private Agent mockAgent;
  private SugarCell currentCell;
  private SugarCell targetCell;
  private List<Agent> agents;

  @BeforeEach
  void setUp() {
    mockSimulation = mock(SugarScape.class);
    mockGrid = mock(Grid.class);
    mockAgent = mock(Agent.class);
    currentCell = mock(SugarCell.class);
    targetCell = mock(SugarCell.class);

    when(mockSimulation.getGrid()).thenReturn(mockGrid);
    when(mockAgent.getPosition()).thenReturn(currentCell);

    agents = new ArrayList<>();
    agents.add(mockAgent);

    movementManager = new MovementManager(mockSimulation);
  }

  /**
   * Tests that an agent moves to the best available cell and collects sugar.
   */
  @Test
  void applyMovement_ValidMove_AgentMovesAndCollectsSugar() {
    List<Cell> validMoves = new ArrayList<>();
    validMoves.add(targetCell);
    when(targetCell.getSugar()).thenReturn(5);

    try (var mockedStatic = mockStatic(GridOperations.class)) {
      mockedStatic.when(() -> GridOperations.findValidMoves(mockAgent, mockSimulation))
          .thenReturn(validMoves);
      mockedStatic.when(() -> GridOperations.findBestMove(mockAgent, validMoves, mockSimulation))
          .thenReturn(targetCell);

      movementManager.applyMovement(agents);

      verify(currentCell).setNextState(SugarScapeState.EMPTY);
      verify(mockAgent).setPosition(targetCell);
      verify(mockAgent).addSugar(5);
      verify(targetCell).setSugar(0);
      verify(targetCell).setNextState(SugarScapeState.AGENT);
      verify(mockGrid).applyNextStates();
    }
  }

  /**
   * Tests that an agent stays in place when no valid moves are available.
   */
  @Test
  void applyMovement_NoValidMoves_AgentStaysInPlace() {
    try (var mockedStatic = mockStatic(GridOperations.class)) {
      mockedStatic.when(() -> GridOperations.findValidMoves(mockAgent, mockSimulation))
          .thenReturn(new ArrayList<>());

      movementManager.applyMovement(agents);

      verify(mockAgent, never()).setPosition(any());
      verify(currentCell, never()).setNextState(any());
      verify(mockAgent).metabolize();
    }
  }

  /**
   * Tests that agent metabolizes sugar after movement.
   */
  @Test
  void applyMovement_AfterMovement_AgentMetabolizes() {
    List<Cell> validMoves = new ArrayList<>();
    validMoves.add(targetCell);

    try (var mockedStatic = mockStatic(GridOperations.class)) {
      mockedStatic.when(() -> GridOperations.findValidMoves(mockAgent, mockSimulation))
          .thenReturn(validMoves);
      mockedStatic.when(() -> GridOperations.findBestMove(mockAgent, validMoves, mockSimulation))
          .thenReturn(targetCell);

      movementManager.applyMovement(agents);

      verify(mockAgent).metabolize();
    }
  }

  /**
   * Tests that multiple agents move in the correct order.
   */
  @Test
  void applyMovement_MultipleAgents_AllAgentsProcessed() {
    Agent mockAgent2 = mock(Agent.class);
    SugarCell cell2 = mock(SugarCell.class);
    when(mockAgent2.getPosition()).thenReturn(cell2);
    agents.add(mockAgent2);

    try (var mockedStatic = mockStatic(GridOperations.class)) {
      mockedStatic.when(() -> GridOperations.findValidMoves(any(), any()))
          .thenReturn(new ArrayList<>());

      movementManager.applyMovement(agents);

      verify(mockAgent).metabolize();
      verify(mockAgent2).metabolize();
      verify(mockGrid).applyNextStates();
    }
  }

  /**
   * Tests that agent chooses cell with most sugar when multiple moves available.
   */
  @Test
  void applyMovement_MultipleMoves_ChoosesBestSugar() {
    SugarCell lowSugarCell = mock(SugarCell.class);
    SugarCell highSugarCell = mock(SugarCell.class);
    when(lowSugarCell.getSugar()).thenReturn(2);
    when(highSugarCell.getSugar()).thenReturn(5);

    List<Cell> validMoves = new ArrayList<>();
    validMoves.add(lowSugarCell);
    validMoves.add(highSugarCell);

    try (var mockedStatic = mockStatic(GridOperations.class)) {
      mockedStatic.when(() -> GridOperations.findValidMoves(mockAgent, mockSimulation))
          .thenReturn(validMoves);
      mockedStatic.when(() -> GridOperations.findBestMove(mockAgent, validMoves, mockSimulation))
          .thenReturn(highSugarCell);

      movementManager.applyMovement(agents);

      verify(mockAgent).setPosition(highSugarCell);
      verify(mockAgent).addSugar(5);
    }
  }

  /**
   * Tests that movement handles edge case of zero sugar in target cell.
   */
  @Test
  void applyMovement_ZeroSugarCell_MovesButNoSugarCollected() {
    List<Cell> validMoves = new ArrayList<>();
    validMoves.add(targetCell);
    when(targetCell.getSugar()).thenReturn(0);

    try (var mockedStatic = mockStatic(GridOperations.class)) {
      mockedStatic.when(() -> GridOperations.findValidMoves(mockAgent, mockSimulation))
          .thenReturn(validMoves);
      mockedStatic.when(() -> GridOperations.findBestMove(mockAgent, validMoves, mockSimulation))
          .thenReturn(targetCell);

      movementManager.applyMovement(agents);

      verify(mockAgent).setPosition(targetCell);
      verify(mockAgent, never()).addSugar(anyInt());
      verify(targetCell).setNextState(SugarScapeState.AGENT);
    }
  }

  /**
   * Tests that movement handles cell that isn't a SugarCell.
   */
  @Test
  void applyMovement_NonSugarCell_MovesWithoutSugarInteraction() {
    Cell normalCell = mock(Cell.class);
    List<Cell> validMoves = new ArrayList<>();
    validMoves.add(normalCell);

    try (var mockedStatic = mockStatic(GridOperations.class)) {
      mockedStatic.when(() -> GridOperations.findValidMoves(mockAgent, mockSimulation))
          .thenReturn(validMoves);
      mockedStatic.when(() -> GridOperations.findBestMove(mockAgent, validMoves, mockSimulation))
          .thenReturn(normalCell);

      movementManager.applyMovement(agents);

      verify(mockAgent).setPosition(normalCell);
      verify(mockAgent, never()).addSugar(anyInt());
      verify(normalCell).setNextState(SugarScapeState.AGENT);
    }
  }

  /**
   * Tests that an IllegalArgumentException is thrown when agent list is null.
   */
  @Test
  void applyMovement_NullAgentList_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () ->
        movementManager.applyMovement(null));
  }

  /**
   * Tests that an IllegalArgumentException is thrown when agent has null position.
   */
  @Test
  void applyMovement_NullAgentPosition_ThrowsException() {
    when(mockAgent.getPosition()).thenReturn(null);

    assertThrows(IllegalArgumentException.class, () ->
        movementManager.applyMovement(agents));
  }

  /**
   * Tests that next states are properly reset when movement fails.
   */
  @Test
  void applyMovement_MovementFails_StatesReset() {
    try (var mockedStatic = mockStatic(GridOperations.class)) {
      mockedStatic.when(() -> GridOperations.findValidMoves(mockAgent, mockSimulation))
          .thenReturn(new ArrayList<>());

      movementManager.applyMovement(agents);

      verify(currentCell).resetNextState();
      verify(mockGrid).applyNextStates();
    }
  }
}
