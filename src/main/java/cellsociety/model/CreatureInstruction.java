package cellsociety.model;

import cellsociety.model.InstructionType;

/**
 * Represents an instruction that can be executed by a creature.
 */
public class CreatureInstruction {
  private final InstructionType type;
  private final int parameter;

  /**
   * Constructs a new creature instruction.
   *
   * @param type the type of instruction
   * @param parameter the parameter for the instruction
   */
  public CreatureInstruction(InstructionType type, int parameter) {
    this.type = type;
    this.parameter = parameter;
  }

  /**
   * Gets the type of this instruction.
   *
   * @return the instruction type
   */
  public InstructionType getType() {
    return type;
  }

  /**
   * Gets the parameter of this instruction.
   *
   * @return the instruction parameter
   */
  public int getParameter() {
    return parameter;
  }

  @Override
  public String toString() {
    return type + " " + parameter;
  }
}

