package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.model.state.SugarScapeState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Random;

/**
 * Test class for RulesOperations.
 * Tests reproduction and trading functionality between agents.
 */
public class RulesOperationsTest {
  private Agent agent1;
  private Agent agent2;
  private Cell cell;
  private Random random;
  private StateInterface defaultState;

  @BeforeEach
  void setUp() {
    defaultState = SugarScapeState.EMPTY;
    cell = new Cell(defaultState);
    random = new Random(42);
    agent1 = new Agent(cell, 20, 2, 2);
    agent2 = new Agent(cell, 20, 2, 2);
    agent1.setSex(Sex.MALE);
    agent2.setSex(Sex.FEMALE);
  }

  /**
   * Tests reproduction conditions when all criteria are met.
   * Expected behavior: Returns true.
   */
  @Test
  void canReproduce_AllConditionsMet_ReturnsTrue() {
    agent1.setFertile(true);
    agent2.setFertile(true);

    assertTrue(RulesOperations.canReproduce(agent1, agent2));
  }

  /**
   * Tests reproduction conditions when agents have insufficient sugar.
   * Expected behavior: Returns false.
   */
  @Test
  void canReproduce_InsufficientSugar_ReturnsFalse() {
    agent1.setFertile(true);
    agent2.setFertile(true);
    agent1.removeSugar(15);

    assertFalse(RulesOperations.canReproduce(agent1, agent2));
  }

  /**
   * Tests child creation with valid parent attributes.
   * Expected behavior: Creates child with averaged attributes.
   */
  @Test
  void reproduce_ValidParents_CreatesChildWithCorrectAttributes() {
    Agent child = RulesOperations.reproduce(agent1, agent2, cell, random);

    assertEquals((agent1.getVision() + agent2.getVision()) / 2, child.getVision());
    assertEquals((agent1.getMetabolism() + agent2.getMetabolism()) / 2,
        child.getMetabolism());
    assertFalse(child.isFertile());
  }

  /**
   * Tests reproduction with null parent.
   * Expected behavior: Throws IllegalArgumentException.
   */
  /**
   * Tests reproduction with null parent.
   * Expected behavior: Throws NullPointerException.
   */
  @Test
  void reproduce_NullParent_ThrowsException() {
    assertThrows(NullPointerException.class, () ->
        RulesOperations.reproduce(null, agent2, cell, random)
    );
  }

  /**
   * Tests trading conditions with valid MRS differences.
   * Expected behavior: Returns true.
   */
  @Test
  void canTrade_ValidMRSDifference_ReturnsTrue() {
    agent1.setSpice(10);
    agent2.setSpice(5);

    assertTrue(RulesOperations.canTrade(agent1, agent2));
  }

  /**
   * Tests trade execution with valid resources.
   * Expected behavior: Correctly transfers resources.
   */
  @Test
  void executeTrade_ValidResources_TransfersResourcesCorrectly() {
    agent1.setSpice(10);
    agent2.setSpice(5);
    int initialSugar1 = agent1.getSugar();
    int initialSpice1 = agent1.getSpice();

    RulesOperations.executeTrade(agent1, agent2);

    assertNotEquals(initialSugar1, agent1.getSugar());
    assertNotEquals(initialSpice1, agent1.getSpice());
  }
}