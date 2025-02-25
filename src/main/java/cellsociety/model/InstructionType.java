package cellsociety.model;

/**
 * Represents a type of instruction that can be executed by a creature.
 */
public enum InstructionType {
  MOVE, LEFT, RIGHT, INFECT,
  IFEMPTY, IFWALL, IFSAME, IFENEMY, IFRANDOM,
  GO
}