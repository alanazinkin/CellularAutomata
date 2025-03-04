package cellsociety.model.simulations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.*;
import cellsociety.model.LoanManager;
import cellsociety.model.state.SugarScapeState;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for SugarScape simulation. Tests the core functionality of the SugarScape simulation
 * including initialization, state management, and rule application.
 *
 * @author Tatum McKinnis
 */
public class SugarScapeTest {

  private SugarScape sugarScape;
  private SimulationConfig mockConfig;
  private Grid mockGrid;
  private Cell mockCell;
  private SugarCell mockSugarCell;

  @BeforeEach
  void setUp() {
    mockConfig = mock(SimulationConfig.class);
    mockGrid = mock(Grid.class);
    mockCell = mock(Cell.class);
    mockSugarCell = mock(SugarCell.class);

    when(mockGrid.getRows()).thenReturn(10);
    when(mockGrid.getCols()).thenReturn(10);
    when(mockGrid.getCell(anyInt(), anyInt())).thenReturn(mockCell);

    int[] mockInitialStates = new int[100];
    Arrays.fill(mockInitialStates, 0);
    Arrays.fill(mockInitialStates, 0, 15, 1);
    Arrays.fill(mockInitialStates, 15, 20, 2);
    when(mockConfig.getInitialStates()).thenReturn(mockInitialStates);

    when(mockCell.getCurrentState()).thenReturn(SugarScapeState.EMPTY);

    sugarScape = new SugarScape(mockConfig, mockGrid);
  }

  /**
   * Tests that the simulation properly initializes the grid with SugarCells.
   */
  @Test
  void initialize_NewSimulation_ConvertsCellsToSugarCells() {
    verify(mockGrid, times(100)).setCellAt(anyInt(), anyInt(), any(SugarCell.class));
  }

  /**
   * Tests that growth patterns are correctly initialized on the grid.
   */
  @Test
  void initialize_GrowthPatterns_SetsSugarLevelsCorrectly() {
    Grid realGrid = new Grid(5, 5, SugarScapeState.EMPTY);

    SimulationConfig realConfig = mock(SimulationConfig.class);
    int[] realInitialStates = new int[25];
    Arrays.fill(realInitialStates, 0);
    when(realConfig.getInitialStates()).thenReturn(realInitialStates);

    SugarScape realSugarScape = new SugarScape(realConfig, realGrid);

    SugarCell corner = (SugarCell) realGrid.getCell(0, 0);
    assertTrue(corner.getMaxSugar() > 10);
    assertEquals(corner.getMaxSugar(), corner.getSugar());
  }

  /**
   * Tests that agents are properly initialized from initial states.
   */
  @Test
  void initialize_WithAgents_CreatesAgentsCorrectly() {
    Grid realGrid = new Grid(5, 5, SugarScapeState.EMPTY);
    Cell agentCell = realGrid.getCell(0, 0);
    agentCell.setCurrentState(SugarScapeState.AGENT);

    SimulationConfig agentConfig = mock(SimulationConfig.class);
    int[] agentInitialStates = new int[25];
    Arrays.fill(agentInitialStates, 1);
    agentInitialStates[0] = 2;
    agentInitialStates[12] = 2;
    when(agentConfig.getInitialStates()).thenReturn(agentInitialStates);

    SugarScape agentSugarScape = new SugarScape(agentConfig, realGrid);

    assertFalse(agentSugarScape.getAgents().isEmpty());
  }

  /**
   * Tests that all managers are properly invoked during rule application.
   */
  @Test
  void applyRules_AllManagers_InvokedInOrder() {
    MovementManager mockMovementManager = mock(MovementManager.class);
    GrowthManager mockGrowthManager = mock(GrowthManager.class);
    ReproductionManager mockReproductionManager = mock(ReproductionManager.class);
    TradingManager mockTradingManager = mock(TradingManager.class);
    LoanManager mockLoanManager = mock(LoanManager.class);
    DiseaseManager mockDiseaseManager = mock(DiseaseManager.class);

    SugarScape spySugarScape = spy(sugarScape);
    try {
      var movementField = SugarScape.class.getDeclaredField("movementManager");
      movementField.setAccessible(true);
      movementField.set(spySugarScape, mockMovementManager);

      var growthField = SugarScape.class.getDeclaredField("growthManager");
      growthField.setAccessible(true);
      growthField.set(spySugarScape, mockGrowthManager);

      var reproductionField = SugarScape.class.getDeclaredField("reproductionManager");
      reproductionField.setAccessible(true);
      reproductionField.set(spySugarScape, mockReproductionManager);

      var tradingField = SugarScape.class.getDeclaredField("tradingManager");
      tradingField.setAccessible(true);
      tradingField.set(spySugarScape, mockTradingManager);

      var loanField = SugarScape.class.getDeclaredField("loanManager");
      loanField.setAccessible(true);
      loanField.set(spySugarScape, mockLoanManager);

      var diseaseField = SugarScape.class.getDeclaredField("diseaseManager");
      diseaseField.setAccessible(true);
      diseaseField.set(spySugarScape, mockDiseaseManager);

    } catch (Exception e) {
      fail("Failed to set up test with reflection: " + e.getMessage());
    }

    spySugarScape.applyRules();

    var inOrder = inOrder(mockMovementManager, mockGrowthManager, mockReproductionManager,
        mockTradingManager, mockLoanManager, mockDiseaseManager);

    inOrder.verify(mockMovementManager).applyMovement(any());
    inOrder.verify(mockGrowthManager).applyGrowBack(anyInt(), anyInt(), anyInt());
    inOrder.verify(mockReproductionManager).applyReproduction(any());
    inOrder.verify(mockTradingManager).applyTrading(any());
    inOrder.verify(mockLoanManager).applyLending(any());
    inOrder.verify(mockDiseaseManager).applyDiseaseRules(any());
  }

  /**
   * Tests that dead agents are properly removed from the simulation.
   */
  @Test
  void applyRules_DeadAgents_RemovedFromSimulation() {
    Agent mockAgent = mock(Agent.class);
    when(mockAgent.isDead()).thenReturn(true);
    when(mockAgent.getPosition()).thenReturn(mockCell);

    sugarScape.getAgents().add(mockAgent);
    sugarScape.applyRules();

    assertTrue(sugarScape.getAgents().isEmpty());
    verify(mockCell).setNextState(SugarScapeState.EMPTY);
  }

  /**
   * Tests that the color map is properly initialized with all states.
   */
  @Test
  void initializeColorMap_AllStates_MappedToColors() {
    var colorMap = sugarScape.getColorMap();

    assertTrue(colorMap.containsKey(SugarScapeState.EMPTY));
    assertTrue(colorMap.containsKey(SugarScapeState.SUGAR));
    assertTrue(colorMap.containsKey(SugarScapeState.AGENT));

    assertEquals("sugar-state-empty", colorMap.get(SugarScapeState.EMPTY));
    assertEquals("sugar-state-sugar", colorMap.get(SugarScapeState.SUGAR));
    assertEquals("sugar-state-agent", colorMap.get(SugarScapeState.AGENT));
  }

  /**
   * Tests that the state map is properly initialized with all states.
   */
  @Test
  void initializeStateMap_AllStates_MappedToIntegers() {
    var stateMap = sugarScape.getStateMap();

    assertEquals(SugarScapeState.EMPTY, stateMap.get(0));
    assertEquals(SugarScapeState.SUGAR, stateMap.get(1));
    assertEquals(SugarScapeState.AGENT, stateMap.get(2));
  }

  /**
   * Tests that an exception is thrown when config is null.
   */
  @Test
  void constructor_NullConfig_ThrowsException() {
    Grid validGrid = new Grid(2, 2, SugarScapeState.EMPTY);
    NullPointerException thrown = assertThrows(NullPointerException.class, () ->
        new SugarScape(null, validGrid));
    assertEquals(
        "Cannot invoke \"cellsociety.controller.SimulationConfig.getInitialStates()\" because \"simulationConfig\" is null",
        thrown.getMessage());
  }

  /**
   * Tests that an exception is thrown when grid is null.
   */
  @Test
  void constructor_NullGrid_ThrowsException() {
    int[] validStates = new int[4];
    Arrays.fill(validStates, 0);
    when(mockConfig.getInitialStates()).thenReturn(validStates);

    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
        new SugarScape(mockConfig, null));
    assertEquals("Grid cannot be null", thrown.getMessage());
  }


  /**
   * Tests that an IllegalArgumentException is thrown when initial states are invalid.
   */
  @Test
  void constructor_InvalidInitialStates_ThrowsException() {
    int[] invalidStates = new int[50];
    when(mockConfig.getInitialStates()).thenReturn(invalidStates);

    assertThrows(IllegalArgumentException.class, () ->
        new SugarScape(mockConfig, mockGrid));
  }
}