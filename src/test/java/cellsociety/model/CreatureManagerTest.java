package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cellsociety.model.state.CreatureState;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for CreatureManager that validates the management of creatures
 * in the cell society simulation.
 * Tests focus on creature registration, location updates, orientation changes,
 * and instruction tracking.
 */
class CreatureManagerTest {

  private Grid mockGrid;
  private CreatureManager manager;
  private Cell mockCell1;
  private Cell mockCell2;

  /**
   * Sets up the test environment before each test by initializing mocks
   * and creating a new CreatureManager instance.
   */
  @BeforeEach
  void setUp() {
    // Create mocks
    mockGrid = mock(Grid.class);
    mockCell1 = mock(Cell.class);
    mockCell2 = mock(Cell.class);

    // Set up grid behavior
    when(mockGrid.getRows()).thenReturn(3);
    when(mockGrid.getCols()).thenReturn(3);

    // Initialize manager with mock grid
    manager = new CreatureManager(mockGrid);
  }

  /**
   * Tests that registering a creature correctly initializes its data
   * with default orientation and instruction index.
   */
  @Test
  void registerCreature_ValidLocation_CreatesCreatureData() {
    // Arrange
    int row = 1;
    int col = 2;

    // Act
    manager.registerCreature(row, col, mockCell1);

    // Assert - verify orientation is set to default (EAST)
    assertEquals(Direction.EAST.getDegrees(), manager.getOrientation(mockCell1));
    assertEquals(0, manager.getInstructionIndex(mockCell1));
  }

  /**
   * Tests that registering multiple creatures at different locations
   * correctly sets up each creature's data.
   */
  @Test
  void registerCreature_MultipleLocations_CreatesMultipleCreatures() {
    // Arrange
    Cell mockCell3 = mock(Cell.class);

    // Act
    manager.registerCreature(0, 0, mockCell1);
    manager.registerCreature(1, 1, mockCell2);
    manager.registerCreature(2, 2, mockCell3);

    // Assert
    assertEquals(Direction.EAST.getDegrees(), manager.getOrientation(mockCell1));
    assertEquals(Direction.EAST.getDegrees(), manager.getOrientation(mockCell2));
    assertEquals(Direction.EAST.getDegrees(), manager.getOrientation(mockCell3));
    assertEquals(0, manager.getInstructionIndex(mockCell1));
    assertEquals(0, manager.getInstructionIndex(mockCell2));
    assertEquals(0, manager.getInstructionIndex(mockCell3));
  }

  /**
   * Tests that updating a creature's location correctly transfers the creature data
   * from the old cell to the new cell.
   */
  @Test
  void updateCreatureLocation_ValidMove_TransfersCreatureData() {
    // Arrange
    manager.registerCreature(0, 0, mockCell1);

    // Act
    manager.updateCreatureLocation(mockCell1, mockCell2);

    // Assert
    assertEquals(Direction.EAST.getDegrees(), manager.getOrientation(mockCell2));
    assertEquals(0, manager.getInstructionIndex(mockCell2));
    // Old cell should no longer have creature data
    assertEquals(Direction.EAST.getDegrees(), manager.getOrientation(mockCell1)); // Default value
  }

  /**
   * Tests that updating the location of a non-existent creature
   * doesn't cause errors and doesn't create new creature data.
   */
  @Test
  void updateCreatureLocation_NonExistentCreature_NoEffect() {
    // Arrange - don't register any creatures

    // Act - update a creature that doesn't exist
    manager.updateCreatureLocation(mockCell1, mockCell2);

    // Assert - should return default values
    assertEquals(Direction.EAST.getDegrees(), manager.getOrientation(mockCell2));
    assertEquals(0, manager.getInstructionIndex(mockCell2));
  }

  /**
   * Tests that getting the orientation of a registered creature
   * returns the correct orientation.
   */
  @Test
  void getOrientation_RegisteredCreature_ReturnsCorrectOrientation() {
    // Arrange
    manager.registerCreature(0, 0, mockCell1);
    double newDegrees = 90.0;
    manager.setOrientation(mockCell1, newDegrees);

    // Act
    double orientation = manager.getOrientation(mockCell1);

    // Assert
    assertEquals(newDegrees, orientation);
  }

  /**
   * Tests that getting the orientation of an unregistered creature
   * returns the default orientation (EAST).
   */
  @Test
  void getOrientation_UnregisteredCreature_ReturnsDefaultOrientation() {
    // Arrange - don't register any creatures

    // Act
    double orientation = manager.getOrientation(mockCell1);

    // Assert
    assertEquals(Direction.EAST.getDegrees(), orientation);
  }

  /**
   * Tests that setting the orientation of a registered creature
   * correctly updates its orientation.
   */
  @Test
  void setOrientation_RegisteredCreature_UpdatesOrientation() {
    // Arrange
    manager.registerCreature(0, 0, mockCell1);
    double newDegrees = 90.0;

    // Act
    manager.setOrientation(mockCell1, newDegrees);

    // Assert
    assertEquals(newDegrees, manager.getOrientation(mockCell1));
  }

  /**
   * Tests that setting the orientation of a creature to different values
   * correctly updates its orientation each time.
   */
  @Test
  void setOrientation_MultipleUpdates_TracksCurrentOrientation() {
    // Arrange
    manager.registerCreature(0, 0, mockCell1);

    // Act & Assert - multiple updates
    manager.setOrientation(mockCell1, 90.0);
    assertEquals(90.0, manager.getOrientation(mockCell1));

    manager.setOrientation(mockCell1, 180.0);
    assertEquals(180.0, manager.getOrientation(mockCell1));

    manager.setOrientation(mockCell1, 270.0);
    assertEquals(270.0, manager.getOrientation(mockCell1));
  }

  /**
   * Tests that setting the orientation of an unregistered creature
   * has no effect and doesn't cause errors.
   */
  @Test
  void setOrientation_UnregisteredCreature_NoEffect() {
    // Arrange - don't register any creatures
    double newDegrees = 90.0;

    // Act
    manager.setOrientation(mockCell1, newDegrees);

    // Assert - should still return default value
    assertEquals(Direction.EAST.getDegrees(), manager.getOrientation(mockCell1));
  }

  /**
   * Tests that getting the instruction index of a registered creature
   * returns the correct index.
   */
  @Test
  void getInstructionIndex_RegisteredCreature_ReturnsCorrectIndex() {
    // Arrange
    manager.registerCreature(0, 0, mockCell1);
    int newIndex = 5;
    manager.setInstructionIndex(mockCell1, newIndex);

    // Act
    int index = manager.getInstructionIndex(mockCell1);

    // Assert
    assertEquals(newIndex, index);
  }

  /**
   * Tests that getting the instruction index of an unregistered creature
   * returns the default index (0).
   */
  @Test
  void getInstructionIndex_UnregisteredCreature_ReturnsDefaultIndex() {
    // Arrange - don't register any creatures

    // Act
    int index = manager.getInstructionIndex(mockCell1);

    // Assert
    assertEquals(0, index);
  }

  /**
   * Tests that setting the instruction index of a registered creature
   * correctly updates its index.
   */
  @Test
  void setInstructionIndex_RegisteredCreature_UpdatesIndex() {
    // Arrange
    manager.registerCreature(0, 0, mockCell1);
    int newIndex = 5;

    // Act
    manager.setInstructionIndex(mockCell1, newIndex);

    // Assert
    assertEquals(newIndex, manager.getInstructionIndex(mockCell1));
  }

  /**
   * Tests that setting the instruction index of a creature to different values
   * correctly updates its index each time.
   */
  @Test
  void setInstructionIndex_MultipleUpdates_TracksCurrentIndex() {
    // Arrange
    manager.registerCreature(0, 0, mockCell1);

    // Act & Assert - multiple updates
    manager.setInstructionIndex(mockCell1, 1);
    assertEquals(1, manager.getInstructionIndex(mockCell1));

    manager.setInstructionIndex(mockCell1, 5);
    assertEquals(5, manager.getInstructionIndex(mockCell1));

    manager.setInstructionIndex(mockCell1, 10);
    assertEquals(10, manager.getInstructionIndex(mockCell1));
  }

  /**
   * Tests that setting the instruction index of an unregistered creature
   * has no effect and doesn't cause errors.
   */
  @Test
  void setInstructionIndex_UnregisteredCreature_NoEffect() {
    // Arrange - don't register any creatures
    int newIndex = 5;

    // Act
    manager.setInstructionIndex(mockCell1, newIndex);

    // Assert - should still return default value
    assertEquals(0, manager.getInstructionIndex(mockCell1));
  }

  /**
   * Tests that getting all creature locations returns the correct
   * locations for all registered creatures.
   */
  @Test
  void getAllCreatureLocations_RegisteredCreatures_ReturnsAllLocations() {
    // Arrange
    Cell mockCell3 = mock(Cell.class);

    // Set up cell states
    when(mockCell1.getCurrentState()).thenReturn(CreatureState.EMPTY);
    when(mockCell2.getCurrentState()).thenReturn(CreatureState.HUNTER);
    when(mockCell3.getCurrentState()).thenReturn(CreatureState.WANDERER);

    // Set up grid cells
    when(mockGrid.getCell(0, 0)).thenReturn(mockCell1);
    when(mockGrid.getCell(0, 1)).thenReturn(mockCell2);
    when(mockGrid.getCell(0, 2)).thenReturn(mockCell3);
    when(mockGrid.getCell(1, 0)).thenReturn(mock(Cell.class));
    when(mockGrid.getCell(1, 1)).thenReturn(mock(Cell.class));
    when(mockGrid.getCell(1, 2)).thenReturn(mock(Cell.class));
    when(mockGrid.getCell(2, 0)).thenReturn(mock(Cell.class));
    when(mockGrid.getCell(2, 1)).thenReturn(mock(Cell.class));
    when(mockGrid.getCell(2, 2)).thenReturn(mock(Cell.class));

    // Register creatures
    manager.registerCreature(0, 1, mockCell2);
    manager.registerCreature(0, 2, mockCell3);

    // Act
    List<CreatureLocation> locations = manager.getAllCreatureLocations();

    // Assert
    assertEquals(2, locations.size());

    // Verify first location
    CreatureLocation loc1 = locations.getFirst();
    assertEquals(0, loc1.getRow());
    assertEquals(1, loc1.getCol());
    assertEquals(mockCell2, loc1.getCell());

// Verify second location
    CreatureLocation loc2 = locations.get(1);
    assertEquals(0, loc2.getRow());
    assertEquals(2, loc2.getCol());
    assertEquals(mockCell3, loc2.getCell());
  }

  /**
   * Tests that getting all creature locations returns an empty list
   * when there are no creatures with non-empty states.
   */
  @Test
  void getAllCreatureLocations_NoLiveCreatures_ReturnsEmptyList() {
    // Arrange
    // Set up cell states as EMPTY
    when(mockCell1.getCurrentState()).thenReturn(CreatureState.EMPTY);
    when(mockCell2.getCurrentState()).thenReturn(CreatureState.EMPTY);

    // Set up grid cells
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        when(mockGrid.getCell(r, c)).thenReturn(mock(Cell.class));
        when(mockGrid.getCell(r, c).getCurrentState()).thenReturn(CreatureState.EMPTY);
      }
    }

    // Register creatures
    manager.registerCreature(0, 0, mockCell1);
    manager.registerCreature(1, 1, mockCell2);

    // Act
    List<CreatureLocation> locations = manager.getAllCreatureLocations();

    // Assert
    assertEquals(0, locations.size());
  }

  /**
   * Tests that getting all creature locations only includes creatures
   * that are both registered and have a non-empty state.
   */
  @Test
  void getAllCreatureLocations_MixedStates_ReturnsOnlyLiveCreatures() {
    // Arrange
    // Set up cell states
    when(mockCell1.getCurrentState()).thenReturn(CreatureState.EMPTY);
    when(mockCell2.getCurrentState()).thenReturn(CreatureState.HUNTER);

    // Set up grid cells
    when(mockGrid.getCell(0, 0)).thenReturn(mockCell1);
    when(mockGrid.getCell(0, 1)).thenReturn(mockCell2);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        if (r != 0 || (c != 0 && c != 1)) {
          when(mockGrid.getCell(r, c)).thenReturn(mock(Cell.class));
          when(mockGrid.getCell(r, c).getCurrentState()).thenReturn(CreatureState.EMPTY);
        }
      }
    }

    // Register creatures
    manager.registerCreature(0, 0, mockCell1);
    manager.registerCreature(0, 1, mockCell2);

    // Act
    List<CreatureLocation> locations = manager.getAllCreatureLocations();

    // Assert
    assertEquals(1, locations.size());
    CreatureLocation loc = locations.get(0);
    assertEquals(0, loc.getRow());
    assertEquals(1, loc.getCol());
    assertEquals(mockCell2, loc.getCell());
  }

  /**
   * Tests that clearing the manager removes all creature data.
   */
  @Test
  void clear_WithRegisteredCreatures_RemovesAllCreatureData() {
    // Arrange
    manager.registerCreature(0, 0, mockCell1);
    manager.registerCreature(1, 1, mockCell2);

    // Act
    manager.clear();

    // Assert - after clearing, cells should have default values
    assertEquals(Direction.EAST.getDegrees(), manager.getOrientation(mockCell1));
    assertEquals(0, manager.getInstructionIndex(mockCell1));
    assertEquals(Direction.EAST.getDegrees(), manager.getOrientation(mockCell2));
    assertEquals(0, manager.getInstructionIndex(mockCell2));

    // GetAllCreatureLocations should be empty
    when(mockGrid.getCell(anyInt(), anyInt())).thenReturn(mock(Cell.class));
    List<CreatureLocation> locations = manager.getAllCreatureLocations();
    assertEquals(0, locations.size());
  }

  /**
   * Tests that clearing an empty manager doesn't cause errors.
   */
  @Test
  void clear_EmptyManager_NoCrash() {
    // Arrange - don't register any creatures

    // Act
    manager.clear();

    // Assert - no exceptions should be thrown
    assertEquals(Direction.EAST.getDegrees(), manager.getOrientation(mockCell1));
    assertEquals(0, manager.getInstructionIndex(mockCell1));
  }

  /**
   * Tests that clearing and then registering new creatures works correctly.
   */
  @Test
  void clear_ThenRegisterNew_WorksCorrectly() {
    // Arrange
    manager.registerCreature(0, 0, mockCell1);
    manager.setOrientation(mockCell1, 90.0);
    manager.setInstructionIndex(mockCell1, 5);

    // Act
    manager.clear();
    manager.registerCreature(1, 1, mockCell2);

    // Assert
    assertEquals(Direction.EAST.getDegrees(), manager.getOrientation(mockCell1));
    assertEquals(0, manager.getInstructionIndex(mockCell1));
    assertEquals(Direction.EAST.getDegrees(), manager.getOrientation(mockCell2));
    assertEquals(0, manager.getInstructionIndex(mockCell2));
  }

  /**
   * Negative test that verifies behavior when a null grid is passed to the constructor.
   * Expected to throw NullPointerException.
   */
  @Test
  void constructor_NullGrid_ThrowsNullPointerException() {
    // Assert
    assertThrows(NullPointerException.class, () -> new CreatureManager(null));
  }

  /**
   * Negative test that verifies behavior when a null cell is passed to registerCreature.
   * Expected to throw NullPointerException.
   */
  @Test
  void registerCreature_NullCell_ThrowsNullPointerException() {
    // Assert
    assertThrows(NullPointerException.class, () -> manager.registerCreature(0, 0, null));
  }
}
