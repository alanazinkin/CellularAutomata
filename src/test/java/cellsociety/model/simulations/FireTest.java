package cellsociety.model.simulations;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.state.FireState;
import cellsociety.controller.SimulationConfig;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for the Fire simulation. Naming convention:
 * [UnitOfWork_StateUnderTest_ExpectedBehavior]
 * @author Tatum McKinnis
 */
class FireTest {

  private Grid grid;
  private SimulationConfig simulationConfig;

  /**
   * Sets up a 3x3 grid with all cells initially set to EMPTY and creates a SimulationConfig. The
   * SimulationConfig is created with dummy values appropriate for a Fire simulation.
   */
  @BeforeEach
  void setUp() {
    simulationConfig = new SimulationConfig(
        "Fire",
        "Fire Simulation",
        "Test Author",
        "Testing Fire simulation",
        3, 3,
        new int[9],
        new HashMap<>()
    );
    grid = new Grid(3, 3, FireState.EMPTY);
  }

  /**
   * applyRules: A burning cell should transition to burnt. Input: Center cell is burning; all
   * others are trees.
   */
  @Test
  void applyRules_BurningCell_TransitionsToBurnt() {
    Cell center = grid.getCell(1, 1);
    center.setCurrentState(FireState.BURNING);

    // Set all other cells to TREE so they don't affect the test.
    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        if (!(row == 1 && col == 1)) {
          grid.getCell(row, col).setCurrentState(FireState.TREE);
        }
      }
    }

    Fire fireSim = new Fire(simulationConfig, grid, 0.0, 0.0);
    fireSim.applyRules();

    assertEquals(FireState.BURNT, center.getNextState(),
        "A burning cell did not become burnt as expected.");
  }

  /**
   * applyRules: A tree with a burning neighbor should ignite. Input: Center tree with its left
   * neighbor burning.
   */
  @Test
  void applyRules_TreeWithBurningNeighbor_TransitionsToBurning() {
    Cell center = grid.getCell(1, 1);
    center.setCurrentState(FireState.TREE);

    Cell leftNeighbor = grid.getCell(1, 0);
    leftNeighbor.setCurrentState(FireState.BURNING);

    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        if (!((row == 1 && col == 1) || (row == 1 && col == 0))) {
          grid.getCell(row, col).setCurrentState(FireState.TREE);
        }
      }
    }

    Fire fireSim = new Fire(simulationConfig, grid, 0.0, 0.0);
    fireSim.applyRules();

    assertEquals(FireState.BURNING, center.getNextState(),
        "A tree with a burning neighbor did not ignite.");
  }

  /**
   * applyRules: A tree should spontaneously ignite when f = 1. Input: All cells are trees and
   * ignition probability (f) is set to 1.
   */
  @Test
  void applyRules_TreeWithoutBurningNeighbor_SpontaneouslyIgnites() {
    Cell center = grid.getCell(1, 1);
    center.setCurrentState(FireState.TREE);

    // All cells remain trees (no burning neighbors).
    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        grid.getCell(row, col).setCurrentState(FireState.TREE);
      }
    }

    Fire fireSim = new Fire(simulationConfig, grid, 0.0, 1.0);
    fireSim.applyRules();

    assertEquals(FireState.BURNING, center.getNextState(),
        "A tree did not spontaneously ignite when f = 1.");
  }

  /**
   * applyRules: An empty cell should grow a tree when p = 1. Input: Center cell is empty and
   * regrowth probability (p) is set to 1.
   */
  @Test
  void applyRules_EmptyCell_GrowsTree() {
    Cell center = grid.getCell(1, 1);
    center.setCurrentState(FireState.EMPTY);

    Fire fireSim = new Fire(simulationConfig, grid, 1.0, 0.0);
    fireSim.applyRules();

    assertEquals(FireState.TREE, center.getNextState(),
        "An empty cell did not grow a tree when p = 1.");
  }

  /**
   * step: The step() method should apply the rules and update the cell states. Input: Center tree
   * with its top neighbor burning.
   */
  @Test
  void step_BurningNeighborAndTree_TransitionsCorrectly() {
    Cell center = grid.getCell(1, 1);
    center.setCurrentState(FireState.TREE);

    Cell topNeighbor = grid.getCell(0, 1);
    topNeighbor.setCurrentState(FireState.BURNING);

    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        if (!((row == 1 && col == 1) || (row == 0 && col == 1))) {
          grid.getCell(row, col).setCurrentState(FireState.TREE);
        }
      }
    }

    Fire fireSim = new Fire(simulationConfig, grid, 0.0, 0.0);
    fireSim.step();

    assertEquals(FireState.BURNT, topNeighbor.getCurrentState(),
        "A burning neighbor did not become burnt after step().");
    assertEquals(FireState.BURNING, center.getCurrentState(),
        "A tree with a burning neighbor did not ignite after step().");
  }

  /**
   * applyRules: A burning cell on the edge should trigger ignition of its adjacent tree. Input:
   * Edge cell (0,0) is burning; its neighbor (0,1) is a tree.
   */
  @Test
  void applyRules_EdgeCell_BurningNeighbor_TriggersNeighborIgnition() {
    Cell edgeCell = grid.getCell(0, 0);
    edgeCell.setCurrentState(FireState.BURNING);

    Cell neighborCell = grid.getCell(0, 1);
    neighborCell.setCurrentState(FireState.TREE);

    for (int row = 0; row < grid.getRows(); row++) {
      for (int col = 0; col < grid.getCols(); col++) {
        if (!((row == 0 && col == 0) || (row == 0 && col == 1))) {
          grid.getCell(row, col).setCurrentState(FireState.TREE);
        }
      }
    }

    Fire fireSim = new Fire(simulationConfig, grid, 0.0, 0.0);
    fireSim.applyRules();

    assertEquals(FireState.BURNING, neighborCell.getNextState(),
        "The edge cell's neighbor did not ignite.");
  }

  /**
   * FireConstructor: Passing a null grid should throw an IllegalArgumentException. Input: Null
   * grid.
   */
  @Test
  void FireConstructor_NullGrid_ThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Fire(simulationConfig, null, 0.5, 0.5);
    }, "Constructing a Fire simulation with a null grid should throw an IllegalArgumentException.");
  }

  /**
   * FireConstructor: Passing an invalid regrowth probability should throw an
   * IllegalArgumentException. Input: Regrowth probability (p) is negative or greater than 1.
   */
  @Test
  void FireConstructor_InvalidRegrowthProbability_ThrowsIllegalArgumentException() {
    assertAll("Invalid regrowth probabilities",
        () -> assertThrows(IllegalArgumentException.class,
            () -> new Fire(simulationConfig, grid, -0.1, 0.5),
            "A negative regrowth probability should throw an IllegalArgumentException."),
        () -> assertThrows(IllegalArgumentException.class,
            () -> new Fire(simulationConfig, grid, 1.1, 0.5),
            "A regrowth probability greater than 1 should throw an IllegalArgumentException.")
    );
  }

  /**
   * FireConstructor: Passing an invalid ignition probability should throw an
   * IllegalArgumentException. Input: Ignition probability (f) is negative or greater than 1.
   */
  @Test
  void FireConstructor_InvalidIgnitionProbability_ThrowsIllegalArgumentException() {
    assertAll("Invalid ignition probabilities",
        () -> assertThrows(IllegalArgumentException.class,
            () -> new Fire(simulationConfig, grid, 0.5, -0.1),
            "A negative ignition probability should throw an IllegalArgumentException."),
        () -> assertThrows(IllegalArgumentException.class,
            () -> new Fire(simulationConfig, grid, 0.5, 1.1),
            "An ignition probability greater than 1 should throw an IllegalArgumentException.")
    );
  }
}




