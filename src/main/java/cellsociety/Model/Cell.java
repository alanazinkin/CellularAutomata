package cellsociety.Model;


import Model.State.FireState;
import Model.State.GameOfLifeState;
import Model.State.PercolationState;
import Model.State.SchellingState;
import Model.State.WaTorWorldState;

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

  /**
   * The state of the cell.
   */
  private StateInterface state;

  /**
   * The next state to which the cell will transition.
   */
  private StateInterface nextState;

  /**
   * Constructs a new {@code Cell} with the specified initial state.
   *
   * @param state The initial state of the cell.
   */
  public Cell(StateInterface state) {
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
   * @param state The new state to set for the cell.
   */
  public void setState(StateInterface state) {
    this.state = state;
  }

  /**
   * Sets the next state for the cell, which will be applied in the next generation.
   *
   * @param nextState The new next state for the cell.
   */
  public void setNextState(StateInterface nextState) {
    this.nextState = nextState;
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
   * @param state The state to reset both the current and next state to.
   */
  public void resetState(StateInterface state) {
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
