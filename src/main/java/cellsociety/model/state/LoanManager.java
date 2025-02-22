package cellsociety.model.state;

import cellsociety.model.Agent;
import cellsociety.model.Loan;
import cellsociety.model.simulations.SugarScape;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class LoanManager {

  private final SugarScape simulation;
  private static final double LOAN_INTEREST_RATE = 0.1;
  private static final int LOAN_DURATION = 10;

  public LoanManager(SugarScape simulation) {
    this.simulation = simulation;
  }

  public void applyLending(List<Agent> agents) {
    for (Agent lender : agents) {
      if (lender.canLend()) {
        // Find potential borrowers.
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

  private boolean hasExistingLoan(Agent lender, Agent borrower) {
    return simulation.getActiveLoansInternal().stream().anyMatch(loan ->
        (loan.getLender() == lender && loan.getBorrower() == borrower) ||
            (loan.getLender() == borrower && loan.getBorrower() == lender)
    );
  }

  private Loan createLoan(Agent lender, Agent borrower) {
    int amount = Math.min(lender.getAvailableToLend(), borrower.getNeededToBorrow());
    return new Loan(lender, borrower, amount, simulation.getCurrentTick(), LOAN_INTEREST_RATE);
  }

  private void transferLoanAmount(Loan loan) {
    loan.getLender().removeSugar(loan.getAmount());
    loan.getBorrower().addSugar(loan.getAmount());
  }

  public void updateLoans(int currentTick) {
    Iterator<Loan> it = simulation.getActiveLoansInternal().iterator();
    while (it.hasNext()) {
      Loan loan = it.next();
      if (loan.getLender().isDead() || loan.getBorrower().isDead()) {
        it.remove();
        continue;
      }
      if (currentTick - loan.getIssueTick() >= LOAN_DURATION) {
        processLoanPayment(loan);
        it.remove();
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
      int partialPayment = borrower.getSugar() / 2;
      borrower.removeSugar(partialPayment);
      lender.addSugar(partialPayment);
      double remainingDebt = (paymentDue - partialPayment) * (1 + LOAN_INTEREST_RATE);
      Loan newLoan = new Loan(lender, borrower, (int) remainingDebt, simulation.getCurrentTick(), LOAN_INTEREST_RATE);
      simulation.getActiveLoansInternal().add(newLoan);
    }
  }
}

