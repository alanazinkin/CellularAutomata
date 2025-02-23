package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cellsociety.model.simulations.SugarScape;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for LoanManager.
 * Tests the lending and loan management functionality in the SugarScape simulation.
 */
public class LoanManagerTest {

  private LoanManager loanManager;
  private SugarScape mockSimulation;
  private List<Loan> activeLoans;
  private Agent lender;
  private Agent borrower;

  @BeforeEach
  void setUp() {
    mockSimulation = mock(SugarScape.class);
    activeLoans = new ArrayList<>();
    when(mockSimulation.getActiveLoansInternal()).thenReturn(activeLoans);

    lender = mock(Agent.class);
    borrower = mock(Agent.class);

    when(lender.canLend()).thenReturn(true);
    when(lender.getAvailableToLend()).thenReturn(10);
    when(borrower.needsBorrow()).thenReturn(true);
    when(borrower.getNeededToBorrow()).thenReturn(5);

    loanManager = new LoanManager(mockSimulation);
  }

  /**
   * Tests that a loan is successfully created between a lender and borrower
   * when all conditions are met.
   */
  @Test
  void applyLending_ValidLenderAndBorrower_CreatesLoan() {
    List<Agent> agents = new ArrayList<>();
    agents.add(lender);
    agents.add(borrower);
    when(mockSimulation.getAgents()).thenReturn(agents);

    loanManager.applyLending(agents);

    assertEquals(1, activeLoans.size());
    verify(lender).removeSugar(5);
    verify(borrower).addSugar(5);
  }

  /**
   * Tests that no loan is created when the lender cannot lend.
   */
  @Test
  void applyLending_LenderCannotLend_NoLoanCreated() {
    when(lender.canLend()).thenReturn(false);
    List<Agent> agents = new ArrayList<>();
    agents.add(lender);
    agents.add(borrower);

    loanManager.applyLending(agents);

    assertTrue(activeLoans.isEmpty());
    verify(lender, never()).removeSugar(anyInt());
    verify(borrower, never()).addSugar(anyInt());
  }

  /**
   * Tests that loans are properly removed when the lender dies.
   */
  @Test
  void updateLoans_LenderDies_LoanRemoved() {
    Loan loan = new Loan(lender, borrower, 5, 0, 0.1);
    activeLoans.add(loan);
    when(lender.isDead()).thenReturn(true);

    loanManager.updateLoans(5);

    assertTrue(activeLoans.isEmpty());
  }

  /**
   * Tests that a loan payment is processed correctly when the borrower
   * has sufficient funds.
   */
  @Test
  void updateLoans_LoanDueWithSufficientFunds_ProcessesPayment() {
    Loan loan = new Loan(lender, borrower, 10, 0, 0.1);
    activeLoans.add(loan);
    when(borrower.getSugar()).thenReturn(11);

    loanManager.updateLoans(10);

    assertTrue(activeLoans.isEmpty());
    verify(borrower).removeSugar(11);
    verify(lender).addSugar(11);
  }

  /**
   * Tests that a new loan is created with increased debt when the borrower
   * cannot fully repay.
   */
  @Test
  void updateLoans_InsufficientFundsForRepayment_CreatesNewLoan() {
    Loan loan = new Loan(lender, borrower, 10, 0, 0.1);
    activeLoans.add(loan);
    when(borrower.getSugar()).thenReturn(5);

    loanManager.updateLoans(10);

    assertEquals(1, activeLoans.size());
    verify(borrower).removeSugar(2); // Half of available sugar
    verify(lender).addSugar(2);
  }

  /**
   * Tests that an IllegalArgumentException is thrown when trying to
   * process a loan with null agents.
   */
  @Test
  void applyLending_NullAgentList_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () ->
        loanManager.applyLending(null));
  }

  /**
   * Tests that an IllegalArgumentException is thrown when trying to
   * update loans with a negative tick value.
   */
  @Test
  void updateLoans_NegativeTick_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () ->
        loanManager.updateLoans(-1));
  }
}
