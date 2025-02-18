package cellsociety.model;

import java.util.*;

/**
 * Represents an agent in the SugarScape simulation.
 * <p>
 * Agents move around the grid, collect and metabolize resources, reproduce,
 * trade with other agents, and can be affected by diseases.
 * </p>
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
   * @param position initial position of the agent
   * @param initialSugar starting amount of sugar
   * @param vision how far the agent can see
   * @param metabolism rate at which agent consumes sugar
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

  private void validateConstructorParams(Cell position, int initialSugar, int vision, int metabolism) {
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

  private void initializeImmuneSystem() {
    this.immuneSystem = new ArrayList<>(IMMUNE_SYSTEM_LENGTH);
    Random random = new Random();
    for (int i = 0; i < IMMUNE_SYSTEM_LENGTH; i++) {
      immuneSystem.add(random.nextInt(2));  // Binary string representation
    }
  }

  /**
   * Updates agent's position on the grid.
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
   * Metabolizes sugar according to agent's metabolism rate.
   */
  public void metabolize() {
    sugar -= metabolism;
  }

  /**
   * Checks if agent has died from lack of resources.
   *
   * @return true if agent's sugar level is 0 or below
   */
  public boolean isDead() {
    return sugar <= 0;
  }

  /**
   * Adds sugar to agent's resources.
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
   * Removes sugar from agent's resources.
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
   * Calculates Marginal Rate of Substitution between sugar and spice.
   *
   * @return the MRS value
   */
  public double getMarginalRateOfSubstitution() {
    if (spice == 0) return Double.POSITIVE_INFINITY;
    return (double) sugar / spice;
  }

  /**
   * Executes a trade of sugar for spice with another agent.
   *
   * @param other other agent in the trade
   * @param sugarAmount amount of sugar to trade
   * @param spiceAmount amount of spice to receive
   * @throws IllegalArgumentException if trade parameters are invalid
   */
  public void tradeSugarForSpice(Agent other, int sugarAmount, double spiceAmount) {
    validateTradeParams(other, sugarAmount, spiceAmount);

    removeSugar(sugarAmount);
    other.addSugar(sugarAmount);

    int spiceToReceive = (int)spiceAmount;
    other.removeSpice(spiceToReceive);
    addSpice(spiceToReceive);
  }

  /**
   * Executes a trade of spice for sugar with another agent.
   *
   * @param other other agent in the trade
   * @param spiceAmount amount of spice to trade
   * @param sugarAmount amount of sugar to receive
   * @throws IllegalArgumentException if trade parameters are invalid
   */
  public void tradeSpiceForSugar(Agent other, int spiceAmount, double sugarAmount) {
    validateTradeParams(other, sugarAmount, spiceAmount);

    removeSpice(spiceAmount);
    other.addSpice(spiceAmount);

    int sugarToReceive = (int)sugarAmount;
    other.removeSugar(sugarToReceive);
    addSugar(sugarToReceive);
  }

  private void validateTradeParams(Agent other, double amount1, double amount2) {
    if (other == null) {
      throw new IllegalArgumentException("Trade partner cannot be null");
    }
    if (amount1 <= 0 || amount2 <= 0) {
      throw new IllegalArgumentException("Trade amounts must be positive");
    }
  }

  /**
   * Determines if agent can lend resources.
   *
   * @return true if agent has excess resources to lend
   */
  public boolean canLend() {
    return (!fertile || sugar > 2 * initialEndowment);
  }

  /**
   * Determines if agent needs to borrow resources.
   *
   * @return true if agent needs resources
   */
  public boolean needsBorrow() {
    return fertile && sugar < initialEndowment;
  }

  /**
   * Gets amount of resources available for lending.
   *
   * @return amount available to lend
   */
  public int getAvailableToLend() {
    if (!canLend()) return 0;
    return fertile ? sugar - 2 * initialEndowment : sugar - initialEndowment;
  }

  /**
   * Gets amount of resources needed to borrow.
   *
   * @return amount needed to borrow
   */
  public int getNeededToBorrow() {
    if (!needsBorrow()) return 0;
    return initialEndowment - sugar;
  }

  /**
   * Updates immune system in response to disease.
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
   * Removes diseases that the immune system can now handle.
   */
  public void checkAndRemoveDiseases() {
    diseases.removeIf(this::isImmuneToDisease);
  }

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

  // Getters and setters

  public Cell getPosition() {
    return position;
  }

  public int getSugar() {
    return sugar;
  }

  public void setSpice(int spice) {
    if (spice < 0) {
      throw new IllegalArgumentException("Spice cannot be negative");
    }
    this.spice = spice;
  }

  public int getSpice() {
    return spice;
  }

  public void addSpice(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Cannot add negative spice amount");
    }
    this.spice += amount;
  }

  public void removeSpice(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Cannot remove negative spice amount");
    }
    if (amount > spice) {
      throw new IllegalArgumentException("Cannot remove more spice than available");
    }
    this.spice -= amount;
  }

  public int getVision() {
    return vision;
  }

  public int getMetabolism() {
    return metabolism;
  }

  public int getInitialEndowment() {
    return initialEndowment;
  }

  public Sex getSex() {
    return sex;
  }

  public void setSex(Sex sex) {
    this.sex = sex;
  }

  public boolean isFertile() {
    return fertile;
  }

  public void setFertile(boolean fertile) {
    this.fertile = fertile;
  }

  public List<Disease> getDiseases() {
    return new ArrayList<>(diseases);
  }

  public void addDisease(Disease disease) {
    if (disease == null) {
      throw new IllegalArgumentException("Disease cannot be null");
    }
    if (!isImmuneToDisease(disease)) {
      diseases.add(disease);
    }
  }

  public Disease getRandomDisease() {
    if (diseases.isEmpty()) return null;
    return diseases.get(new Random().nextInt(diseases.size()));
  }

  public List<Integer> getImmuneSystem() {
    return new ArrayList<>(immuneSystem);
  }
}
