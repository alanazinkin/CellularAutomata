package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cellsociety.model.state.DarwinState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Test class for CreatureCell. Tests the functionality of the CreatureCell class, which extends the
 * Cell class to add creature-specific properties for the Darwin simulation.
 *
 * <p>These tests verify that:
 * <ul>
 *   <li>CreatureCells correctly store and manage species information</li>
 *   <li>Orientation manipulation works correctly</li>
 *   <li>Program counter management functions properly</li>
 *   <li>Infection mechanics work as expected</li>
 *   <li>State transitions behave properly</li>
 * </ul>
 * </p>
 *
 * @author Tatum McKinnis
 */
public class CreatureCellTest {

  private CreatureCell creatureCell;
  private Species mockSpecies1;
  private Species mockSpecies2;

  @BeforeEach
  void setUp() {
    mockSpecies1 = mock(Species.class);
    when(mockSpecies1.getName()).thenReturn("TestSpecies1");
    when(mockSpecies1.getProgramLength()).thenReturn(5);

    mockSpecies2 = mock(Species.class);
    when(mockSpecies2.getName()).thenReturn("TestSpecies2");

    creatureCell = new CreatureCell(DarwinState.CREATURE, mockSpecies1, 90);
  }

  /**
   * Tests that the constructor correctly initializes the creature cell.
   */
  @Test
  void constructor_InitialValues_CorrectlyInitialized() {
    assertEquals(DarwinState.CREATURE, creatureCell.getCurrentState(),
        "Initial state should be CREATURE");
    assertEquals(mockSpecies1, creatureCell.getSpecies(),
        "Initial species should be mockSpecies1");
    assertEquals(90, creatureCell.getOrientation(),
        "Initial orientation should be 90 degrees");
    assertEquals(0, creatureCell.getProgramCounter(),
        "Initial program counter should be 0");
    assertFalse(creatureCell.isInfected(),
        "Initially should not be infected");
  }

  /**
   * Tests that the species can be changed.
   */
  @Test
  void setSpecies_NewSpecies_SpeciesChanged() {
    creatureCell.setSpecies(mockSpecies2);

    assertEquals(mockSpecies2, creatureCell.getSpecies(),
        "Species should be updated to mockSpecies2");

    assertEquals(mockSpecies1, creatureCell.getOriginalSpecies(),
        "Original species should NOT be updated when directly changing species");
  }

  /**
   * Tests that orientation is normalized when set.
   */
  @Test
  void setOrientation_VariousValues_NormalizedCorrectly() {
    creatureCell.setOrientation(0);
    assertEquals(0, creatureCell.getOrientation());

    creatureCell.setOrientation(90);
    assertEquals(90, creatureCell.getOrientation());

    creatureCell.setOrientation(180);
    assertEquals(180, creatureCell.getOrientation());

    creatureCell.setOrientation(270);
    assertEquals(270, creatureCell.getOrientation());

    creatureCell.setOrientation(-90);
    assertEquals(270, creatureCell.getOrientation(),
        "-90 degrees should normalize to 270 degrees");

    creatureCell.setOrientation(450);
    assertEquals(90, creatureCell.getOrientation(),
        "450 degrees should normalize to 90 degrees");
  }

  /**
   * Tests that the program counter can be advanced.
   */
  @Test
  void advanceProgramCounter_NormalCase_IncrementsByOne() {
    assertEquals(0, creatureCell.getProgramCounter());

    creatureCell.advanceProgramCounter();

    assertEquals(1, creatureCell.getProgramCounter(),
        "Program counter should increment by 1");
  }

  /**
   * Tests that the program counter wraps around when it reaches the end of the program.
   */
  @Test
  void advanceProgramCounter_ReachesEndOfProgram_WrapsAround() {
    creatureCell.setProgramCounter(4);
    creatureCell.advanceProgramCounter();

    assertEquals(0, creatureCell.getProgramCounter(),
        "Program counter should wrap around to 0 after reaching the end");
  }

  /**
   * Tests that the counter doesn't advance when species is null.
   */
  @Test
  void advanceProgramCounter_NullSpecies_CounterRemainsSame() {
    creatureCell.setSpecies(null);

    creatureCell.advanceProgramCounter();

    assertEquals(0, creatureCell.getProgramCounter(),
        "Program counter should not change when species is null");
  }

  /**
   * Tests that turning left changes orientation correctly.
   */
  @Test
  void turnLeft_VariousAngles_OrientationChangesCorrectly() {
    assertEquals(90, creatureCell.getOrientation());

    creatureCell.turnLeft(90);
    assertEquals(0, creatureCell.getOrientation(),
        "Should turn from east (90) to north (0)");

    creatureCell.turnLeft(45);
    assertEquals(315, creatureCell.getOrientation(),
        "Should turn from north (0) to northwest (315)");

    creatureCell.turnLeft(315);
    assertEquals(0, creatureCell.getOrientation(),
        "Should complete almost a full circle back to north (0)");
  }

  /**
   * Tests that turning right changes orientation correctly.
   */
  @Test
  void turnRight_VariousAngles_OrientationChangesCorrectly() {
    assertEquals(90, creatureCell.getOrientation());

    creatureCell.turnRight(90);
    assertEquals(180, creatureCell.getOrientation(),
        "Should turn from east (90) to south (180)");

    creatureCell.turnRight(45);
    assertEquals(225, creatureCell.getOrientation(),
        "Should turn from south (180) to southwest (225)");

    creatureCell.turnRight(315);
    assertEquals(180, creatureCell.getOrientation(),
        "Should complete almost a full circle back to south (180)");
  }

  /**
   * Tests that the infection is set correctly.
   */
  @Test
  void setInfection_ValidParameters_InfectionSetCorrectly() {
    creatureCell.setInfection(mockSpecies2, 5);

    assertTrue(creatureCell.isInfected(),
        "Cell should be marked as infected");
    assertEquals(mockSpecies2, creatureCell.getSpecies(),
        "Cell's species should change to the infecting species");
    assertEquals(mockSpecies1, creatureCell.getOriginalSpecies(),
        "Original species should be preserved");
    assertEquals(5, creatureCell.getInfectionStepsRemaining(),
        "Infection steps should be set correctly");
    assertEquals(0, creatureCell.getProgramCounter(),
        "Program counter should be reset after infection");
  }

  /**
   * Tests that the infection counter decreases correctly and reverts species when done.
   */
  @Test
  void decreaseInfectionCounter_InfectionActive_CounterDecreasesAndEventuallyReverts() {
    creatureCell.setInfection(mockSpecies2, 3);

    assertTrue(creatureCell.isInfected());
    assertEquals(mockSpecies2, creatureCell.getSpecies());
    assertEquals(3, creatureCell.getInfectionStepsRemaining());

    creatureCell.decreaseInfectionCounter();
    assertTrue(creatureCell.isInfected(),
        "Should still be infected");
    assertEquals(2, creatureCell.getInfectionStepsRemaining(),
        "Counter should decrease by 1");
    assertEquals(mockSpecies2, creatureCell.getSpecies(),
        "Species should still be the infecting species");

    creatureCell.decreaseInfectionCounter();
    assertTrue(creatureCell.isInfected());
    assertEquals(1, creatureCell.getInfectionStepsRemaining());

    creatureCell.decreaseInfectionCounter();
    assertFalse(creatureCell.isInfected(),
        "Should no longer be infected");
    assertEquals(0, creatureCell.getInfectionStepsRemaining(),
        "Counter should be 0");
    assertEquals(mockSpecies1, creatureCell.getSpecies(),
        "Species should revert to original");
  }

  /**
   * Tests that nothing happens when decreasing the counter for a non-infected cell.
   */
  @Test
  void decreaseInfectionCounter_NotInfected_NoChange() {
    assertFalse(creatureCell.isInfected());
    assertEquals(0, creatureCell.getInfectionStepsRemaining());

    creatureCell.decreaseInfectionCounter();

    assertFalse(creatureCell.isInfected(),
        "Should still not be infected");
    assertEquals(0, creatureCell.getInfectionStepsRemaining(),
        "Counter should still be 0");
  }

  /**
   * Tests that applying the next state works correctly.
   */
  @Test
  void applyNextState_StateChanged_UpdatesCurrentState() {
    creatureCell.setNextState(DarwinState.EMPTY);
    creatureCell.applyNextState();

    assertEquals(DarwinState.EMPTY, creatureCell.getCurrentState(),
        "Current state should be updated to EMPTY");
    assertEquals(DarwinState.CREATURE, creatureCell.getPrevState(),
        "Previous state should be updated to CREATURE");
  }

  /**
   * Tests that toString returns the expected string representation.
   */
  @Test
  void toString_VariousStates_ReturnsCorrectRepresentation() {
    assertEquals("TestSpecies1", creatureCell.toString(),
        "toString should return the species name for a creature");

    CreatureCell emptyCell = new CreatureCell(DarwinState.EMPTY, null, 0);
    assertEquals("Empty", emptyCell.toString(),
        "toString should return 'Empty' for an empty cell");

    CreatureCell noSpeciesCell = new CreatureCell(DarwinState.CREATURE, null, 0);
    assertEquals("No species", noSpeciesCell.toString(),
        "toString should return 'No species' for a creature without a species");
  }
}
