package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Cell;
import cellsociety.model.CreatureManager;
import cellsociety.model.state.CreatureState;
import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.StateInterface;
import cellsociety.model.CreatureLocation;
import cellsociety.model.Direction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creature World Simulation implementation extending the base Simulation class.
 * This simulation models a world populated by different species of creatures,
 * each defined by a program that determines its behavior on each step.
 *
 * @author Tatum McKinnis
 */
public class CreatureWorld extends Simulation {
  private static final String COLOR_EMPTY = "#FFFFFF";
  private static final String COLOR_HUNTER = "#FF0000";
  private static final String COLOR_WANDERER = "#0000FF";
  private static final String COLOR_FLYTRAP = "#00FF00";

  private final CreatureManager creatureManager;

  /**
   * Constructs a new CreatureWorldSimulation with the specified configuration and grid.
   *
   * @param simulationConfig contains initial simulation parameters and state configuration
   * @param grid the grid structure to use for this simulation
   */
  public CreatureWorld(SimulationConfig simulationConfig, Grid grid) {
    super(simulationConfig, grid);
    this.creatureManager = new CreatureManager(getGrid());
    initializeCreatures();
  }

  /**
   * Initializes the mapping between states and their color representations.
   *
   * @return a map from states to color strings
   */
  @Override
  protected Map<StateInterface, String> initializeColorMap() {
    Map<StateInterface, String> colorMap = new HashMap<>();
    colorMap.put(CreatureState.EMPTY, COLOR_EMPTY);
    colorMap.put(CreatureState.HUNTER, COLOR_HUNTER);
    colorMap.put(CreatureState.WANDERER, COLOR_WANDERER);
    colorMap.put(CreatureState.FLYTRAP, COLOR_FLYTRAP);
    return colorMap;
  }

  /**
   * Initializes the mapping between numeric values and states.
   *
   * @return a map from integers to state interfaces
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    Map<Integer, StateInterface> stateMap = new HashMap<>();
    for (CreatureState state : CreatureState.values()) {
      stateMap.put(state.getNumericValue(), state);
    }
    return stateMap;
  }

  /**
   * Initializes all creatures in the grid.
   */
  private void initializeCreatures() {
    Grid grid = getGrid();
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        Cell cell = grid.getCell(r, c);
        if (cell.getCurrentState() != CreatureState.EMPTY) {
          creatureManager.registerCreature(r, c, cell);
        }
      }
    }
  }

  /**
   * Reinitializes the grid states based on the provided configuration.
   *
   * @param simulationConfig the configuration containing initial states
   */
  @Override
  public void reinitializeGridStates(SimulationConfig simulationConfig) {
    super.reinitializeGridStates(simulationConfig);
    creatureManager.clear();
    initializeCreatures();
  }

  /**
   * Applies the rules of the creature world simulation.
   * This method is called during each simulation step to prepare cells
   * for the next state update.
   */
  @Override
  protected void applyRules() {
    List<CreatureLocation> creatureLocations = creatureManager.getAllCreatureLocations();

    // For each creature, set its next state based on its current state
    // This preserves the creature's state until external program execution modifies it
    for (CreatureLocation location : creatureLocations) {
      int row = location.getRow();
      int col = location.getCol();
      Cell cell = getGrid().getCell(row, col);

      // By default, maintain the current state for the next step
      cell.setNextState(cell.getCurrentState());
    }
  }

  /**
   * Gets the creature manager for this simulation.
   *
   * @return the creature manager
   */
  public CreatureManager getCreatureManager() {
    return creatureManager;
  }

  /**
   * Gets all creature locations in the current simulation state.
   *
   * @return a list of creature locations
   */
  public List<CreatureLocation> getAllCreatureLocations() {
    return creatureManager.getAllCreatureLocations();
  }

  /**
   * Gets the creature type at the specified location.
   *
   * @param row the row coordinate
   * @param col the column coordinate
   * @return the creature state at the location, or EMPTY if no creature exists
   */
  public CreatureState getCreatureTypeAt(int row, int col) {
    Grid grid = getGrid();
    if (!grid.isValidPosition(row, col)) {
      return CreatureState.EMPTY;
    }

    Cell cell = grid.getCell(row, col);
    return (CreatureState) cell.getCurrentState();
  }

  /**
   * Adds a creature to the grid at the specified location.
   *
   * @param row the row coordinate
   * @param col the column coordinate
   * @param state the creature state
   * @param direction the initial direction
   * @return true if the creature was added successfully, false otherwise
   */
  public boolean addCreature(int row, int col, CreatureState state, Direction direction) {
    Grid grid = getGrid();
    if (!grid.isValidPosition(row, col)) {
      return false;
    }

    Cell cell = grid.getCell(row, col);
    if (cell.getCurrentState() != CreatureState.EMPTY) {
      return false;
    }

    cell.setCurrentState(state);
    cell.setNextState(state); // Also update next state
    creatureManager.registerCreature(row, col, cell);
    creatureManager.setOrientation(cell, direction.getDegrees());
    return true;
  }

  /**
   * Removes a creature from the grid at the specified location.
   *
   * @param row the row coordinate
   * @param col the column coordinate
   * @return true if a creature was removed, false otherwise
   */
  public boolean removeCreature(int row, int col) {
    Grid grid = getGrid();
    if (!grid.isValidPosition(row, col)) {
      return false;
    }

    Cell cell = grid.getCell(row, col);
    if (cell.getCurrentState() == CreatureState.EMPTY) {
      return false;
    }

    // The CreatureManager doesn't have an unregisterCreature method in your provided code
    // So we'll just set the state to EMPTY and let the cell remain in the creatureDataMap
    cell.setCurrentState(CreatureState.EMPTY);
    cell.setNextState(CreatureState.EMPTY); // Also update next state
    return true;
  }

  /**
   * Moves a creature from one location to another.
   *
   * @param fromRow the source row coordinate
   * @param fromCol the source column coordinate
   * @param toRow the destination row coordinate
   * @param toCol the destination column coordinate
   * @return true if the creature was moved successfully, false otherwise
   */
  public boolean moveCreature(int fromRow, int fromCol, int toRow, int toCol) {
    Grid grid = getGrid();
    if (!grid.isValidPosition(fromRow, fromCol) || !grid.isValidPosition(toRow, toCol)) {
      return false;
    }

    Cell sourceCell = grid.getCell(fromRow, fromCol);
    Cell targetCell = grid.getCell(toRow, toCol);

    if (sourceCell.getCurrentState() == CreatureState.EMPTY) {
      return false; // No creature to move
    }

    if (targetCell.getCurrentState() != CreatureState.EMPTY) {
      return false; // Target cell is not empty
    }

    // Save source state
    CreatureState creatureState = (CreatureState) sourceCell.getCurrentState();

    // Update target cell state
    targetCell.setCurrentState(creatureState);
    targetCell.setNextState(creatureState); // Also update next state

    // Update source cell state
    sourceCell.setCurrentState(CreatureState.EMPTY);
    sourceCell.setNextState(CreatureState.EMPTY); // Also update next state

    // Update creature manager data
    creatureManager.updateCreatureLocation(sourceCell, targetCell);

    return true;
  }

  /**
   * Gets the orientation of a creature.
   *
   * @param row the row coordinate
   * @param col the column coordinate
   * @return the orientation in degrees, or 0 if no creature exists at the location
   */
  public double getCreatureOrientation(int row, int col) {
    Grid grid = getGrid();
    if (!grid.isValidPosition(row, col)) {
      return 0;
    }

    Cell cell = grid.getCell(row, col);
    if (cell.getCurrentState() == CreatureState.EMPTY) {
      return 0;
    }

    return creatureManager.getOrientation(cell);
  }

  /**
   * Sets the orientation of a creature.
   *
   * @param row the row coordinate
   * @param col the column coordinate
   * @param direction the direction to set
   * @return true if the orientation was set successfully, false otherwise
   */
  public boolean setCreatureOrientation(int row, int col, Direction direction) {
    Grid grid = getGrid();
    if (!grid.isValidPosition(row, col)) {
      return false;
    }

    Cell cell = grid.getCell(row, col);
    if (cell.getCurrentState() == CreatureState.EMPTY) {
      return false; // No creature to rotate
    }

    creatureManager.setOrientation(cell, direction.getDegrees());
    return true;
  }
}
