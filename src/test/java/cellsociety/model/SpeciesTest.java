package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for Species. Tests the functionality of the Species class,
 * which represents a species in the Darwin simulation with a name, color, and program.
 *
 * <p>These tests verify that:
 * <ul>
 *   <li>Species correctly stores its properties</li>
 *   <li>Program instructions are properly managed</li>
 *   <li>Error handling works correctly for invalid instruction indices</li>
 * </ul>
 * </p>
 *
 * @author Tatum McKinnis
 */
public class SpeciesTest {

  private Species species;
  private List<Instruction> program;
  private Instruction instruction1;
  private Instruction instruction2;
  private Instruction instruction3;

  @BeforeEach
  void setUp() {
    instruction1 = new Instruction(Instruction.Type.MOVE, 1);
    instruction2 = new Instruction(Instruction.Type.LEFT, 90);
    instruction3 = new Instruction(Instruction.Type.IFENEMY, 5);
    program = new ArrayList<>();
    program.add(instruction1);
    program.add(instruction2);
    program.add(instruction3);
    species = new Species("TestSpecies", "#FF0000", program);
  }

  /**
   * Tests that the constructor correctly initializes all properties.
   */
  @Test
  void constructor_InitialValues_CorrectlyInitialized() {
    assertEquals("TestSpecies", species.getName(),
        "Name should be set correctly");
    assertEquals("#FF0000", species.getColor(),
        "Color should be set correctly");
    assertEquals(3, species.getProgramLength(),
        "Program length should match the number of instructions");
  }

  /**
   * Tests that the constructor makes a defensive copy of the program.
   */
  @Test
  void constructor_ProgramList_MakesDefensiveCopy() {
    program.add(new Instruction(Instruction.Type.RIGHT, 45));
    assertEquals(3, species.getProgramLength(),
        "Program length should not change when original list is modified");
    assertThrows(IndexOutOfBoundsException.class, () -> species.getInstruction(3),
        "Should throw exception when accessing instruction that doesn't exist in species");
  }

  /**
   * Tests that getInstruction returns the correct instruction at a valid index.
   */
  @Test
  void getInstruction_ValidIndex_ReturnsCorrectInstruction() {
    assertEquals(instruction1, species.getInstruction(0),
        "First instruction should match");
    assertEquals(instruction2, species.getInstruction(1),
        "Second instruction should match");
    assertEquals(instruction3, species.getInstruction(2),
        "Third instruction should match");
  }

  /**
   * Tests that getInstruction throws an exception for a negative index.
   */
  @Test
  void getInstruction_NegativeIndex_ThrowsIndexOutOfBoundsException() {
    IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
        () -> species.getInstruction(-1),
        "Should throw exception for negative index");
    assertTrue(exception.getMessage().contains("Program index out of range"),
        "Exception message should indicate the index is out of range");
  }

  /**
   * Tests that getInstruction throws an exception for an index beyond the program length.
   */
  @Test
  void getInstruction_IndexBeyondLength_ThrowsIndexOutOfBoundsException() {
    IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
        () -> species.getInstruction(3),
        "Should throw exception for index beyond program length");
    assertTrue(exception.getMessage().contains("Program index out of range"),
        "Exception message should indicate the index is out of range");
  }

  /**
   * Tests that getProgramLength returns the correct number of instructions.
   */
  @Test
  void getProgramLength_ReturnsCorrectCount() {
    assertEquals(3, species.getProgramLength(),
        "Program length should match the number of instructions");
    Species emptyProgramSpecies = new Species("EmptyProgram", "#000000", new ArrayList<>());
    assertEquals(0, emptyProgramSpecies.getProgramLength(),
        "Program length should be 0 for empty program");
  }

  /**
   * Tests that toString returns the name of the species.
   */
  @Test
  void toString_ReturnsSpeciesName() {
    assertEquals("TestSpecies", species.toString(),
        "toString should return the species name");
    Species anotherSpecies = new Species("AnotherSpecies", "#00FF00", program);
    assertEquals("AnotherSpecies", anotherSpecies.toString(),
        "toString should return the species name");
  }

  /**
   * Tests that a species can be created with a very large program.
   */
  @Test
  void constructor_LargeProgram_HandlesProperly() {
    List<Instruction> largeProgram = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      largeProgram.add(new Instruction(Instruction.Type.GO, i + 1));
    }
    Species largeSpecies = new Species("LargeProgram", "#0000FF", largeProgram);
    assertEquals(1000, largeSpecies.getProgramLength(),
        "Program length should match the number of instructions");
    assertEquals(Instruction.Type.GO, largeSpecies.getInstruction(0).getType());
    assertEquals(1, largeSpecies.getInstruction(0).getParameter());
    assertEquals(Instruction.Type.GO, largeSpecies.getInstruction(500).getType());
    assertEquals(501, largeSpecies.getInstruction(500).getParameter());
    assertEquals(Instruction.Type.GO, largeSpecies.getInstruction(999).getType());
    assertEquals(1000, largeSpecies.getInstruction(999).getParameter());
  }
}
