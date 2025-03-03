package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Cell;
import cellsociety.model.CreatureCell;
import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.Species;
import cellsociety.model.Instruction;
import cellsociety.model.state.DarwinState;
import cellsociety.model.StateInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Implementation of the Darwin simulation, modeling creatures following programmed behaviors.
 *
 * <p>This simulation models a world populated by different species of creatures where each species
 * is defined by the program they execute. Creatures can look ahead, move, turn, and infect other
 * creatures, all based on their species' programmed behavior.</p>
 *
 * <p>Key features:
 * <ul>
 *   <li>Species-specific behavior through program execution</li>
 *   <li>Creature movement and orientation in four cardinal directions</li>
 *   <li>Ability to infect other creatures, temporarily changing their species</li>
 *   <li>Conditional program execution based on environment sensing</li>
 * </ul>
 * </p>
 * @author Tatum McKinnis
 */
public class Darwin extends Simulation {

  private static final int EMPTY_STATE_KEY = 0;
  private static final int CREATURE_STATE_KEY = 1;
  private static final Random RANDOM = new Random();

  private final Map<String, Species> speciesRegistry;

  /**
   * Constructs a Darwin simulation with the specified parameters.
   *
   * @param simulationConfig Configuration settings for the simulation.
   * @param grid             The grid on which the simulation will run.
   * @param speciesMap       Map of species names to Species objects
   */
  public Darwin(SimulationConfig simulationConfig, Grid grid, Map<String, Species> speciesMap) {
    super(simulationConfig, grid);
    this.speciesRegistry = new HashMap<>(speciesMap);

    convertGridToCreatureCells(grid);
  }

  /**
   * Converts all cells in the grid to CreatureCells while preserving their states.
   *
   * @param grid the grid whose cells need to be converted
   */
  private void convertGridToCreatureCells(Grid grid) {
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        Cell cell = grid.getCell(r, c);
        if (!(cell instanceof CreatureCell)) {
          Species species = null;
          int orientation = 0;

          if (cell.getCurrentState() == DarwinState.CREATURE) {
            List<Species> allSpecies = new ArrayList<>(speciesRegistry.values());
            if (!allSpecies.isEmpty()) {
              species = allSpecies.get(RANDOM.nextInt(allSpecies.size()));
              orientation = RANDOM.nextInt(4) * 90;
            }
          }

          CreatureCell creatureCell = new CreatureCell(cell.getCurrentState(), species, orientation);
          grid.setCellAt(r, c, creatureCell);
        }
      }
    }
  }

  /**
   * Initializes the color map for visualizing the Darwin model.
   *
   * @return A map of states to color strings.
   */
  @Override
  protected Map<StateInterface, String> initializeColorMap() {
    Map<StateInterface, String> colorMap = new HashMap<>();
    colorMap.put(DarwinState.EMPTY, "darwin-state-empty");
    colorMap.put(DarwinState.CREATURE, "darwin-state-creature");
    return colorMap;
  }

  /**
   * Initializes the state map, associating state keys with Darwin state objects.
   *
   * @return A map of state keys to DarwinState objects.
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    Map<Integer, StateInterface> stateMap = new HashMap<>();
    stateMap.put(EMPTY_STATE_KEY, DarwinState.EMPTY);
    stateMap.put(CREATURE_STATE_KEY, DarwinState.CREATURE);
    return stateMap;
  }

  /**
   * Initializes state counts for all states to 0.0
   */
  @Override
  public void initializeStateCounts() {
    Map<StateInterface, Double> stateCounts = getStateCounts();
    stateCounts.put(DarwinState.EMPTY, 0.0);
    stateCounts.put(DarwinState.CREATURE, 0.0);
    setStateCounts(stateCounts);
  }

  /**
   * Applies the rules of the Darwin model for one iteration of the simulation.
   * Each creature executes its program until performing an action.
   */
  @Override
  public void applyRules() {
    Grid grid = getGrid();
    List<CreatureCell> processedCells = new ArrayList<>();

    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        CreatureCell cell = getCreatureCell(r, c);

        if (cell.getCurrentState() != DarwinState.CREATURE ||
            processedCells.contains(cell) ||
            cell.isInfected()) {
          continue;
        }

        executeProgram(cell, r, c);
        processedCells.add(cell);
      }
    }

    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        CreatureCell cell = getCreatureCell(r, c);
        if (cell.getCurrentState() == DarwinState.CREATURE && cell.isInfected()) {
          cell.decreaseInfectionCounter();
        }
      }
    }
  }

  /**
   * Executes a program for a creature cell until an action is performed.
   *
   * @param cell The creature cell
   * @param row  The row position of the cell
   * @param col  The column position of the cell
   */
  private void executeProgram(CreatureCell cell, int row, int col) {
    Species species = cell.getSpecies();
    if (species == null || species.getProgramLength() == 0) {
      return; // No program to execute
    }

    boolean actionPerformed = false;
    int instructionCount = 0;

    while (!actionPerformed && instructionCount < species.getProgramLength()) {
      int pc = cell.getProgramCounter();
      Instruction instruction = species.getInstruction(pc);

      actionPerformed = executeInstruction(cell, instruction, row, col);

      if (actionPerformed) {
        cell.advanceProgramCounter();
        break;
      }

      instructionCount++;
    }
  }

  /**
   * Executes a single instruction for a creature.
   *
   * @param cell        The creature cell
   * @param instruction The instruction to execute
   * @param row         The row position of the cell
   * @param col         The column position of the cell
   * @return true if an action was performed, false for control instructions
   */
  private boolean executeInstruction(CreatureCell cell, Instruction instruction, int row, int col) {
    Instruction.Type type = instruction.getType();
    int parameter = instruction.getParameter();

    switch (type) {
      case MOVE:
        return moveCreature(cell, row, col, parameter);

      case LEFT:
        cell.turnLeft(parameter);
        return true;

      case RIGHT:
        cell.turnRight(parameter);
        return true;

      case INFECT:
        return infectCreatureAhead(cell, row, col, parameter);

      case IFEMPTY:
        if (isEmptyAhead(row, col, cell.getOrientation())) {
          cell.setProgramCounter(parameter - 1);
        }
        cell.advanceProgramCounter();
        return false;

      case IFWALL:
        if (isWallAhead(row, col, cell.getOrientation())) {
          cell.setProgramCounter(parameter - 1);
        }
        cell.advanceProgramCounter();
        return false;

      case IFSAME:
        if (isSameSpeciesAhead(cell, row, col)) {
          cell.setProgramCounter(parameter - 1);
        }
        cell.advanceProgramCounter();
        return false;

      case IFENEMY:
        if (isEnemyAhead(cell, row, col)) {
          cell.setProgramCounter(parameter - 1);
        }
        cell.advanceProgramCounter();
        return false;

      case IFRANDOM:
        if (RANDOM.nextBoolean()) {
          cell.setProgramCounter(parameter - 1);
        }
        cell.advanceProgramCounter();
        return false;

      case GO:
        cell.setProgramCounter(parameter - 1);
        cell.advanceProgramCounter();
        return false;

      default:
        cell.advanceProgramCounter();
        return false;
    }
  }

  /**
   * Moves a creature in its current direction.
   *
   * @param cell     The creature cell
   * @param row      The current row position
   * @param col      The current column position
   * @param distance The distance to move
   * @return true if the move was performed
   */
  private boolean moveCreature(CreatureCell cell, int row, int col, int distance) {
    int[] newPosition = calculatePositionAhead(row, col, cell.getOrientation(), distance);
    int newRow = newPosition[0];
    int newCol = newPosition[1];

    Grid grid = getGrid();
    if (!grid.isValidPosition(newRow, newCol)) {
      return true;
    }

    CreatureCell targetCell = getCreatureCell(newRow, newCol);
    if (targetCell.getCurrentState() != DarwinState.EMPTY) {
      return true;
    }

    Species species = cell.getSpecies();
    int orientation = cell.getOrientation();
    int programCounter = cell.getProgramCounter();
    boolean infected = cell.isInfected();
    Species originalSpecies = cell.getOriginalSpecies();
    int infectionSteps = cell.getInfectionStepsRemaining();


    cell.setNextState(DarwinState.EMPTY);
    cell.setSpecies(null);
    cell.setOrientation(0);

    targetCell.setNextState(DarwinState.CREATURE);
    targetCell.setSpecies(species);
    targetCell.setOrientation(orientation);
    targetCell.setProgramCounter(programCounter);

    if (infected) {
      targetCell.setInfection(originalSpecies, infectionSteps);
    }

    return true;
  }

  /**
   * Attempts to infect a creature in front of the given creature.
   *
   * @param cell  The creature cell
   * @param row   The row position
   * @param col   The column position
   * @param steps The number of steps the infection lasts
   * @return true if an infection was attempted
   */
  private boolean infectCreatureAhead(CreatureCell cell, int row, int col, int steps) {

    int[] position = calculatePositionAhead(row, col, cell.getOrientation(), 1);

    Grid grid = getGrid();
    if (!grid.isValidPosition(position[0], position[1])) {
      return true;
    }

    CreatureCell targetCell = getCreatureCell(position[0], position[1]);

    if (targetCell.getCurrentState() == DarwinState.CREATURE &&
        !isSameSpecies(cell, targetCell)) {
      targetCell.setInfection(cell.getSpecies(), steps);
    }

    return true;
  }

  /**
   * Checks if the space ahead of a creature is empty.
   *
   * @param row         The row position
   * @param col         The column position
   * @param orientation The orientation in degrees
   * @return true if the space ahead is empty, false otherwise
   */
  private boolean isEmptyAhead(int row, int col, int orientation) {
    int[] position = calculatePositionAhead(row, col, orientation, 1);
    Grid grid = getGrid();

    if (!grid.isValidPosition(position[0], position[1])) {
      return false;
    }

    CreatureCell cell = getCreatureCell(position[0], position[1]);
    return cell.getCurrentState() == DarwinState.EMPTY;
  }

  /**
   * Checks if the space ahead of a creature is a wall (out of bounds).
   *
   * @param row         The row position
   * @param col         The column position
   * @param orientation The orientation in degrees
   * @return true if the space ahead is a wall, false otherwise
   */
  private boolean isWallAhead(int row, int col, int orientation) {
    int[] position = calculatePositionAhead(row, col, orientation, 1);
    return !getGrid().isValidPosition(position[0], position[1]);
  }

  /**
   * Checks if the space ahead of a creature contains a creature of the same species.
   *
   * @param cell The creature cell
   * @param row  The row position
   * @param col  The column position
   * @return true if a creature of the same species is ahead, false otherwise
   */
  private boolean isSameSpeciesAhead(CreatureCell cell, int row, int col) {
    int[] position = calculatePositionAhead(row, col, cell.getOrientation(), 1);
    Grid grid = getGrid();

    if (!grid.isValidPosition(position[0], position[1])) {
      return false;
    }

    CreatureCell targetCell = getCreatureCell(position[0], position[1]);
    return targetCell.getCurrentState() == DarwinState.CREATURE &&
        isSameSpecies(cell, targetCell);
  }

  /**
   * Checks if two creatures are of the same species.
   *
   * @param cell1 The first creature cell
   * @param cell2 The second creature cell
   * @return true if the creatures are of the same species, false otherwise
   */
  private boolean isSameSpecies(CreatureCell cell1, CreatureCell cell2) {
    return cell1.getSpecies() == cell2.getSpecies();
  }

  /**
   * Checks if the space ahead of a creature contains a creature of a different species.
   *
   * @param cell The creature cell
   * @param row  The row position
   * @param col  The column position
   * @return true if a creature of a different species is ahead, false otherwise
   */
  private boolean isEnemyAhead(CreatureCell cell, int row, int col) {
    int[] position = calculatePositionAhead(row, col, cell.getOrientation(), 1);
    Grid grid = getGrid();

    if (!grid.isValidPosition(position[0], position[1])) {
      return false;
    }

    CreatureCell targetCell = getCreatureCell(position[0], position[1]);
    return targetCell.getCurrentState() == DarwinState.CREATURE &&
        !isSameSpecies(cell, targetCell);
  }

  /**
   * Calculates the position ahead of a creature based on its orientation and distance.
   *
   * @param row         The current row position
   * @param col         The current column position
   * @param orientation The orientation in degrees
   * @param distance    The distance ahead
   * @return The [row, col] position ahead of the creature
   */
  private int[] calculatePositionAhead(int row, int col, int orientation, int distance) {
    int newRow = row;
    int newCol = col;

    switch (orientation) {
      case 0: // North
        newRow = row - distance;
        break;
      case 90: // East
        newCol = col + distance;
        break;
      case 180: // South
        newRow = row + distance;
        break;
      case 270: // West
        newCol = col - distance;
        break;
    }

    return new int[] {newRow, newCol};
  }

  /**
   * Retrieves the creature cell at the specified coordinates.
   *
   * @param row The row of the desired cell
   * @param col The column of the desired cell
   * @return The CreatureCell at the specified coordinates
   * @throws ClassCastException if the cell at the specified coordinates is not a CreatureCell
   */
  private CreatureCell getCreatureCell(int row, int col) {
    Cell cell = getGrid().getCell(row, col);
    if (!(cell instanceof CreatureCell)) {
      throw new ClassCastException(
          String.format("Cell at (%d,%d) is not a CreatureCell", row, col));
    }
    return (CreatureCell) cell;
  }

  /**
   * Adds a creature to the simulation at the specified position.
   *
   * @param row       The row position
   * @param col       The column position
   * @param species   The species of the creature
   * @param orientation The initial orientation
   * @return True if the creature was added, false if the position is invalid or occupied
   */
  public boolean addCreature(int row, int col, Species species, int orientation) {
    Grid grid = getGrid();

    if (!grid.isValidPosition(row, col)) {
      return false;
    }

    CreatureCell cell = getCreatureCell(row, col);
    if (cell.getCurrentState() != DarwinState.EMPTY) {
      return false;
    }

    cell.setCurrentState(DarwinState.CREATURE);
    cell.setSpecies(species);
    cell.setOrientation(orientation);

    return true;
  }

  /**
   * Gets the species registry map.
   *
   * @return The species registry map
   */
  public Map<String, Species> getSpeciesRegistry() {
    return new HashMap<>(speciesRegistry);
  }
}
