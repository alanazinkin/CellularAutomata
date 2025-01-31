package cellsociety.Model;

import cellsociety.Model.State.*;

/**
 * Represents a cell that holds a state, which can vary depending on the simulation.
 * The state can be one of the following:
 * <ul>
 *   <li>In Conway's Game of Life: {@link GameOfLifeState#ALIVE}, {@link GameOfLifeState#DEAD}</li>
 *   <li>In the Percolation simulation: {@link PercolationState#OPEN}, {@link PercolationState#BLOCKED}</li>
 *   <li>In the Fire simulation: {@link FireState#TREE}, {@link FireState#BURNING}, {@link FireState#BURNT}, {@link FireState#EMPTY}</li>
 *   <li>In Schelling's Model of Segregation: {@link SchellingState#AGENT}, {@link SchellingState#EMPTY_CELL}</li>
 *   <li>In the Wa-Tor World simulation: {@link WaTorWorldState#FISH}, {@link WaTorWorldState#SHARK}, {@link WaTorWorldState#EMPTY}</li>
 * </ul>
 * This class provides methods to get and set the state of the cell. The specific states depend
 * on the simulation being used, and the behavior of the cell is determined by its current state
 * within that simulation.
 */
public class Cell {

  private StateInterface state;
  private StateInterface nextState;

  /**
   * Constructs a new {@code Cell} with the specified initial state.
   *
   * @param state The initial state of the cell. Must not be null.
   * @throws IllegalArgumentException if the provided state is null
   */
  public Cell(StateInterface state) {
    if (state == null) {
      throw new IllegalArgumentException("Initial state cannot be null");
    }
    this.state = state;
    this.nextState = state;
  }

  /**
   * Returns the current state of the cell.
   *
   * @return the state of the cell.
   */
  public StateInterface getState() {
    return state;
  }

  /**
   * Sets a new state for the cell.
   *
   * @param state The new state to set for the cell. Must not be null.
   * @throws IllegalArgumentException if the provided state is null
   */
  public void setState(StateInterface state) {
    if (state == null) {
      throw new IllegalArgumentException("State cannot be null");
    }
    this.state = state;
  }

  /**
   * Sets the next state for the cell, which will be applied in the next generation.
   *
   * @param nextState The new next state for the cell. Must not be null.
   * @throws IllegalArgumentException if the provided next state is null
   */
  public void setNextState(StateInterface nextState) {
    if (nextState == null) {
      throw new IllegalArgumentException("Next state cannot be null");
    }
    this.nextState = nextState;
  }

  /**
   * Returns the next state of the cell.
   *
   * @return the next state of the cell.
   */
  public StateInterface getNextState() {
    return nextState;
  }

  /**
   * Applies the next state to the cell, updating its current state.
   */
  public void applyNextState() {
    this.state = this.nextState;
  }

  /**
   * Resets the next state back to the current state after applying the update.
   */
  public void resetNextState() {
    this.nextState = this.state;
  }

  /**
   * Resets both the current state and next state to the specified state.
   *
   * @param state The state to reset both the current and next state to. Must not be null.
   * @throws IllegalArgumentException if the provided state is null
   */
  public void resetState(StateInterface state) {
    if (state == null) {
      throw new IllegalArgumentException("State cannot be null");
    }
    this.state = state;
    this.nextState = state;
  }

  /**
   * Returns a string representation of the cell's current state.
   *
   * @return a string representing the state of the cell.
   */
  @Override
  public String toString() {
    return state.toString();
  }
}


