package cellsociety.model;

/**
 * Represents the result of executing an instruction.
 * @author Tatum McKinnis
 */
public class InstructionResult {
  private final int nextInstructionIndex;
  private final boolean actionExecuted;

  /**
   * Constructs a new instruction result.
   *
   * @param nextInstructionIndex the index of the next instruction to execute, or -1 to continue normally
   * @param actionExecuted whether an action was executed
   */
  InstructionResult(int nextInstructionIndex, boolean actionExecuted) {
    this.nextInstructionIndex = nextInstructionIndex;
    this.actionExecuted = actionExecuted;
  }

  /**
   * Gets the index of the next instruction to execute.
   *
   * @return the next instruction index, or -1 to continue normally
   */
  public int getNextInstructionIndex() {
    return nextInstructionIndex;
  }

  /**
   * Checks if an action was executed.
   *
   * @return true if an action was executed, false otherwise
   */
  public boolean isActionExecuted() {
    return actionExecuted;
  }
}
