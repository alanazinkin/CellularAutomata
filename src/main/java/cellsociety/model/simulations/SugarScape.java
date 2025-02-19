package cellsociety.model.simulations;

import cellsociety.controller.SimulationConfig;
import cellsociety.model.*;
import cellsociety.model.state.SugarScapeState;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the SugarScape simulation, an agent-based model where agents consume and move based on sugar availability.
 * The environment includes sugar-growing cells, agents with metabolic rates, and economic interactions such as loans.
 * <p>
 * The simulation progresses by updating agent movements, sugar regrowth, and economic transactions each tick.
 * </p>
 */
public class SugarScape extends Simulation {

  private final List<Agent> agents;
  private final List<Loan> activeLoans;
  private final Random random;
  private int currentTick;
  private static final int DEFAULT_SUGAR_GROW_BACK_INTERVAL = 1;
  private static final int DEFAULT_SUGAR_GROW_BACK_RATE = 1;
  private final int sugarGrowBackInterval;
  private final int sugarGrowBackRate;
  private static final double LOAN_INTEREST_RATE = 0.1;
  private static final int LOAN_DURATION = 10;

  private static final int EMPTY_STATE = 0;
  private static final int SUGAR_STATE = 1;
  private static final int AGENT_STATE = 2;

  /**
   * Constructs a SugarScape simulation with the specified configuration and grid.
   *
   * @param simulationConfig The configuration parameters for the simulation.
   * @param grid The grid representing the environment of the simulation.
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
    initializeGrowthPatterns();
    initializeFromStates();
  }

  /**
   * Initializes agents based on the current states of the grid.
   */
  private void initializeFromStates() {
    Grid grid = getGrid();
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        Cell cell = grid.getCell(r, c);
        if (cell.getCurrentState() == SugarScapeState.AGENT) {
          Agent agent = createAgent(cell);
          agents.add(agent);
          cell.setNextState(SugarScapeState.AGENT);
        }
      }
    }
    getGrid().applyNextStates();
  }

  /**
   * Converts all cells in the grid to SugarCell instances, ensuring they support sugar growth mechanics.
   *
   * @param grid The grid to be converted.
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
   * Initializes the mapping between simulation states and their corresponding CSS classes for visualization.
   *
   * @return A map associating simulation states with CSS class names.
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
   * Initializes the mapping between integer state representations and their corresponding simulation states.
   *
   * @return A map associating integer values with simulation states.
   */
  @Override
  protected Map<Integer, StateInterface> initializeStateMap() {
    Map<Integer, StateInterface> stateMap = new HashMap<>();
    stateMap.put(EMPTY_STATE, SugarScapeState.EMPTY);
    stateMap.put(SUGAR_STATE, SugarScapeState.SUGAR);
    stateMap.put(AGENT_STATE, SugarScapeState.AGENT);
    return stateMap;
  }


  /**
   * Initializes the growth patterns of sugar across the grid.
   * Sugar peaks are placed at opposite corners of the grid, with
   * the sugar capacity being inversely related to the distance
   * from each corner. SugarCell states are updated accordingly.
   */
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
          // Change this line:
          if (cell.getCurrentState() != SugarScapeState.AGENT) {
            sugarCell.setNextState(SugarScapeState.SUGAR);
          }
        }
      }
    }
    grid.applyNextStates();
  }

  /**
   * Applies all simulation rules for the current tick. The rules include
   * agent movement, sugar grow back, reproduction, trading, lending,
   * immune system, and disease transmission. Dead agents are removed
   * and loans are updated.
   */
  @Override
  protected void applyRules() {

    List<Agent> shuffledAgents = new ArrayList<>(agents);
    Collections.shuffle(shuffledAgents, random);

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

  /**
   * Applies the movement rule for the agents. Each agent moves to
   * the best valid move based on their environment. If an agent
   * lands on a SugarCell, they collect sugar, and the cell's sugar is depleted.
   * The agent's position is updated, and the old cell's state is set to EMPTY.
   *
   * @param shuffledAgents a list of agents to apply the movement rule to.
   */
  private void applyMovementRule(List<Agent> shuffledAgents) {
    for (Agent agent : shuffledAgents) {
      List<Cell> validMoves = findValidMoves(agent);
      if (!validMoves.isEmpty()) {
        Cell bestMove = findBestMove(agent, validMoves);
        if (bestMove != null) {
          Cell oldCell = agent.getPosition();
          oldCell.setNextState(SugarScapeState.EMPTY);

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

  /**
   * Applies the sugar grow back rule. Every `sugarGrowBackInterval` ticks,
   * the sugar in SugarCells that are empty will grow back by a fixed rate
   * until they reach their maximum capacity.
   */
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

  /**
   * Applies the reproduction rule. Fertile agents can reproduce with neighboring agents,
   * provided they have enough sugar and an empty adjacent cell is available.
   * New agents are added to the simulation.
   *
   * @param shuffledAgents a list of agents to apply the reproduction rule to.
   */
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

  /**
   * Applies the trading rule. Agents that can trade with neighbors will
   * engage in trade, exchanging sugar and spice based on their marginal
   * rate of substitution.
   *
   * @param shuffledAgents a list of agents to apply the trading rule to.
   */
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

  /**
   * Applies the lending rule. Agents that can lend will offer loans
   * to potential borrowers who need it. Loans are created and transferred
   * accordingly.
   *
   * @param shuffledAgents a list of agents to apply the lending rule to.
   */
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

  /**
   * Determines whether a loan can be created between a lender and a borrower.
   *
   * @param lender   the agent providing the loan
   * @param borrower the agent requesting the loan
   * @return true if the lender can lend, the borrower needs a loan, and there is no existing loan between them
   */
  private boolean canCreateLoan(Agent lender, Agent borrower) {
    return lender.canLend() && borrower.needsBorrow() && !hasExistingLoan(lender, borrower);
  }

  /**
   * Applies the immune system rule to agents, updating their immune response based on existing diseases.
   *
   * @param shuffledAgents a shuffled list of agents to process
   */
  private void applyImmuneSystemRule(List<Agent> shuffledAgents) {
    for (Agent agent : shuffledAgents) {
      if (!agent.getDiseases().isEmpty()) {
        Disease randomDisease = selectRandomDisease(agent);
        agent.updateImmuneSystem(randomDisease);
        agent.checkAndRemoveDiseases();
      }
    }
  }

  /**
   * Simulates disease transmission between neighboring agents.
   *
   * @param shuffledAgents a shuffled list of agents to process
   */
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

  /**
   * Finds valid moves for an agent based on available neighboring cells.
   *
   * @param agent the agent whose valid moves are being determined
   * @return a list of valid neighboring cells the agent can move to
   */
  private List<Cell> findValidMoves(Agent agent) {
    Cell agentCell = agent.getPosition();
    Grid grid = getGrid();
    int row = -1;
    int col = -1;

    // Locate the agent's position in the grid
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

  /**
   * Finds the best move for an agent among the valid options, prioritizing sugar availability.
   *
   * @param agent      the agent making the move
   * @param validMoves the list of valid move options
   * @return the best cell to move to, or null if no optimal move is found
   */
  private Cell findBestMove(Agent agent, List<Cell> validMoves) {
    return validMoves.stream()
        .map(cell -> (Cell)cell)
        .filter(cell -> cell instanceof SugarCell)
        .max((cell1, cell2) -> {
          SugarCell sugar1 = (SugarCell) cell1;
          SugarCell sugar2 = (SugarCell) cell2;
          int sugarCompare = Integer.compare(sugar1.getSugar(), sugar2.getSugar());
          if (sugarCompare != 0) return sugarCompare;
          return Integer.compare(
              -calculateDistance(agent.getPosition(), cell1),
              -calculateDistance(agent.getPosition(), cell2)
          );
        })
        .orElse(null);
  }

  /**
   * Calculates the Manhattan distance between two cells in the grid.
   *
   * @param cell1 the first cell
   * @param cell2 the second cell
   * @return the Manhattan distance between the two cells
   */
  private int calculateDistance(Cell cell1, Cell cell2) {
    Grid grid = getGrid();
    int row1 = -1, col1 = -1, row2 = -1, col2 = -1;

    // Locate positions of both cells
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

  /**
   * Returns a list of agents that are neighboring the given agent. An agent is considered a neighbor
   * if it is adjacent (Manhattan distance of 1) to the given agent in the grid.
   *
   * @param agent the agent whose neighbors are to be found.
   * @return a list of neighboring agents.
   */
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

  /**
   * Finds a random empty cell in the grid. If no empty cells are found, the method returns null.
   *
   * @return a random empty cell from the grid, or null if no empty cells are available.
   */
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

  /**
   * Finds an adjacent empty cell to the given agent. The method searches the grid for a cell adjacent
   * to the agent's current position that is in the "EMPTY" state. If no adjacent empty cells are found,
   * the method returns null.
   *
   * @param agent the agent for which to find an adjacent empty cell.
   * @return an adjacent empty cell, or null if no such cell exists.
   */
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


  /**
   * Creates a new agent with random attributes for vision, metabolism, and initial sugar.
   * The agent's sex and fertility status are also randomly assigned.
   *
   * @param position the initial position of the agent.
   * @return the newly created agent.
   */
  private Agent createAgent(Cell position) {
    int vision = random.nextInt(4) + 1;  // Vision range 1-4
    int metabolism = random.nextInt(3) + 1;  // Metabolism rate 1-3
    int initialSugar = random.nextInt(20) + 10;  // Initial sugar 10-29
    Agent agent = new Agent(position, initialSugar, vision, metabolism);
    agent.setSex(random.nextBoolean() ? Sex.MALE : Sex.FEMALE);
    agent.setFertile(random.nextBoolean());
    return agent;
  }

  /**
   * Determines if two agents can reproduce. The agents must both be fertile,
   * have different sexes, and have enough sugar to meet their initial endowment requirements.
   *
   * @param agent1 the first agent.
   * @param agent2 the second agent.
   * @return true if the agents can reproduce, false otherwise.
   */
  private boolean canReproduce(Agent agent1, Agent agent2) {
    return agent1.isFertile() &&
        agent2.isFertile() &&
        agent1.getSex() != agent2.getSex() &&
        agent1.getSugar() >= agent1.getInitialEndowment() &&
        agent2.getSugar() >= agent2.getInitialEndowment();
  }

  /**
   * Reproduces two parent agents and creates a new child agent. The child's sugar is based on the
   * average sugar of the parents, and the child's vision and metabolism are inherited from the parents.
   * The parents lose half of their initial endowment in the process.
   *
   * @param parent1 the first parent agent.
   * @param parent2 the second parent agent.
   * @param position the position where the child agent will be placed.
   * @return the newly created child agent.
   */
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

  /**
   * Inherits the vision attribute from the two parent agents by averaging their vision values.
   *
   * @param parent1 the first parent agent.
   * @param parent2 the second parent agent.
   * @return the inherited vision value.
   */
  private int inheritVision(Agent parent1, Agent parent2) {
    return (parent1.getVision() + parent2.getVision()) / 2;
  }

  /**
   * Inherits the metabolism attribute from the two parent agents by averaging their metabolism values.
   *
   * @param parent1 the first parent agent.
   * @param parent2 the second parent agent.
   * @return the inherited metabolism value.
   */
  private int inheritMetabolism(Agent parent1, Agent parent2) {
    return (parent1.getMetabolism() + parent2.getMetabolism()) / 2;
  }


  /**
   * Determines whether two agents can trade based on their marginal rates of substitution (MRS).
   * A trade is possible if the agents have different MRS values and the absolute difference between
   * their MRS values is greater than a threshold of 0.1.
   *
   * @param agent1 the first agent involved in the trade.
   * @param agent2 the second agent involved in the trade.
   * @return true if the agents can trade, false otherwise.
   */
  private boolean canTrade(Agent agent1, Agent agent2) {
    double mrs1 = agent1.getMarginalRateOfSubstitution();
    double mrs2 = agent2.getMarginalRateOfSubstitution();
    return mrs1 != mrs2 && Math.abs(mrs1 - mrs2) > 0.1;
  }

  /**
   * Executes a trade between two agents. The trade is based on the geometric mean of the agents'
   * marginal rates of substitution (MRS). If the result is greater than or equal to 1, the first agent
   * trades sugar for spice with the second agent. Otherwise, the first agent trades spice for sugar.
   *
   * @param agent1 the first agent involved in the trade.
   * @param agent2 the second agent involved in the trade.
   */
  private void executeTrade(Agent agent1, Agent agent2) {
    double p = Math.sqrt(agent1.getMarginalRateOfSubstitution() *
        agent2.getMarginalRateOfSubstitution());

    if (p >= 1) {
      agent1.tradeSugarForSpice(agent2, 1, p);
    } else {
      agent1.tradeSpiceForSugar(agent2, 1, 1 / p);
    }
  }

  /**
   * Finds potential borrowers for a given lender. A potential borrower is an agent who needs to borrow,
   * does not already have an existing loan with the lender, and is not the lender themselves.
   *
   * @param lender the agent who is offering a loan.
   * @return a list of agents who are potential borrowers.
   */
  private List<Agent> findPotentialBorrowers(Agent lender) {
    return agents.stream()
        .filter(agent -> agent != lender)
        .filter(Agent::needsBorrow)
        .filter(agent -> !hasExistingLoan(lender, agent))
        .collect(Collectors.toList());
  }

  /**
   * Checks if there is an existing loan between a lender and a borrower.
   *
   * @param lender the potential lender.
   * @param borrower the potential borrower.
   * @return true if a loan exists between the lender and the borrower, false otherwise.
   */
  private boolean hasExistingLoan(Agent lender, Agent borrower) {
    return activeLoans.stream()
        .anyMatch(loan ->
            (loan.getLender() == lender && loan.getBorrower() == borrower) ||
                (loan.getLender() == borrower && loan.getBorrower() == lender)
        );
  }

  /**
   * Creates a new loan between a lender and a borrower. The loan amount is the smaller of the amount
   * the lender is available to lend and the amount the borrower needs to borrow.
   *
   * @param lender the agent offering the loan.
   * @param borrower the agent requesting the loan.
   * @return a new Loan object representing the loan between the lender and borrower.
   */
  private Loan createLoan(Agent lender, Agent borrower) {
    int amount = Math.min(lender.getAvailableToLend(), borrower.getNeededToBorrow());
    return new Loan(lender, borrower, amount, currentTick, LOAN_INTEREST_RATE);
  }

  /**
   * Transfers the loan amount from the lender to the borrower. The lender loses the amount of the loan,
   * and the borrower gains it.
   *
   * @param loan the loan whose amount is to be transferred.
   */
  private void transferLoanAmount(Loan loan) {
    loan.getLender().removeSugar(loan.getAmount());
    loan.getBorrower().addSugar(loan.getAmount());
  }


  /**
   * Updates the status of all active loans. Loans are removed if either the lender or borrower is dead,
   * or if the loan duration has been exceeded. If the loan duration is up, the payment is processed and the loan is removed.
   */
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

  /**
   * Processes the payment for a given loan. If the borrower has enough sugar to pay the full amount,
   * the payment is completed. If not, a partial payment is made and a new loan is created for the remaining debt.
   *
   * @param loan the loan to process.
   */
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

  /**
   * Selects a random disease from the list of diseases that an agent has. If the agent has no diseases,
   * the method returns null.
   *
   * @param agent the agent whose diseases are to be selected from.
   * @return a random disease from the agent's diseases, or null if the agent has no diseases.
   */
  private Disease selectRandomDisease(Agent agent) {
    List<Disease> diseases = agent.getDiseases();
    return diseases.isEmpty() ? null :
        diseases.get(random.nextInt(diseases.size()));
  }

  /**
   * Removes all dead agents from the simulation. The agents are removed from the list of agents and
   * their corresponding grid positions are set to EMPTY.
   */
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
  /**
   * Counts the number of agents of a specific sex.
   *
   * @param sex the sex to filter agents by (e.g., male or female).
   * @return the number of agents matching the specified sex.
   */
  private long countAgentsBySex(Sex sex) {
    return agents.stream()
        .filter(agent -> agent.getSex() == sex)
        .count();
  }

  /**
   * Counts the number of fertile agents in the simulation.
   * An agent is considered fertile if it satisfies the fertility condition defined in the `isFertile` method.
   *
   * @return the number of fertile agents.
   */
  private long countFertileAgents() {
    return agents.stream()
        .filter(Agent::isFertile)
        .count();
  }


  /**
   * Calculates the total amount of sugar in the simulation, including both sugar possessed by agents
   * and sugar available in the grid cells.
   *
   * @return the total amount of sugar in the system, summing agent sugar and grid sugar.
   */
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

  /**
   * Calculates the total amount of spice possessed by all agents in the simulation.
   *
   * @return the total amount of spice held by agents.
   */
  private int calculateTotalSpice() {
    return agents.stream()
        .mapToInt(Agent::getSpice)
        .sum();
  }

  /**
   * Calculates the average amount of sugar per agent in the simulation.
   * If there are no agents, the method returns 0.
   *
   * @return the average sugar per agent, or 0 if there are no agents.
   */
  private double calculateAverageSugarPerAgent() {
    return agents.isEmpty() ? 0 :
        agents.stream()
            .mapToInt(Agent::getSugar)
            .average()
            .orElse(0);
  }

  /**
   * Calculates the average amount of spice per agent in the simulation.
   * If there are no agents, the method returns 0.
   *
   * @return the average spice per agent, or 0 if there are no agents.
   */
  private double calculateAverageSpicePerAgent() {
    return agents.isEmpty() ? 0 :
        agents.stream()
            .mapToInt(Agent::getSpice)
            .average()
            .orElse(0);
  }


  /**
   * Counts the number of infected agents in the simulation.
   * An agent is considered infected if it has at least one disease.
   *
   * @return the number of infected agents.
   */
  private long countInfectedAgents() {
    return agents.stream()
        .filter(agent -> !agent.getDiseases().isEmpty())
        .count();
  }

  /**
   * Calculates the average number of diseases per infected agent.
   * If there are no infected agents, the method returns 0.
   *
   * @return the average number of diseases per infected agent, or 0 if no agents are infected.
   */
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

