package cellsociety.Model.State;

/**
    * Enum representing the states for Conway's Game of Life simulation.
    * <p>
 * Conway's Game of Life has two possible states: ALIVE and DEAD. A cell can either be alive
    * or dead based on the number of neighbors it has in each generation.
 * </p>
    */
public enum GameOfLifeState {

  /**
   * Represents a cell that is alive in Conway's Game of Life.
   */
  ALIVE,

  /**
   * Represents a cell that is dead in Conway's Game of Life.
   */
  DEAD
}
