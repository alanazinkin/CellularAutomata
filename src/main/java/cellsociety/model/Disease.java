/**
 * This package contains core classes for the simulation model in the Cell Society project.
 * <p>
 * The classes in this package define the behavior of various simulation components,
 * including cell states, environmental conditions, and interaction rules.
 * </p>
 */
package cellsociety.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a disease in the SugarScape simulation.
 * <p>
 * A disease is modeled as a binary pattern that can interact with agents' immune systems.
 * The disease can:
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
   * Creates a new disease with a random binary pattern.
   * <p>
   * Initializes the disease with a random binary pattern of a default length.
   * </p>
   */
  public Disease() {
    this.pattern = generateRandomPattern(DEFAULT_PATTERN_LENGTH);
  }

  /**
   * Creates a new disease with a specified binary pattern.
   * <p>
   * The provided pattern string must consist only of 0s and 1s.
   * </p>
   *
   * @param patternString string representation of the binary pattern
   * @throws IllegalArgumentException if the pattern contains invalid characters
   */
  public Disease(String patternString) {
    validatePatternString(patternString);
    this.pattern = new ArrayList<>();
    for (char c : patternString.toCharArray()) {
      pattern.add(Character.getNumericValue(c));
    }
  }

  /**
   * Validates that the given pattern string consists only of 0s and 1s.
   *
   * @param patternString the binary pattern string to validate
   * @throws IllegalArgumentException if the pattern contains invalid characters or is empty
   */
  private void validatePatternString(String patternString) {
    if (patternString == null || patternString.isEmpty()) {
      throw new IllegalArgumentException("Pattern string cannot be null or empty");
    }
    if (!patternString.matches("[01]+")) {
      throw new IllegalArgumentException("Pattern string must contain only 0s and 1s");
    }
  }

  /**
   * Generates a random binary pattern of the specified length.
   *
   * @param length the length of the binary pattern
   * @return a list representing the random binary pattern
   */
  private List<Integer> generateRandomPattern(int length) {
    List<Integer> newPattern = new ArrayList<>(length);
    for (int i = 0; i < length; i++) {
      newPattern.add(random.nextInt(2));
    }
    return newPattern;
  }

  /**
   * Gets a copy of the disease's binary pattern.
   *
   * @return a list representing the disease's binary pattern
   */
  public List<Integer> getPattern() {
    return new ArrayList<>(pattern);
  }

  /**
   * Creates a clone of the disease with potential mutations.
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
   * The match is successful if this disease's pattern appears as a continuous subsequence
   * in the target pattern.
   * </p>
   *
   * @param targetPattern the pattern to check for matches
   * @return {@code true} if this pattern is found within the target pattern, {@code false} otherwise
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
   * @return a string of 0s and 1s representing the pattern
   */
  public String patternToString() {
    return pattern.stream()
        .map(String::valueOf)
        .reduce("", String::concat);
  }

  /**
   * Returns a string representation of the disease.
   *
   * @return a formatted string representing the disease's pattern
   */
  @Override
  public String toString() {
    return String.format("Disease[pattern=%s]", patternToString());
  }

  /**
   * Checks if this disease is equal to another object.
   * <p>
   * Two diseases are considered equal if their binary patterns are identical.
   * </p>
   *
   * @param obj the object to compare
   * @return {@code true} if the objects are equal, {@code false} otherwise
   */
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

  /**
   * Computes the hash code for this disease based on its pattern.
   *
   * @return the hash code value for this disease
   */
  @Override
  public int hashCode() {
    return pattern.hashCode();
  }
}
