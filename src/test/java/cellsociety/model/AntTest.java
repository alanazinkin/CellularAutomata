package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Ant} functionality.
 * <p>
 * This class verifies core ant behavior including initialization, movement, orientation changes,
 * and food state transitions. Naming convention: * [UnitOfWork_StateUnderTest_ExpectedBehavior]
 * </p>
 *
 * @author Tatum McKinnis
 */
class AntTest {

  private static final int TEST_ROW = 5;
  private static final int TEST_COL = 5;
  private static final Orientation TEST_ORIENTATION = Orientation.N;
  private static final boolean HAS_FOOD = true;

  /**
   * Tests constructor with valid parameters.
   * <p>
   * Verifies that an {@link Ant} is correctly initialized with the provided parameters.
   * </p>
   */
  @Test
  void Constructor_ValidParameters_AntInitializedCorrectly() {
    Ant ant = new Ant(TEST_ROW, TEST_COL, TEST_ORIENTATION, HAS_FOOD);
    assertEquals(TEST_ROW, ant.getRow());
    assertEquals(TEST_COL, ant.getCol());
    assertEquals(TEST_ORIENTATION, ant.getOrientation());
    assertTrue(ant.hasFood());
  }

  /**
   * Tests setting position with valid coordinates.
   * <p>
   * Verifies that updating the row and column via setters correctly changes the ant's position.
   * </p>
   */
  @Test
  void setPosition_ValidCoordinates_PositionUpdatedCorrectly() {
    Ant ant = new Ant(TEST_ROW, TEST_COL, TEST_ORIENTATION, false);
    int newRow = TEST_ROW + 1;
    int newCol = TEST_COL - 1;
    ant.setRow(newRow);
    ant.setCol(newCol);
    assertEquals(newRow, ant.getRow());
    assertEquals(newCol, ant.getCol());
  }

  /**
   * Tests setting orientation with a valid new orientation.
   * <p>
   * Verifies that {@link Ant#setOrientation(Orientation)} correctly updates the ant's orientation.
   * </p>
   */
  @Test
  void setOrientation_ValidOrientation_OrientationUpdatedCorrectly() {
    Ant ant = new Ant(TEST_ROW, TEST_COL, TEST_ORIENTATION, false);
    Orientation newOrientation = Orientation.SE;
    ant.setOrientation(newOrientation);
    assertEquals(newOrientation, ant.getOrientation());
  }

  /**
   * Tests toggling the food state.
   * <p>
   * Verifies that the food state flag can be updated from false to true and vice versa.
   * </p>
   */
  @Test
  void setFoodState_ValidToggle_FlagUpdatedCorrectly() {
    Ant ant = new Ant(TEST_ROW, TEST_COL, TEST_ORIENTATION, false);
    ant.setHasFood(true);
    assertTrue(ant.hasFood());
    ant.setHasFood(false);
    assertFalse(ant.hasFood());
  }

  /**
   * Tests constructor behavior with a null orientation.
   * <p>
   * Verifies that passing a null orientation to the constructor throws a
   * {@link NullPointerException}.
   * </p>
   */
  @Test
  void Constructor_NullOrientation_ThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () ->
        new Ant(TEST_ROW, TEST_COL, null, HAS_FOOD));
  }

  /**
   * Tests setting negative position coordinates.
   * <p>
   * Verifies that updating the ant's position with negative values does not throw an exception, as
   * boundary enforcement is managed elsewhere.
   * </p>
   */
  @Test
  void setPosition_NegativeCoordinates_NoExceptionThrown() {
    Ant ant = new Ant(TEST_ROW, TEST_COL, TEST_ORIENTATION, false);
    assertDoesNotThrow(() -> {
      ant.setRow(-1);
      ant.setCol(-1);
    });
  }
}
