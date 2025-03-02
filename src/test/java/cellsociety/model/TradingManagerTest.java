package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cellsociety.model.simulations.SugarScape;
import cellsociety.controller.SimulationConfig;
import cellsociety.model.state.SugarScapeState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Test class for TradingManager.
 * Tests the management and execution of trades between agents.
 * @author Tatum McKinnis
 */
public class TradingManagerTest {
  private TradingManager tradingManager;
  private SugarScape simulation;
  private List<Agent> agents;
  private Cell cell;
  private Grid grid;
  private SimulationConfig config;
  private static final int GRID_SIZE = 5;
  StateInterface defaultState = SugarScapeState.EMPTY;

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
        new HashMap<>(),
        "Default"
    );

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
    tradingManager = new TradingManager(simulation);

    cell = new SugarCell(2, 2, SugarScapeState.EMPTY);
    grid.setCellAt(2, 2, cell);

    agents = new ArrayList<>();
    Agent agent1 = new Agent(cell, 20, 2, 2);
    Agent agent2 = new Agent(cell, 20, 2, 2);
    agent1.setSpice(10);
    agent2.setSpice(5);

    agents.add(agent1);
    agents.add(agent2);

    simulation.getAgents().addAll(agents);
  }

  /**
   * Tests applying trading to agents with valid trading conditions.
   * Expected behavior: Executes trades between eligible agents.
   */
  @Test
  void applyTrading_ValidAgents_ExecutesTradesBetweenEligible() {
    Agent agent1 = agents.get(0);
    Agent agent2 = agents.get(1);

    agent1.addSugar(100);
    agent1.setSpice(1);
    agent2.addSugar(1);
    agent2.setSpice(100);

    agent1.setFertile(true);
    agent2.setFertile(true);

    SugarCell cell1 = new SugarCell(2, 2, SugarScapeState.AGENT);
    SugarCell cell2 = new SugarCell(2, 3, SugarScapeState.AGENT);
    grid.setCellAt(2, 2, cell1);
    grid.setCellAt(2, 3, cell2);
    agent1.setPosition(cell1);
    agent2.setPosition(cell2);

    int initialSpice = agent1.getSpice();
    int initialSugar = agent1.getSugar();

    tradingManager.applyTrading(agents);

    assertTrue(initialSpice != agent1.getSpice() || initialSugar != agent1.getSugar(),
        "At least one resource should change after trading");
  }

  /**
   * Tests applying trading with empty agent list.
   * Expected behavior: Completes without error.
   */
  @Test
  void applyTrading_EmptyList_CompletesWithoutError() {
    List<Agent> emptyList = new ArrayList<>();
    assertDoesNotThrow(() -> tradingManager.applyTrading(emptyList));
  }

  /**
   * Tests applying trading with null agent list.
   * Expected behavior: Throws NullPointerException.
   */
  @Test
  void applyTrading_NullList_ThrowsException() {
    assertThrows(NullPointerException.class, () ->
        tradingManager.applyTrading(null)
    );
  }

  /**
   * Tests applying trading with invalid agent positions.
   * Expected behavior: Handles invalid positions gracefully.
   */
  @Test
  void applyTrading_InvalidAgentPositions_HandlesGracefully() {
    Agent invalidAgent = new Agent(new Cell(defaultState), 20, 2, 2);
    agents.add(invalidAgent);
    assertDoesNotThrow(() -> tradingManager.applyTrading(agents));
  }
}