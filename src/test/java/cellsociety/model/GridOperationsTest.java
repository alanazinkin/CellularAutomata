package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cellsociety.model.simulations.SugarScape;
import cellsociety.model.state.SugarScapeState;
import cellsociety.controller.SimulationConfig;
import java.util.HashMap;
import java.util.List;

/**
 * Test class for GridOperations.
 * Tests grid-related operations including movement, distance calculations, and neighbor finding.
 * @author Tatum McKinnis
 */
public class GridOperationsTest {
  private SugarScape simulation;
  private Grid grid;
  private Agent agent;
  private SimulationConfig config;
  private static final int GRID_SIZE = 5;

  @BeforeEach
  void setUp() {
    int[] initialStates = new int[GRID_SIZE * GRID_SIZE];
    for(int i = 0; i < initialStates.length; i++) {
      initialStates[i] = 0;
    }

    config = new SimulationConfig(
        "SugarScape",
        "SugarScape Simulation",
        "Test Author",
        "Test Description",
        GRID_SIZE,
        GRID_SIZE,
        initialStates,
        new HashMap<>()
    );

    StateInterface defaultState = SugarScapeState.EMPTY;
    grid = new Grid(GRID_SIZE, GRID_SIZE, defaultState);

    for(int r = 0; r < GRID_SIZE; r++) {
      for(int c = 0; c < GRID_SIZE; c++) {
        SugarCell sugarCell = new SugarCell(r, c, SugarScapeState.EMPTY);
        sugarCell.setMaxSugar(10);
        sugarCell.setSugar(10);
        grid.setCellAt(r, c, sugarCell);
      }
    }

    simulation = new SugarScape(config, grid);

    SugarCell agentCell = new SugarCell(2, 2, SugarScapeState.EMPTY);
    grid.setCellAt(2, 2, agentCell);
    agent = new Agent(agentCell, 20, 2, 2);
    simulation.getAgents().add(agent);
  }

  /**
   * Tests finding valid moves for an agent with empty neighboring cells.
   * Expected behavior: Returns list of valid empty cells.
   */
  @Test
  void findValidMoves_EmptyNeighbors_ReturnsValidMoves() {
    Cell emptyCell = new SugarCell(2, 3, SugarScapeState.EMPTY);
    grid.setCellAt(2, 3, emptyCell);

    List<Cell> validMoves = GridOperations.findValidMoves(agent, simulation);

    assertFalse(validMoves.isEmpty());
    assertTrue(validMoves.contains(emptyCell));
  }

  /**
   * Tests finding valid moves when surrounded by occupied cells.
   * Expected behavior: Returns empty list.
   */
  @Test
  void findValidMoves_NoValidMoves_ReturnsEmptyList() {
    for (int r = 1; r <= 3; r++) {
      for (int c = 1; c <= 3; c++) {
        if (r != 2 || c != 2) {
          Cell occupiedCell = new SugarCell(r, c, SugarScapeState.AGENT);
          grid.setCellAt(r, c, occupiedCell);
        }
      }
    }

    List<Cell> validMoves = GridOperations.findValidMoves(agent, simulation);

    assertTrue(validMoves.isEmpty());
  }

  /**
   * Tests finding best move among valid moves with different sugar levels.
   * Expected behavior: Returns cell with highest sugar.
   */
  @Test
  void findBestMove_DifferentSugarLevels_ReturnsHighestSugar() {
    SugarCell lowSugar = new SugarCell(2, 3, SugarScapeState.SUGAR);
    lowSugar.setSugar(5);
    lowSugar.setMaxSugar(5);
    grid.setCellAt(2, 3, lowSugar);

    SugarCell highSugar = new SugarCell(2, 1, SugarScapeState.SUGAR);
    highSugar.setSugar(10);
    highSugar.setMaxSugar(10);
    grid.setCellAt(2, 1, highSugar);

    List<Cell> validMoves = List.of(lowSugar, highSugar);
    Cell bestMove = GridOperations.findBestMove(agent, validMoves, simulation);

    assertEquals(highSugar, bestMove, "Should choose cell with highest sugar");
  }

  /**
   * Tests calculating Manhattan distance between two cells.
   * Expected behavior: Returns correct distance.
   */
  @Test
  void calculateDistance_ValidCells_ReturnsCorrectDistance() {
    Cell cell1 = new SugarCell(1, 1, SugarScapeState.EMPTY);
    Cell cell2 = new SugarCell(3, 4, SugarScapeState.EMPTY);
    grid.setCellAt(1, 1, cell1);
    grid.setCellAt(3, 4, cell2);

    int distance = GridOperations.calculateDistance(cell1, cell2, simulation);

    assertEquals(5, distance); // Manhattan distance: |3-1| + |4-1| = 5
  }

  /**
   * Tests calculating distance with invalid cells.
   * Expected behavior: Returns -1 or handles gracefully.
   */
  @Test
  void calculateDistance_InvalidCells_HandlesGracefully() {
    SugarCell cell1 = new SugarCell(1, 1, SugarScapeState.EMPTY);
    SugarCell invalidCell = new SugarCell(3, 3, SugarScapeState.EMPTY);
    grid.setCellAt(1, 1, cell1);

    int distance = GridOperations.calculateDistance(cell1, invalidCell, simulation);
    assertEquals(4, distance); // |3-1| + |3-1| = 4, implementation calculates Manhattan distance regardless
  }

  /**
   * Tests getting agent neighbors for an agent with adjacent agents.
   * Expected behavior: Returns list of adjacent agents.
   */
  @Test
  void getAgentNeighbors_AdjacentAgents_ReturnsCorrectNeighbors() {
    Cell neighborCell = new SugarCell(2, 3, SugarScapeState.AGENT);
    grid.setCellAt(2, 3, neighborCell);
    Agent neighborAgent = new Agent(neighborCell, 20, 2, 2);
    simulation.getAgents().add(neighborAgent);

    List<Agent> neighbors = GridOperations.getAgentNeighbors(agent, simulation);

    assertEquals(1, neighbors.size());
    assertTrue(neighbors.contains(neighborAgent));
  }

  /**
   * Tests getting agent neighbors with null agent.
   * Expected behavior: Throws NullPointerException.
   */
  @Test
  void getAgentNeighbors_NullAgent_ThrowsException() {
    try {
      GridOperations.getAgentNeighbors(null, simulation);
      fail("Should throw NullPointerException");
    } catch (NullPointerException e) {
      // Test passes
    }
  }

  /**
   * Tests getting agent neighbors with null simulation.
   * Expected behavior: Throws NullPointerException.
   */
  @Test
  void getAgentNeighbors_NullSimulation_ThrowsException() {
    try {
      GridOperations.getAgentNeighbors(agent, null);
      fail("Should throw NullPointerException");
    } catch (NullPointerException e) {
    }
  }
}