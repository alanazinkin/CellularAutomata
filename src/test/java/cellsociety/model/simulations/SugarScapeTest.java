package cellsociety.model.simulations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.*;
import cellsociety.model.state.SugarScapeState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

/**
 * Test class for SugarScape simulation. Tests various aspects of the simulation including: -
 * Initialization - Agent movement and interactions - Resource management - Loan system - Disease
 * transmission Adheres to [UnitOfWork_StateUnderTest_ExpectedBehavior] naming convention.
 */
public class SugarScapeTest {

  private SugarScape simulation;
  private SimulationConfig config;
  private Grid grid;
  private static final int GRID_SIZE = 5;

  @BeforeEach
  void setUp() {
    config = mock(SimulationConfig.class);
    when(config.getInitialStates()).thenReturn(new int[GRID_SIZE * GRID_SIZE]);

    // Create grid with SugarCells
    grid = new Grid(GRID_SIZE, GRID_SIZE, SugarScapeState.EMPTY);
    // Manually set SugarCells after grid creation
    for (int r = 0; r < GRID_SIZE; r++) {
      for (int c = 0; c < GRID_SIZE; c++) {
        SugarCell cell = new SugarCell(r, c, SugarScapeState.EMPTY);
        cell.setMaxSugar(10); // Set a default max sugar value
        grid.setCellAt(r, c, cell);
      }
    }

    simulation = new SugarScape(config, grid);
  }

  /**
   * Tests initialization of color map. Verifies that all states have corresponding colors.
   */
  @Test
  void initializeColorMap_WhenCalled_ReturnsValidColorMapping() {
    Map<StateInterface, String> colorMap = simulation.getColorMap();

    assertNotNull(colorMap);
    assertEquals("white", colorMap.get(SugarScapeState.EMPTY));
    assertEquals("yellow", colorMap.get(SugarScapeState.SUGAR));
    assertEquals("blue", colorMap.get(SugarScapeState.AGENT));
  }

  /**
   * Tests initialization of state map. Verifies that all numeric states map to correct
   * SugarScapeStates.
   */
  @Test
  void initializeStateMap_WhenCalled_ReturnsValidStateMapping() {
    Map<Integer, StateInterface> stateMap = simulation.getStateMap();

    assertNotNull(stateMap);
    assertEquals(SugarScapeState.EMPTY, stateMap.get(0));
    assertEquals(SugarScapeState.SUGAR, stateMap.get(1));
    assertEquals(SugarScapeState.AGENT, stateMap.get(2));
  }

  /**
   * Tests adding an agent to an empty cell. Verifies successful agent addition and grid state
   * update.
   */
  @Test
  void addAgent_ToEmptyCell_SuccessfullyAddsAgent() {
    SugarCell cell = (SugarCell) grid.getCell(0, 0);
    cell.setCurrentState(SugarScapeState.EMPTY);

    Agent agent = new Agent(cell, 10, 1, 1);
    simulation.addAgent(agent);

    assertTrue(simulation.getAgents().contains(agent));
    assertEquals(SugarScapeState.AGENT, cell.getCurrentState());
  }

  /**
   * Tests adding an agent to an occupied cell. Verifies that appropriate exception is thrown.
   */
  @Test
  void addAgent_ToOccupiedCell_ThrowsException() {
    SugarCell cell = (SugarCell) grid.getCell(0, 0);
    cell.setCurrentState(SugarScapeState.AGENT);
    Agent agent = new Agent(cell, 10, 1, 1);

    assertThrows(IllegalArgumentException.class, () -> simulation.addAgent(agent));
  }

  /**
   * Tests adding a valid loan between two agents. Verifies successful loan creation and resource
   * transfer.
   */
  @Test
  void addLoan_BetweenValidAgents_SuccessfullyAddsLoan() {
    // Set up cells
    SugarCell cell1 = new SugarCell(0, 0, SugarScapeState.EMPTY);
    SugarCell cell2 = new SugarCell(0, 1, SugarScapeState.EMPTY);
    grid.setCellAt(0, 0, cell1);
    grid.setCellAt(0, 1, cell2);

    // Create agents
    Agent lender = new Agent(cell1, 100, 1, 1);
    Agent borrower = new Agent(cell2, 10, 1, 1);

    // Add agents
    simulation.addAgent(lender);
    simulation.addAgent(borrower);

    // Create and add loan
    Loan loan = new Loan(lender, borrower, 50, 0, 0.1);
    simulation.addLoan(loan);

    assertEquals(1, simulation.getActiveLoans().size());
    assertEquals(50, lender.getSugar());  // Started with 100, gave 50
    assertEquals(60, borrower.getSugar()); // Started with 10, got 50
  }

  /**
   * Tests adding a duplicate loan between agents. Verifies that appropriate exception is thrown.
   */
  @Test
  void addLoan_DuplicateLoan_ThrowsException() {
    // Set up cells
    SugarCell cell1 = new SugarCell(0, 0, SugarScapeState.EMPTY);
    SugarCell cell2 = new SugarCell(0, 1, SugarScapeState.EMPTY);
    grid.setCellAt(0, 0, cell1);
    grid.setCellAt(0, 1, cell2);

    // Create agents
    Agent lender = new Agent(cell1, 100, 1, 1);
    Agent borrower = new Agent(cell2, 10, 1, 1);

    // Add agents
    simulation.addAgent(lender);
    simulation.addAgent(borrower);

    // Add first loan
    Loan loan1 = new Loan(lender, borrower, 50, 0, 0.1);
    simulation.addLoan(loan1);

    // Try to add second loan
    Loan loan2 = new Loan(lender, borrower, 30, 0, 0.1);
    assertThrows(IllegalArgumentException.class, () -> simulation.addLoan(loan2));
  }

  /**
   * Tests retrieving simulation statistics. Verifies that all expected statistics are present and
   * accurate.
   */
  @Test
  void getStatistics_WithActiveSimulation_ReturnsValidStats() {
    // Set up cells
    SugarCell cell1 = new SugarCell(0, 0, SugarScapeState.EMPTY);
    SugarCell cell2 = new SugarCell(0, 1, SugarScapeState.EMPTY);
    grid.setCellAt(0, 0, cell1);
    grid.setCellAt(0, 1, cell2);

    // Create agents with proper sex values
    Agent agent1 = new Agent(cell1, 100, 1, 1);
    Agent agent2 = new Agent(cell2, 50, 1, 1);
    agent1.setSex(Sex.MALE);
    agent2.setSex(Sex.FEMALE);

    // Add agents
    simulation.addAgent(agent1);
    simulation.addAgent(agent2);

    // Get and verify statistics
    Map<String, Object> stats = simulation.getStatistics();

    assertNotNull(stats);
    assertEquals(2, stats.get("agentCount"));
    assertEquals(1L, stats.get("maleCount"));
    assertEquals(1L, stats.get("femaleCount"));
    assertEquals(150, stats.get("totalSugar")); // 100 + 50
  }

  /**
   * Tests step execution and growth patterns. Verifies that sugar grows back correctly.
   */
  @Test
  void step_WithEmptyGrid_GrowsBackSugar() {
    // Set up cell with proper initial state
    SugarCell cell = new SugarCell(0, 0, SugarScapeState.EMPTY);
    cell.setMaxSugar(5);
    cell.setSugar(0);
    grid.setCellAt(0, 0, cell);

    // Force cell to be in correct state for sugar growth
    cell.setCurrentState(SugarScapeState.EMPTY);

    // Execute step
    simulation.step();

    // Verify sugar growth
    assertEquals(1, cell.getSugar()); // Default grow back rate is 1
  }
}