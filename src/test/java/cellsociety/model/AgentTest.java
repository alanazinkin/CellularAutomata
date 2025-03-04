package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cellsociety.model.state.SugarScapeState;
import java.util.List;

/**
 * Test class for Agent in the SugarScape simulation. Tests initialization, resource management,
 * trades, loans, and disease handling.
 *
 * @author Tatum McKinnis
 */
class AgentTest {

  private Agent agent;
  private SugarCell cell;
  private static final int INITIAL_SUGAR = 50;
  private static final int VISION = 3;
  private static final int METABOLISM = 2;

  /**
   * Sets up the testing environment before each test. Initializes the agent and a sugar cell for
   * testing purposes.
   */
  @BeforeEach
  void setUp() {
    cell = new SugarCell(0, 0, SugarScapeState.EMPTY);
    agent = new Agent(cell, INITIAL_SUGAR, VISION, METABOLISM);
  }

  /**
   * Verifies that the Agent is created with the correct parameters (sugar, vision, metabolism, and
   * position).
   */
  @Test
  void constructor_WithValidParams_CreatesAgent() {
    assertEquals(INITIAL_SUGAR, agent.getSugar());
    assertEquals(VISION, agent.getVision());
    assertEquals(METABOLISM, agent.getMetabolism());
    assertEquals(cell, agent.getPosition());
  }

  /**
   * Verifies that an IllegalArgumentException is thrown when the cell parameter is null.
   */
  @Test
  void constructor_WithNullCell_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Agent(null, INITIAL_SUGAR, VISION, METABOLISM));
  }

  /**
   * Verifies that an IllegalArgumentException is thrown when the sugar amount is negative.
   */
  @Test
  void constructor_WithNegativeSugar_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Agent(cell, -1, VISION, METABOLISM));
  }

  /**
   * Verifies that an IllegalArgumentException is thrown when the vision is negative.
   */
  @Test
  void constructor_WithNegativeVision_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Agent(cell, INITIAL_SUGAR, -1, METABOLISM));
  }

  /**
   * Verifies that an IllegalArgumentException is thrown when the metabolism is negative.
   */
  @Test
  void constructor_WithNegativeMetabolism_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Agent(cell, INITIAL_SUGAR, VISION, -1));
  }

  /**
   * Verifies that the agent's sugar is reduced by the metabolism rate when the metabolize method is
   * called.
   */
  @Test
  void metabolize_WhenCalled_ReducesSugarByMetabolismRate() {
    agent.metabolize();
    assertEquals(INITIAL_SUGAR - METABOLISM, agent.getSugar());
  }

  /**
   * Verifies that the agent is not dead when it has positive sugar.
   */
  @Test
  void isDead_WithPositiveSugar_ReturnsFalse() {
    assertFalse(agent.isDead());
  }

  /**
   * Verifies that the agent is dead when its sugar reaches zero. The agent's sugar is reduced to
   * zero through metabolism.
   */
  @Test
  void isDead_WithZeroSugar_ReturnsTrue() {
    for (int i = 0; i < INITIAL_SUGAR / METABOLISM; i++) {
      agent.metabolize();
    }
    assertTrue(agent.isDead());
  }

  /**
   * Verifies that the agent's sugar is increased by the specified amount when addSugar is called
   * with a positive value.
   */
  @Test
  void addSugar_WithPositiveAmount_IncreasesSugar() {
    int addAmount = 10;
    agent.addSugar(addAmount);
    assertEquals(INITIAL_SUGAR + addAmount, agent.getSugar());
  }

  /**
   * Verifies that an IllegalArgumentException is thrown when a negative amount of sugar is added.
   */
  @Test
  void addSugar_WithNegativeAmount_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> agent.addSugar(-10));
  }

  /**
   * Verifies that the agent's sugar is decreased by the specified amount when removeSugar is called
   * with a valid amount.
   */
  @Test
  void removeSugar_WithValidAmount_DecreasesSugar() {
    int removeAmount = 10;
    agent.removeSugar(removeAmount);
    assertEquals(INITIAL_SUGAR - removeAmount, agent.getSugar());
  }

  /**
   * Verifies that an IllegalArgumentException is thrown when the agent tries to remove more sugar
   * than it has.
   */
  @Test
  void removeSugar_WithTooMuchAmount_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> agent.removeSugar(INITIAL_SUGAR + 1));
  }

  /**
   * Verifies that the marginal rate of substitution returns infinity when no spice is available.
   */
  @Test
  void getMarginalRateOfSubstitution_WithNoSpice_ReturnsInfinity() {
    assertEquals(Double.POSITIVE_INFINITY, agent.getMarginalRateOfSubstitution());
  }

  /**
   * Verifies that the agent and another agent exchange resources correctly when trading sugar for
   * spice.
   */
  @Test
  void tradeSugarForSpice_WithValidTrade_ExchangesResources() {
    Agent other = new Agent(new SugarCell(1, 0, SugarScapeState.EMPTY),
        INITIAL_SUGAR, VISION, METABOLISM);
    other.setSpice(20);

    agent.tradeSugarForSpice(other, 10, 5);

    assertEquals(INITIAL_SUGAR - 10, agent.getSugar());
    assertEquals(INITIAL_SUGAR + 10, other.getSugar());
    assertEquals(5, agent.getSpice());
    assertEquals(15, other.getSpice());
  }

  /**
   * Verifies that a new disease is added to the agent's list of diseases.
   */
  @Test
  void addDisease_WithNewDisease_AddsToList() {
    Disease disease = new Disease("10101010");
    agent.addDisease(disease);
    assertTrue(agent.getDiseases().contains(disease));
  }

  /**
   * Verifies that an IllegalArgumentException is thrown when trying to add a null disease.
   */
  @Test
  void addDisease_WithNullDisease_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> agent.addDisease(null));
  }

  /**
   * Verifies that the agent's immune system is updated after adding and processing a disease.
   */
  @Test
  void updateImmuneSystem_WithDisease_ModifiesImmuneSystem() {
    Disease disease = new Disease("10101010");
    List<Integer> originalImmuneSystem = agent.getImmuneSystem();
    agent.addDisease(disease);
    agent.updateImmuneSystem(disease);

    assertNotEquals(originalImmuneSystem, agent.getImmuneSystem());
  }
}
