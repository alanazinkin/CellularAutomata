package cellsociety.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses rule strings for cellular automata, such as Conway's Game of Life.
 * Supports rule formats like "B3/S23" and "S23/B3", where:
 * @author Angela Predolac
 */
public class RuleStringParser {
    // Matches B3/S23 format
    private static final Pattern BS_PATTERN = Pattern.compile("B([0-8]+)/S([0-8]+)");
    // Matches S23/B3 format
    private static final Pattern SB_PATTERN = Pattern.compile("S([0-8]+)/B([0-8]+)");

    private final Set<Integer> birthRules = new HashSet<>();
    private final Set<Integer> survivalRules = new HashSet<>();

    /**
     * Constructs a RuleStringParser and parses the provided rule string.
     * If the string is null or empty, the default Conway's Game of Life rules (B3/S23) are applied.
     *
     * @param ruleString The rule string to be parsed (e.g., "B3/S23" or "S23/B3").
     * @throws IllegalArgumentException if the rule string format is invalid.
     */
    public RuleStringParser(String ruleString) {
        if (ruleString == null || ruleString.trim().isEmpty()) {
            setDefaultRules(); // Conway's Game of Life rules
        } else {
            parseRuleString(ruleString.trim().toUpperCase());
        }
    }

    /**
     * Parses the rule string and extracts birth and survival rules.
     *
     * @param ruleString The rule string to parse.
     * @throws IllegalArgumentException if the format is invalid.
     */
    private void parseRuleString(String ruleString) {
        // Try B/S format
        Matcher bsMatcher = BS_PATTERN.matcher(ruleString);
        if (bsMatcher.matches()) {
            addRules(birthRules, bsMatcher.group(1));
            addRules(survivalRules, bsMatcher.group(2));
            return;
        }

        // Try S/B format
        Matcher sbMatcher = SB_PATTERN.matcher(ruleString);
        if (sbMatcher.matches()) {
            addRules(survivalRules, sbMatcher.group(1));
            addRules(birthRules, sbMatcher.group(2));
            return;
        }

        throw new IllegalArgumentException("Invalid rule string format: " + ruleString +
                "\nExpected formats: B3/S23, S23/B3, or 23/3");
    }

    /**
     * Sets the default rules for Conway's Game of Life (B3/S23).
     */
    private void setDefaultRules() {
        // Conway's Game of Life default rules: B3/S23
        birthRules.add(3);
        survivalRules.add(2);
        survivalRules.add(3);
    }

    /**
     * Adds rule values to a given set while ensuring they are within the valid range (0-8).
     *
     * @param ruleSet The set to store rule numbers.
     * @param numbers A string of digits representing rule numbers.
     * @throws IllegalArgumentException if any number is outside the range 0-8.
     */
    private void addRules(Set<Integer> ruleSet, String numbers) {
        // Validate that all numbers are between 0 and 8
        for (char c : numbers.toCharArray()) {
            int num = Character.getNumericValue(c);
            if (num < 0 || num > 8) {
                throw new IllegalArgumentException(
                        "Invalid rule number: " + num + ". Numbers must be between 0 and 8.");
            }
            ruleSet.add(num);
        }
    }

    /**
     * Returns the set of birth rule numbers.
     *
     * @return A set of integers representing birth rules.
     */
    public Set<Integer> getBirthRules() {
        return new HashSet<>(birthRules);
    }

    /**
     * Returns the set of survival rule numbers.
     *
     * @return A set of integers representing survival rules.
     */
    public Set<Integer> getSurvivalRules() {
        return new HashSet<>(survivalRules);
    }

    @Override
    public String toString() {
        return String.format("B%s/S%s",
                birthRules.stream()
                        .sorted()
                        .map(String::valueOf)
                        .reduce("", String::concat),
                survivalRules.stream()
                        .sorted()
                        .map(String::valueOf)
                        .reduce("", String::concat));
    }
}
