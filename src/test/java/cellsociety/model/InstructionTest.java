package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for Instruction. Tests the functionality of the Instruction class, which represents a
 * single instruction in a species program in the Darwin simulation.
 *
 * <p>These tests verify that:
 * <ul>
 *   <li>Instructions correctly identify as action or control instructions</li>
 *   <li>Instructions properly store their type and parameter</li>
 *   <li>String representation works correctly</li>
 * </ul>
 * </p>
 *
 * @author Tatum McKinnis
 */
public class InstructionTest {

  /**
   * Tests that the constructor correctly initializes all properties.
   */
  @Test
  void constructor_InitialValues_CorrectlyInitialized() {
    Instruction moveInstruction = new Instruction(Instruction.Type.MOVE, 2);

    assertEquals(Instruction.Type.MOVE, moveInstruction.getType(),
        "Type should be set to MOVE");
    assertEquals(2, moveInstruction.getParameter(),
        "Parameter should be set to 2");
    assertTrue(moveInstruction.isActionInstruction(),
        "MOVE should be identified as an action instruction");

    Instruction goInstruction = new Instruction(Instruction.Type.GO, 5);

    assertEquals(Instruction.Type.GO, goInstruction.getType(),
        "Type should be set to GO");
    assertEquals(5, goInstruction.getParameter(),
        "Parameter should be set to 5");
    assertFalse(goInstruction.isActionInstruction(),
        "GO should not be identified as an action instruction");
  }

  /**
   * Tests that all action instructions are correctly identified.
   */
  @Test
  void isActionInstruction_ActionTypes_ReturnsTrue() {
    List<Instruction.Type> actionTypes = Arrays.asList(
        Instruction.Type.MOVE,
        Instruction.Type.LEFT,
        Instruction.Type.RIGHT,
        Instruction.Type.INFECT
    );

    for (Instruction.Type type : actionTypes) {
      Instruction instruction = new Instruction(type, 1);
      assertTrue(instruction.isActionInstruction(),
          type + " should be identified as an action instruction");
    }
  }

  /**
   * Tests that all control instructions are correctly identified as not being action instructions.
   */
  @Test
  void isActionInstruction_ControlTypes_ReturnsFalse() {
    List<Instruction.Type> controlTypes = Arrays.asList(
        Instruction.Type.IFEMPTY,
        Instruction.Type.IFWALL,
        Instruction.Type.IFSAME,
        Instruction.Type.IFENEMY,
        Instruction.Type.IFRANDOM,
        Instruction.Type.GO
    );

    for (Instruction.Type type : controlTypes) {
      Instruction instruction = new Instruction(type, 1);
      assertFalse(instruction.isActionInstruction(),
          type + " should not be identified as an action instruction");
    }
  }

  /**
   * Tests that getType returns the correct instruction type.
   */
  @Test
  void getType_VariousTypes_ReturnsCorrectType() {
    assertEquals(Instruction.Type.MOVE, new Instruction(Instruction.Type.MOVE, 1).getType());
    assertEquals(Instruction.Type.LEFT, new Instruction(Instruction.Type.LEFT, 90).getType());
    assertEquals(Instruction.Type.RIGHT, new Instruction(Instruction.Type.RIGHT, 45).getType());
    assertEquals(Instruction.Type.INFECT, new Instruction(Instruction.Type.INFECT, 5).getType());
    assertEquals(Instruction.Type.IFEMPTY, new Instruction(Instruction.Type.IFEMPTY, 3).getType());
    assertEquals(Instruction.Type.IFWALL, new Instruction(Instruction.Type.IFWALL, 7).getType());
    assertEquals(Instruction.Type.IFSAME, new Instruction(Instruction.Type.IFSAME, 2).getType());
    assertEquals(Instruction.Type.IFENEMY, new Instruction(Instruction.Type.IFENEMY, 4).getType());
    assertEquals(Instruction.Type.IFRANDOM,
        new Instruction(Instruction.Type.IFRANDOM, 6).getType());
    assertEquals(Instruction.Type.GO, new Instruction(Instruction.Type.GO, 1).getType());
  }

  /**
   * Tests that getParameter returns the correct parameter value.
   */
  @Test
  void getParameter_VariousValues_ReturnsCorrectParameter() {
    int[] testParams = {0, 1, 10, 45, 90, 180, 360, 1000};

    for (int param : testParams) {
      Instruction instruction = new Instruction(Instruction.Type.MOVE, param);
      assertEquals(param, instruction.getParameter(),
          "Parameter should match the value provided to the constructor");
    }
  }

  /**
   * Tests that toString returns a properly formatted string representation.
   */
  @Test
  void toString_VariousInstructions_ReturnsFormattedString() {
    Instruction moveInstruction = new Instruction(Instruction.Type.MOVE, 2);
    assertEquals("MOVE 2", moveInstruction.toString(),
        "String representation should include type and parameter");

    Instruction leftInstruction = new Instruction(Instruction.Type.LEFT, 90);
    assertEquals("LEFT 90", leftInstruction.toString(),
        "String representation should include type and parameter");

    Instruction ifEnemyInstruction = new Instruction(Instruction.Type.IFENEMY, 5);
    assertEquals("IFENEMY 5", ifEnemyInstruction.toString(),
        "String representation should include type and parameter");

    Instruction goInstruction = new Instruction(Instruction.Type.GO, 1);
    assertEquals("GO 1", goInstruction.toString(),
        "String representation should include type and parameter");
  }

  /**
   * Tests that instructions with negative parameters are handled correctly.
   */
  @Test
  void constructor_NegativeParameter_StoresCorrectly() {
    Instruction instruction = new Instruction(Instruction.Type.MOVE, -1);

    assertEquals(-1, instruction.getParameter(),
        "Negative parameter should be stored correctly");
    assertEquals("MOVE -1", instruction.toString(),
        "String representation should show negative parameter");
  }

  /**
   * Tests that instructions with zero parameters are handled correctly.
   */
  @Test
  void constructor_ZeroParameter_StoresCorrectly() {
    Instruction instruction = new Instruction(Instruction.Type.MOVE, 0);

    assertEquals(0, instruction.getParameter(),
        "Zero parameter should be stored correctly");
    assertEquals("MOVE 0", instruction.toString(),
        "String representation should show zero parameter");
  }

  /**
   * Tests that instructions with very large parameters are handled correctly.
   */
  @Test
  void constructor_LargeParameter_StoresCorrectly() {
    int largeParam = Integer.MAX_VALUE;
    Instruction instruction = new Instruction(Instruction.Type.GO, largeParam);

    assertEquals(largeParam, instruction.getParameter(),
        "Large parameter should be stored correctly");
    assertEquals("GO " + largeParam, instruction.toString(),
        "String representation should show large parameter");
  }

  /**
   * Tests that each instruction type is represented in the Type enum.
   */
  @Test
  void type_AllCommandTypes_ExistInEnum() {
    Instruction.Type[] types = Instruction.Type.values();
    List<String> typeNames = Arrays.asList(
        "MOVE", "LEFT", "RIGHT", "INFECT",
        "IFEMPTY", "IFWALL", "IFSAME", "IFENEMY", "IFRANDOM", "GO"
    );

    assertEquals(typeNames.size(), types.length,
        "Type enum should have exactly " + typeNames.size() + " values");

    for (Instruction.Type type : types) {
      assertTrue(typeNames.contains(type.name()),
          "Type enum should contain " + type.name());
    }
  }

  /**
   * Tests the equality behavior of instructions with same type and parameter.
   */
  @Test
  void equals_SameTypeAndParameter_NotEqual() {
    Instruction instruction1 = new Instruction(Instruction.Type.MOVE, 2);
    Instruction instruction2 = new Instruction(Instruction.Type.MOVE, 2);

    assertNotEquals(instruction1, instruction2,
        "Different instruction instances should not be equal by default");
  }

  /**
   * Tests the instructions with different types are not equal.
   */
  @Test
  void equals_DifferentType_NotEqual() {
    Instruction moveInstruction = new Instruction(Instruction.Type.MOVE, 2);
    Instruction leftInstruction = new Instruction(Instruction.Type.LEFT, 2);

    assertNotEquals(moveInstruction, leftInstruction,
        "Instructions with different types should not be equal");
  }

  /**
   * Tests the instructions with different parameters are not equal.
   */
  @Test
  void equals_DifferentParameter_NotEqual() {
    Instruction instruction1 = new Instruction(Instruction.Type.MOVE, 1);
    Instruction instruction2 = new Instruction(Instruction.Type.MOVE, 2);

    assertNotEquals(instruction1, instruction2,
        "Instructions with different parameters should not be equal");
  }
}
