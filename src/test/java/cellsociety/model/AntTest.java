package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Ant} functionality.
 * <p>
 * Verifies core ant behavior including movement, orientation changes, and food carrying state.
 * </p>
 */
class AntTest {
  private static final int TEST_ROW = 5;
  private static final int TEST_COL = 5;
  private static final Orientation TEST_ORIENTATION = Orientation.N;
  private static final boolean HAS_FOOD = true;

  /**
   * Tests ant initialization with valid parameters.
   * <p>
   * Verifies that all constructor parameters are correctly stored and accessible via getters.
   * </p>
   */
  @Test
  void testAntInitializationWithValidParameters() {
    Ant ant = new Ant(TEST_ROW, TEST_COL, TEST_ORIENTATION, HAS_FOOD);

    assertEquals(TEST_ROW, ant.getRow());
    assertEquals(TEST_COL, ant.getCol());
    assertEquals(TEST_ORIENTATION, ant.getOrientation());
    assertTrue(ant.hasFood());
  }

  /**
   * Tests position update functionality.
   * <p>
   * Verifies that row and column setters correctly modify the ant's position coordinates.
   * </p>
   */
  @Test
  void testPositionUpdateChangesCoordinates() {
    Ant ant = new Ant(TEST_ROW, TEST_COL, TEST_ORIENTATION, false);
    int newRow = TEST_ROW + 1;
    int newCol = TEST_COL - 1;

    ant.setRow(newRow);
    ant.setCol(newCol);

    assertEquals(newRow, ant.getRow());
    assertEquals(newCol, ant.getCol());
  }

  /**
   * Tests orientation update functionality.
   * <p>
   * Verifies that the orientation setter correctly modifies the ant's facing direction.
   * </p>
   */
  @Test
  void testOrientationUpdateChangesDirection() {
    Ant ant = new Ant(TEST_ROW, TEST_COL, TEST_ORIENTATION, false);
    Orientation newOrientation = Orientation.SE;

    ant.setOrientation(newOrientation);

    assertEquals(newOrientation, ant.getOrientation());
  }

  /**
   * Tests food state transitions.
   * <p>
   * Verifies that the hasFood flag can be toggled between true and false states.
   * </p>
   */
  @Test
  void testFoodStateChangeUpdatesFlag() {
    Ant ant = new Ant(TEST_ROW, TEST_COL, TEST_ORIENTATION, false);

    ant.setHasFood(true);
    assertTrue(ant.hasFood());

    ant.setHasFood(false);
    assertFalse(ant.hasFood());
  }

  /**
   * Tests null orientation handling in constructor.
   * <p>
   * Verifies that passing a null orientation throws NullPointerException.
   * </p>
   */
  @Test
  void testNullOrientationThrowsException() {
    assertThrows(NullPointerException.class, () ->
        new Ant(TEST_ROW, TEST_COL, null, HAS_FOOD));
  }

  /**
   * Tests negative position assignment handling.
   * <p>
   * Verifies that negative coordinates don't throw exceptions, as grid boundary enforcement
   * is handled separately in the simulation.
   * </p>
   */
  @Test
  void testNegativePositionAssignmentDoesNotThrow() {
    Ant ant = new Ant(TEST_ROW, TEST_COL, TEST_ORIENTATION, false);

    assertDoesNotThrow(() -> {
      ant.setRow(-1);
      ant.setCol(-1);
    });
  }
}