package cellsociety.model;

import cellsociety.model.state.CreatureState;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the registry of programs for different creature species.
 * This class belongs to the model layer as it maintains associations
 * between states and their behavior definitions.
 * @author Tatum McKinnis
 */
public class ProgramRegistry {
  private final Map<CreatureState, CreatureProgram> speciesPrograms = new HashMap<>();

  /**
   * Registers a program for a specific creature state.
   *
   * @param state the creature state
   * @param program the program to register
   */
  public void registerProgram(CreatureState state, CreatureProgram program) {
    speciesPrograms.put(state, program);
  }

  /**
   * Gets the program for a specific creature state.
   *
   * @param state the creature state
   * @return the program for the state, or null if not found
   */
  public CreatureProgram getProgram(CreatureState state) {
    return speciesPrograms.get(state);
  }

  /**
   * Checks if a program is registered for the given state.
   *
   * @param state the creature state
   * @return true if a program is registered, false otherwise
   */
  public boolean hasProgram(CreatureState state) {
    return speciesPrograms.containsKey(state);
  }

  /**
   * Clears all registered programs.
   */
  public void clear() {
    speciesPrograms.clear();
  }
}