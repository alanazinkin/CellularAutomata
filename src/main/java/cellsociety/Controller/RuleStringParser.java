package cellsociety.Controller;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleStringParser {
    // Matches B3/S23 format
    private static final Pattern BS_PATTERN = Pattern.compile("B([0-8]+)/S([0-8]+)");
    // Matches S23/B3 format
    private static final Pattern SB_PATTERN = Pattern.compile("S([0-8]+)/B([0-8]+)");

    private final Set<Integer> birthRules = new HashSet<>();
    private final Set<Integer> survivalRules = new HashSet<>();

    public RuleStringParser(String ruleString) {
        if (ruleString == null || ruleString.trim().isEmpty()) {
            setDefaultRules(); // Conway's Game of Life rules
        } else {
            parseRuleString(ruleString.trim().toUpperCase());
        }
    }

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

    private void setDefaultRules() {
        // Conway's Game of Life default rules: B3/S23
        birthRules.add(3);
        survivalRules.add(2);
        survivalRules.add(3);
    }

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

    public Set<Integer> getBirthRules() {
        return new HashSet<>(birthRules);
    }

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
