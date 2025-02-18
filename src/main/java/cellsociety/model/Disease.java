package cellsociety.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a disease in the SugarScape simulation.
 * <p>
 * A disease is represented as a binary pattern that can interact with agents' immune systems. The
 * disease can:
 * <ul>
 *   <li>Store and manipulate its genetic pattern</li>
 *   <li>Be transmitted between agents</li>
 *   <li>Be matched against immune system patterns</li>
 *   <li>Create copies of itself with potential mutations</li>
 * </ul>
 * The pattern is represented as a list of binary digits (0s and 1s).
 * </p>
 *
 * @author Tatum McKinnis
 */
public class Disease implements Cloneable {

  private List<Integer> pattern;
  private static final int DEFAULT_PATTERN_LENGTH = 8;
  private static final double MUTATION_RATE = 0.1;
  private static final Random random = new Random();

  /**
   * Creates a new disease with a random pattern.
   * <p>
   * Initializes the disease with a random binary pattern of default length.
   * </p>
   */
  public Disease() {
    this.pattern = generateRandomPattern(DEFAULT_PATTERN_LENGTH);
  }

  /**
   * Creates a new disease with a specified pattern string.
   * <p>
   * The pattern string should consist of only 0s and 1s.
   * </p>
   *
   * @param patternString string representation of the binary pattern
   * @throws IllegalArgumentException if pattern contains invalid characters
   */
  public Disease(String patternString) {
    validatePatternString(patternString);
    this.pattern = new ArrayList<>();
    for (char c : patternString.toCharArray()) {
      pattern.add(Character.getNumericValue(c));
    }
  }

  private void validatePatternString(String patternString) {
    if (patternString == null || patternString.isEmpty()) {
      throw new IllegalArgumentException("Pattern string cannot be null or empty");
    }
    if (!patternString.matches("[01]+")) {
      throw new IllegalArgumentException("Pattern string must contain only 0s and 1s");
    }
  }

  private List<Integer> generateRandomPattern(int length) {
    List<Integer> newPattern = new ArrayList<>(length);
    for (int i = 0; i < length; i++) {
      newPattern.add(random.nextInt(2));
    }
    return newPattern;
  }

  /**
   * Gets a copy of the disease's pattern.
   *
   * @return list representing the disease's binary pattern
   */
  public List<Integer> getPattern() {
    return new ArrayList<>(pattern);
  }

  /**
   * Creates a clone of the disease with possible mutations.
   * <p>
   * Each bit in the pattern has a chance to mutate based on the mutation rate.
   * </p>
   *
   * @return a new Disease instance with a potentially mutated pattern
   */
  @Override
  public Disease clone() {
    Disease clone = new Disease();
    clone.pattern = new ArrayList<>(this.pattern);

    // Apply mutations
    for (int i = 0; i < clone.pattern.size(); i++) {
      if (random.nextDouble() < MUTATION_RATE) {
        // Flip the bit
        clone.pattern.set(i, 1 - clone.pattern.get(i));
      }
    }

    return clone;
  }

  /**
   * Checks if this disease's pattern matches part of another pattern.
   * <p>
   * The match is considered successful if this disease's pattern appears as a continuous
   * subsequence in the target pattern.
   * </p>
   *
   * @param targetPattern pattern to check for matches
   * @return true if this pattern is found within the target pattern
   */
  public boolean matchesPattern(List<Integer> targetPattern) {
    if (targetPattern == null || targetPattern.size() < pattern.size()) {
      return false;
    }

    String diseaseStr = patternToString();
    String targetStr = targetPattern.stream()
        .map(String::valueOf)
        .reduce("", String::concat);

    return targetStr.contains(diseaseStr);
  }

  /**
   * Converts the disease's pattern to a string representation.
   *
   * @return string of 0s and 1s representing the pattern
   */
  public String patternToString() {
    return pattern.stream()
        .map(String::valueOf)
        .reduce("", String::concat);
  }

  @Override
  public String toString() {
    return String.format("Disease[pattern=%s]", patternToString());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Disease other = (Disease) obj;
    return pattern.equals(other.pattern);
  }

  @Override
  public int hashCode() {
    return pattern.hashCode();
  }
}
