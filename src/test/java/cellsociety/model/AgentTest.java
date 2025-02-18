package cellsociety.model;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cellsociety.model.state.SugarScapeState;
import java.util.List;

/**
 * Test class for Agent in the SugarScape simulation.
 * Tests initialization, resource management, trades, loans, and disease handling.
 */
class AgentTest {
  private Agent agent;
  private SugarCell cell;
  private static final int INITIAL_SUGAR = 50;
  private static final int VISION = 3;
  private static final int METABOLISM = 2;

  @BeforeEach
  void setUp() {
    cell = new SugarCell(0, 0, SugarScapeState.EMPTY);
    agent = new Agent(cell, INITIAL_SUGAR, VISION, METABOLISM);
  }

  @Test
  void constructor_WithValidParams_CreatesAgent() {
    assertEquals(INITIAL_SUGAR, agent.getSugar());
    assertEquals(VISION, agent.getVision());
    assertEquals(METABOLISM, agent.getMetabolism());
    assertEquals(cell, agent.getPosition());
  }

  @Test
  void constructor_WithNullCell_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Agent(null, INITIAL_SUGAR, VISION, METABOLISM));
  }

  @Test
  void constructor_WithNegativeSugar_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Agent(cell, -1, VISION, METABOLISM));
  }

  @Test
  void constructor_WithNegativeVision_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Agent(cell, INITIAL_SUGAR, -1, METABOLISM));
  }

  @Test
  void constructor_WithNegativeMetabolism_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Agent(cell, INITIAL_SUGAR, VISION, -1));
  }

  @Test
  void metabolize_WhenCalled_ReducesSugarByMetabolismRate() {
    agent.metabolize();
    assertEquals(INITIAL_SUGAR - METABOLISM, agent.getSugar());
  }

  @Test
  void isDead_WithPositiveSugar_ReturnsFalse() {
    assertFalse(agent.isDead());
  }

  @Test
  void isDead_WithZeroSugar_ReturnsTrue() {
    for (int i = 0; i < INITIAL_SUGAR/METABOLISM; i++) {
      agent.metabolize();
    }
    assertTrue(agent.isDead());
  }

  @Test
  void addSugar_WithPositiveAmount_IncreasesSugar() {
    int addAmount = 10;
    agent.addSugar(addAmount);
    assertEquals(INITIAL_SUGAR + addAmount, agent.getSugar());
  }

  @Test
  void addSugar_WithNegativeAmount_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> agent.addSugar(-10));
  }

  @Test
  void removeSugar_WithValidAmount_DecreasesSugar() {
    int removeAmount = 10;
    agent.removeSugar(removeAmount);
    assertEquals(INITIAL_SUGAR - removeAmount, agent.getSugar());
  }

  @Test
  void removeSugar_WithTooMuchAmount_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> agent.removeSugar(INITIAL_SUGAR + 1));
  }

  @Test
  void getMarginalRateOfSubstitution_WithNoSpice_ReturnsInfinity() {
    assertEquals(Double.POSITIVE_INFINITY, agent.getMarginalRateOfSubstitution());
  }

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

  @Test
  void addDisease_WithNewDisease_AddsToList() {
    Disease disease = new Disease("10101010");
    agent.addDisease(disease);
    assertTrue(agent.getDiseases().contains(disease));
  }

  @Test
  void addDisease_WithNullDisease_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> agent.addDisease(null));
  }

  @Test
  void updateImmuneSystem_WithDisease_ModifiesImmuneSystem() {
    Disease disease = new Disease("10101010");
    List<Integer> originalImmuneSystem = agent.getImmuneSystem();
    agent.addDisease(disease);
    agent.updateImmuneSystem(disease);

    assertNotEquals(originalImmuneSystem, agent.getImmuneSystem());
  }
}