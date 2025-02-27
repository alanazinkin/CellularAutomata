package cellsociety.model;


import java.util.ArrayList;
import java.util.List;

/**
 * Represents a program that defines a creature's behavior.
 * This class is a pure model class that only stores the program data structure.
 * Parsing of program text is handled by the ProgramParser in the controller layer.
 * @author Tatum McKinnis
 */
public class CreatureProgram {
  private final String speciesName;
  private final List<CreatureInstruction> instructions;

  /**
   * Constructs a new creature program for the specified species.
   *
   * @param speciesName the name of the species
   */
  public CreatureProgram(String speciesName) {
    this.speciesName = speciesName;
    this.instructions = new ArrayList<>();
  }

  /**
   * Gets the name of the species this program defines.
   *
   * @return the species name
   */
  public String getSpeciesName() {
    return speciesName;
  }

  /**
   * Gets the list of instructions in this program.
   *
   * @return the list of instructions
   */
  public List<CreatureInstruction> getInstructions() {
    return instructions;
  }

  /**
   * Adds an instruction to this program.
   *
   * @param instruction the instruction to add
   */
  public void addInstruction(CreatureInstruction instruction) {
    instructions.add(instruction);
  }

  /**
   * Adds multiple instructions to this program.
   *
   * @param newInstructions the instructions to add
   */
  public void addInstructions(List<CreatureInstruction> newInstructions) {
    instructions.addAll(newInstructions);
  }

  /**
   * Clears all instructions from this program.
   */
  public void clearInstructions() {
    instructions.clear();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Program for species: ").append(speciesName).append("\n");
    for (int i = 0; i < instructions.size(); i++) {
      sb.append(i + 1).append(": ").append(instructions.get(i)).append("\n");
    }
    return sb.toString();
  }
}
