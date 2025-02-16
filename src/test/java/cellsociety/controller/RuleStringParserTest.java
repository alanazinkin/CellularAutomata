package cellsociety.controller;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RuleStringParserTest {

    @Test
    void testBSFormat() {
        RuleStringParser parser = new RuleStringParser("B3/S23");
        assertTrue(parser.getBirthRules().contains(3));
        assertTrue(parser.getSurvivalRules().contains(2));
        assertTrue(parser.getSurvivalRules().contains(3));
        assertEquals(1, parser.getBirthRules().size());
        assertEquals(2, parser.getSurvivalRules().size());
    }

    @Test
    void testSBFormat() {
        RuleStringParser parser = new RuleStringParser("S23/B3");
        assertTrue(parser.getBirthRules().contains(3));
        assertTrue(parser.getSurvivalRules().contains(2));
        assertTrue(parser.getSurvivalRules().contains(3));
        assertEquals(1, parser.getBirthRules().size());
        assertEquals(2, parser.getSurvivalRules().size());
    }

    @Test
    void testEmptyRuleString() {
        RuleStringParser parser = new RuleStringParser("");
        // Should default to Conway's Game of Life rules
        assertTrue(parser.getBirthRules().contains(3));
        assertTrue(parser.getSurvivalRules().containsAll(Set.of(2, 3)));
        assertEquals(1, parser.getBirthRules().size());
        assertEquals(2, parser.getSurvivalRules().size());
    }

    @Test
    void testInvalidRuleString() {
        assertThrows(IllegalArgumentException.class, () -> new RuleStringParser("B39/S23")); // 9 is invalid
        assertThrows(IllegalArgumentException.class, () -> new RuleStringParser("B3S23")); // Missing separator
        assertThrows(IllegalArgumentException.class, () -> new RuleStringParser("Hello")); // Invalid format
    }

}