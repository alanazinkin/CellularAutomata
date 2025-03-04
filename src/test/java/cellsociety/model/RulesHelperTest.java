package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.model.state.SugarScapeState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Random;

/**
 * Test class for RulesHelper utility class. Tests the helper methods used in the SugarScape
 * simulation.
 *
 * @author Tatum McKinnis
 */
public class RulesHelperTest {

  private Grid grid;
  private Cell cell;
  private Random random;
  private StateInterface defaultState;

  @BeforeEach
  void setUp() {
    defaultState = SugarScapeState.EMPTY;
    grid = new Grid(5, 5, defaultState);
    cell = new Cell(defaultState);
    random = new Random(42); // Fixed seed for reproducible tests
  }

  /**
   * Tests getting coordinates of an existing cell in the grid. Expected behavior: Returns correct
   * coordinates.
   */
  @Test
  void getCellCoordinates_ExistingCell_ReturnsCorrectCoordinates() {
    grid.setCellAt(2, 3, cell);
    int[] coordinates = RulesHelper.getCellCoordinates(cell, grid);

    assertArrayEquals(new int[]{2, 3}, coordinates);
  }

  /**
   * Tests getting coordinates of a cell not in the grid. Expected behavior: Returns {-1, -1}.
   */
  @Test
  void getCellCoordinates_NonexistentCell_ReturnsNegativeCoordinates() {
    Cell nonexistentCell = new Cell(defaultState);
    int[] coordinates = RulesHelper.getCellCoordinates(nonexistentCell, grid);

    assertArrayEquals(new int[]{-1, -1}, coordinates);
  }

  /**
   * Tests creating an agent with random attributes within valid ranges. Expected behavior: Creates
   * agent with attributes in expected ranges.
   */
  @Test
  void createAgent_ValidParameters_CreatesAgentWithValidRanges() {
    Agent agent = RulesHelper.createAgent(cell, random);

    assertNotNull(agent);
    assertTrue(agent.getVision() >= 1 && agent.getVision() <= 4);
    assertTrue(agent.getMetabolism() >= 1 && agent.getMetabolism() <= 3);
    assertTrue(agent.getSugar() >= 10 && agent.getSugar() <= 29);
  }

  /**
   * Tests creating agent with null cell. Expected behavior: Throws IllegalArgumentException.
   */
  @Test
  void createAgent_NullCell_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () ->
        RulesHelper.createAgent(null, random)
    );
  }

  /**
   * Tests creating agent with null random. Expected behavior: Throws IllegalArgumentException.
   */
  @Test
  void createAgent_NullRandom_ThrowsException() {
    assertThrows(NullPointerException.class, () ->
        RulesHelper.createAgent(cell, null)
    );
  }
}