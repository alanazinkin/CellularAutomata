package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

/**
 * Test class for Disease in the SugarScape simulation. Tests disease creation, mutation, and
 * pattern matching.
 *
 * @author Tatum McKinnis
 */
class DiseaseTest {

  private Disease disease;
  private static final String TEST_PATTERN = "10101010";

  /**
   * Sets up the testing environment before each test. Initializes the disease with a test pattern.
   */
  @BeforeEach
  void setUp() {
    disease = new Disease(TEST_PATTERN);
  }

  /**
   * Verifies that the Disease is created with the correct pattern when the constructor is called
   * with a valid pattern.
   */
  @Test
  void constructor_WithValidPattern_CreatesDisease() {
    assertEquals(TEST_PATTERN, disease.patternToString());
  }

  /**
   * Verifies that an IllegalArgumentException is thrown when the constructor is called with an
   * invalid pattern.
   */
  @Test
  void constructor_WithInvalidPattern_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new Disease("12345"));
  }

  /**
   * Verifies that an IllegalArgumentException is thrown when the constructor is called with a null
   * pattern.
   */
  @Test
  void constructor_WithNullPattern_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new Disease(null));
  }

  /**
   * Verifies that an IllegalArgumentException is thrown when the constructor is called with an
   * empty pattern.
   */
  @Test
  void constructor_WithEmptyPattern_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new Disease(""));
  }

  /**
   * Verifies that the getPattern method returns a defensive copy of the pattern. Modifications to
   * the returned pattern should not affect the original pattern.
   */
  @Test
  void getPattern_WhenCalled_ReturnsDefensiveCopy() {
    List<Integer> pattern = disease.getPattern();
    pattern.clear(); // Modifying the returned list
    assertFalse(disease.getPattern().isEmpty()); // Original should be unchanged
  }

  /**
   * Verifies that the clone method creates a new instance of the Disease with the same pattern. The
   * cloned object should not be the same instance as the original.
   */
  @Test
  void clone_WhenCalled_CreatesCopy() {
    Disease cloned = disease.clone();
    assertNotSame(disease, cloned);
    assertEquals(disease.patternToString(), cloned.patternToString());
  }

  /**
   * Verifies that the matchesPattern method returns true when the target pattern contains the
   * disease's pattern.
   */
  @Test
  void matchesPattern_WithContainedPattern_ReturnsTrue() {
    List<Integer> targetPattern = new ArrayList<>();
    for (char c : "1110101010111".toCharArray()) {
      targetPattern.add(Character.getNumericValue(c));
    }
    assertTrue(disease.matchesPattern(targetPattern));
  }

  /**
   * Verifies that the matchesPattern method returns false when the target pattern does not match
   * the disease's pattern.
   */
  @Test
  void matchesPattern_WithNonMatchingPattern_ReturnsFalse() {
    List<Integer> targetPattern = new ArrayList<>();
    for (char c : "11111111".toCharArray()) {
      targetPattern.add(Character.getNumericValue(c));
    }
    assertFalse(disease.matchesPattern(targetPattern));
  }

  /**
   * Verifies that the matchesPattern method returns false when a null pattern is passed.
   */
  @Test
  void matchesPattern_WithNullPattern_ReturnsFalse() {
    assertFalse(disease.matchesPattern(null));
  }

  /**
   * Verifies that the matchesPattern method returns false when the target pattern is shorter than
   * the disease's pattern.
   */
  @Test
  void matchesPattern_WithShorterPattern_ReturnsFalse() {
    List<Integer> targetPattern = new ArrayList<>();
    for (char c : "1010".toCharArray()) {
      targetPattern.add(Character.getNumericValue(c));
    }
    assertFalse(disease.matchesPattern(targetPattern));
  }

  /**
   * Verifies that the equals method returns true when the disease has the same pattern as another
   * disease.
   */
  @Test
  void equals_WithSamePattern_ReturnsTrue() {
    Disease other = new Disease(TEST_PATTERN);
    assertEquals(disease, other);
  }

  /**
   * Verifies that the equals method returns false when the disease has a different pattern than
   * another disease.
   */
  @Test
  void equals_WithDifferentPattern_ReturnsFalse() {
    Disease other = new Disease("11110000");
    assertNotEquals(disease, other);
  }

  /**
   * Verifies that the hashCode method returns the same hash code for diseases with the same
   * pattern.
   */
  @Test
  void hashCode_WithSamePattern_ReturnsSameHash() {
    Disease other = new Disease(TEST_PATTERN);
    assertEquals(disease.hashCode(), other.hashCode());
  }
}
