package cellsociety.Model.Simulations;

import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.State.WaTorWorldState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for WaTorWorld simulation.
 *
 * <p>
 * These tests cover the behavior of fish and sharks in the simulation,
 * including movement, reproduction, starvation, and eating. Because the
 * simulation uses randomness, controlled initial states are set up on small (3x3)
 * grids to ensure deterministic behavior in each test case.
 * </p>
 *
 * <p>
 * The test class includes both positive and negative test cases to validate
 * proper functioning and exception handling of the simulation.
 * </p>
 */
class WaTorWorldTest {

  private Grid grid;

  /**
   * Initializes a 3x3 grid with empty states before each test.
   */
  @BeforeEach
  void setupGrid() {
    grid = new Grid(3, 3, WaTorWorldState.EMPTY);
  }

  /**
   * Tests that a fish moves without reproducing when its breed time has not been reached.
   */
  @Test
  void testFishMovementWithoutReproduction() {
    grid.getCell(1, 1).setState(WaTorWorldState.FISH);
    int fishBreedTime = 3;
    int sharkBreedTime = 3, sharkInitialEnergy = 5, sharkEnergyGain = 2;
    WaTorWorld simulation = new WaTorWorld(grid, fishBreedTime, sharkBreedTime, sharkInitialEnergy, sharkEnergyGain);

    simulation.step();

    int fishCount = countState(WaTorWorldState.FISH);
    assertEquals(1, fishCount, "There should be exactly one fish after moving without reproduction");
    assertEquals("Empty", grid.getCell(1, 1).getState().getStateValue(),
        "The original cell should be empty after the fish moves");
  }

  /**
   * Tests that a fish reproduces when its breed time has been reached.
   */
  @Test
  void testFishReproductionWhenBreedTimeReached() {
    grid.getCell(1, 1).setState(WaTorWorldState.FISH);
    int fishBreedTime = 1;
    int sharkBreedTime = 3, sharkInitialEnergy = 5, sharkEnergyGain = 2;
    WaTorWorld simulation = new WaTorWorld(grid, fishBreedTime, sharkBreedTime, sharkInitialEnergy, sharkEnergyGain);

    simulation.step();

    int fishCount = countState(WaTorWorldState.FISH);
    assertEquals(2, fishCount, "Fish should reproduce when breed time is reached, resulting in two fish");
  }

  /**
   * Tests that a shark dies due to starvation when its energy reaches zero.
   */
  @Test
  void testSharkDeathDueToStarvation() {
    grid.getCell(1, 1).setState(WaTorWorldState.SHARK);
    int fishBreedTime = 3, sharkBreedTime = 3, sharkInitialEnergy = 1, sharkEnergyGain = 2;
    WaTorWorld simulation = new WaTorWorld(grid, fishBreedTime, sharkBreedTime, sharkInitialEnergy, sharkEnergyGain);

    simulation.step();

    assertEquals("Empty", grid.getCell(1, 1).getState().getStateValue(),
        "Shark should die of starvation and leave the cell empty");
  }

  /**
   * Tests that a shark eats a fish and reproduces when its breed time is reached.
   */
  @Test
  void testSharkEatsFishAndReproduces() {
    grid.getCell(1, 1).setState(WaTorWorldState.SHARK);
    grid.getCell(1, 2).setState(WaTorWorldState.FISH);
    int fishBreedTime = 3, sharkBreedTime = 1, sharkInitialEnergy = 5, sharkEnergyGain = 2;
    WaTorWorld simulation = new WaTorWorld(grid, fishBreedTime, sharkBreedTime, sharkInitialEnergy, sharkEnergyGain);

    simulation.step();

    int sharkCount = countState(WaTorWorldState.SHARK);
    assertEquals(2, sharkCount, "After eating and reproduction, there should be two sharks");
    assertEquals("Shark", grid.getCell(1, 2).getState().getStateValue(),
        "The fish cell should now contain a shark");
  }

  // ----- Negative Tests -----

  /**
   * Tests that the constructor throws a NullPointerException when given a null grid.
   */
  @Test
  void testConstructorWithNullGridThrowsException() {
    Exception exception = assertThrows(NullPointerException.class, () -> {
      new WaTorWorld(null, 3, 3, 5, 2);
    });
    assertNotNull(exception.getMessage(), "Exception message should not be null");
  }

  // ----- Helper Methods -----

  /**
   * Counts the number of cells in the grid that have the specified state.
   *
   * @param target the target state (e.g., FISH or SHARK)
   * @return the count of cells matching the target state
   */
  private int countState(WaTorWorldState target) {
    int count = 0;
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        Cell cell = grid.getCell(r, c);
        if (cell != null && cell.getState() == target) {
          count++;
        }
      }
    }
    return count;
  }
}


