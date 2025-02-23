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

public class SugarScape extends Simulation {

  private final List<Agent> agents;
  private final List<Loan> activeLoans;
  private final Random random;
  private int currentTick;

  // Managers for different rule sets
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

  public SugarScape(SimulationConfig simulationConfig, Grid grid) {
    super(simulationConfig, grid);
    convertGridToSugarCells(grid);
    this.agents = new ArrayList<>();
    this.activeLoans = new ArrayList<>();
    this.random = new Random();
    this.currentTick = 0;
    this.sugarGrowBackInterval = DEFAULT_SUGAR_GROW_BACK_INTERVAL;
    this.sugarGrowBackRate = DEFAULT_SUGAR_GROW_BACK_RATE;

    // Initialize managers
    movementManager = new MovementManager(this);
    growthManager = new GrowthManager(this);
    reproductionManager = new ReproductionManager(this);
    tradingManager = new TradingManager(this);
    loanManager = new LoanManager(this);
    diseaseManager = new DiseaseManager(this);

    initializeGrowthPatterns();
    initializeFromStates();
  }

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

  @Override
  protected Map<StateInterface, String> initializeColorMap() {
    Map<StateInterface, String> colorMap = new HashMap<>();
    colorMap.put(SugarScapeState.EMPTY, "sugar-state-empty");
    colorMap.put(SugarScapeState.SUGAR, "sugar-state-sugar");
    colorMap.put(SugarScapeState.AGENT, "sugar-state-agent");
    return colorMap;
  }

  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    Map<Integer, StateInterface> stateMap = new HashMap<>();
    stateMap.put(0, SugarScapeState.EMPTY);
    stateMap.put(1, SugarScapeState.SUGAR);
    stateMap.put(2, SugarScapeState.AGENT);
    return stateMap;
  }

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

  // Public getters
  public List<Agent> getAgents() {
    return agents; // Assuming internal modification by managers is acceptable
  }

  public List<Loan> getActiveLoans() {
    return Collections.unmodifiableList(activeLoans);
  }

  public Random getRandom() {
    return random;
  }

  // Package-private getter for managers to access the modifiable loans list
  public List<Loan> getActiveLoansInternal() {
    return activeLoans;
  }

  // Getter for the current tick (used in LoanManager)
  public int getCurrentTick() {
    return currentTick;
  }
}
