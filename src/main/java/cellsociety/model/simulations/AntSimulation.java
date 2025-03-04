package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Ant;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.state.AntState;
import cellsociety.model.Orientation;
import cellsociety.model.StateInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simulation implementing ant foraging behavior with pheromone trails and dynamic state
 * management.
 * <p>
 * This simulation extends the {@link Simulation} abstract class and models ants that:
 * <ul>
 *   <li>Follow pheromone gradients to find food sources and return to nests</li>
 *   <li>Deposit and track two types of pheromones (home and food directions)</li>
 *   <li>Avoid obstacles and overcrowded cells</li>
 *   <li>Exhibit probabilistic movement behavior</li>
 * </ul>
 * The simulation handles pheromone evaporation and diffusion dynamics automatically.
 * </p>
 *
 * @author Tatum McKinnis
 */
public class AntSimulation extends Simulation {

  private static final double EVAPORATION_RATE = 0.05;
  private static final double DIFFUSION_RATE = 0.1;
  private static final int MAX_ANTS_PER_CELL = 10;
  private static final double MAX_PHEROMONE = 100.0;

  private final List<Ant> ants;

  /**
   * Constructs a new AntSimulation with specified configuration and grid.
   *
   * @param config Contains simulation parameters and initial state configuration
   * @param grid   The grid structure to use for this simulation
   */
  public AntSimulation(SimulationConfig config, Grid grid) {
    super(config, grid);
    this.ants = initializeAnts(config);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Initializes color mappings for visualization:
   * <ul>
   *   <li>Nest: Red</li>
   *   <li>Food: Green</li>
   *   <li>Obstacle: Black</li>
   *   <li>Empty: White</li>
   * </ul>
   * </p>
   */
  @Override
  protected Map<StateInterface, String> initializeColorMap() {
    Map<StateInterface, String> colorMap = new HashMap<>();
    colorMap.put(new AntState(true, false, false, 0, 0, 0), "ant-state-nest");
    colorMap.put(new AntState(false, true, false, 0, 0, 0), "ant-state-food");
    colorMap.put(new AntState(false, false, true, 0, 0, 0), "ant-state-obstacle");
    colorMap.put(new AntState(false, false, false, 0, 0, 0), "ant-state-empty");
    return colorMap;
  }

  /**
   * Initializes state counts for all states to 0.0
   */
  @Override
  public void initializeStateCounts() {
    Map<StateInterface, Double> stateCounts = getStateCounts();
    stateCounts.put(new AntState(true, false, false, 0, 0, 0), 0.0);
    stateCounts.put(new AntState(false, true, false, 0, 0, 0), 0.0);
    stateCounts.put(new AntState(false, false, true, 0, 0, 0), 0.0);
    stateCounts.put(new AntState(false, false, false, 0, 0, 0), 0.0);
    setStateCounts(stateCounts);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Initializes state mappings for grid configuration:
   * <ul>
   *   <li>0: Empty cell</li>
   *   <li>1: Nest (max home pheromone)</li>
   *   <li>2: Food source (max food pheromone)</li>
   *   <li>3: Obstacle</li>
   * </ul>
   * </p>
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    Map<Integer, StateInterface> stateMap = new HashMap<>();
    stateMap.put(0, new AntState(false, false, false, 0, 0, 0));
    stateMap.put(1, new AntState(true, false, false, MAX_PHEROMONE, 0, 0));
    stateMap.put(2, new AntState(false, true, false, 0, MAX_PHEROMONE, 0));
    stateMap.put(3, new AntState(false, false, true, 0, 0, 0));
    return stateMap;
  }

  /**
   * Initializes ants at nest locations based on configuration parameters.
   *
   * @param config Simulation configuration containing parameters
   * @return List of initialized ants
   */
  private List<Ant> initializeAnts(SimulationConfig config) {
    List<Ant> ants = new ArrayList<>();
    Map<String, Double> params = config.getParameters();
    int numAnts = params.containsKey("numAnts") ? params.get("numAnts").intValue() : 10;

    int totalNests = 0;
    int totalAntsCreated = 0;

    for (int r = 0; r < getGrid().getRows(); r++) {
      for (int c = 0; c < getGrid().getCols(); c++) {
        AntState state = (AntState) getGrid().getCell(r, c).getCurrentState();
        if (state.isNest()) {
          totalNests++;
          for (int i = 0; i < numAnts; i++) {
            ants.add(new Ant(r, c, Orientation.N, false));
            totalAntsCreated++;
          }
        }
      }
    }

    return ants;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Executes one simulation step:
   * <ol>
   *   <li>Initializes next cell states</li>
   *   <li>Processes all ant movements</li>
   *   <li>Applies pheromone evaporation/diffusion</li>
   *   <li>Commits state changes to grid</li>
   * </ol>
   * </p>
   */
  @Override
  public void applyRules() {
    initializeNextStates();

    ants.forEach(this::processAntMovement);

    applyEvaporationAndDiffusion();
    getGrid().applyNextStates();
  }

  /**
   * Initializes all cells' next states to their current states.
   */
  private void initializeNextStates() {
    Grid grid = getGrid();
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        Cell cell = grid.getCell(r, c);
        cell.setNextState(cell.getCurrentState());
      }
    }
  }

  /**
   * Processes movement logic for a single ant.
   *
   * @param ant The ant to process
   */
  private void processAntMovement(Ant ant) {
    int currentRow = ant.getRow();
    int currentCol = ant.getCol();
    Cell currentCell = getGrid().getCell(currentRow, currentCol);
    AntState currentState = (AntState) currentCell.getCurrentState();

    if (ant.hasFood() && currentState.isNest()) {
      ant.setHasFood(false);
    } else if (!ant.hasFood() && currentState.isFood()) {
      ant.setHasFood(true);
    }

    depositPheromones(currentRow, currentCol, ant.hasFood());
    executeMovement(ant);
  }

  /**
   * Returns an unmodifiable view of the ants in the simulation.
   * <p>
   * This provides safe access to the ant population without exposing the internal list to
   * modification. The returned list reflects the current state of ants but cannot be modified
   * directly.
   * </p>
   *
   * @return an unmodifiable list containing all active ants in the simulation
   * @see Collections#unmodifiableList
   */
  public List<Ant> getAnts() {
    return Collections.unmodifiableList(this.ants);
  }

  /**
   * Deposits pheromones at the ant's current location.
   *
   * @param row     Grid row coordinate
   * @param col     Grid column coordinate
   * @param hasFood True if ant is carrying food (deposits food pheromones), false if searching
   *                (deposits home pheromones)
   */
  private void depositPheromones(int row, int col, boolean hasFood) {
    Cell cell = getGrid().getCell(row, col);
    AntState nextState = (AntState) cell.getNextState();
    double desired = calculateDesiredPheromone(row, col, hasFood, nextState);

    if (hasFood && desired > nextState.getFoodPheromone()) {
      cell.setNextState(nextState.withFoodPheromone(desired));
    } else if (!hasFood && desired > nextState.getHomePheromone()) {
      cell.setNextState(nextState.withHomePheromone(desired));
    }
  }


  /**
   * Provides access to the maximum pheromone value for testing purposes
   *
   * @return The maximum allowed pheromone concentration
   */
  public static double getMaxPheromone() {
    return MAX_PHEROMONE;
  }

  /**
   * Calculates desired pheromone level for deposition.
   *
   * @param row     Grid row coordinate
   * @param col     Grid column coordinate
   * @param hasFood Whether the ant is carrying food
   * @param state   Current cell state
   * @return Pheromone level to deposit
   */
  private double calculateDesiredPheromone(int row, int col, boolean hasFood, AntState state) {
    if (hasFood) {
      return state.isFood() ? MAX_PHEROMONE
          : Math.max(0, getMaxNeighborPheromone(row, col, false) - 2);
    }
    return state.isNest() ? MAX_PHEROMONE
        : Math.max(0, getMaxNeighborPheromone(row, col, true) - 2);
  }

  /**
   * Finds maximum pheromone value in neighboring cells.
   *
   * @param row    Center cell row
   * @param col    Center cell column
   * @param isHome Whether to check home (true) or food (false) pheromones
   * @return Maximum pheromone value found
   */
  private double getMaxNeighborPheromone(int row, int col, boolean isHome) {
    double max = 0;
    for (int dr = -1; dr <= 1; dr++) {
      for (int dc = -1; dc <= 1; dc++) {
        if (dr == 0 && dc == 0) {
          continue;
        }
        int nr = row + dr;
        int nc = col + dc;
        if (getGrid().isValidPosition(nr, nc)) {
          AntState neighbor = (AntState) getGrid().getCell(nr, nc).getCurrentState();
          double pheromone = isHome ? neighbor.getHomePheromone() : neighbor.getFoodPheromone();
          max = Math.max(max, pheromone);
        }
      }
    }
    return max;
  }

  /**
   * Executes movement logic for an ant.
   *
   * @param ant The ant to move
   */
  private void executeMovement(Ant ant) {
    List<int[]> possibleMoves = getPossibleMoves(ant);

    if (!possibleMoves.isEmpty()) {
      int[] selectedMove = selectMove(possibleMoves, ant.hasFood());
      if (selectedMove != null) {
        moveAnt(ant, selectedMove[0], selectedMove[1]);
      }
    }
  }

  /**
   * Gets valid movement options for an ant.
   *
   * @param ant The ant to evaluate
   * @return List of valid [row,col] positions the ant can move to
   */
  private List<int[]> getPossibleMoves(Ant ant) {
    return filterValidPositions(getAllNeighborPositions(ant.getRow(), ant.getCol()));
  }

  /**
   * Gets all neighboring positions around a location.
   *
   * @param row Center row
   * @param col Center column
   * @return List of [row,col] neighbor positions
   */
  private List<int[]> getAllNeighborPositions(int row, int col) {
    List<int[]> positions = new ArrayList<>();
    for (int dr = -1; dr <= 1; dr++) {
      for (int dc = -1; dc <= 1; dc++) {
        if (dr == 0 && dc == 0) {
          continue;
        }
        int nr = row + dr;
        int nc = col + dc;
        if (getGrid().isValidPosition(nr, nc)) {
          positions.add(new int[]{nr, nc});
        }
      }
    }
    return positions;
  }

  /**
   * Filters out invalid positions (obstacles/overcrowded).
   *
   * @param positions Positions to filter
   * @return Valid movement positions
   */
  private List<int[]> filterValidPositions(List<int[]> positions) {
    List<int[]> valid = new ArrayList<>();
    for (int[] pos : positions) {
      Cell cell = getGrid().getCell(pos[0], pos[1]);
      AntState state = (AntState) cell.getCurrentState();
      AntState nextState = (AntState) cell.getNextState();

      if (!state.isObstacle() && nextState.getAntCount() < MAX_ANTS_PER_CELL) {
        valid.add(pos);
      }
    }
    return valid;
  }

  /**
   * Selects a movement position probabilistically based on pheromones.
   *
   * @param possiblePositions Valid movement options
   * @param hasFood           Whether ant is carrying food
   * @return Selected [row,col] position, or null if no valid moves
   */
  private int[] selectMove(List<int[]> possiblePositions, boolean hasFood) {
    if (possiblePositions.isEmpty()) {
      return null;
    }

    double[] weights = new double[possiblePositions.size()];
    double totalWeight = 0;

    for (int i = 0; i < possiblePositions.size(); i++) {
      int[] pos = possiblePositions.get(i);
      weights[i] = getPheromoneWeight(pos[0], pos[1], hasFood);
      totalWeight += weights[i];
    }

    if (totalWeight <= 0) {
      return possiblePositions.get((int)(Math.random() * possiblePositions.size()));
    }

    double rand = Math.random() * totalWeight;
    double sum = 0;

    for (int i = 0; i < weights.length; i++) {
      sum += weights[i];
      if (sum >= rand) {
        return possiblePositions.get(i);
      }
    }

    return possiblePositions.get(possiblePositions.size() - 1);
  }

  /**
   * Calculates pheromone weight for position selection.
   *
   * @param row     Position row
   * @param col     Position column
   * @param hasFood Whether ant is carrying food
   * @return Weight value for selection probability
   */
  private double getPheromoneWeight(int row, int col, boolean hasFood) {
    Cell cell = getGrid().getCell(row, col);
    AntState state = (AntState) cell.getCurrentState();

    double baseWeight = 1.0;
    double pheromoneWeight = hasFood ? state.getHomePheromone() : state.getFoodPheromone();

    double randomFactor = Math.random() * 0.5;

    return baseWeight + pheromoneWeight + randomFactor;
  }

  /**
   * Moves an ant to a new position and updates orientation.
   *
   * @param ant    The ant to move
   * @param newRow Target row
   * @param newCol Target column
   */
  private void moveAnt(Ant ant, int newRow, int newCol) {
    int oldRow = ant.getRow();
    int oldCol = ant.getCol();

    if (oldRow != newRow || oldCol != newCol) {
      int dr = newRow - oldRow;
      int dc = newCol - oldCol;
      ant.setOrientation(Orientation.fromDrDc(dr, dc));

      ant.setLastRow(oldRow);
      ant.setLastCol(oldCol);
      ant.setSteps(ant.getSteps() + 1);

    }

    updateAntCount(oldRow, oldCol, -1);
    ant.setRow(newRow);
    ant.setCol(newCol);
    updateAntCount(newRow, newCol, 1);
  }

  /**
   * Updates ant count in a cell's next state.
   *
   * @param row   Cell row
   * @param col   Cell column
   * @param delta Change in ant count (+1/-1)
   */
  private void updateAntCount(int row, int col, int delta) {
    Cell cell = getGrid().getCell(row, col);
    AntState nextState = (AntState) cell.getNextState();
    int newCount = nextState.getAntCount() + delta;

    newCount = Math.max(0, newCount);

    cell.setNextState(nextState.withAntCount(newCount));
  }

  /**
   * Applies pheromone evaporation and diffusion across the grid.
   */
  private void applyEvaporationAndDiffusion() {
    Grid grid = getGrid();
    int rows = grid.getRows();
    int cols = grid.getCols();
    double[][] homeContributions = new double[rows][cols];
    double[][] foodContributions = new double[rows][cols];

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        Cell cell = grid.getCell(r, c);
        AntState nextState = (AntState) cell.getNextState();

        double homeEvap = nextState.getHomePheromone() * (1 - EVAPORATION_RATE);
        double foodEvap = nextState.getFoodPheromone() * (1 - EVAPORATION_RATE);

        distributeDiffusion(r, c, homeEvap, foodEvap, homeContributions, foodContributions);
        applyEvaporation(cell, homeEvap, foodEvap);
      }
    }

    applyContributions(homeContributions, foodContributions);
  }

  /**
   * Distributes diffusion contributions to neighboring cells.
   *
   * @param row               Current cell row
   * @param col               Current cell column
   * @param homeEvap          Current cell's home pheromone after evaporation
   * @param foodEvap          Current cell's food pheromone after evaporation
   * @param homeContributions Matrix tracking home pheromone diffusion
   * @param foodContributions Matrix tracking food pheromone diffusion
   */
  private void distributeDiffusion(int row, int col, double homeEvap, double foodEvap,
      double[][] homeContributions, double[][] foodContributions) {
    double homeDiffuse = homeEvap * DIFFUSION_RATE;
    double foodDiffuse = foodEvap * DIFFUSION_RATE;

    for (int dr = -1; dr <= 1; dr++) {
      for (int dc = -1; dc <= 1; dc++) {
        if (dr == 0 && dc == 0) {
          continue;
        }
        int nr = row + dr;
        int nc = col + dc;
        if (getGrid().isValidPosition(nr, nc)) {
          homeContributions[nr][nc] += homeDiffuse / 8;
          foodContributions[nr][nc] += foodDiffuse / 8;
        }
      }
    }
  }

  /**
   * Applies evaporation to a cell's pheromone levels.
   *
   * @param cell     Target cell
   * @param homeEvap Home pheromone after evaporation
   * @param foodEvap Food pheromone after evaporation
   */
  private void applyEvaporation(Cell cell, double homeEvap, double foodEvap) {
    AntState nextState = (AntState) cell.getNextState();
    cell.setNextState(nextState
        .withHomePheromone(homeEvap - (homeEvap * DIFFUSION_RATE))
        .withFoodPheromone(foodEvap - (foodEvap * DIFFUSION_RATE)));
  }

  /**
   * Applies accumulated diffusion contributions to all cells.
   *
   * @param homeContributions Home pheromone diffusion amounts
   * @param foodContributions Food pheromone diffusion amounts
   */
  private void applyContributions(double[][] homeContributions, double[][] foodContributions) {
    Grid grid = getGrid();
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        Cell cell = grid.getCell(r, c);
        AntState nextState = (AntState) cell.getNextState();

        double home = Math.min(
            nextState.getHomePheromone() + homeContributions[r][c],
            MAX_PHEROMONE
        );
        double food = Math.min(
            nextState.getFoodPheromone() + foodContributions[r][c],
            MAX_PHEROMONE
        );

        cell.setNextState(nextState
            .withHomePheromone(home)
            .withFoodPheromone(food));
      }
    }
  }

  /**
   * Returns an unmodifiable map of states to their corresponding color representations. This map
   * provides a way to associate each state with a specific color for visualization purposes.
   *
   * @return An unmodifiable {@code Map} where keys are {@code StateInterface} objects and values
   * are color strings.
   */
  @Override
  public Map<StateInterface, String> getColorMap() {
    return Collections.unmodifiableMap(super.getColorMap());
  }

  /**
   * Returns an unmodifiable map of state IDs to their corresponding {@code StateInterface} objects.
   * This map allows for efficient retrieval of states based on their unique integer identifiers.
   *
   * @return An unmodifiable {@code Map} where keys are integer state IDs and values are
   * {@code StateInterface} objects.
   */
  @Override
  public Map<Integer, StateInterface> getStateMap() {
    return Collections.unmodifiableMap(super.getStateMap());
  }
}