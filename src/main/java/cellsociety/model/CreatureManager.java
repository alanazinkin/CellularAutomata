package cellsociety.model;

import cellsociety.model.state.CreatureState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages creatures in the simulation, tracking their positions and orientations.
 */
public class CreatureManager {

  private final Grid grid;
  private final Map<Cell, CreatureData> creatureDataMap = new HashMap<>();

  /**
   * Constructs a new CreatureManager for the given grid.
   *
   * @param grid the grid containing creatures
   */
  public CreatureManager(Grid grid) {
    if (grid == null) {
      throw new NullPointerException("Grid cannot be null");
    }
    this.grid = grid;
  }


  /**
   * Registers a creature at the specified location.
   *
   * @param row  the row of the creature
   * @param col  the column of the creature
   * @param cell the cell containing the creature
   */
  public void registerCreature(int row, int col, Cell cell) {
    if (cell == null) {
      throw new NullPointerException("Cell cannot be null");
    }
    creatureDataMap.put(cell, new CreatureData(0, Direction.EAST));
  }

  /**
   * Updates a creature's location when it moves.
   *
   * @param oldCell the old cell
   * @param newCell the new cell
   */
  public void updateCreatureLocation(Cell oldCell, Cell newCell) {
    CreatureData data = creatureDataMap.remove(oldCell);
    if (data != null) {
      creatureDataMap.put(newCell, data);
    }
  }

  /**
   * Gets the orientation of a creature.
   *
   * @param cell the cell containing the creature
   * @return the orientation in degrees
   */
  public double getOrientation(Cell cell) {
    CreatureData data = creatureDataMap.get(cell);
    return data != null ? data.direction.getDegrees() : Direction.EAST.getDegrees();
  }

  /**
   * Sets the orientation of a creature.
   *
   * @param cell    the cell containing the creature
   * @param degrees the orientation in degrees
   */
  public void setOrientation(Cell cell, double degrees) {
    CreatureData data = creatureDataMap.get(cell);
    if (data != null) {
      data.direction = Direction.fromDegrees(degrees);
    }
  }

  /**
   * Gets the program instruction index for a creature.
   *
   * @param cell the cell containing the creature
   * @return the instruction index
   */
  public int getInstructionIndex(Cell cell) {
    CreatureData data = creatureDataMap.get(cell);
    return data != null ? data.instructionIndex : 0;
  }

  /**
   * Sets the program instruction index for a creature.
   *
   * @param cell  the cell containing the creature
   * @param index the instruction index
   */
  public void setInstructionIndex(Cell cell, int index) {
    CreatureData data = creatureDataMap.get(cell);
    if (data != null) {
      data.instructionIndex = index;
    }
  }

  /**
   * Gets all creature locations in the grid.
   *
   * @return a list of creature locations
   */
  public List<CreatureLocation> getAllCreatureLocations() {
    List<CreatureLocation> locations = new ArrayList<>();

    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        Cell cell = grid.getCell(r, c);
        if (cell.getCurrentState() != CreatureState.EMPTY && creatureDataMap.containsKey(cell)) {
          locations.add(new CreatureLocation(r, c, cell));
        }
      }
    }

    return locations;
  }

  /**
   * Clears all creature data.
   */
  public void clear() {
    creatureDataMap.clear();
  }
}
