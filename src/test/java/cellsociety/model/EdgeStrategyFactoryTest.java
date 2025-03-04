package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link EdgeStrategyFactory}.
 * <p>
 * This class verifies the behavior of the factory for creating edge strategies.
 * </p>
 *
 * @author Tatum McKinnis
 */
class EdgeStrategyFactoryTest {

  /**
   * Tests creating a bounded edge strategy.
   * <p>
   * Verifies that the factory correctly creates a bounded edge strategy.
   * </p>
   */
  @Test
  void createEdgeStrategy_BoundedType_ReturnsBoundedStrategy() {
    EdgeStrategy strategy = EdgeStrategyFactory.createEdgeStrategy("BOUNDED");
    assertTrue(strategy instanceof BoundedEdge);
    assertEquals("BOUNDED", strategy.getType());
  }

  /**
   * Tests creating a toroidal edge strategy.
   * <p>
   * Verifies that the factory correctly creates a toroidal edge strategy.
   * </p>
   */
  @Test
  void createEdgeStrategy_ToroidalType_ReturnsToroidalStrategy() {
    EdgeStrategy strategy = EdgeStrategyFactory.createEdgeStrategy("TOROIDAL");
    assertTrue(strategy instanceof ToroidalEdge);
    assertEquals("TOROIDAL", strategy.getType());
  }

  /**
   * Tests creating a mirror edge strategy.
   * <p>
   * Verifies that the factory correctly creates a mirror edge strategy.
   * </p>
   */
  @Test
  void createEdgeStrategy_MirrorType_ReturnsMirrorStrategy() {
    EdgeStrategy strategy = EdgeStrategyFactory.createEdgeStrategy("MIRROR");
    assertTrue(strategy instanceof MirrorEdge);
    assertEquals("MIRROR", strategy.getType());
  }

  /**
   * Tests creating an infinite edge strategy.
   * <p>
   * Verifies that the factory correctly creates an infinite edge strategy.
   * </p>
   */
  @Test
  void createEdgeStrategy_InfiniteType_ReturnsInfiniteStrategy() {
    EdgeStrategy strategy = EdgeStrategyFactory.createEdgeStrategy("INFINITE");
    assertTrue(strategy instanceof InfiniteEdge);
    assertEquals("INFINITE", strategy.getType());
  }

  /**
   * Tests creating a strategy with an unknown type.
   * <p>
   * Verifies that the factory throws an exception for unknown types.
   * </p>
   */
  @Test
  void createEdgeStrategy_UnknownType_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> EdgeStrategyFactory.createEdgeStrategy("UNKNOWN"));
  }

  /**
   * Tests case insensitivity in type strings.
   * <p>
   * Verifies that the factory handles type strings regardless of case.
   * </p>
   */
  @Test
  void createEdgeStrategy_MixedCaseType_CreateCorrectStrategy() {
    EdgeStrategy strategy = EdgeStrategyFactory.createEdgeStrategy("tOrOiDaL");
    assertTrue(strategy instanceof ToroidalEdge);
    assertEquals("TOROIDAL", strategy.getType());
  }
}