package cellsociety.model;

/**
 * Data class for creature metadata.
 * <p>
 * This class encapsulates the state information for a creature, including its current instruction index and direction.
 * </p>
 * @author Tatum McKinnis
 */
public class CreatureData {

  /**
   * The index of the current instruction for the creature.
   */
  int instructionIndex;

  /**
   * The direction the creature is currently facing.
   */
  Direction direction;

  /**
   * Constructs a new CreatureData instance with the specified instruction index and direction.
   *
   * @param instructionIndex the index of the instruction that the creature is currently executing
   * @param direction the direction the creature is currently facing
   */
  CreatureData(int instructionIndex, Direction direction) {
    this.instructionIndex = instructionIndex;
    this.direction = direction;
  }
}


