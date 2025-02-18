package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cellsociety.model.state.SugarScapeState;

/**
 * Test class for Loan in the SugarScape simulation.
 * Tests loan creation, validation, and calculations.
 */
class LoanTest {
  private Loan loan;
  private Agent lender;
  private Agent borrower;
  private static final int LOAN_AMOUNT = 50;
  private static final double INTEREST_RATE = 0.1;
  private static final int ISSUE_TICK = 0;

  @BeforeEach
  void setUp() {
    lender = new Agent(new SugarCell(0, 0, SugarScapeState.EMPTY), 100, 1, 1);
    borrower = new Agent(new SugarCell(1, 0, SugarScapeState.EMPTY), 20, 1, 1);
    loan = new Loan(lender, borrower, LOAN_AMOUNT, ISSUE_TICK, INTEREST_RATE);
  }

  @Test
  void constructor_WithValidParams_CreatesLoan() {
    assertEquals(lender, loan.getLender());
    assertEquals(borrower, loan.getBorrower());
    assertEquals(LOAN_AMOUNT, loan.getAmount());
    assertEquals(INTEREST_RATE, loan.getInterest());
    assertEquals(ISSUE_TICK, loan.getIssueTick());
  }

  @Test
  void constructor_WithNullLender_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Loan(null, borrower, LOAN_AMOUNT, ISSUE_TICK, INTEREST_RATE));
  }

  @Test
  void constructor_WithNullBorrower_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Loan(lender, null, LOAN_AMOUNT, ISSUE_TICK, INTEREST_RATE));
  }

  @Test
  void constructor_WithSameAgent_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Loan(lender, lender, LOAN_AMOUNT, ISSUE_TICK, INTEREST_RATE));
  }

  @Test
  void constructor_WithNegativeAmount_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Loan(lender, borrower, -1, ISSUE_TICK, INTEREST_RATE));
  }

  @Test
  void constructor_WithNegativeInterest_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Loan(lender, borrower, LOAN_AMOUNT, ISSUE_TICK, -0.1));
  }

  @Test
  void updateAmount_WithPositiveAmount_UpdatesAmount() {
    int newAmount = 75;
    loan.updateAmount(newAmount);
    assertEquals(newAmount, loan.getAmount());
  }

  @Test
  void updateAmount_WithNegativeAmount_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> loan.updateAmount(-1));
  }

  @Test
  void setIssueTick_WithValidTick_UpdatesTick() {
    int newTick = 5;
    loan.setIssueTick(newTick);
    assertEquals(newTick, loan.getIssueTick());
  }

  @Test
  void setIssueTick_WithNegativeTick_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> loan.setIssueTick(-1));
  }

  @Test
  void getAmountDue_WhenCalled_CalculatesCorrectAmount() {
    double expectedAmount = LOAN_AMOUNT * (1 + INTEREST_RATE);
    assertEquals(expectedAmount, loan.getAmountDue());
  }
}