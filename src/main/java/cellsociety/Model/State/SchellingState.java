package cellsociety.Model.State;
/**
 * Enum representing the states for Schelling's Model of Segregation.
 * <p>
 * Schelling's Model of Segregation simulation has two states: AGENT and EMPTY_CELL. AGENT represents
 * an individual agent in the simulation, and EMPTY_CELL represents an empty space where no agent exists.
 * </p>
 */
public enum SchellingState {

  /**
   * Represents an agent in the Schelling's Model of Segregation simulation.
   */
  AGENT,

  /**
   * Represents an empty cell in the Schelling's Model of Segregation simulation.
   */
  EMPTY_CELL
}