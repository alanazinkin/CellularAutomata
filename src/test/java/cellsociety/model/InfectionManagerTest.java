package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link InfectionManager}.
 * Tests the functionality of managing infections in the simulation.
 *
 * @author Tatum McKinnis
 */
public class InfectionManagerTest {

  private InfectionManager manager;
  private Cell mockCell;
  private StateInterface mockOriginalState;
  private Grid mockGrid;

  /**
   * Sets up the test environment before each test case.
   */
  @BeforeEach
  public void setUp() {
    manager = new InfectionManager();
    mockCell = mock(Cell.class);
    mockOriginalState = mock(StateInterface.class);
    mockGrid = mock(Grid.class);
  }

  /**
   * Tests that infecting a creature works correctly.
   */
  @Test
  public void infectCreature_WithValidParameters_InfectsSuccessfully() {
    manager.infectCreature(mockCell, mockOriginalState, 5);

    assertTrue(manager.isNewlyInfected(mockCell));
  }

  /**
   * Tests that a newly infected creature is detected correctly.
   */
  @Test
  public void isNewlyInfected_AfterInfection_ReturnsTrue() {
    manager.infectCreature(mockCell, mockOriginalState, 5);

    assertTrue(manager.isNewlyInfected(mockCell));
  }

  /**
   * Tests that a non-infected creature is detected correctly.
   */
  @Test
  public void isNewlyInfected_WithNonInfectedCell_ReturnsFalse() {
    assertFalse(manager.isNewlyInfected(mockCell));
  }

  /**
   * Tests that updating infections decreases the remaining steps.
   */
  @Test
  public void updateInfections_AfterInfection_UpdatesInfectionStatus() {
    manager.infectCreature(mockCell, mockOriginalState, 1);

    manager.updateInfections(mockGrid);

    // After updating, the cell should no longer be newly infected
    assertFalse(manager.isNewlyInfected(mockCell));

    // Cell's next state should be set to the original state
    verify(mockCell).setNextState(mockOriginalState);
  }

  /**
   * Tests that infections with multiple steps work correctly.
   */
  @Test
  public void updateInfections_WithMultipleSteps_MaintainsInfection() {
    manager.infectCreature(mockCell, mockOriginalState, 2);

    manager.updateInfections(mockGrid);

    assertFalse(manager.isNewlyInfected(mockCell));
    verify(mockCell, never()).setNextState(any());

    manager.updateInfections(mockGrid);

    verify(mockCell).setNextState(mockOriginalState);
  }

  /**
   * Tests that clearing the manager works correctly.
   */
  @Test
  public void clear_AfterInfection_RemovesAllInfections() {
    manager.infectCreature(mockCell, mockOriginalState, 5);
    manager.clear();

    assertFalse(manager.isNewlyInfected(mockCell));
  }

  /**
   * Tests that multiple cells can be infected independently.
   */
  @Test
  public void infectCreature_WithMultipleCells_InfectsIndependently() {
    Cell mockCell2 = mock(Cell.class);
    StateInterface mockOriginalState2 = mock(StateInterface.class);

    manager.infectCreature(mockCell, mockOriginalState, 3);
    manager.infectCreature(mockCell2, mockOriginalState2, 5);

    assertTrue(manager.isNewlyInfected(mockCell));
    assertTrue(manager.isNewlyInfected(mockCell2));

    manager.updateInfections(mockGrid);

    assertFalse(manager.isNewlyInfected(mockCell));
    assertFalse(manager.isNewlyInfected(mockCell2));

    manager.updateInfections(mockGrid);
    manager.updateInfections(mockGrid);

    verify(mockCell).setNextState(mockOriginalState);
    verify(mockCell2, never()).setNextState(any());

    manager.updateInfections(mockGrid);
    manager.updateInfections(mockGrid);

    verify(mockCell2).setNextState(mockOriginalState2);
  }
}