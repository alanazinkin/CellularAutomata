package cellsociety.model;

/**
 * Represents a loan between two agents in the SugarScape simulation.
 * <p>
 * A loan tracks:
 * <ul>
 *   <li>The lending agent</li>
 *   <li>The borrowing agent</li>
 *   <li>The amount of sugar loaned</li>
 *   <li>The interest rate</li>
 *   <li>The simulation tick when the loan was issued</li>
 * </ul>
 * Loans are created when fertile agents need resources to reproduce and other agents have excess
 * resources to lend. They are managed over time with interest accumulation and repayment schedules.
 * </p>
 *
 * @author Tatum McKinnis
 */
public class Loan {

  private final Agent lender;
  private final Agent borrower;
  private int amount;
  private final double interest;
  private int issueTick;

  /**
   * Creates a new loan between two agents.
   * <p>
   * Initializes a loan with the specified parameters. The loan amount and interest rate must be
   * positive, and the lender and borrower must be different agents.
   * </p>
   *
   * @param lender    the agent providing the loan
   * @param borrower  the agent receiving the loan
   * @param amount    the amount of sugar being loaned
   * @param issueTick the simulation tick when the loan was issued
   * @param interest  the interest rate of the loan
   * @throws IllegalArgumentException if any parameters are invalid
   */
  public Loan(Agent lender, Agent borrower, int amount, int issueTick, double interest) {
    validateConstructorParams(lender, borrower, amount, interest);
    this.lender = lender;
    this.borrower = borrower;
    this.amount = amount;
    this.issueTick = issueTick;
    this.interest = interest;
  }

  /**
   * Validates the parameters for loan-related constructor methods.
   * <p>
   * Ensures that the lender and borrower are valid, the loan amount is positive, and the interest
   * rate is non-negative. Throws an {@code IllegalArgumentException} if any condition is violated.
   * </p>
   *
   * @param lender   the agent providing the loan
   * @param borrower the agent receiving the loan
   * @param amount   the amount of the loan, must be positive
   * @param interest the interest rate, must be non-negative
   * @throws IllegalArgumentException if any validation check fails
   */
  private void validateConstructorParams(Agent lender, Agent borrower, int amount,
      double interest) {
    if (lender == null) {
      throw new IllegalArgumentException("Lender cannot be null");
    }
    if (borrower == null) {
      throw new IllegalArgumentException("Borrower cannot be null");
    }
    if (lender == borrower) {
      throw new IllegalArgumentException("Lender and borrower cannot be the same agent");
    }
    if (amount <= 0) {
      throw new IllegalArgumentException("Loan amount must be positive");
    }
    if (interest < 0) {
      throw new IllegalArgumentException("Interest rate cannot be negative");
    }
  }


  /**
   * Updates the loan amount.
   * <p>
   * Used when restructuring loans or applying penalties. The new amount must be positive.
   * </p>
   *
   * @param newAmount the new loan amount
   * @throws IllegalArgumentException if newAmount is not positive
   */
  public void updateAmount(int newAmount) {
    if (newAmount <= 0) {
      throw new IllegalArgumentException("New loan amount must be positive");
    }
    this.amount = newAmount;
  }

  /**
   * Updates the issue tick of the loan.
   * <p>
   * Used when restructuring loans or tracking repayment schedules.
   * </p>
   *
   * @param newIssueTick the new issue tick
   * @throws IllegalArgumentException if newIssueTick is negative
   */
  public void setIssueTick(int newIssueTick) {
    if (newIssueTick < 0) {
      throw new IllegalArgumentException("Issue tick cannot be negative");
    }
    this.issueTick = newIssueTick;
  }

  /**
   * Calculates the total amount due including interest.
   * <p>
   * The total amount is the principal plus interest (amount * (1 + interest)).
   * </p>
   *
   * @return the total amount due
   */
  public double getAmountDue() {
    return amount * (1 + interest);
  }

  /**
   * Gets the lending agent.
   *
   * @return the agent who provided the loan
   */
  public Agent getLender() {
    return lender;
  }

  /**
   * Gets the borrowing agent.
   *
   * @return the agent who received the loan
   */
  public Agent getBorrower() {
    return borrower;
  }

  /**
   * Gets the current loan amount.
   *
   * @return the principal amount of the loan
   */
  public int getAmount() {
    return amount;
  }

  /**
   * Gets the interest rate of the loan.
   *
   * @return the interest rate as a decimal
   */
  public double getInterest() {
    return interest;
  }

  /**
   * Gets the simulation tick when the loan was issued.
   *
   * @return the issue tick
   */
  public int getIssueTick() {
    return issueTick;
  }

  @Override
  public String toString() {
    return String.format("Loan[amount=%d, interest=%.2f, issueTick=%d]",
        amount, interest, issueTick);
  }
}
