package cellsociety.model;

import cellsociety.model.Direction;

/**
 * Data class for creature metadata.
 */
public class CreatureData {
  int instructionIndex;
  Direction direction;

  CreatureData(int instructionIndex, Direction direction) {
    this.instructionIndex = instructionIndex;
    this.direction = direction;
  }
}

