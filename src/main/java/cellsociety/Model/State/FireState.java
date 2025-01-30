package cellsociety.Model.State;
/**
 * Enum representing the states for the Spreading of Fire simulation.
 * <p>
 * The Spreading of Fire simulation includes four states: TREE, BURNING, BURNT, and EMPTY. A tree may
 * catch fire, burn, or remain as an empty cell after burning out.
 * </p>
 */
public enum FireState {

  /**
   * Represents a tree in the Spreading of Fire simulation, ready to catch fire.
   */
  TREE,

  /**
   * Represents a burning tree in the Spreading of Fire simulation.
   */
  BURNING,

  /**
   * Represents a burnt tree (empty cell) in the Spreading of Fire simulation.
   */
  BURNT,

  /**
   * Represents an empty cell in the Spreading of Fire simulation.
   */
  EMPTY
}