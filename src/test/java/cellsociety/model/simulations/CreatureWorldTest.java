//package cellsociety.model.simulations;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import cellsociety.controller.SimulationConfig;
//import cellsociety.model.Cell;
//import cellsociety.model.CreatureLocation;
//import cellsociety.model.CreatureManager;
//import cellsociety.model.Direction;
//import cellsociety.model.Grid;
//import cellsociety.model.state.CreatureState;
//import cellsociety.model.StateInterface;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//class CreatureWorldTest {
//
//  private static final int GRID_ROWS = 5;
//  private static final int GRID_COLS = 5;
//
//  @Mock
//  private Grid mockGrid;
//
//  @Mock
//  private SimulationConfig mockConfig;
//
//  @Mock
//  private CreatureManager mockCreatureManager;
//
//  private Cell[][] mockCells;
//
//  private CreatureWorld creatureWorld;
//
//  @BeforeEach
//  void setUp() throws Exception {
//    MockitoAnnotations.openMocks(this);
//
//    // Set up the mock grid
//    when(mockGrid.getRows()).thenReturn(GRID_ROWS);
//    when(mockGrid.getCols()).thenReturn(GRID_COLS);
//
//    // Create and configure mock cells
//    mockCells = new Cell[GRID_ROWS][GRID_COLS];
//    for (int r = 0; r < GRID_ROWS; r++) {
//      for (int c = 0; c < GRID_COLS; c++) {
//        mockCells[r][c] = mock(Cell.class);
//        when(mockCells[r][c].getCurrentState()).thenReturn(CreatureState.EMPTY);
//        when(mockGrid.getCell(r, c)).thenReturn(mockCells[r][c]);
//      }
//    }
//
//    // Set up grid.isValidPosition to return true for valid coordinates
//    when(mockGrid.isValidPosition(anyInt(), anyInt())).thenAnswer(invocation -> {
//      int row = invocation.getArgument(0);
//      int col = invocation.getArgument(1);
//      return row >= 0 && row < GRID_ROWS && col >= 0 && col < GRID_COLS;
//    });
//
//    // Set up initial states for simulation config
//    int[] initialStates = new int[GRID_ROWS * GRID_COLS];
//    when(mockConfig.getInitialStates()).thenReturn(initialStates);
//
//    // Create the CreatureWorld with mocked dependencies
//    creatureWorld = new CreatureWorld(mockConfig, mockGrid);
//
//    // Replace the internal creatureManager with our mock using reflection
//    Field creatureManagerField = CreatureWorld.class.getDeclaredField("creatureManager");
//    creatureManagerField.setAccessible(true);
//    creatureManagerField.set(creatureWorld, mockCreatureManager);
//  }
//
//  @Test
//  void testInitializeColorMap() {
//    // Get the color map
//    Map<StateInterface, String> colorMap = creatureWorld.getColorMap();
//
//    // Verify the color map contains the expected mappings
//    assertEquals("#FFFFFF", colorMap.get(CreatureState.EMPTY));
//    assertEquals("#FF0000", colorMap.get(CreatureState.HUNTER));
//    assertEquals("#0000FF", colorMap.get(CreatureState.WANDERER));
//    assertEquals("#00FF00", colorMap.get(CreatureState.FLYTRAP));
//  }
//
//  @Test
//  void testInitializeStateMap() {
//    // Get the state map
//    Map<Integer, StateInterface> stateMap = creatureWorld.getStateMap();
//
//    // Verify the state map contains the expected mappings
//    assertEquals(CreatureState.EMPTY, stateMap.get(CreatureState.EMPTY.getNumericValue()));
//    assertEquals(CreatureState.HUNTER, stateMap.get(CreatureState.HUNTER.getNumericValue()));
//    assertEquals(CreatureState.WANDERER, stateMap.get(CreatureState.WANDERER.getNumericValue()));
//    assertEquals(CreatureState.FLYTRAP, stateMap.get(CreatureState.FLYTRAP.getNumericValue()));
//  }
//
//  @Test
//  void testAddCreature() {
//    // Configure cell to be empty
//    Cell cell = mockCells[1][2];
//    when(cell.getCurrentState()).thenReturn(CreatureState.EMPTY);
//
//    // Test adding a creature
//    boolean result = creatureWorld.addCreature(1, 2, CreatureState.HUNTER, Direction.NORTH);
//
//    // Verify the result and interactions
//    assertTrue(result);
//    verify(cell).setCurrentState(CreatureState.HUNTER);
//    verify(cell).setNextState(CreatureState.HUNTER);
//    verify(mockCreatureManager).registerCreature(1, 2, cell);
//    verify(mockCreatureManager).setOrientation(cell, Direction.NORTH.getDegrees());
//  }
//
//  @Test
//  void testAddCreatureInvalidLocation() {
//    // Test adding a creature at an invalid location
//    boolean result = creatureWorld.addCreature(-1, 2, CreatureState.HUNTER, Direction.NORTH);
//
//    // Verify the result
//    assertFalse(result);
//    verify(mockCreatureManager, never()).registerCreature(anyInt(), anyInt(), any(Cell.class));
//  }
//
//  @Test
//  void testAddCreatureToOccupiedCell() {
//    // Configure cell to be occupied
//    Cell cell = mockCells[1][2];
//    when(cell.getCurrentState()).thenReturn(CreatureState.WANDERER);
//
//    // Test adding a creature to occupied cell
//    boolean result = creatureWorld.addCreature(1, 2, CreatureState.HUNTER, Direction.NORTH);
//
//    // Verify the result
//    assertFalse(result);
//    verify(cell, never()).setCurrentState(any(StateInterface.class));
//    verify(mockCreatureManager, never()).registerCreature(anyInt(), anyInt(), any(Cell.class));
//  }
//
//  @Test
//  void testRemoveCreature() {
//    // Configure cell to have a creature
//    Cell cell = mockCells[1][2];
//    when(cell.getCurrentState()).thenReturn(CreatureState.HUNTER);
//
//    // Test removing a creature
//    boolean result = creatureWorld.removeCreature(1, 2);
//
//    // Verify the result and interactions
//    assertTrue(result);
//    verify(cell).setCurrentState(CreatureState.EMPTY);
//    verify(cell).setNextState(CreatureState.EMPTY);
//  }
//
//  @Test
//  void testRemoveCreatureFromEmptyCell() {
//    // Cell is already empty by default in setup
//
//    // Test removing a creature from an empty cell
//    boolean result = creatureWorld.removeCreature(1, 2);
//
//    // Verify the result
//    assertFalse(result);
//    verify(mockCells[1][2], never()).setCurrentState(any(StateInterface.class));
//  }
//
//  @Test
//  void testRemoveCreatureInvalidLocation() {
//    // Test removing a creature from an invalid location
//    boolean result = creatureWorld.removeCreature(-1, 2);
//
//    // Verify the result
//    assertFalse(result);
//    verify(mockGrid, never()).getCell(-1, 2);
//  }
//
//  @Test
//  void testMoveCreature() {
//    // Configure source cell to have a creature
//    Cell sourceCell = mockCells[1][2];
//    when(sourceCell.getCurrentState()).thenReturn(CreatureState.HUNTER);
//
//    // Configure target cell to be empty
//    Cell targetCell = mockCells[3][4];
//    // Already empty by default
//
//    // Test moving a creature
//    boolean result = creatureWorld.moveCreature(1, 2, 3, 4);
//
//    // Verify the result and interactions
//    assertTrue(result);
//    verify(sourceCell).setCurrentState(CreatureState.EMPTY);
//    verify(sourceCell).setNextState(CreatureState.EMPTY);
//    verify(targetCell).setCurrentState(CreatureState.HUNTER);
//    verify(targetCell).setNextState(CreatureState.HUNTER);
//    verify(mockCreatureManager).updateCreatureLocation(sourceCell, targetCell);
//  }
//
//  @Test
//  void testMoveCreatureToOccupiedCell() {
//    // Configure source cell to have a creature
//    Cell sourceCell = mockCells[1][2];
//    when(sourceCell.getCurrentState()).thenReturn(CreatureState.HUNTER);
//
//    // Configure target cell to be occupied
//    Cell targetCell = mockCells[3][4];
//    when(targetCell.getCurrentState()).thenReturn(CreatureState.WANDERER);
//
//    // Test moving to an occupied cell
//    boolean result = creatureWorld.moveCreature(1, 2, 3, 4);
//
//    // Verify the result
//    assertFalse(result);
//    verify(sourceCell, never()).setCurrentState(any(StateInterface.class));
//    verify(targetCell, never()).setCurrentState(any(StateInterface.class));
//    verify(mockCreatureManager, never()).updateCreatureLocation(any(Cell.class), any(Cell.class));
//  }
//
//  @Test
//  void testMoveCreatureFromEmptyCell() {
//    // Source cell is empty by default in setup
//
//    // Test moving from an empty cell
//    boolean result = creatureWorld.moveCreature(1, 2, 3, 4);
//
//    // Verify the result
//    assertFalse(result);
//    verify(mockCells[1][2], never()).setCurrentState(any(StateInterface.class));
//    verify(mockCells[3][4], never()).setCurrentState(any(StateInterface.class));
//    verify(mockCreatureManager, never()).updateCreatureLocation(any(Cell.class), any(Cell.class));
//  }
//
//  @Test
//  void testSetCreatureOrientation() {
//    // Configure cell to have a creature
//    Cell cell = mockCells[1][2];
//    when(cell.getCurrentState()).thenReturn(CreatureState.HUNTER);
//
//    // Test setting orientation
//    boolean result = creatureWorld.setCreatureOrientation(1, 2, Direction.SOUTH);
//
//    // Verify the result and interactions
//    assertTrue(result);
//    verify(mockCreatureManager).setOrientation(cell, Direction.SOUTH.getDegrees());
//  }
//
//  @Test
//  void testGetCreatureTypeAt() {
//    // Configure cell to have a specific creature type
//    Cell cell = mockCells[1][2];
//    when(cell.getCurrentState()).thenReturn(CreatureState.HUNTER);
//
//    // Test getting creature type
//    CreatureState result = creatureWorld.getCreatureTypeAt(1, 2);
//
//    // Verify the result
//    assertEquals(CreatureState.HUNTER, result);
//  }
//
//  @Test
//  void testGetCreatureOrientation() {
//    // Configure cell to have a creature
//    Cell cell = mockCells[1][2];
//    when(cell.getCurrentState()).thenReturn(CreatureState.HUNTER);
//
//    // Configure creature manager to return a specific orientation
//    when(mockCreatureManager.getOrientation(cell)).thenReturn(90.0);
//
//    // Test getting orientation
//    double result = creatureWorld.getCreatureOrientation(1, 2);
//
//    // Verify the result
//    assertEquals(90.0, result);
//  }
//
//  @Test
//  void testGetAllCreatureLocations() {
//    // Create a list of mock creature locations
//    List<CreatureLocation> mockLocations = new ArrayList<>();
//    mockLocations.add(new CreatureLocation(1, 2, mockCells[1][2]));
//
//    // Configure creature manager to return the locations
//    when(mockCreatureManager.getAllCreatureLocations()).thenReturn(mockLocations);
//
//    // Test getting all creature locations
//    List<CreatureLocation> result = creatureWorld.getAllCreatureLocations();
//
//    // Verify the result
//    assertEquals(mockLocations, result);
//    verify(mockCreatureManager).getAllCreatureLocations();
//  }
//
//  @Test
//  void testApplyRules() {
//    // Create a list of mock creature locations
//    List<CreatureLocation> mockLocations = new ArrayList<>();
//    mockLocations.add(new CreatureLocation(1, 2, mockCells[1][2]));
//
//    // Configure cell to have a creature
//    Cell cell = mockCells[1][2];
//    when(cell.getCurrentState()).thenReturn(CreatureState.HUNTER);
//
//    // Configure creature manager to return the locations
//    when(mockCreatureManager.getAllCreatureLocations()).thenReturn(mockLocations);
//
//    // We can't directly call applyRules (it's protected), so we call step which calls it
//    creatureWorld.step();
//
//    // Verify the next state was set correctly
//    verify(cell).setNextState(CreatureState.HUNTER);
//  }
//
//  @Test
//  void testReinitializeGridStates() {
//    // Configure a new simulation config
//    SimulationConfig newConfig = mock(SimulationConfig.class);
//    int[] newStates = new int[GRID_ROWS * GRID_COLS];
//    when(newConfig.getInitialStates()).thenReturn(newStates);
//
//    // Call reinitializeGridStates
//    creatureWorld.reinitializeGridStates(newConfig);
//
//    // Verify creature manager was cleared
//    verify(mockCreatureManager).clear();
//  }
//}