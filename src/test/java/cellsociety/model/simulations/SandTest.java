package cellsociety.model.simulations;

import static org.junit.Assert.*;

import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.StateInterface;
import cellsociety.model.state.SandState;
import org.junit.Before;
import org.junit.Test;
import cellsociety.controller.SimulationConfig;
import java.util.HashMap;

/**
 * Test class for Sand implementing comprehensive test coverage for particle behavior.
 * <p>
 * This test suite verifies: - Basic particle movement and interactions - Water flow behavior - Wall
 * interactions - Error conditions and edge cases - State transitions and stability
 * </p>
 *
 * @author Tatum McKinnis
 */
public class SandTest {

  private Sand simulation;
  private Grid grid;
  private SimulationConfig config;

  /**
   * Sets up the test environment before each test.
   * <p>
   * Creates a 5x5 grid with all cells initially empty and initializes the simulation with test
   * configuration.
   * </p>
   */
  @Before
  public void setUp() {
    grid = new Grid(5, 5, SandState.EMPTY);
    int[] initialStates = new int[25];
    config = new SimulationConfig("Sand", "Sand Simulation", "Test Author",
        "A simulation of falling sand", 5, 5, initialStates, new HashMap<>());
    simulation = new Sand(config, grid);
  }

  /**
   * Tests that sand particles fall straight down when space is available.
   * <p>
   * Places a sand particle at the top of the grid and verifies it falls one cell down after a
   * single step.
   * </p>
   */
  @Test
  public void step_SandParticleFalling_MovesDownOneCell() {
    simulation.setCellState(0, 2, 1);
    simulation.step();

    assertEquals(SandState.EMPTY, grid.getCell(0, 2).getCurrentState());
    assertEquals(SandState.SAND, grid.getCell(1, 2).getCurrentState());
  }

  /**
   * Tests that sand particles properly stack on top of each other.
   * <p>
   * Places two sand particles and verifies they form a stable stack after multiple simulation
   * steps.
   * </p>
   */
  @Test
  public void step_SandParticleStacking_FormsStablePile() {
    simulation.setCellState(0, 2, 1);
    simulation.setCellState(2, 2, 1);

    simulation.step();
    simulation.step();

    assertEquals(SandState.EMPTY, grid.getCell(0, 2).getCurrentState());
    assertEquals(SandState.SAND, grid.getCell(1, 2).getCurrentState());
    assertEquals(SandState.SAND, grid.getCell(2, 2).getCurrentState());
  }

  /**
   * Tests that water particles spread horizontally when blocked.
   * <p>
   * Places a water particle above a wall and verifies it flows to at least one side when blocked
   * from falling.
   * </p>
   */
  @Test
  public void step_WaterParticleBlocked_SpreadsSideways() {
    simulation.setCellState(1, 2, 3);
    simulation.setCellState(2, 2, 2);

    simulation.step();

    assertTrue("Water should spread to at least one side",
        grid.getCell(1, 1).getCurrentState() == SandState.WATER ||
            grid.getCell(1, 3).getCurrentState() == SandState.WATER
    );
    assertEquals("Original water cell should be empty",
        SandState.EMPTY, grid.getCell(1, 2).getCurrentState());
  }

  /**
   * Tests that water flows more readily horizontally than sand.
   * <p>
   * Compares the horizontal spread of water versus sand when both are blocked from falling directly
   * downward.
   * </p>
   */
  @Test
  public void step_WaterFlowBehavior_SpreadsFartherThanSand() {
    simulation.setCellState(0, 2, 3);
    simulation.setCellState(1, 2, 2);
    simulation.setCellState(0, 0, 1);
    simulation.setCellState(1, 0, 2);

    for (int i = 0; i < 3; i++) {
      simulation.step();
    }

    int waterSpread = countHorizontalSpread(1, SandState.WATER);
    int sandSpread = countHorizontalSpread(1, SandState.SAND);
    assertTrue("Water should spread further than sand", waterSpread > sandSpread);
  }

  /**
   * Helper method to count how far a particle type has spread horizontally.
   *
   * @param row   The row to check for particle spread
   * @param state The state type to count
   * @return The number of cells in the row containing the specified state
   */
  private int countHorizontalSpread(int row, SandState state) {
    int count = 0;
    for (int c = 0; c < grid.getCols(); c++) {
      if (grid.getCell(row, c).getCurrentState() == state) {
        count++;
      }
    }
    return count;
  }

  /**
   * Tests that particles cannot move through walls.
   * <p>
   * Places a sand particle above a wall and verifies it stops moving when it reaches the wall.
   * </p>
   */
  @Test
  public void step_ParticleHitsWall_StopsMoving() {
    simulation.setCellState(0, 2, 1);
    simulation.setCellState(2, 2, 2);

    simulation.step();

    assertEquals("Sand should stop above wall",
        SandState.SAND, grid.getCell(1, 2).getCurrentState());
    assertEquals("Wall should remain in place",
        SandState.WALL, grid.getCell(2, 2).getCurrentState());
  }

  /**
   * Tests that simulation throws exception with null grid.
   * <p>
   * Verifies that attempting to create a simulation with a null grid results in an
   * IllegalArgumentException.
   * </p>
   *
   * @throws IllegalArgumentException expected when grid is null
   */
  @Test(expected = IllegalArgumentException.class)
  public void constructor_NullGrid_ThrowsException() {
    new Sand(config, null);
  }

  /**
   * Tests that simulation throws exception with empty initial states.
   * <p>
   * Verifies that attempting to create a simulation with an empty initial states array results in
   * an IllegalArgumentException.
   * </p>
   *
   * @throws IllegalArgumentException expected when initial states array is empty
   */
  @Test(expected = IllegalArgumentException.class)
  public void constructor_EmptyInitialStates_ThrowsException() {
    config = new SimulationConfig("Sand", "Sand Simulation", "Test Author",
        "A simulation of falling sand", 5, 5, new int[0], new HashMap<>());
    new Sand(config, grid);
  }

  /**
   * Tests that water particles follow the path of least resistance.
   * <p>
   * Creates a scenario where water has multiple possible paths and verifies it takes the easiest
   * available route.
   * </p>
   */
  @Test
  public void step_WaterPathFinding_FollowsLeastResistance() {
    // Set up a path with obstacles
    simulation.setCellState(0, 2, 3);  // Water at top
    simulation.setCellState(1, 2, 2);  // Wall blocking direct down
    simulation.setCellState(1, 1, 2);  // Wall blocking left

    simulation.step();

    assertEquals("Water should flow to the right side",
        SandState.WATER, grid.getCell(1, 3).getCurrentState());
    assertEquals("Original position should be empty",
        SandState.EMPTY, grid.getCell(0, 2).getCurrentState());
  }

  /**
   * Tests that multiple particles can form a diagonal pile.
   * <p>
   * Places multiple sand particles and verifies they form a stable diagonal arrangement when
   * blocked on one side.
   * </p>
   */
  @Test
  public void step_MultipleParticles_FormDiagonalPile() {
    // Place wall to force diagonal stacking
    simulation.setCellState(2, 0, 2);  // Wall

    // Place several sand particles
    simulation.setCellState(0, 0, 1);
    simulation.setCellState(0, 1, 1);

    // Run several steps to let particles settle
    for (int i = 0; i < 4; i++) {
      simulation.step();
    }

    // Verify diagonal formation
    assertEquals(SandState.SAND, grid.getCell(1, 1).getCurrentState());
    assertEquals(SandState.SAND, grid.getCell(1, 2).getCurrentState());
  }

  /**
   * Tests the stability of a wall configuration.
   * <p>
   * Verifies that wall particles remain stationary and don't get affected by other particle
   * movements.
   * </p>
   */
  @Test
  public void step_WallStability_RemainsStationary() {
    // Create wall configuration
    simulation.setCellState(1, 1, 2);
    simulation.setCellState(1, 2, 2);

    // Place particles around walls
    simulation.setCellState(0, 1, 1);  // Sand above
    simulation.setCellState(0, 2, 3);  // Water above

    // Run several steps
    for (int i = 0; i < 3; i++) {
      simulation.step();
    }

    // Verify walls haven't moved
    assertEquals("First wall should remain in place",
        SandState.WALL, grid.getCell(1, 1).getCurrentState());
    assertEquals("Second wall should remain in place",
        SandState.WALL, grid.getCell(1, 2).getCurrentState());
  }

  /**
   * Tests particle behavior at grid boundaries.
   * <p>
   * Verifies that particles behave correctly when they reach the edges of the simulation grid.
   * </p>
   */
  @Test
  public void step_GridBoundary_ParticlesStopAtEdge() {
    // Place particles at grid edges
    simulation.setCellState(grid.getRows() - 1, 0, 1);  // Sand at bottom
    simulation.setCellState(0, grid.getCols() - 1, 3);  // Water at right edge

    simulation.step();

    // Verify particles remain at boundaries
    assertEquals("Sand should stay at bottom",
        SandState.SAND, grid.getCell(grid.getRows() - 1, 0).getCurrentState());
    assertTrue("Water should not move beyond grid bounds",
        grid.getCell(0, grid.getCols() - 1).getCurrentState() == SandState.WATER ||
            grid.getCell(1, grid.getCols() - 1).getCurrentState() == SandState.WATER
    );
  }

  /**
   * Tests that invalid state values are handled appropriately.
   * <p>
   * Attempts to set an invalid state value and verifies that the cell maintains its previous valid
   * state.
   * </p>
   */
  @Test
  public void setCellState_InvalidStateValue_CellUnchanged() {
    Cell targetCell = grid.getCell(0, 0);
    StateInterface originalState = targetCell.getCurrentState();

    // Attempt to set an invalid state value
    simulation.setCellState(0, 0, 999);  // Invalid state value

    assertEquals("Cell state should remain unchanged",
        originalState, targetCell.getCurrentState());
  }
}
