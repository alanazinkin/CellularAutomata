package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link NeighborhoodFactory}.
 * <p>
 * This class verifies the behavior of the factory for creating neighborhood strategies.
 * </p>
 *
 * @author Tatum McKinnis
 */
class NeighborhoodFactoryTest {

  /**
   * Tests creating a Moore neighborhood strategy.
   * <p>
   * Verifies that the factory correctly creates a Moore neighborhood strategy.
   * </p>
   */
  @Test
  void createNeighborhoodStrategy_MooreType_ReturnsMooreStrategy() {
    NeighborhoodStrategy strategy = NeighborhoodFactory.createNeighborhoodStrategy("MOORE");
    assertTrue(strategy instanceof MooreNeighborhood);
    assertEquals("MOORE", strategy.getType());
  }

  /**
   * Tests creating a Von Neumann neighborhood strategy.
   * <p>
   * Verifies that the factory correctly creates a Von Neumann neighborhood strategy.
   * </p>
   */
  @Test
  void createNeighborhoodStrategy_VonNeumannType_ReturnsVonNeumannStrategy() {
    NeighborhoodStrategy strategy = NeighborhoodFactory.createNeighborhoodStrategy("VON_NEUMANN");
    assertTrue(strategy instanceof VonNeumannNeighborhood);
    assertEquals("VON_NEUMANN", strategy.getType());
  }

  /**
   * Tests creating an extended Moore neighborhood strategy.
   * <p>
   * Verifies that the factory correctly creates an extended Moore neighborhood strategy with the
   * specified radius.
   * </p>
   */
  @Test
  void createNeighborhoodStrategy_ExtendedMooreType_ReturnsExtendedMooreStrategy() {
    NeighborhoodStrategy strategy = NeighborhoodFactory.createNeighborhoodStrategy(
        "EXTENDED_MOORE_2");
    assertTrue(strategy instanceof ExtendedMooreNeighborhood);
    assertEquals("EXTENDED_MOORE_2", strategy.getType());
  }

  /**
   * Tests creating a strategy with an unknown type.
   * <p>
   * Verifies that the factory throws an exception for unknown types.
   * </p>
   */
  @Test
  void createNeighborhoodStrategy_UnknownType_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> NeighborhoodFactory.createNeighborhoodStrategy("UNKNOWN"));
  }

  /**
   * Tests creating multiple neighborhood strategies.
   * <p>
   * Verifies that the factory correctly creates a multiple neighborhoods strategy.
   * </p>
   */
  @Test
  void createMultipleNeighborhoods_VariousTypes_ReturnsMultipleStrategy() {
    String[] types = {"MOORE", "VON_NEUMANN"};
    NeighborhoodStrategy strategy = NeighborhoodFactory.createMultipleNeighborhoods(types);

    assertTrue(strategy instanceof MultipleNeighborhoods);
    assertEquals("MULTIPLE", strategy.getType());
  }

  /**
   * Tests creating multiple neighborhood strategies with an empty array.
   * <p>
   * Verifies that the factory correctly handles an empty array of types.
   * </p>
   */
  @Test
  void createMultipleNeighborhoods_EmptyArray_ReturnsMultipleStrategyWithNoSubstrategies() {
    String[] types = {};
    NeighborhoodStrategy strategy = NeighborhoodFactory.createMultipleNeighborhoods(types);

    assertTrue(strategy instanceof MultipleNeighborhoods);
    assertEquals("MULTIPLE", strategy.getType());

    List<int[]> coords = strategy.getNeighborCoordinates(3, 3);
    assertEquals(0, coords.size());
  }
}