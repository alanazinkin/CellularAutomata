package cellsociety.model;

import cellsociety.model.simulations.SugarScape;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages loan transactions between agents in the SugarScape simulation. Agents can lend and borrow
 * sugar based on their financial needs and availability. The LoanManager facilitates loan creation,
 * repayment, and updates active loans over time.
 *
 * @author Tatum McKinnis
 */
public class LoanManager {

  private final SugarScape simulation;
  private static final double LOAN_INTEREST_RATE = 0.1;
  private static final int LOAN_DURATION = 10;

  /**
   * Constructs a LoanManager for managing loans in the given SugarScape simulation.
   *
   * @param simulation the SugarScape simulation instance
   */
  public LoanManager(SugarScape simulation) {
    this.simulation = simulation;
  }

  /**
   * Applies lending logic where agents who can lend provide loans to agents in need. Ensures that
   * loans are only issued if they do not already exist between the lender and borrower.
   *
   * @param agents the list of agents participating in the lending process
   * @throws IllegalArgumentException if the provided agent list is null
   */
  public void applyLending(List<Agent> agents) {
    if (agents == null) {
      throw new IllegalArgumentException("Agent list cannot be null");
    }

    for (Agent lender : agents) {
      if (lender.canLend()) {
        List<Agent> potentialBorrowers = simulation.getAgents().stream()
            .filter(agent -> agent != lender)
            .filter(Agent::needsBorrow)
            .filter(borrower -> !hasExistingLoan(lender, borrower))
            .collect(Collectors.toList());

        for (Agent borrower : potentialBorrowers) {
          if (lender.canLend() && borrower.needsBorrow() && !hasExistingLoan(lender, borrower)) {
            Loan loan = createLoan(lender, borrower);
            simulation.getActiveLoansInternal().add(loan);
            transferLoanAmount(loan);
          }
        }
      }
    }
  }

  /**
   * Checks whether a loan already exists between the specified lender and borrower.
   *
   * @param lender   the lending agent
   * @param borrower the borrowing agent
   * @return true if a loan already exists between them, false otherwise
   */
  private boolean hasExistingLoan(Agent lender, Agent borrower) {
    return simulation.getActiveLoansInternal().stream().anyMatch(loan ->
        (loan.getLender() == lender && loan.getBorrower() == borrower) ||
            (loan.getLender() == borrower && loan.getBorrower() == lender)
    );
  }

  /**
   * Creates a new loan agreement between a lender and a borrower.
   *
   * @param lender   the agent providing the loan
   * @param borrower the agent receiving the loan
   * @return a newly created Loan instance
   */
  private Loan createLoan(Agent lender, Agent borrower) {
    int amount = Math.min(lender.getAvailableToLend(), borrower.getNeededToBorrow());
    return new Loan(lender, borrower, amount, simulation.getCurrentTick(), LOAN_INTEREST_RATE);
  }

  /**
   * Transfers the loan amount from the lender to the borrower.
   *
   * @param loan the loan being processed
   */
  private void transferLoanAmount(Loan loan) {
    loan.getLender().removeSugar(loan.getAmount());
    loan.getBorrower().addSugar(loan.getAmount());
  }

  /**
   * Updates the status of active loans based on the current tick of the simulation. Removes loans
   * that have reached maturity and processes repayments.
   *
   * @param currentTick the current tick in the simulation
   * @throws IllegalArgumentException if the current tick is negative
   */
  public void updateLoans(int currentTick) {
    if (currentTick < 0) {
      throw new IllegalArgumentException("Current tick cannot be negative");
    }

    List<Loan> newLoans = new ArrayList<>();
    Iterator<Loan> it = simulation.getActiveLoansInternal().iterator();

    while (it.hasNext()) {
      Loan loan = it.next();
      if (loan.getLender().isDead() || loan.getBorrower().isDead()) {
        it.remove();
        continue;
      }
      if (currentTick - loan.getIssueTick() >= LOAN_DURATION) {
        Loan newLoan = processLoanPayment(loan);
        if (newLoan != null) {
          newLoans.add(newLoan);
        }
        it.remove();
      }
    }
    simulation.getActiveLoansInternal().addAll(newLoans);
  }

  /**
   * Processes loan repayment when a loan reaches maturity. If the borrower cannot fully repay the
   * loan, a new loan is created with the remaining debt.
   *
   * @param loan the loan being processed for repayment
   * @return a new loan with the remaining debt if full payment is not possible, otherwise null
   */
  private Loan processLoanPayment(Loan loan) {
    Agent borrower = loan.getBorrower();
    Agent lender = loan.getLender();
    double paymentDue = loan.getAmount() * (1 + loan.getInterest());

    if (borrower.getSugar() >= paymentDue) {
      borrower.removeSugar((int) paymentDue);
      lender.addSugar((int) paymentDue);
      return null;
    } else {
      int partialPayment = borrower.getSugar() / 2;
      borrower.removeSugar(partialPayment);
      lender.addSugar(partialPayment);
      double remainingDebt = (paymentDue - partialPayment) * (1 + LOAN_INTEREST_RATE);
      return new Loan(lender, borrower, (int) remainingDebt, simulation.getCurrentTick(),
          LOAN_INTEREST_RATE);
    }
  }
}