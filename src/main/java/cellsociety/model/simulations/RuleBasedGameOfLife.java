package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Grid;
import cellsociety.model.Cell;
import cellsociety.model.Simulation;
import cellsociety.model.state.GameOfLifeState;
import cellsociety.model.StateInterface;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleBasedGameOfLife extends Simulation {

  private final Set<Integer> surviveConditions = new HashSet<>();
  private final Set<Integer> birthConditions = new HashSet<>();

  public RuleBasedGameOfLife(SimulationConfig config, Grid grid) {
    super(config, grid);
    parseRuleCode(config.getParameters().getOrDefault("ruleCode", 323.0));
  }

  // Add these required methods from Simulation abstract class
  @Override
  public Map<StateInterface, String> initializeColorMap() {
    return Map.of(
        GameOfLifeState.ALIVE, "gameoflife-state-alive",
        GameOfLifeState.DEAD, "gameoflife-state-dead"
    );
  }

  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    return Map.of(
        0, GameOfLifeState.DEAD,
        1, GameOfLifeState.ALIVE
    );
  }

  // Rest of the existing implementation remains the same
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

  private void validateCellState(Cell cell) {
    StateInterface state = cell.getCurrentState();
    if (!(state instanceof GameOfLifeState)) {
      throw new IllegalStateException("Invalid cell state: " + state.getClass().getSimpleName());
    }
  }

  private int countLiveNeighbors(List<Cell> neighbors) {
    return (int) neighbors.stream()
        .filter(c -> c.getCurrentState() == GameOfLifeState.ALIVE)
        .count();
  }

  private GameOfLifeState determineNextState(GameOfLifeState current, int neighbors) {
    if (current == GameOfLifeState.ALIVE) {
      return surviveConditions.contains(neighbors) ?
          GameOfLifeState.ALIVE : GameOfLifeState.DEAD;
    } else {
      return birthConditions.contains(neighbors) ?
          GameOfLifeState.ALIVE : GameOfLifeState.DEAD;
    }
  }

  private void parseRuleCode(double code) {
    String codeString = String.valueOf((int) code);

    // Split into birth (first digit) and survival (remaining digits)
    if (codeString.length() < 2) {
      throw new IllegalArgumentException("Invalid rule code format");
    }

    String birthPart = codeString.substring(0, 1);
    String survivePart = codeString.substring(1);

    parseNumbers(birthPart, birthConditions);
    parseNumbers(survivePart, surviveConditions);
  }

  private void parseRuleString(String ruleString) {
    Matcher matcher = Pattern.compile("B(\\d+)/S(\\d+)").matcher(ruleString);
    if (!matcher.find()) {
      throw new IllegalArgumentException("Invalid rule format: " + ruleString);
    }

    parseNumbers(matcher.group(1), birthConditions);
    parseNumbers(matcher.group(2), surviveConditions);
  }

  private void parseNumbers(String input, Set<Integer> output) {
    for (char c : input.toCharArray()) {
      if (!Character.isDigit(c)) {
        throw new IllegalArgumentException("Invalid character in rule: " + c);
      }
      int num = Character.getNumericValue(c);
      if (num < 0 || num > 8) {  // Neighbor counts must be 0-8
        throw new IllegalArgumentException("Invalid neighbor count: " + num);
      }
      output.add(num);
    }
  }
}