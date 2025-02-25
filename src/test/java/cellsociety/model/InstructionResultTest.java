package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test class for InstructionResult that validates the creation and behavior of instruction results.
 * Tests focus on validating the correct construction, property retrieval, and handling of different
 * scenarios like normal flow continuation or action execution.
 */
class InstructionResultTest {

  /**
   * Tests that the constructor initializes the next instruction index and action executed flag
   * correctly, and that the getters return the expected values.
   */
  @Test
  void getNextInstructionIndex_PositiveIndex_ReturnsCorrectIndex() {
    // Arrange
    int nextIndex = 5;
    boolean actionExecuted = true;

    // Act
    InstructionResult result = new InstructionResult(nextIndex, actionExecuted);

    // Assert
    assertEquals(nextIndex, result.getNextInstructionIndex());
  }

  /**
   * Tests that the isActionExecuted method returns true when an action has been executed.
   */
  @Test
  void isActionExecuted_ActionExecutedTrue_ReturnsTrue() {
    // Arrange
    int nextIndex = 5;
    boolean actionExecuted = true;

    // Act
    InstructionResult result = new InstructionResult(nextIndex, actionExecuted);

    // Assert
    assertTrue(result.isActionExecuted());
  }

  /**
   * Tests that the isActionExecuted method returns false when no action has been executed.
   */
  @Test
  void isActionExecuted_ActionExecutedFalse_ReturnsFalse() {
    // Arrange
    int nextIndex = 0;
    boolean actionExecuted = false;

    // Act
    InstructionResult result = new InstructionResult(nextIndex, actionExecuted);

    // Assert
    assertFalse(result.isActionExecuted());
  }

  /**
   * Tests that the getNextInstructionIndex method returns the correct value when the index is
   * zero.
   */
  @Test
  void getNextInstructionIndex_ZeroIndex_ReturnsZero() {
    // Arrange
    int nextIndex = 0;
    boolean actionExecuted = true;

    // Act
    InstructionResult result = new InstructionResult(nextIndex, actionExecuted);

    // Assert
    assertEquals(nextIndex, result.getNextInstructionIndex());
  }

  /**
   * Tests that the getNextInstructionIndex method returns the correct value when the index is
   * negative. This is the special case where -1 indicates continuing with normal program flow.
   */
  @Test
  void getNextInstructionIndex_NegativeIndex_ReturnsNegativeOne() {
    // Arrange
    int continueNormallyIndex = -1; // The code indicates -1 means continue normally
    boolean actionExecuted = true;

    // Act
    InstructionResult result = new InstructionResult(continueNormallyIndex, actionExecuted);

    // Assert
    assertEquals(continueNormallyIndex, result.getNextInstructionIndex());
  }

  /**
   * Tests creating an InstructionResult with different combinations of index and action executed
   * flag.
   */
  @Test
  void constructor_VariousCombinations_ReturnsCorrectValues() {
    // Arrange & Act
    InstructionResult result1 = new InstructionResult(1, true);
    InstructionResult result2 = new InstructionResult(2, false);
    InstructionResult result3 = new InstructionResult(-1, true);
    InstructionResult result4 = new InstructionResult(-1, false);

    // Assert
    assertEquals(1, result1.getNextInstructionIndex());
    assertTrue(result1.isActionExecuted());

    assertEquals(2, result2.getNextInstructionIndex());
    assertFalse(result2.isActionExecuted());

    assertEquals(-1, result3.getNextInstructionIndex());
    assertTrue(result3.isActionExecuted());

    assertEquals(-1, result4.getNextInstructionIndex());
    assertFalse(result4.isActionExecuted());
  }

  /**
   * Tests the scenario where an action is not executed but a specific next instruction is
   * provided.
   */
  @Test
  void constructor_NoActionWithNextInstruction_ReturnsCorrectValues() {
    // Arrange
    int nextIndex = 10;
    boolean actionExecuted = false;

    // Act
    InstructionResult result = new InstructionResult(nextIndex, actionExecuted);

    // Assert
    assertEquals(nextIndex, result.getNextInstructionIndex());
    assertFalse(result.isActionExecuted());
  }

  /**
   * Negative test for extreme index value (MAX_INTEGER). Tests that the constructor and getter
   * properly handle extreme values.
   */
  @Test
  void getNextInstructionIndex_MaxInteger_ReturnsMaxInteger() {
    // Arrange
    int nextIndex = Integer.MAX_VALUE;
    boolean actionExecuted = true;

    // Act
    InstructionResult result = new InstructionResult(nextIndex, actionExecuted);

    // Assert
    assertEquals(nextIndex, result.getNextInstructionIndex());
  }

  /**
   * Negative test for extreme index value (MIN_INTEGER). Tests that the constructor and getter
   * properly handle extreme values.
   */
  @Test
  void getNextInstructionIndex_MinInteger_ReturnsMinInteger() {
    // Arrange
    int nextIndex = Integer.MIN_VALUE;
    boolean actionExecuted = false;

    // Act
    InstructionResult result = new InstructionResult(nextIndex, actionExecuted);

    // Assert
    assertEquals(nextIndex, result.getNextInstructionIndex());
  }
}
