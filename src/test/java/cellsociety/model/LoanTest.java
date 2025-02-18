package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cellsociety.model.state.SugarScapeState;

/**
 * Test class for Loan in the SugarScape simulation. Tests loan creation, validation, and
 * calculations.
 *
 * @author Tatum McKinnis
 */
class LoanTest {

  private Loan loan;
  private Agent lender;
  private Agent borrower;
  private static final int LOAN_AMOUNT = 50;
  private static final double INTEREST_RATE = 0.1;
  private static final int ISSUE_TICK = 0;

  /**
   * Sets up the testing environment before each test. Initializes the lender, borrower, and loan.
   */
  @BeforeEach
  void setUp() {
    lender = new Agent(new SugarCell(0, 0, SugarScapeState.EMPTY), 100, 1, 1);
    borrower = new Agent(new SugarCell(1, 0, SugarScapeState.EMPTY), 20, 1, 1);
    loan = new Loan(lender, borrower, LOAN_AMOUNT, ISSUE_TICK, INTEREST_RATE);
  }

  /**
   * Verifies that the loan is correctly created with valid parameters. Ensures that the lender,
   * borrower, loan amount, interest rate, and issue tick are set correctly.
   */
  @Test
  void constructor_WithValidParams_CreatesLoan() {
    assertEquals(lender, loan.getLender());
    assertEquals(borrower, loan.getBorrower());
    assertEquals(LOAN_AMOUNT, loan.getAmount());
    assertEquals(INTEREST_RATE, loan.getInterest());
    assertEquals(ISSUE_TICK, loan.getIssueTick());
  }

  /**
   * Verifies that an IllegalArgumentException is thrown when the lender is null in the loan
   * constructor.
   */
  @Test
  void constructor_WithNullLender_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Loan(null, borrower, LOAN_AMOUNT, ISSUE_TICK, INTEREST_RATE));
  }

  /**
   * Verifies that an IllegalArgumentException is thrown when the borrower is null in the loan
   * constructor.
   */
  @Test
  void constructor_WithNullBorrower_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Loan(lender, null, LOAN_AMOUNT, ISSUE_TICK, INTEREST_RATE));
  }

  /**
   * Verifies that an IllegalArgumentException is thrown when the lender and borrower are the same
   * agent in the loan constructor.
   */
  @Test
  void constructor_WithSameAgent_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Loan(lender, lender, LOAN_AMOUNT, ISSUE_TICK, INTEREST_RATE));
  }

  /**
   * Verifies that an IllegalArgumentException is thrown when the loan amount is negative in the
   * loan constructor.
   */
  @Test
  void constructor_WithNegativeAmount_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Loan(lender, borrower, -1, ISSUE_TICK, INTEREST_RATE));
  }

  /**
   * Verifies that an IllegalArgumentException is thrown when the interest rate is negative in the
   * loan constructor.
   */
  @Test
  void constructor_WithNegativeInterest_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Loan(lender, borrower, LOAN_AMOUNT, ISSUE_TICK, -0.1));
  }

  /**
   * Verifies that the loan amount is updated correctly when a positive value is passed to
   * updateAmount().
   */
  @Test
  void updateAmount_WithPositiveAmount_UpdatesAmount() {
    int newAmount = 75;
    loan.updateAmount(newAmount);
    assertEquals(newAmount, loan.getAmount());
  }

  /**
   * Verifies that an IllegalArgumentException is thrown when a negative value is passed to
   * updateAmount().
   */
  @Test
  void updateAmount_WithNegativeAmount_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> loan.updateAmount(-1));
  }

  /**
   * Verifies that the issue tick is updated correctly when a valid tick is passed to
   * setIssueTick().
   */
  @Test
  void setIssueTick_WithValidTick_UpdatesTick() {
    int newTick = 5;
    loan.setIssueTick(newTick);
    assertEquals(newTick, loan.getIssueTick());
  }

  /**
   * Verifies that an IllegalArgumentException is thrown when a negative issue tick is passed to
   * setIssueTick().
   */
  @Test
  void setIssueTick_WithNegativeTick_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> loan.setIssueTick(-1));
  }

  /**
   * Verifies that the correct amount due is calculated based on the loan amount and interest rate.
   */
  @Test
  void getAmountDue_WhenCalled_CalculatesCorrectAmount() {
    double expectedAmount = LOAN_AMOUNT * (1 + INTEREST_RATE);
    assertEquals(expectedAmount, loan.getAmountDue());
  }
}
