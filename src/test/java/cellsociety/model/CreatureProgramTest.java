package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Test class for {@link CreatureProgram}.
 * Tests the functionality of the program that defines a creature's behavior.
 *
 * @author Tatum McKinnis
 */
public class CreatureProgramTest {

  private CreatureProgram program;
  private static final String SPECIES_NAME = "TestSpecies";

  /**
   * Sets up the test environment before each test case.
   * Creates a new CreatureProgram for a test species.
   */
  @BeforeEach
  public void setUp() {
    program = new CreatureProgram(SPECIES_NAME);
  }

  /**
   * Tests that the constructor properly initializes the program with the given species name.
   */
  @Test
  public void constructor_WithSpeciesName_InitializesCorrectly() {
    assertEquals(SPECIES_NAME, program.getSpeciesName());
    assertTrue(program.getInstructions().isEmpty());
  }

  /**
   * Tests that adding an instruction to the program works correctly.
   */
  @Test
  public void addInstruction_WithValidInstruction_AddsToList() {
    CreatureInstruction mockInstruction = mock(CreatureInstruction.class);
    program.addInstruction(mockInstruction);

    assertEquals(1, program.getInstructions().size());
    assertSame(mockInstruction, program.getInstructions().get(0));
  }

  /**
   * Tests that adding multiple instructions to the program maintains their order.
   */
  @Test
  public void addInstruction_WithMultipleInstructions_MaintainsOrder() {
    CreatureInstruction instruction1 = mock(CreatureInstruction.class);
    CreatureInstruction instruction2 = mock(CreatureInstruction.class);
    CreatureInstruction instruction3 = mock(CreatureInstruction.class);

    program.addInstruction(instruction1);
    program.addInstruction(instruction2);
    program.addInstruction(instruction3);

    List<CreatureInstruction> instructions = program.getInstructions();
    assertEquals(3, instructions.size());
    assertSame(instruction1, instructions.get(0));
    assertSame(instruction2, instructions.get(1));
    assertSame(instruction3, instructions.get(2));
  }

  /**
   * Tests that adding multiple instructions at once works correctly.
   */
  @Test
  public void addInstructions_WithValidList_AddsAllInstructions() {
    CreatureInstruction instruction1 = mock(CreatureInstruction.class);
    CreatureInstruction instruction2 = mock(CreatureInstruction.class);
    List<CreatureInstruction> instructionList = List.of(instruction1, instruction2);

    program.addInstructions(instructionList);

    List<CreatureInstruction> result = program.getInstructions();
    assertEquals(2, result.size());
    assertSame(instruction1, result.get(0));
    assertSame(instruction2, result.get(1));
  }

  /**
   * Tests that clearing instructions works correctly.
   */
  @Test
  public void clearInstructions_WithExistingInstructions_RemovesAll() {
    CreatureInstruction instruction = mock(CreatureInstruction.class);
    program.addInstruction(instruction);

    program.clearInstructions();

    assertTrue(program.getInstructions().isEmpty());
  }

  /**
   * Tests that the toString method returns a non-empty string.
   */
  @Test
  public void toString_WithInstructions_ReturnsFormattedString() {
    CreatureInstruction instruction = mock(CreatureInstruction.class);
    when(instruction.toString()).thenReturn("MOVE 10");
    program.addInstruction(instruction);

    String result = program.toString();

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertTrue(result.contains(SPECIES_NAME));
    assertTrue(result.contains("MOVE 10"));
  }
}