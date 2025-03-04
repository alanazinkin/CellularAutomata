package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Grid;
import cellsociety.model.Cell;
import cellsociety.model.Simulation;
import cellsociety.model.state.GameOfLifeState;
import cellsociety.model.StateInterface;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule-based implementation of the Game of Life simulation. In this version, cells have specific
 * rules governing birth and survival conditions.
 *
 * @author Tatum McKinnis
 */
public class RuleBasedGameOfLife extends Simulation {

  private final Set<Integer> surviveConditions = new HashSet<>();
  private final Set<Integer> birthConditions = new HashSet<>();

  /**
   * Constructor that initializes the Game of Life simulation with a given configuration and grid.
   * It parses the rule code to determine the birth and survival conditions.
   *
   * @param config The simulation configuration containing parameters like ruleCode.
   * @param grid   The grid representing the environment in which the simulation takes place.
   */
  public RuleBasedGameOfLife(SimulationConfig config, Grid grid) {
    super(config, grid);

    // Get the rule code from parameters, supporting string or numeric format
    Object ruleCodeParam = config.getParameters().getOrDefault("ruleCode", 323.0);

    if (ruleCodeParam instanceof Double) {
      parseNumericRuleCode((Double) ruleCodeParam);
    } else {
      parseStringRuleCode(ruleCodeParam.toString());
    }
  }

  /**
   * Initializes the color map for the simulation, mapping each cell state to a specific color.
   *
   * @return A map of cell states to CSS class names representing colors.
   */
  @Override
  public Map<StateInterface, String> initializeColorMap() {
    return Map.of(
        GameOfLifeState.ALIVE, "gameoflife-state-alive",
        GameOfLifeState.DEAD, "gameoflife-state-dead"
    );
  }

  /**
   * Initializes state counts for all states to 0.0
   */
  @Override
  public void initializeStateCounts() {
    Map<StateInterface, Double> stateCounts = getStateCounts();
    stateCounts.put(GameOfLifeState.ALIVE, 0.0);
    stateCounts.put(GameOfLifeState.DEAD, 0.0);
    setStateCounts(stateCounts);
  }

  /**
   * Initializes the state map, associating integers with the corresponding GameOfLifeState.
   *
   * @return A map of integer values to GameOfLifeState (0 -> DEAD, 1 -> ALIVE).
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    return Map.of(
        0, GameOfLifeState.DEAD,
        1, GameOfLifeState.ALIVE
    );
  }

  /**
   * Applies the rules of the Game of Life to all cells in the grid. For each cell, the next state
   * is determined based on the current state and the number of live neighbors.
   */
  @Override
  public void applyRules() {
    Grid grid = getGrid();
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        Cell cell = grid.getCell(r, c);
        validateCellState(cell);
        GameOfLifeState currentState = (GameOfLifeState) cell.getCurrentState();
        List<Cell> neighbors = grid.getNeighbors(r, c);
        int liveNeighbors = countLiveNeighbors(neighbors);
        cell.setNextState(determineNextState(currentState, liveNeighbors));
      }
    }
  }

  /**
   * Validates the current state of a cell to ensure it is of type GameOfLifeState.
   *
   * @param cell The cell whose state needs to be validated.
   * @throws IllegalStateException if the cell's state is not a valid GameOfLifeState.
   */
  private void validateCellState(Cell cell) {
    StateInterface state = cell.getCurrentState();
    if (!(state instanceof GameOfLifeState)) {
      throw new IllegalStateException("Invalid cell state: " + state.getClass().getSimpleName());
    }
  }

  /**
   * Counts the number of live neighbors of a given cell.
   *
   * @param neighbors The list of neighboring cells.
   * @return The number of live neighbors.
   */
  private int countLiveNeighbors(List<Cell> neighbors) {
    return (int) neighbors.stream()
        .filter(c -> c.getCurrentState() == GameOfLifeState.ALIVE)
        .count();
  }

  /**
   * Determines the next state of a cell based on its current state and the number of live
   * neighbors.
   *
   * @param current   The current state of the cell.
   * @param neighbors The number of live neighbors.
   * @return The next state of the cell.
   */
  private GameOfLifeState determineNextState(GameOfLifeState current, int neighbors) {
    if (current == GameOfLifeState.ALIVE) {
      return surviveConditions.contains(neighbors) ?
          GameOfLifeState.ALIVE : GameOfLifeState.DEAD;
    } else {
      return birthConditions.contains(neighbors) ?
          GameOfLifeState.ALIVE : GameOfLifeState.DEAD;
    }
  }

  /**
   * Parses the numeric rule code to determine the birth and survival conditions. The rule code is
   * split into two parts: one for birth and one for survival conditions.
   *
   * @param code The rule code (a double value representing a string-based rule).
   * @throws IllegalArgumentException if the rule code format is invalid.
   */
  private void parseNumericRuleCode(double code) {
    String codeString = String.valueOf((int) code);

    if (codeString.length() < 2) {
      throw new IllegalArgumentException("Invalid rule code format");
    }

    String birthPart = codeString.substring(0, 1);
    String survivePart = codeString.substring(1);

    parseDigits(birthPart, birthConditions);
    parseDigits(survivePart, surviveConditions);
  }

  /**
   * Parses a string rule code in B/S format (e.g., "B3/S23") to determine birth and survival
   * conditions. Supports both B/S and S/B formats.
   *
   * @param ruleString The rule string in B/S or S/B format.
   * @throws IllegalArgumentException if the rule string format is invalid.
   */
  private void parseStringRuleCode(String ruleString) {
    ruleString = ruleString.toUpperCase().trim().replaceAll("\\s+", "");

    Pattern bsPattern = Pattern.compile("B([0-8]*)/?S([0-8]*)");
    Pattern sbPattern = Pattern.compile("S([0-8]*)/?B([0-8]*)");

    Matcher bsMatcher = bsPattern.matcher(ruleString);
    Matcher sbMatcher = sbPattern.matcher(ruleString);

    if (bsMatcher.matches()) {
      parseDigits(bsMatcher.group(1), birthConditions);
      parseDigits(bsMatcher.group(2), surviveConditions);
    } else if (sbMatcher.matches()) {
      parseDigits(sbMatcher.group(2), birthConditions);
      parseDigits(sbMatcher.group(1), surviveConditions);
    } else {
      throw new IllegalArgumentException("Invalid rule string format: " + ruleString +
          ". Expected format: B3/S23 or S23/B3");
    }
  }

  /**
   * Parses a string of digits and adds them to a set, ensuring the digits represent valid neighbor
   * counts (0-8).
   *
   * @param input  The input string of digits.
   * @param output The set to store the valid neighbor counts.
   * @throws IllegalArgumentException if any character in the string is not a digit or if the digit
   *                                  is out of range.
   */
  private void parseDigits(String input, Set<Integer> output) {
    for (char c : input.toCharArray()) {
      if (!Character.isDigit(c)) {
        throw new IllegalArgumentException("Invalid character in rule: " + c);
      }
      int num = Character.getNumericValue(c);
      if (num < 0 || num > 8) {
        throw new IllegalArgumentException("Invalid neighbor count: " + num);
      }
      output.add(num);
    }
  }
}