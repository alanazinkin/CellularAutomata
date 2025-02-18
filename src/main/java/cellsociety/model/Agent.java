package cellsociety.model;

import java.util.*;

/**
 * Represents an agent in the SugarScape simulation.
 * <p>
 * Agents move around the grid, collect and metabolize resources, reproduce, trade with other
 * agents, and can be affected by diseases.
 * </p>
 *
 * @author Tatum McKinnis
 */
public class Agent {

  private Cell position;
  private int sugar;
  private int spice;
  private final int vision;
  private final int metabolism;
  private final int initialEndowment;
  private Sex sex;
  private boolean fertile;
  private List<Disease> diseases;
  private List<Integer> immuneSystem;
  private static final int IMMUNE_SYSTEM_LENGTH = 10;

  /**
   * Creates a new Agent with specified attributes.
   *
   * @param position     initial position of the agent
   * @param initialSugar starting amount of sugar
   * @param vision       how far the agent can see
   * @param metabolism   rate at which agent consumes sugar
   * @throws IllegalArgumentException if any parameters are invalid
   */
  public Agent(Cell position, int initialSugar, int vision, int metabolism) {
    validateConstructorParams(position, initialSugar, vision, metabolism);

    this.position = position;
    this.sugar = initialSugar;
    this.initialEndowment = initialSugar;
    this.vision = vision;
    this.metabolism = metabolism;
    this.spice = 0;
    this.diseases = new ArrayList<>();
    initializeImmuneSystem();
  }

  /**
   * Validates parameters passed to the Agent constructor.
   *
   * @param position     the initial cell position
   * @param initialSugar starting amount of sugar
   * @param vision       vision range of the agent
   * @param metabolism   metabolism rate of the agent
   * @throws IllegalArgumentException if any parameter is invalid
   */
  private void validateConstructorParams(Cell position, int initialSugar, int vision,
      int metabolism) {
    if (position == null) {
      throw new IllegalArgumentException("Position cannot be null");
    }
    if (initialSugar < 0) {
      throw new IllegalArgumentException("Initial sugar cannot be negative");
    }
    if (vision <= 0) {
      throw new IllegalArgumentException("Vision must be positive");
    }
    if (metabolism <= 0) {
      throw new IllegalArgumentException("Metabolism must be positive");
    }
  }

  /**
   * Initializes the immune system with random binary values.
   */
  private void initializeImmuneSystem() {
    this.immuneSystem = new ArrayList<>(IMMUNE_SYSTEM_LENGTH);
    Random random = new Random();
    for (int i = 0; i < IMMUNE_SYSTEM_LENGTH; i++) {
      immuneSystem.add(random.nextInt(2));  // Binary value (0 or 1)
    }
  }

  /**
   * Updates the agent's position on the grid.
   *
   * @param newPosition the new cell position
   * @throws IllegalArgumentException if newPosition is null
   */
  public void setPosition(Cell newPosition) {
    if (newPosition == null) {
      throw new IllegalArgumentException("New position cannot be null");
    }
    this.position = newPosition;
  }

  /**
   * Metabolizes sugar according to the agent's metabolism rate.
   */
  public void metabolize() {
    sugar -= metabolism;
  }

  /**
   * Checks if the agent has died from a lack of resources.
   *
   * @return true if the agent's sugar level is 0 or below, false otherwise
   */
  public boolean isDead() {
    return sugar <= 0;
  }

  /**
   * Adds sugar to the agent's resources.
   *
   * @param amount amount of sugar to add
   * @throws IllegalArgumentException if amount is negative
   */
  public void addSugar(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Cannot add negative sugar amount");
    }
    this.sugar += amount;
  }

  /**
   * Removes sugar from the agent's resources.
   *
   * @param amount amount of sugar to remove
   * @throws IllegalArgumentException if amount is negative or more than available
   */
  public void removeSugar(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Cannot remove negative sugar amount");
    }
    if (amount > sugar) {
      throw new IllegalArgumentException("Cannot remove more sugar than available");
    }
    this.sugar -= amount;
  }

  /**
   * Calculates the Marginal Rate of Substitution (MRS) between sugar and spice.
   *
   * @return the MRS value; returns positive infinity if spice is 0
   */
  public double getMarginalRateOfSubstitution() {
    if (spice == 0) {
      return Double.POSITIVE_INFINITY;
    }
    return (double) sugar / spice;
  }

  /**
   * Executes a trade of sugar for spice with another agent.
   *
   * @param other       the other agent in the trade
   * @param sugarAmount amount of sugar to trade
   * @param spiceAmount amount of spice to receive
   * @throws IllegalArgumentException if trade parameters are invalid
   */
  public void tradeSugarForSpice(Agent other, int sugarAmount, double spiceAmount) {
    validateTradeParams(other, sugarAmount, spiceAmount);

    removeSugar(sugarAmount);
    other.addSugar(sugarAmount);

    int spiceToReceive = (int) spiceAmount;
    other.removeSpice(spiceToReceive);
    addSpice(spiceToReceive);
  }

  /**
   * Executes a trade of spice for sugar with another agent.
   *
   * @param other       the other agent in the trade
   * @param spiceAmount amount of spice to trade
   * @param sugarAmount amount of sugar to receive
   * @throws IllegalArgumentException if trade parameters are invalid
   */
  public void tradeSpiceForSugar(Agent other, int spiceAmount, double sugarAmount) {
    validateTradeParams(other, sugarAmount, spiceAmount);

    removeSpice(spiceAmount);
    other.addSpice(spiceAmount);

    int sugarToReceive = (int) sugarAmount;
    other.removeSugar(sugarToReceive);
    addSugar(sugarToReceive);
  }

  /**
   * Validates parameters for a trade operation.
   *
   * @param other   the trade partner
   * @param amount1 first trade amount (sugar or spice)
   * @param amount2 second trade amount (spice or sugar)
   * @throws IllegalArgumentException if any parameter is invalid
   */
  private void validateTradeParams(Agent other, double amount1, double amount2) {
    if (other == null) {
      throw new IllegalArgumentException("Trade partner cannot be null");
    }
    if (amount1 <= 0 || amount2 <= 0) {
      throw new IllegalArgumentException("Trade amounts must be positive");
    }
  }

  /**
   * Determines if the agent can lend resources.
   *
   * @return true if the agent has excess resources to lend
   */
  public boolean canLend() {
    return (!fertile || sugar > 2 * initialEndowment);
  }

  /**
   * Determines if the agent needs to borrow resources.
   *
   * @return true if the agent needs additional resources
   */
  public boolean needsBorrow() {
    return fertile && sugar < initialEndowment;
  }

  /**
   * Gets the amount of resources available for lending.
   *
   * @return the amount available to lend; returns 0 if lending is not possible
   */
  public int getAvailableToLend() {
    if (!canLend()) {
      return 0;
    }
    return fertile ? sugar - 2 * initialEndowment : sugar - initialEndowment;
  }

  /**
   * Gets the amount of resources needed to borrow.
   *
   * @return the amount needed to borrow; returns 0 if borrowing is not needed
   */
  public int getNeededToBorrow() {
    if (!needsBorrow()) {
      return 0;
    }
    return initialEndowment - sugar;
  }

  /**
   * Updates the immune system in response to a disease. The immune system is adjusted to better
   * match the disease pattern.
   *
   * @param disease the disease to respond to
   */
  public void updateImmuneSystem(Disease disease) {
    List<Integer> diseasePattern = disease.getPattern();
    int bestMatchStart = findBestMatch(diseasePattern);

    if (bestMatchStart >= 0) {
      for (int i = 0; i < diseasePattern.size(); i++) {
        if (immuneSystem.get(bestMatchStart + i) != diseasePattern.get(i)) {
          immuneSystem.set(bestMatchStart + i, diseasePattern.get(i));
          break;
        }
      }
    }
  }

  /**
   * Finds the starting index in the immune system that best matches the given disease pattern.
   *
   * @param pattern the disease pattern to match
   * @return the starting index of the best match; -1 if no match is found
   */
  private int findBestMatch(List<Integer> pattern) {
    int bestMatchStart = -1;
    int maxMatches = 0;

    for (int i = 0; i <= immuneSystem.size() - pattern.size(); i++) {
      int matches = countMatches(pattern, i);
      if (matches > maxMatches) {
        maxMatches = matches;
        bestMatchStart = i;
      }
    }

    return bestMatchStart;
  }

  /**
   * Counts the number of matching elements between the immune system and the disease pattern,
   * starting at a specified index.
   *
   * @param pattern the disease pattern
   * @param start   the starting index in the immune system
   * @return the count of matching elements
   */
  private int countMatches(List<Integer> pattern, int start) {
    int matches = 0;
    for (int i = 0; i < pattern.size(); i++) {
      if (immuneSystem.get(start + i).equals(pattern.get(i))) {
        matches++;
      }
    }
    return matches;
  }

  /**
   * Removes diseases that the agent's immune system can now handle.
   */
  public void checkAndRemoveDiseases() {
    diseases.removeIf(this::isImmuneToDisease);
  }

  /**
   * Checks if the agent's immune system is immune to a specific disease. This is determined by
   * checking if the immune system contains the disease's pattern.
   *
   * @param disease the disease to check
   * @return true if the immune system contains the disease pattern, false otherwise
   */
  private boolean isImmuneToDisease(Disease disease) {
    List<Integer> pattern = disease.getPattern();
    String patternStr = pattern.stream()
        .map(String::valueOf)
        .reduce("", String::concat);

    String immuneStr = immuneSystem.stream()
        .map(String::valueOf)
        .reduce("", String::concat);

    return immuneStr.contains(patternStr);
  }

  /**
   * Gets the current position of the agent.
   *
   * @return the cell representing the agent's position
   */
  public Cell getPosition() {
    return position;
  }

  /**
   * Gets the current amount of sugar the agent has.
   *
   * @return the amount of sugar
   */
  public int getSugar() {
    return sugar;
  }

  /**
   * Sets the amount of spice the agent has.
   *
   * @param spice the new spice amount
   * @throws IllegalArgumentException if spice is negative
   */
  public void setSpice(int spice) {
    if (spice < 0) {
      throw new IllegalArgumentException("Spice cannot be negative");
    }
    this.spice = spice;
  }

  /**
   * Gets the current amount of spice the agent has.
   *
   * @return the amount of spice
   */
  public int getSpice() {
    return spice;
  }

  /**
   * Adds spice to the agent's resources.
   *
   * @param amount the amount of spice to add
   * @throws IllegalArgumentException if amount is negative
   */
  public void addSpice(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Cannot add negative spice amount");
    }
    this.spice += amount;
  }

  /**
   * Removes spice from the agent's resources.
   *
   * @param amount the amount of spice to remove
   * @throws IllegalArgumentException if amount is negative or exceeds available spice
   */
  public void removeSpice(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Cannot remove negative spice amount");
    }
    if (amount > spice) {
      throw new IllegalArgumentException("Cannot remove more spice than available");
    }
    this.spice -= amount;
  }

  /**
   * Gets the vision range of the agent.
   *
   * @return the vision range
   */
  public int getVision() {
    return vision;
  }

  /**
   * Gets the metabolism rate of the agent.
   *
   * @return the metabolism rate
   */
  public int getMetabolism() {
    return metabolism;
  }

  /**
   * Gets the initial endowment of sugar given to the agent at creation.
   *
   * @return the initial sugar endowment
   */
  public int getInitialEndowment() {
    return initialEndowment;
  }

  /**
   * Gets the sex of the agent.
   *
   * @return the sex
   */
  public Sex getSex() {
    return sex;
  }

  /**
   * Sets the sex of the agent.
   *
   * @param sex the sex to set
   */
  public void setSex(Sex sex) {
    this.sex = sex;
  }

  /**
   * Checks if the agent is fertile.
   *
   * @return true if fertile, false otherwise
   */
  public boolean isFertile() {
    return fertile;
  }

  /**
   * Sets the fertility status of the agent.
   *
   * @param fertile true if the agent should be fertile, false otherwise
   */
  public void setFertile(boolean fertile) {
    this.fertile = fertile;
  }

  /**
   * Gets a copy of the list of diseases the agent currently has.
   *
   * @return a list of diseases
   */
  public List<Disease> getDiseases() {
    return new ArrayList<>(diseases);
  }

  /**
   * Adds a disease to the agent's list of diseases if the agent is not already immune.
   *
   * @param disease the disease to add
   * @throws IllegalArgumentException if the disease is null
   */
  public void addDisease(Disease disease) {
    if (disease == null) {
      throw new IllegalArgumentException("Disease cannot be null");
    }
    if (!isImmuneToDisease(disease)) {
      diseases.add(disease);
    }
  }

  /**
   * Retrieves a random disease from the agent's list of diseases.
   *
   * @return a random Disease, or null if the agent has no diseases
   */
  public Disease getRandomDisease() {
    if (diseases.isEmpty()) {
      return null;
    }
    return diseases.get(new Random().nextInt(diseases.size()));
  }

  /**
   * Gets a copy of the agent's immune system.
   *
   * @return a list representing the immune system
   */
  public List<Integer> getImmuneSystem() {
    return new ArrayList<>(immuneSystem);
  }
}
