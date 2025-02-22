package cellsociety.model;

import cellsociety.model.Agent;
import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.Sex;
import java.util.Random;

/**
 * Provides helper methods for common operations in the SugarScape simulation.
 * <p>
 * This class includes utility functions to locate cell coordinates in the grid and to create
 * agents with randomized attributes.
 * </p>
 */
public class RulesHelper {

  /**
   * Finds the coordinates of the target cell within a grid.
   *
   * @param targetCell the cell to locate
   * @param grid the Grid instance to search within
   * @return an int array where index 0 is the row and index 1 is the column, or {-1, -1} if not found
   */
  public static int[] getCellCoordinates(Cell targetCell, Grid grid) {
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        if (grid.getCell(r, c) == targetCell) {
          return new int[]{r, c};
        }
      }
    }
    return new int[]{-1, -1};
  }

  /**
   * Creates a new agent with random attributes for vision, metabolism, and initial sugar.
   * The agent's sex is chosen randomly and its fertility status is randomly assigned.
   *
   * @param position the initial position (cell) for the agent
   * @param random a Random instance used for attribute generation
   * @return a new Agent instance with random attributes
   */
  public static Agent createAgent(Cell position, Random random) {
    int vision = random.nextInt(4) + 1;       // Vision between 1 and 4
    int metabolism = random.nextInt(3) + 1;   // Metabolism between 1 and 3
    int initialSugar = random.nextInt(20) + 10; // Initial sugar between 10 and 29

    Agent agent = new Agent(position, initialSugar, vision, metabolism);
    agent.setSex(random.nextBoolean() ? Sex.MALE : Sex.FEMALE);
    agent.setFertile(random.nextBoolean());

    return agent;
  }
}
