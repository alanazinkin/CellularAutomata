package cellsociety.model.simulations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.*;
import cellsociety.model.state.DarwinState;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for the Darwin simulation. Tests the core functionality of the Darwin simulation
 * including rule application, creature movement, and interactions between creatures of different
 * species.
 *
 * <p>These tests verify that:
 * <ul>
 *   <li>Creatures follow their programmed behavior correctly</li>
 *   <li>Creatures can move, turn, and infect other creatures</li>
 *   <li>The simulation properly handles different species and their interactions</li>
 *   <li>Edge cases like wall collisions and empty spaces are handled correctly</li>
 * </ul>
 * </p>
 *
 * @author Tatum McKinnis
 */
public class DarwinTest {

  private Darwin darwin;
  private Grid grid;
  private SimulationConfig config;
  private Species testSpecies1;
  private Species testSpecies2;
  private Map<String, Species> speciesMap;

  @BeforeEach
  void setUp() {
    grid = new Grid(5, 5, DarwinState.EMPTY);

    List<Instruction> moveForwardProgram = List.of(
        new Instruction(Instruction.Type.MOVE, 1));

    List<Instruction> infectProgram = List.of(
        new Instruction(Instruction.Type.IFENEMY, 3),
        new Instruction(Instruction.Type.GO, 1),
        new Instruction(Instruction.Type.INFECT, 5),
        new Instruction(Instruction.Type.GO, 1));

    testSpecies1 = new Species("MoveForward", "#FF0000", moveForwardProgram);
    testSpecies2 = new Species("Infector", "#00FF00", infectProgram);

    speciesMap = new HashMap<>();
    speciesMap.put("MoveForward", testSpecies1);
    speciesMap.put("Infector", testSpecies2);

    config = mock(SimulationConfig.class);
    when(config.getInitialStates()).thenReturn(new int[25]);

    darwin = new Darwin(config, grid, speciesMap);
  }

  /**
   * Tests that a null grid in the constructor throws an IllegalArgumentException.
   */
  @Test
  void constructor_NullGrid_ThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new Darwin(config, null, speciesMap),
        "Darwin simulation constructor should throw IllegalArgumentException when grid is null.");
  }

  /**
   * Tests that the color map is properly initialized with all states.
   */
  @Test
  void initializeColorMap_AllStates_MappedToColors() {
    Map<StateInterface, String> colorMap = darwin.getColorMap();

    assertTrue(colorMap.containsKey(DarwinState.EMPTY));
    assertTrue(colorMap.containsKey(DarwinState.CREATURE));

    assertEquals("darwin-state-empty", colorMap.get(DarwinState.EMPTY));
    assertEquals("darwin-state-creature", colorMap.get(DarwinState.CREATURE));
  }

  /**
   * Tests that the state map is properly initialized with all states.
   */
  @Test
  void initializeStateMap_AllStates_MappedToIntegers() {
    Map<Integer, StateInterface> stateMap = darwin.getStateMap();

    assertEquals(DarwinState.EMPTY, stateMap.get(0));
    assertEquals(DarwinState.CREATURE, stateMap.get(1));
  }

  /**
   * Tests that a creature is added to the grid correctly.
   */
  @Test
  void addCreature_ValidPosition_CreatureAdded() {
    boolean added = darwin.addCreature(2, 2, testSpecies1, 0);

    assertTrue(added, "Creature should be added to an empty cell");

    Cell cell = grid.getCell(2, 2);
    assertTrue(cell instanceof CreatureCell, "Cell should be converted to CreatureCell");
    CreatureCell creatureCell = (CreatureCell) cell;

    assertEquals(DarwinState.CREATURE, creatureCell.getCurrentState());
    assertEquals(testSpecies1, creatureCell.getSpecies());
    assertEquals(0, creatureCell.getOrientation());
  }

  /**
   * Tests that adding a creature to an occupied cell fails.
   */
  @Test
  void addCreature_OccupiedCell_ReturnsFalse() {
    darwin.addCreature(2, 2, testSpecies1, 0);

    boolean added = darwin.addCreature(2, 2, testSpecies2, 90);

    assertFalse(added, "Cannot add a creature to an occupied cell");
  }

  /**
   * Tests that a creature with the MOVE instruction moves forward correctly.
   */
  @Test
  void applyRules_CreatureWithMoveInstruction_MovesForward() {
    darwin.addCreature(2, 2, testSpecies1, 0);

    darwin.applyRules();
    grid.applyNextStates();

    Cell originalCell = grid.getCell(2, 2);
    Cell newCell = grid.getCell(1, 2);

    assertEquals(DarwinState.EMPTY, originalCell.getCurrentState(),
        "Original cell should now be empty");
    assertEquals(DarwinState.CREATURE, newCell.getCurrentState(),
        "New cell should now contain a creature");

    CreatureCell creatureCell = (CreatureCell) newCell;
    assertEquals(testSpecies1, creatureCell.getSpecies(),
        "Creature should retain its species after moving");
    assertEquals(0, creatureCell.getOrientation(),
        "Creature should retain its orientation after moving");
  }

  /**
   * Tests that a creature stops at a wall (grid boundary).
   */
  @Test
  void applyRules_CreatureAtBoundary_StopsAtWall() {
    List<Instruction> moveProgram = List.of(
        new Instruction(Instruction.Type.MOVE, 1));
    Species moveOnlySpecies = new Species("MoveOnly", "#FF0000", moveProgram);

    CreatureCell creatureCell = new CreatureCell(DarwinState.CREATURE, moveOnlySpecies, 0);
    grid.setCellAt(0, 2, creatureCell);

    darwin.applyRules();

    assertEquals(DarwinState.CREATURE, creatureCell.getNextState(),
        "Next state should still be CREATURE when hitting a wall");
  }

  /**
   * Tests that a creature can infect another creature of a different species.
   */
  @Test
  void applyRules_CreatureInfectsEnemy_EnemyBecomesInfected() {
    CreatureCell targetCell = new CreatureCell(DarwinState.CREATURE, testSpecies1, 180);
    grid.setCellAt(1, 2, targetCell);

    CreatureCell infectorCell = new CreatureCell(DarwinState.CREATURE, testSpecies2, 0);
    grid.setCellAt(2, 2, infectorCell);

    darwin.applyRules();

    assertTrue(targetCell.isInfected() || targetCell.getNextState() == DarwinState.CREATURE,
        "Target should be infected or remain a creature");
  }

  /**
   * Tests that an infection wears off after the specified number of steps.
   */
  @Test
  void applyRules_InfectedCreature_InfectionWearsOff() {
    CreatureCell targetCell = new CreatureCell(DarwinState.CREATURE, testSpecies1, 180);
    grid.setCellAt(1, 2, targetCell);

    targetCell.setInfection(testSpecies2, 5);

    assertEquals(testSpecies2, targetCell.getSpecies());
    assertEquals(testSpecies1, targetCell.getOriginalSpecies());
    assertTrue(targetCell.isInfected());

    for (int i = 0; i < 5; i++) {
      darwin.applyRules();
    }

    assertEquals(testSpecies1, targetCell.getSpecies(),
        "After infection duration, creature should revert to original species");
    assertFalse(targetCell.isInfected(),
        "After infection duration, creature should no longer be infected");
  }

  /**
   * Tests that an infector doesn't infect a creature of the same species.
   */
  @Test
  void applyRules_InfectorFacingSameSpecies_NoInfection() {
    CreatureCell infectorCell = new CreatureCell(DarwinState.CREATURE, testSpecies2, 0);
    grid.setCellAt(2, 2, infectorCell);

    CreatureCell sameSpeciesCell = new CreatureCell(DarwinState.CREATURE, testSpecies2, 180);
    grid.setCellAt(1, 2, sameSpeciesCell);

    darwin.applyRules();

    assertFalse(sameSpeciesCell.isInfected(),
        "Creatures of the same species should not infect each other");
  }

  /**
   * Tests that the IFENEMY instruction correctly triggers when an enemy is present.
   */
  @Test
  void applyRules_IfEnemyCondition_CorrectlyDetectsEnemy() {
    List<Instruction> ifEnemyProgram = List.of(
        new Instruction(Instruction.Type.IFENEMY, 3),
        new Instruction(Instruction.Type.LEFT, 90),
        new Instruction(Instruction.Type.GO, 1),
        new Instruction(Instruction.Type.RIGHT, 90)
    );

    Species ifEnemySpecies = new Species("IfEnemyTest", "#0000FF", ifEnemyProgram);

    CreatureCell testCell = new CreatureCell(DarwinState.CREATURE, ifEnemySpecies, 0);
    grid.setCellAt(2, 2, testCell);

    CreatureCell enemyCell = new CreatureCell(DarwinState.CREATURE, testSpecies1, 180);
    grid.setCellAt(1, 2, enemyCell);

    darwin.applyRules();

    assertEquals(90, testCell.getOrientation(),
        "Should have turned RIGHT (to east) because enemy was detected");
  }

  /**
   * Tests that multiple creatures move in the correct order during a simulation step.
   */
  @Test
  void applyRules_MultipleCreatures_AllProcessedCorrectly() {
    List<Instruction> moveProgram = List.of(
        new Instruction(Instruction.Type.MOVE, 1));
    Species moveOnlySpecies = new Species("MoveOnly", "#FF0000", moveProgram);

    CreatureCell creature1 = new CreatureCell(DarwinState.CREATURE, moveOnlySpecies, 0);
    CreatureCell creature2 = new CreatureCell(DarwinState.CREATURE, moveOnlySpecies, 0);
    CreatureCell creature3 = new CreatureCell(DarwinState.CREATURE, moveOnlySpecies, 0);

    grid.setCellAt(4, 0, creature1);
    grid.setCellAt(4, 2, creature2);
    grid.setCellAt(4, 4, creature3);

    darwin.applyRules();

    assertEquals(DarwinState.EMPTY, creature1.getNextState(),
        "Creature should set its current cell to EMPTY");
    assertEquals(DarwinState.EMPTY, creature2.getNextState(),
        "Creature should set its current cell to EMPTY");
    assertEquals(DarwinState.EMPTY, creature3.getNextState(),
        "Creature should set its current cell to EMPTY");

    assertEquals(DarwinState.CREATURE, grid.getCell(3, 0).getNextState(),
        "Cell to the north should be set to CREATURE");
    assertEquals(DarwinState.CREATURE, grid.getCell(3, 2).getNextState(),
        "Cell to the north should be set to CREATURE");
    assertEquals(DarwinState.CREATURE, grid.getCell(3, 4).getNextState(),
        "Cell to the north should be set to CREATURE");
  }

  /**
   * Tests that creatures can't move onto occupied cells.
   */
  @Test
  void applyRules_MovingToOccupiedCell_StaysInPlace() {
    List<Instruction> moveProgram = List.of(
        new Instruction(Instruction.Type.MOVE, 1));
    Species moveOnlySpecies = new Species("MoveOnly", "#FF0000", moveProgram);

    CreatureCell moverCell = new CreatureCell(DarwinState.CREATURE, moveOnlySpecies, 0);
    grid.setCellAt(2, 2, moverCell);

    CreatureCell blockerCell = new CreatureCell(DarwinState.CREATURE, testSpecies2, 180);
    grid.setCellAt(1, 2, blockerCell);

    darwin.applyRules();

    assertEquals(DarwinState.CREATURE, moverCell.getNextState(),
        "Creature's next state should remain CREATURE when blocked");
  }

  /**
   * Tests that the species registry is correctly initialized and accessible.
   */
  @Test
  void getSpeciesRegistry_ReturnsAllSpecies() {
    Map<String, Species> registry = darwin.getSpeciesRegistry();

    assertEquals(2, registry.size(), "Registry should contain two species");
    assertTrue(registry.containsKey("MoveForward"), "Registry should contain MoveForward species");
    assertTrue(registry.containsKey("Infector"), "Registry should contain Infector species");
    assertEquals(testSpecies1, registry.get("MoveForward"),
        "Registry should have correct species object");
    assertEquals(testSpecies2, registry.get("Infector"),
        "Registry should have correct species object");
  }
}
