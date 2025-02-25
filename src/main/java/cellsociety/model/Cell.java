package cellsociety.model;

import cellsociety.model.state.*;

/**
 * Represents a cell that holds a state, which can vary depending on the simulation. The cell
 * maintains the current state, a next state (to be applied in the next generation), and a previous
 * state (the state prior to the current state). The state can be one of the following:
 * <ul>
 *   <li>In Conway's Game of Life: {@link GameOfLifeState#ALIVE}, {@link GameOfLifeState#DEAD}</li>
 *   <li>In the Percolation simulation: {@link PercolationState#OPEN}, {@link PercolationState#BLOCKED}</li>
 *   <li>In the Fire simulation: {@link FireState#TREE}, {@link FireState#BURNING}, {@link FireState#BURNT}, {@link FireState#EMPTY}</li>
 *   <li>In Schelling's Model of Segregation: {@link SchellingState#AGENT}, {@link SchellingState#EMPTY_CELL}</li>
 *   <li>In the Wa-Tor World simulation: {@link WaTorWorldState#FISH}, {@link WaTorWorldState#SHARK}, {@link WaTorWorldState#EMPTY}</li>
 * </ul>
 * This class provides methods to get and set the states of the cell. The behavior of the cell is
 * determined by its current state within the simulation, and the previous state is tracked for reference.
 *
 * @author Tatum McKinnis
 */
public class Cell {

  private static final String NULL_STATE_ERROR = "State cannot be null";
  private static final String NULL_NEXT_STATE_ERROR = "Next state cannot be null";
  private static final String NULL_INITIAL_STATE_ERROR = "Initial state cannot be null";

  private StateInterface currentState;
  private StateInterface nextState;
  private StateInterface prevState;

  /**
   * Constructs a new {@code Cell} with the specified initial state. Initializes the current, next,
   * and previous state to the provided state.
   *
   * @param state The initial state of the cell. Must not be null.
   * @throws IllegalArgumentException if the provided state is null
   */
  public Cell(StateInterface state) {
    validateState(state, NULL_INITIAL_STATE_ERROR);
    this.currentState = state;
    this.nextState = state;
    this.prevState = state;
  }

  /**
   * Returns the current state of the cell.
   *
   * @return the current state of the cell.
   */
  public StateInterface getCurrentState() {
    return currentState;
  }

  /**
   * Returns the previous state of the cell.
   *
   * @return the previous state of the cell.
   */
  public StateInterface getPrevState() {
    return prevState;
  }

  /**
   * Sets a new state for the cell. Updates the previous state to the old current state.
   *
   * @param newState The new state to set for the cell. Must not be null.
   * @throws IllegalArgumentException if the provided state is null
   */
  public void setCurrentState(StateInterface newState) {
    validateState(newState, NULL_STATE_ERROR);
    this.prevState = this.currentState;
    this.currentState = newState;
  }

  /**
   * Sets the next state for the cell, which will be applied in the next generation.
   *
   * @param nextState The new next state for the cell. Must not be null.
   * @throws IllegalArgumentException if the provided next state is null
   */
  public void setNextState(StateInterface nextState) {
    validateState(nextState, NULL_NEXT_STATE_ERROR);
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
   * Applies the next state to the cell. Before updating, the current state is saved as the previous
   * state.
   */
  public void applyNextState() {
    this.prevState = this.currentState;
    this.currentState = this.nextState;
  }

  public void applyPrevState() {
    this.nextState = this.currentState;
    this.currentState = this.prevState;
    this.prevState =  MockState.STATE_ONE;
  }

  /**
   * Resets the next state back to the current state after applying the update.
   */
  public void resetNextState() {
    this.nextState = this.currentState;
  }

  /**
   * Resets the current, next, and previous state to the specified state.
   *
   * @param state The state to reset the cell to. Must not be null.
   * @throws IllegalArgumentException if the provided state is null
   */
  public void resetState(StateInterface state) {
    validateState(state, NULL_STATE_ERROR);
    this.currentState = state;
    this.nextState = state;
    this.prevState = state;
  }

  /**
   * Returns a string representation of the cell's current state.
   *
   * @return a string representing the current state of the cell.
   */
  @Override
  public String toString() {
    return currentState.toString();
  }

  /**
   * Centralized state validation method
   *
   * @param state        State to validate
   * @param errorMessage Message to use if validation fails
   */
  private void validateState(StateInterface state, String errorMessage) {
    if (state == null) {
      throw new IllegalArgumentException(errorMessage);
    }
  }
}
