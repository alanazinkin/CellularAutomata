package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.Agent;
import cellsociety.model.Cell;
import cellsociety.model.Disease;
import cellsociety.model.Grid;
import cellsociety.model.Loan;
import cellsociety.model.Sex;
import cellsociety.model.Simulation;
import cellsociety.model.StateInterface;
import cellsociety.model.SugarCell;
import cellsociety.model.state.SugarScapeState;
import java.util.*;
import java.util.stream.Collectors;

public class SugarScape extends Simulation {

  private final List<Agent> agents;
  private final List<Loan> activeLoans;
  private final Random random;
  private int currentTick;
  private static final int DEFAULT_SUGAR_GROW_BACK_INTERVAL = 1;  // Grow back every tick
  private static final int DEFAULT_SUGAR_GROW_BACK_RATE = 1;    // Grow back 1 unit per interval
  private final int sugarGrowBackInterval;
  private final int sugarGrowBackRate;
  private static final double LOAN_INTEREST_RATE = 0.1;
  private static final int LOAN_DURATION = 10;

  private static final int EMPTY_STATE = 0;
  private static final int SUGAR_STATE = 1;
  private static final int AGENT_STATE = 2;


  public SugarScape(SimulationConfig simulationConfig, Grid grid) {
    super(simulationConfig, grid);
    this.agents = new ArrayList<>();
    this.activeLoans = new ArrayList<>();
    this.random = new Random();
    this.currentTick = 0;
    this.sugarGrowBackInterval = DEFAULT_SUGAR_GROW_BACK_INTERVAL;
    this.sugarGrowBackRate = DEFAULT_SUGAR_GROW_BACK_RATE;
    initializeGrowthPatterns();
    initializeAgents(10);  // Default to 10 initial agents
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
    stateMap.put(EMPTY_STATE, SugarScapeState.EMPTY);
    stateMap.put(SUGAR_STATE, SugarScapeState.SUGAR);
    stateMap.put(AGENT_STATE, SugarScapeState.AGENT);
    return stateMap;
  }

  private void initializeGrowthPatterns() {
    Grid grid = getGrid();
    int size = grid.getRows();

    // Create sugar peaks at opposite corners
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
          sugarCell.setNextState(SugarScapeState.SUGAR);
        }
      }
    }
    grid.applyNextStates();
  }

  private void initializeAgents(int count) {
    for (int i = 0; i < count; i++) {
      Cell randomCell = findRandomEmptyCell();
      if (randomCell != null) {
        Agent agent = createAgent(randomCell);
        agents.add(agent);
        randomCell.setNextState(SugarScapeState.AGENT);
      }
    }
    getGrid().applyNextStates();
  }

  @Override
  protected void applyRules() {
    List<Agent> shuffledAgents = new ArrayList<>(agents);
    Collections.shuffle(shuffledAgents, random);

    // Apply rules sequentially
    applyMovementRule(shuffledAgents);
    applyGrowBackRule();
    applyReproductionRule(shuffledAgents);
    applyTradingRule(shuffledAgents);
    applyLendingRule(shuffledAgents);
    applyImmuneSystemRule(shuffledAgents);
    applyTransmissionRule(shuffledAgents);

    removeDeadAgents();
    updateLoans();
    currentTick++;
  }

  private void applyMovementRule(List<Agent> shuffledAgents) {
    for (Agent agent : shuffledAgents) {
      List<Cell> validMoves = findValidMoves(agent);
      if (!validMoves.isEmpty()) {
        Cell bestMove = findBestMove(agent, validMoves);
        if (bestMove != null) {
          // Update old cell
          Cell oldCell = agent.getPosition();
          oldCell.setNextState(SugarScapeState.EMPTY);

          // Update new cell and agent
          if (bestMove instanceof SugarCell sugarCell) {
            agent.addSugar(sugarCell.getSugar());
            sugarCell.setSugar(0);
          }
          agent.setPosition(bestMove);
          bestMove.setNextState(SugarScapeState.AGENT);
        }
      }
      agent.metabolize();
    }
    getGrid().applyNextStates();
  }

  private void applyGrowBackRule() {
    if (currentTick % sugarGrowBackInterval == 0) {
      Grid grid = getGrid();
      for (int r = 0; r < grid.getRows(); r++) {
        for (int c = 0; c < grid.getCols(); c++) {
          Cell cell = grid.getCell(r, c);
          if (cell instanceof SugarCell sugarCell) {
            if (cell.getCurrentState() == SugarScapeState.EMPTY) {
              int newSugar = Math.min(
                  sugarCell.getSugar() + sugarGrowBackRate,
                  sugarCell.getMaxSugar()
              );
              sugarCell.setSugar(newSugar);
              if (newSugar > 0) {
                cell.setNextState(SugarScapeState.SUGAR);
              }
            }
          }
        }
      }
      grid.applyNextStates();
    }
  }

  private void applyReproductionRule(List<Agent> shuffledAgents) {
    List<Agent> newAgents = new ArrayList<>();

    for (Agent agent : shuffledAgents) {
      if (!agent.isFertile()) {
        continue;
      }

      List<Agent> neighbors = getNeighbors(agent);
      Collections.shuffle(neighbors, random);

      for (Agent neighbor : neighbors) {
        if (canReproduce(agent, neighbor)) {
          Cell emptyCell = findAdjacentEmptyCell(agent);
          if (emptyCell != null) {
            Agent child = reproduce(agent, neighbor, emptyCell);
            newAgents.add(child);
            emptyCell.setNextState(SugarScapeState.AGENT);
            break;
          }
        }
      }
    }

    agents.addAll(newAgents);
    getGrid().applyNextStates();
  }

  private void applyTradingRule(List<Agent> shuffledAgents) {
    for (Agent agent : shuffledAgents) {
      List<Agent> neighbors = getNeighbors(agent);
      for (Agent neighbor : neighbors) {
        if (canTrade(agent, neighbor)) {
          executeTrade(agent, neighbor);
        }
      }
    }
  }

  private void applyLendingRule(List<Agent> shuffledAgents) {
    for (Agent agent : shuffledAgents) {
      if (agent.canLend()) {
        List<Agent> borrowers = findPotentialBorrowers(agent);
        for (Agent borrower : borrowers) {
          if (canCreateLoan(agent, borrower)) {
            Loan loan = createLoan(agent, borrower);
            activeLoans.add(loan);
            transferLoanAmount(loan);
          }
        }
      }
    }
  }

  private boolean canCreateLoan(Agent lender, Agent borrower) {
    return lender.canLend() && borrower.needsBorrow() && !hasExistingLoan(lender, borrower);
  }

  private void applyImmuneSystemRule(List<Agent> shuffledAgents) {
    for (Agent agent : shuffledAgents) {
      if (!agent.getDiseases().isEmpty()) {
        Disease randomDisease = selectRandomDisease(agent);
        agent.updateImmuneSystem(randomDisease);
        agent.checkAndRemoveDiseases();
      }
    }
  }

  private void applyTransmissionRule(List<Agent> shuffledAgents) {
    for (Agent agent : shuffledAgents) {
      if (!agent.getDiseases().isEmpty()) {
        List<Agent> neighbors = getNeighbors(agent);
        for (Agent neighbor : neighbors) {
          Disease disease = agent.getRandomDisease();
          if (disease != null) {
            neighbor.addDisease(disease.clone());
          }
        }
      }
    }
  }

  private List<Cell> findValidMoves(Agent agent) {
    Cell agentCell = agent.getPosition();
    Grid grid = getGrid();
    int row = -1;
    int col = -1;

    // Find the agent's position in the grid
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        if (grid.getCell(r, c) == agentCell) {
          row = r;
          col = c;
          break;
        }
      }
      if (row != -1) {
        break;
      }
    }

    return getGrid().getNeighbors(row, col).stream()
        .filter(cell -> cell.getCurrentState() == SugarScapeState.EMPTY ||
            cell.getCurrentState() == SugarScapeState.SUGAR)
        .collect(Collectors.toList());
  }

  private Cell findBestMove(Agent agent, List<Cell> validMoves) {
    return validMoves.stream()
        .max(Comparator
            .comparingInt(cell -> ((SugarCell) cell).getSugar())
            .thenComparing(cell -> -calculateDistance(agent.getPosition(), (Cell) cell)))
        .orElse(null);
  }

  private int calculateDistance(Cell cell1, Cell cell2) {
    Grid grid = getGrid();
    int row1 = -1, col1 = -1, row2 = -1, col2 = -1;

    // Find positions of both cells
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        if (grid.getCell(r, c) == cell1) {
          row1 = r;
          col1 = c;
        }
        if (grid.getCell(r, c) == cell2) {
          row2 = r;
          col2 = c;
        }
      }
      if (row1 != -1 && row2 != -1) {
        break;
      }
    }

    return Math.abs(row1 - row2) + Math.abs(col1 - col2);  // Manhattan distance
  }

  private List<Agent> getNeighbors(Agent agent) {
    Cell agentCell = agent.getPosition();
    Grid grid = getGrid();

    // Find agent's position
    final int[] agentPos = new int[]{-1, -1}; // [row, col]
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        if (grid.getCell(r, c) == agentCell) {
          agentPos[0] = r;
          agentPos[1] = c;
          break;
        }
      }
      if (agentPos[0] != -1) {
        break;
      }
    }

    return agents.stream()
        .filter(other -> other != agent)
        .filter(other -> {
          // Find other agent's position
          Cell otherCell = other.getPosition();
          for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
              if (grid.getCell(r, c) == otherCell) {
                // Calculate Manhattan distance
                int distance = Math.abs(r - agentPos[0]) + Math.abs(c - agentPos[1]);
                return distance == 1;
              }
            }
          }
          return false;
        })
        .collect(Collectors.toList());
  }

  private Cell findRandomEmptyCell() {
    List<Cell> emptyCells = new ArrayList<>();
    Grid grid = getGrid();
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        Cell cell = grid.getCell(r, c);
        if (cell.getCurrentState() == SugarScapeState.EMPTY) {
          emptyCells.add(cell);
        }
      }
    }
    return emptyCells.isEmpty() ? null : emptyCells.get(random.nextInt(emptyCells.size()));
  }

  private Cell findAdjacentEmptyCell(Agent agent) {
    Cell agentCell = agent.getPosition();
    Grid grid = getGrid();
    int row = -1;
    int col = -1;

    // Find the agent's position in the grid
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        if (grid.getCell(r, c) == agentCell) {
          row = r;
          col = c;
          break;
        }
      }
      if (row != -1) {
        break;
      }
    }

    return getGrid().getNeighbors(row, col).stream()
        .filter(cell -> cell.getCurrentState() == SugarScapeState.EMPTY)
        .findFirst()
        .orElse(null);
  }

  private Agent createAgent(Cell position) {
    int vision = random.nextInt(4) + 1;  // Vision range 1-4
    int metabolism = random.nextInt(3) + 1;  // Metabolism rate 1-3
    int initialSugar = random.nextInt(20) + 10;  // Initial sugar 10-29
    Agent agent = new Agent(position, initialSugar, vision, metabolism);
    agent.setSex(random.nextBoolean() ? Sex.MALE : Sex.FEMALE);
    agent.setFertile(random.nextBoolean());
    return agent;
  }

  private boolean canReproduce(Agent agent1, Agent agent2) {
    return agent1.isFertile() &&
        agent2.isFertile() &&
        agent1.getSex() != agent2.getSex() &&
        agent1.getSugar() >= agent1.getInitialEndowment() &&
        agent2.getSugar() >= agent2.getInitialEndowment();
  }

  private Agent reproduce(Agent parent1, Agent parent2, Cell position) {
    int childSugar = (parent1.getInitialEndowment() + parent2.getInitialEndowment()) / 2;
    parent1.removeSugar(parent1.getInitialEndowment() / 2);
    parent2.removeSugar(parent2.getInitialEndowment() / 2);

    Agent child = new Agent(
        position,
        childSugar,
        inheritVision(parent1, parent2),
        inheritMetabolism(parent1, parent2)
    );

    child.setSex(random.nextBoolean() ? Sex.MALE : Sex.FEMALE);
    child.setFertile(false);  // Start infertile

    return child;
  }

  private int inheritVision(Agent parent1, Agent parent2) {
    return (parent1.getVision() + parent2.getVision()) / 2;
  }

  private int inheritMetabolism(Agent parent1, Agent parent2) {
    return (parent1.getMetabolism() + parent2.getMetabolism()) / 2;
  }

  private boolean canTrade(Agent agent1, Agent agent2) {
    double mrs1 = agent1.getMarginalRateOfSubstitution();
    double mrs2 = agent2.getMarginalRateOfSubstitution();
    return mrs1 != mrs2 && Math.abs(mrs1 - mrs2) > 0.1;
  }

  private void executeTrade(Agent agent1, Agent agent2) {
    double p = Math.sqrt(agent1.getMarginalRateOfSubstitution() *
        agent2.getMarginalRateOfSubstitution());

    if (p >= 1) {
      agent1.tradeSugarForSpice(agent2, 1, p);
    } else {
      agent1.tradeSpiceForSugar(agent2, 1, 1 / p);
    }
  }

  private List<Agent> findPotentialBorrowers(Agent lender) {
    return agents.stream()
        .filter(agent -> agent != lender)
        .filter(Agent::needsBorrow)
        .filter(agent -> !hasExistingLoan(lender, agent))
        .collect(Collectors.toList());
  }

  private boolean hasExistingLoan(Agent lender, Agent borrower) {
    return activeLoans.stream()
        .anyMatch(loan ->
            (loan.getLender() == lender && loan.getBorrower() == borrower) ||
                (loan.getLender() == borrower && loan.getBorrower() == lender)
        );
  }

  private Loan createLoan(Agent lender, Agent borrower) {
    int amount = Math.min(lender.getAvailableToLend(), borrower.getNeededToBorrow());
    return new Loan(lender, borrower, amount, currentTick, LOAN_INTEREST_RATE);
  }

  private void transferLoanAmount(Loan loan) {
    loan.getLender().removeSugar(loan.getAmount());
    loan.getBorrower().addSugar(loan.getAmount());
  }

  private void updateLoans() {
    Iterator<Loan> loanIterator = activeLoans.iterator();
    while (loanIterator.hasNext()) {
      Loan loan = loanIterator.next();

      if (loan.getLender().isDead() || loan.getBorrower().isDead()) {
        loanIterator.remove();
        continue;
      }

      if (currentTick - loan.getIssueTick() >= LOAN_DURATION) {
        processLoanPayment(loan);
        loanIterator.remove();
      }
    }
  }

  private void processLoanPayment(Loan loan) {
    Agent borrower = loan.getBorrower();
    Agent lender = loan.getLender();

    double paymentDue = loan.getAmount() * (1 + loan.getInterest());

    if (borrower.getSugar() >= paymentDue) {
      borrower.removeSugar((int) paymentDue);
      lender.addSugar((int) paymentDue);
    } else {
      // Partial payment
      int partialPayment = borrower.getSugar() / 2;
      borrower.removeSugar(partialPayment);
      lender.addSugar(partialPayment);

      // Create new loan for remaining amount
      double remainingDebt = (paymentDue - partialPayment) * (1 + LOAN_INTEREST_RATE);
      Loan newLoan = new Loan(lender, borrower, (int) remainingDebt, currentTick,
          LOAN_INTEREST_RATE);
      activeLoans.add(newLoan);
    }
  }

  private Disease selectRandomDisease(Agent agent) {
    List<Disease> diseases = agent.getDiseases();
    return diseases.isEmpty() ? null :
        diseases.get(random.nextInt(diseases.size()));
  }

  private void removeDeadAgents() {
    Iterator<Agent> agentIterator = agents.iterator();
    while (agentIterator.hasNext()) {
      Agent agent = agentIterator.next();
      if (agent.isDead()) {
        agent.getPosition().setNextState(SugarScapeState.EMPTY);
        agentIterator.remove();
      }
    }
    getGrid().applyNextStates();
  }

  /**
   * Gets the current tick of the simulation.
   *
   * @return the current simulation tick
   */
  public int getCurrentTick() {
    return currentTick;
  }

  /**
   * Gets an unmodifiable list of all agents in the simulation.
   *
   * @return list of all agents
   */
  public List<Agent> getAgents() {
    return Collections.unmodifiableList(agents);
  }

  /**
   * Gets an unmodifiable list of all active loans in the simulation.
   *
   * @return list of all active loans
   */
  public List<Loan> getActiveLoans() {
    return Collections.unmodifiableList(activeLoans);
  }

  /**
   * Adds a new agent to the simulation.
   *
   * @param agent the agent to add
   * @throws IllegalArgumentException if agent's position is already occupied
   */
  public void addAgent(Agent agent) {
    if (agent.getPosition().getCurrentState() != SugarScapeState.EMPTY) {
      throw new IllegalArgumentException("Cannot add agent to occupied cell");
    }
    agents.add(agent);
    agent.getPosition().setNextState(SugarScapeState.AGENT);
    getGrid().applyNextStates();
  }

  /**
   * Adds a new loan to the simulation.
   *
   * @param loan the loan to add
   * @throws IllegalArgumentException if a loan already exists between the agents
   */
  public void addLoan(Loan loan) {
    if (hasExistingLoan(loan.getLender(), loan.getBorrower())) {
      throw new IllegalArgumentException("Loan already exists between these agents");
    }
    activeLoans.add(loan);
    transferLoanAmount(loan);
  }

  /**
   * Gets statistics about the current state of the simulation.
   *
   * @return map containing various simulation statistics
   */
  public Map<String, Object> getStatistics() {
    Map<String, Object> stats = new HashMap<>();

    stats.put("currentTick", currentTick);
    stats.put("agentCount", agents.size());
    stats.put("activeLoanCount", activeLoans.size());

    // Population statistics
    stats.put("maleCount", countAgentsBySex(Sex.MALE));
    stats.put("femaleCount", countAgentsBySex(Sex.FEMALE));
    stats.put("fertileCount", countFertileAgents());

    // Resource statistics
    stats.put("totalSugar", calculateTotalSugar());
    stats.put("totalSpice", calculateTotalSpice());
    stats.put("averageSugarPerAgent", calculateAverageSugarPerAgent());
    stats.put("averageSpicePerAgent", calculateAverageSpicePerAgent());

    // Disease statistics
    stats.put("infectedAgentCount", countInfectedAgents());
    stats.put("averageDiseasesPerInfectedAgent", calculateAverageDiseasesPerInfectedAgent());

    return stats;
  }

  private long countAgentsBySex(Sex sex) {
    return agents.stream()
        .filter(agent -> agent.getSex() == sex)
        .count();
  }

  private long countFertileAgents() {
    return agents.stream()
        .filter(Agent::isFertile)
        .count();
  }

  private int calculateTotalSugar() {
    int agentSugar = agents.stream()
        .mapToInt(Agent::getSugar)
        .sum();

    int gridSugar = 0;
    Grid grid = getGrid();
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        Cell cell = grid.getCell(r, c);
        if (cell instanceof SugarCell) {
          gridSugar += ((SugarCell) cell).getSugar();
        }
      }
    }

    return agentSugar + gridSugar;
  }

  private int calculateTotalSpice() {
    return agents.stream()
        .mapToInt(Agent::getSpice)
        .sum();
  }

  private double calculateAverageSugarPerAgent() {
    return agents.isEmpty() ? 0 :
        agents.stream()
            .mapToInt(Agent::getSugar)
            .average()
            .orElse(0);
  }

  private double calculateAverageSpicePerAgent() {
    return agents.isEmpty() ? 0 :
        agents.stream()
            .mapToInt(Agent::getSpice)
            .average()
            .orElse(0);
  }

  private long countInfectedAgents() {
    return agents.stream()
        .filter(agent -> !agent.getDiseases().isEmpty())
        .count();
  }

  private double calculateAverageDiseasesPerInfectedAgent() {
    List<Agent> infectedAgents = agents.stream()
        .filter(agent -> !agent.getDiseases().isEmpty())
        .collect(Collectors.toList());

    return infectedAgents.isEmpty() ? 0 :
        infectedAgents.stream()
            .mapToInt(agent -> agent.getDiseases().size())
            .average()
            .orElse(0);
  }
}

