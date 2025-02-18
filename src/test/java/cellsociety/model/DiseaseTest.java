package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

/**
 * Test class for Disease in the SugarScape simulation.
 * Tests disease creation, mutation, and pattern matching.
 */
class DiseaseTest {
  private Disease disease;
  private static final String TEST_PATTERN = "10101010";

  @BeforeEach
  void setUp() {
    disease = new Disease(TEST_PATTERN);
  }

  @Test
  void constructor_WithValidPattern_CreatesDisease() {
    assertEquals(TEST_PATTERN, disease.patternToString());
  }

  @Test
  void constructor_WithInvalidPattern_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new Disease("12345"));
  }

  @Test
  void constructor_WithNullPattern_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new Disease(null));
  }

  @Test
  void constructor_WithEmptyPattern_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new Disease(""));
  }

  @Test
  void getPattern_WhenCalled_ReturnsDefensiveCopy() {
    List<Integer> pattern = disease.getPattern();
    pattern.clear(); // Modifying the returned list
    assertFalse(disease.getPattern().isEmpty()); // Original should be unchanged
  }

  @Test
  void clone_WhenCalled_CreatesCopy() {
    Disease cloned = disease.clone();
    assertNotSame(disease, cloned);
    assertEquals(disease.patternToString(), cloned.patternToString());
  }

  @Test
  void matchesPattern_WithContainedPattern_ReturnsTrue() {
    List<Integer> targetPattern = new ArrayList<>();
    for (char c : "1110101010111".toCharArray()) {
      targetPattern.add(Character.getNumericValue(c));
    }
    assertTrue(disease.matchesPattern(targetPattern));
  }

  @Test
  void matchesPattern_WithNonMatchingPattern_ReturnsFalse() {
    List<Integer> targetPattern = new ArrayList<>();
    for (char c : "11111111".toCharArray()) {
      targetPattern.add(Character.getNumericValue(c));
    }
    assertFalse(disease.matchesPattern(targetPattern));
  }

  @Test
  void matchesPattern_WithNullPattern_ReturnsFalse() {
    assertFalse(disease.matchesPattern(null));
  }

  @Test
  void matchesPattern_WithShorterPattern_ReturnsFalse() {
    List<Integer> targetPattern = new ArrayList<>();
    for (char c : "1010".toCharArray()) {
      targetPattern.add(Character.getNumericValue(c));
    }
    assertFalse(disease.matchesPattern(targetPattern));
  }

  @Test
  void equals_WithSamePattern_ReturnsTrue() {
    Disease other = new Disease(TEST_PATTERN);
    assertEquals(disease, other);
  }

  @Test
  void equals_WithDifferentPattern_ReturnsFalse() {
    Disease other = new Disease("11110000");
    assertNotEquals(disease, other);
  }

  @Test
  void hashCode_WithSamePattern_ReturnsSameHash() {
    Disease other = new Disease(TEST_PATTERN);
    assertEquals(disease.hashCode(), other.hashCode());
  }
}
