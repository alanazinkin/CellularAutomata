package cellsociety.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a species in the Darwin simulation.
 * A species is defined by its name, color, and the program it executes.
 * @author Tatum McKinnis
 */
public class Species {
  private final String name;
  private final String color;
  private final List<Instruction> program;

  /**
   * Creates a new species with the given name, color, and program.
   *
   * @param name The name of the species
   * @param color The color used to represent this species in visualizations
   * @param program The list of instructions that define this species' behavior
   */
  public Species(String name, String color, List<Instruction> program) {
    this.name = name;
    this.color = color;
    this.program = new ArrayList<>(program);
  }

  /**
   * Gets the name of this species.
   *
   * @return The species name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the color used to represent this species.
   *
   * @return The color string
   */
  public String getColor() {
    return color;
  }

  /**
   * Gets the number of instructions in the species program.
   *
   * @return The program length
   */
  public int getProgramLength() {
    return program.size();
  }

  /**
   * Gets a specific instruction from the program.
   *
   * @param index The index of the instruction to get
   * @return The instruction at the specified index
   * @throws IndexOutOfBoundsException if the index is out of range
   */
  public Instruction getInstruction(int index) {
    if (index < 0 || index >= program.size()) {
      throw new IndexOutOfBoundsException("Program index out of range: " + index);
    }
    return program.get(index);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return name;
  }
}
