package cellsociety.model;

/**
 * Represents an instruction in a species program.
 * Instructions can be action instructions (MOVE, LEFT, RIGHT, INFECT)
 * or control instructions (IFEMPTY, IFWALL, IFSAME, IFENEMY, IFRANDOM, GO).
 * @author Tatum McKinnis
 */
public class Instruction {
  /**
   * The type of instruction.
   */
  public enum Type {
    MOVE,
    LEFT,
    RIGHT,
    INFECT,
    IFEMPTY,
    IFWALL,
    IFSAME,
    IFENEMY,
    IFRANDOM,
    GO
  }

  private final Type type;
  private final int parameter;
  private final boolean isActionInstruction;

  /**
   * Creates a new instruction with the specified type and parameter.
   *
   * @param type The type of instruction
   * @param parameter The parameter for this instruction (e.g., steps, degrees, instruction number)
   */
  public Instruction(Type type, int parameter) {
    this.type = type;
    this.parameter = parameter;

    this.isActionInstruction = type == Type.MOVE || type == Type.LEFT ||
        type == Type.RIGHT || type == Type.INFECT;
  }

  /**
   * Gets the type of this instruction.
   *
   * @return The instruction type
   */
  public Type getType() {
    return type;
  }

  /**
   * Gets the parameter for this instruction.
   *
   * @return The instruction parameter
   */
  public int getParameter() {
    return parameter;
  }

  /**
   * Checks if this is an action instruction that ends a simulation step.
   *
   * @return true if this is an action instruction, false otherwise
   */
  public boolean isActionInstruction() {
    return isActionInstruction;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.format("%s %d", type, parameter);
  }
}