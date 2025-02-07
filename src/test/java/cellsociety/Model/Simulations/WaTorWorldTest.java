package cellsociety.Model.Simulations;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.State.WaTorWorldState;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit test class for the WaTorWorld simulation.
 * Naming convention: [UnitOfWork_StateUnderTest_ExpectedBehavior]
 */
class WaTorWorldTest {

  private Grid grid;
  private SimulationConfig simConfig;

  /**
   * Initializes a 3×3 grid with EMPTY states and a SimulationConfig before each test.
   */
  @BeforeEach
  void setupGrid() {
    grid = new Grid(3, 3, WaTorWorldState.EMPTY);
    simConfig = new SimulationConfig(
        "WaTorWorld",
        "WaTorWorld Simulation",
        "Test Author",
        "Testing WaTorWorld simulation",
        3, 3,
        new int[9],
        new HashMap<>()
    );
  }

  /**
   * step: Fish movement without reproduction.
   * Input: A fish is placed at (1,1) with a breed time of 3 (not reached).
   * Expected: After one step, the fish moves so that exactly one fish exists and its original cell becomes empty.
   */
  @Test
  void step_FishMovementWithoutReproduction_FishCountOneAndOriginEmpty() {
    grid.getCell(1, 1).setState(WaTorWorldState.FISH);
    int fishBreedTime = 3;
    int sharkBreedTime = 3, sharkInitialEnergy = 5, sharkEnergyGain = 2;
    WaTorWorld simulation = new WaTorWorld(simConfig, grid, fishBreedTime, sharkBreedTime, sharkInitialEnergy, sharkEnergyGain);

    simulation.step();

    int fishCount = countState(WaTorWorldState.FISH);
    assertEquals(1, fishCount, "There should be exactly one fish after moving without reproduction");
    assertEquals("Empty", grid.getCell(1, 1).getState().getStateValue(),
        "The original cell should be empty after the fish moves");
  }

  /**
   * step: Fish reproduction when breed time is reached.
   * Input: A fish is placed at (1,1) with a breed time of 1.
   * Expected: After one step, reproduction occurs so that there are two fish.
   */
  @Test
  void step_FishReproductionWhenBreedTimeReached_FishCountEqualsTwo() {
    grid.getCell(1, 1).setState(WaTorWorldState.FISH);
    int fishBreedTime = 1;
    int sharkBreedTime = 3, sharkInitialEnergy = 5, sharkEnergyGain = 2;
    WaTorWorld simulation = new WaTorWorld(simConfig, grid, fishBreedTime, sharkBreedTime, sharkInitialEnergy, sharkEnergyGain);

    simulation.step();

    int fishCount = countState(WaTorWorldState.FISH);
    assertEquals(2, fishCount, "Fish should reproduce when breed time is reached, resulting in two fish");
  }

  /**
   * step: Shark death due to starvation.
   * Input: A shark is placed at (1,1) with an initial energy of 1.
   * Expected: After one step, the shark dies of starvation and the cell becomes empty.
   */
  @Test
  void step_SharkDeathDueToStarvation_CellBecomesEmpty() {
    grid.getCell(1, 1).setState(WaTorWorldState.SHARK);
    int fishBreedTime = 3, sharkBreedTime = 3, sharkInitialEnergy = 1, sharkEnergyGain = 2;
    WaTorWorld simulation = new WaTorWorld(simConfig, grid, fishBreedTime, sharkBreedTime, sharkInitialEnergy, sharkEnergyGain);

    simulation.step();

    assertEquals("Empty", grid.getCell(1, 1).getState().getStateValue(),
        "Shark should die of starvation and leave the cell empty");
  }

  /**
   * step: Shark eats a fish and reproduces.
   * Input: A shark is placed at (1,1) and a fish at (1,2) with shark breed time 1.
   * Expected: After one step, the shark eats the fish and reproduction occurs so that there are two sharks,
   * and the cell originally containing the fish now holds a shark.
   */
  @Test
  void step_SharkEatsFishAndReproduces_SharkCountEqualsTwoAndPreyCellConverted() {
    grid.getCell(1, 1).setState(WaTorWorldState.SHARK);
    grid.getCell(1, 2).setState(WaTorWorldState.FISH);
    int fishBreedTime = 3, sharkBreedTime = 1, sharkInitialEnergy = 5, sharkEnergyGain = 2;
    WaTorWorld simulation = new WaTorWorld(simConfig, grid, fishBreedTime, sharkBreedTime, sharkInitialEnergy, sharkEnergyGain);

    simulation.step();

    int sharkCount = countState(WaTorWorldState.SHARK);
    assertEquals(2, sharkCount, "After eating and reproduction, there should be two sharks");
    assertEquals("Shark", grid.getCell(1, 2).getState().getStateValue(),
        "The fish cell should now contain a shark");
  }

  /**
   * WaTorWorldConstructor: Passing a null grid should throw a NullPointerException.
   * Input: Null grid.
   */
  @Test
  void WaTorWorldConstructor_NullGrid_ThrowsNullPointerException() {
    Exception exception = assertThrows(NullPointerException.class, () -> {
      new WaTorWorld(simConfig, null, 3, 3, 5, 2);
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
