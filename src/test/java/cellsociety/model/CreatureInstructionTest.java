package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test class for CreatureInstruction that tests the creation and behavior of creature instructions.
 * Tests focus on validating the correct construction, property retrieval, and string representation.
 */
class CreatureInstructionTest {

  /**
   * Tests that the constructor initializes the instruction type and parameter correctly,
   * and that the getters return the expected values.
   */
  @Test
  void getType_AfterConstruction_ReturnsCorrectType() {
    // Arrange
    InstructionType type = InstructionType.MOVE;
    int parameter = 5;

    // Act
    CreatureInstruction instruction = new CreatureInstruction(type, parameter);

    // Assert
    assertEquals(type, instruction.getType());
  }

  /**
   * Tests that the constructor works correctly with conditional instruction types.
   */
  @Test
  void getType_ConditionalInstructions_ReturnsCorrectType() {
    // Arrange & Act
    CreatureInstruction ifEmptyInstruction = new CreatureInstruction(InstructionType.IFEMPTY, 10);
    CreatureInstruction ifWallInstruction = new CreatureInstruction(InstructionType.IFWALL, 15);
    CreatureInstruction ifSameInstruction = new CreatureInstruction(InstructionType.IFSAME, 20);
    CreatureInstruction ifEnemyInstruction = new CreatureInstruction(InstructionType.IFENEMY, 25);
    CreatureInstruction ifRandomInstruction = new CreatureInstruction(InstructionType.IFRANDOM, 30);
    CreatureInstruction goInstruction = new CreatureInstruction(InstructionType.GO, 5);

    // Assert
    assertEquals(InstructionType.IFEMPTY, ifEmptyInstruction.getType());
    assertEquals(InstructionType.IFWALL, ifWallInstruction.getType());
    assertEquals(InstructionType.IFSAME, ifSameInstruction.getType());
    assertEquals(InstructionType.IFENEMY, ifEnemyInstruction.getType());
    assertEquals(InstructionType.IFRANDOM, ifRandomInstruction.getType());
    assertEquals(InstructionType.GO, goInstruction.getType());
  }

  /**
   * Tests that the getParameter method returns the correct parameter value
   * that was provided during construction.
   */
  @Test
  void getParameter_AfterConstruction_ReturnsCorrectParameter() {
    // Arrange
    InstructionType type = InstructionType.MOVE;
    int parameter = 5;

    // Act
    CreatureInstruction instruction = new CreatureInstruction(type, parameter);

    // Assert
    assertEquals(parameter, instruction.getParameter());
  }

  /**
   * Tests that the toString method returns a string with the expected format:
   * "InstructionType parameter".
   */
  @Test
  void toString_ValidInstruction_ReturnsFormattedString() {
    // Arrange
    InstructionType type = InstructionType.RIGHT;
    int parameter = 90;
    CreatureInstruction instruction = new CreatureInstruction(type, parameter);

    // Act
    String result = instruction.toString();

    // Assert
    assertEquals(type + " " + parameter, result);
  }

  /**
   * Tests that the toString method handles zero and negative parameter values correctly
   * in the string representation.
   */
  @Test
  void toString_NegativeParameter_IncludesNegativeSign() {
    // Arrange
    InstructionType type = InstructionType.LEFT;
    int parameter = -45;
    CreatureInstruction instruction = new CreatureInstruction(type, parameter);

    // Act
    String result = instruction.toString();

    // Assert
    assertEquals(type + " " + parameter, result);
  }

  /**
   * Tests that instructions with different instruction types store and return
   * their types correctly through the getType method.
   */
  @Test
  void getType_DifferentInstructionTypes_ReturnsCorrectTypes() {
    // Arrange & Act
    CreatureInstruction moveInstruction = new CreatureInstruction(InstructionType.MOVE, 1);
    CreatureInstruction leftInstruction = new CreatureInstruction(InstructionType.LEFT, 45);
    CreatureInstruction rightInstruction = new CreatureInstruction(InstructionType.RIGHT, 45);
    CreatureInstruction infectInstruction = new CreatureInstruction(InstructionType.INFECT, 0);

    // Assert
    assertEquals(InstructionType.MOVE, moveInstruction.getType());
    assertEquals(InstructionType.LEFT, leftInstruction.getType());
    assertEquals(InstructionType.RIGHT, rightInstruction.getType());
    assertEquals(InstructionType.INFECT, infectInstruction.getType());
  }

  /**
   * Tests that instructions with different parameter values store and return
   * their parameters correctly through the getParameter method.
   */
  @Test
  void getParameter_DifferentParameters_ReturnsCorrectValues() {
    // Arrange & Act
    CreatureInstruction instruction1 = new CreatureInstruction(InstructionType.MOVE, 0);
    CreatureInstruction instruction2 = new CreatureInstruction(InstructionType.MOVE, 100);
    CreatureInstruction instruction3 = new CreatureInstruction(InstructionType.MOVE, -50);

    // Assert
    assertEquals(0, instruction1.getParameter());
    assertEquals(100, instruction2.getParameter());
    assertEquals(-50, instruction3.getParameter());
  }
}
