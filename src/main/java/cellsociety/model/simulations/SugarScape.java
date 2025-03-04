package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Agent;
import cellsociety.model.Cell;
import cellsociety.model.DiseaseManager;
import cellsociety.model.Grid;
import cellsociety.model.GrowthManager;
import cellsociety.model.Loan;
import cellsociety.model.MovementManager;
import cellsociety.model.ReproductionManager;
import cellsociety.model.RulesHelper;
import cellsociety.model.Simulation;
import cellsociety.model.StateInterface;
import cellsociety.model.SugarCell;
import cellsociety.model.TradingManager;
import cellsociety.model.LoanManager;
import cellsociety.model.state.SugarScapeState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Represents the SugarScape simulation, a grid-based simulation where agents interact with sugar
 * resources and engage in various activities such as movement, growth, reproduction, trading,
 * lending, and disease management.
 * <p>
 * This class extends the {@link Simulation} class and ties together multiple rule managers to
 * simulate the behavior of agents and sugar resources over discrete time steps.
 * </p>
 *
 * @author Tatum McKinnis
 */
public class SugarScape extends Simulation {

  private final List<Agent> agents;
  private final List<Loan> activeLoans;
  private final Random random;
  private int currentTick;

  private final MovementManager movementManager;
  private final GrowthManager growthManager;
  private final ReproductionManager reproductionManager;
  private final TradingManager tradingManager;
  private final LoanManager loanManager;
  private final DiseaseManager diseaseManager;

  private static final int DEFAULT_SUGAR_GROW_BACK_INTERVAL = 1;
  private static final int DEFAULT_SUGAR_GROW_BACK_RATE = 1;
  private final int sugarGrowBackInterval;
  private final int sugarGrowBackRate;

  /**
   * Constructs a new SugarScape simulation with the specified configuration and grid.
   * <p>
   * The constructor converts the provided grid cells to {@link SugarCell} objects, initializes the
   * agent list and active loans, and sets up various managers responsible for simulation rules. It
   * also initializes the growth patterns and sets the initial states based on the grid.
   * </p>
   *
   * @param simulationConfig the configuration parameters for the simulation
   * @param grid             the grid on which the simulation will run
   */
  public SugarScape(SimulationConfig simulationConfig, Grid grid) {
    super(simulationConfig, grid);
    convertGridToSugarCells(grid);
    this.agents = new ArrayList<>();
    this.activeLoans = new ArrayList<>();
    this.random = new Random();
    this.currentTick = 0;
    this.sugarGrowBackInterval = DEFAULT_SUGAR_GROW_BACK_INTERVAL;
    this.sugarGrowBackRate = DEFAULT_SUGAR_GROW_BACK_RATE;

    movementManager = new MovementManager(this);
    growthManager = new GrowthManager(this);
    reproductionManager = new ReproductionManager(this);
    tradingManager = new TradingManager(this);
    loanManager = new LoanManager(this);
    diseaseManager = new DiseaseManager(this);

    initializeGrowthPatterns();
    initializeFromStates();
  }

  /**
   * Initializes agents on the grid by creating an {@link Agent} for each cell with the AGENT
   * state.
   * <p>
   * For each cell in the grid that is in the {@link SugarScapeState#AGENT} state, an agent is
   * created, added to the agent list, and its next state is set to AGENT.
   * </p>
   */
  private void initializeFromStates() {
    Grid grid = getGrid();
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        Cell cell = grid.getCell(r, c);
        if (cell.getCurrentState() == SugarScapeState.AGENT) {
          Agent agent = RulesHelper.createAgent(cell, random);
          agents.add(agent);
          cell.setNextState(SugarScapeState.AGENT);
        }
      }
    }
    grid.applyNextStates();
  }

  /**
   * Converts all cells in the grid to {@link SugarCell} instances.
   * <p>
   * This method iterates over the grid, and for each cell that is not already a {@link SugarCell},
   * creates a new SugarCell using the cell's current state, and sets it in the grid.
   * </p>
   *
   * @param grid the grid to convert
   */
  private void convertGridToSugarCells(Grid grid) {
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        Cell cell = grid.getCell(r, c);
        if (!(cell instanceof SugarCell)) {
          SugarCell sugarCell = new SugarCell(r, c, cell.getCurrentState());
          grid.setCellAt(r, c, sugarCell);
        }
      }
    }
  }

  /**
   * Initializes the sugar growth patterns across the grid.
   * <p>
   * The sugar capacity for each cell is determined based on its distance from the grid edges. Cells
   * closer to the center tend to have a higher maximum sugar capacity. The sugar levels are set
   * accordingly, and non-agent cells are set to the SUGAR state.
   * </p>
   */
  private void initializeGrowthPatterns() {
    Grid grid = getGrid();
    int size = grid.getRows();
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        double distance1 = Math.sqrt(r * r + c * c);
        double distance2 = Math.sqrt((size - r - 1) * (size - r - 1) +
            (size - c - 1) * (size - c - 1));
        int sugarCapacity = (int) (Math.max(15 - Math.min(distance1, distance2), 1));
        Cell cell = grid.getCell(r, c);
        if (cell instanceof SugarCell sugarCell) {
          sugarCell.setMaxSugar(sugarCapacity);
          sugarCell.setSugar(sugarCapacity);
          if (cell.getCurrentState() != SugarScapeState.AGENT) {
            sugarCell.setNextState(SugarScapeState.SUGAR);
          }
        }
      }
    }
    grid.applyNextStates();
  }

  /**
   * Initializes the color mapping for different simulation states.
   * <p>
   * The returned map associates each {@link StateInterface} instance with a CSS class name to be
   * used for rendering the grid cells.
   * </p>
   *
   * @return a map linking states to their respective CSS class names
   */
  @Override
  protected Map<StateInterface, String> initializeColorMap() {
    Map<StateInterface, String> colorMap = new HashMap<>();
    colorMap.put(SugarScapeState.EMPTY, "sugar-state-empty");
    colorMap.put(SugarScapeState.SUGAR, "sugar-state-sugar");
    colorMap.put(SugarScapeState.AGENT, "sugar-state-agent");
    return colorMap;
  }

  /**
   * Initializes state counts for all states to 0.0
   *
   * @return initialized stateCounts map with 0s for each state, meaning no cells are in this state
   */
  @Override
  public void initializeStateCounts() {
    Map<StateInterface, Double> stateCounts = getStateCounts();
    stateCounts.put(SugarScapeState.EMPTY, 0.0);
    stateCounts.put(SugarScapeState.SUGAR, 0.0);
    stateCounts.put(SugarScapeState.AGENT, 0.0);
    setStateCounts(stateCounts);
  }

  /**
   * Initializes the state mapping for the simulation.
   * <p>
   * The returned map associates integer values with their corresponding {@link StateInterface}
   * states.
   * </p>
   *
   * @return a map linking numeric values to simulation states
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    Map<Integer, StateInterface> stateMap = new HashMap<>();
    stateMap.put(0, SugarScapeState.EMPTY);
    stateMap.put(1, SugarScapeState.SUGAR);
    stateMap.put(2, SugarScapeState.AGENT);
    return stateMap;
  }

  /**
   * Applies the simulation rules for the current tick.
   * <p>
   * The method shuffles the list of agents and then sequentially applies rules for movement, sugar
   * growth, reproduction, trading, lending, and disease. It also removes dead agents and updates
   * active loans, incrementing the simulation tick at the end.
   * </p>
   */
  @Override
  protected void applyRules() {
    var shuffledAgents = new ArrayList<>(agents);
    Collections.shuffle(shuffledAgents, random);

    movementManager.applyMovement(shuffledAgents);
    growthManager.applyGrowBack(currentTick, sugarGrowBackInterval, sugarGrowBackRate);
    reproductionManager.applyReproduction(shuffledAgents);
    tradingManager.applyTrading(shuffledAgents);
    loanManager.applyLending(shuffledAgents);
    diseaseManager.applyDiseaseRules(shuffledAgents);

    removeDeadAgents();
    loanManager.updateLoans(currentTick);
    currentTick++;
  }

  /**
   * Removes agents that are dead from the simulation.
   * <p>
   * For each agent that is determined to be dead, its position is set to the EMPTY state, and it is
   * removed from the active agent list. The grid is then updated to reflect these changes.
   * </p>
   */
  private void removeDeadAgents() {
    agents.removeIf(agent -> {
      if (agent.isDead()) {
        agent.getPosition().setNextState(SugarScapeState.EMPTY);
        return true;
      }
      return false;
    });
    getGrid().applyNextStates();
  }

  /**
   * Returns the list of agents in the simulation.
   * <p>
   * Note: The returned list is modifiable and reflects the internal state of the simulation.
   * </p>
   *
   * @return a list of agents
   */
  public List<Agent> getAgents() {
    return agents;
  }

  /**
   * Returns an unmodifiable list of active loans in the simulation.
   *
   * @return a list of active loans
   */
  public List<Loan> getActiveLoans() {
    return Collections.unmodifiableList(activeLoans);
  }

  /**
   * Returns the {@link Random} instance used in the simulation.
   *
   * @return the random number generator
   */
  public Random getRandom() {
    return random;
  }

  /**
   * Returns the modifiable list of active loans.
   * <p>
   * This package-private method is intended for use by manager classes that need to update the
   * active loans during the simulation.
   * </p>
   *
   * @return a modifiable list of active loans
   */
  public List<Loan> getActiveLoansInternal() {
    return activeLoans;
  }

  /**
   * Returns the current simulation tick.
   *
   * @return the current tick count
   */
  public int getCurrentTick() {
    return currentTick;
  }
}
