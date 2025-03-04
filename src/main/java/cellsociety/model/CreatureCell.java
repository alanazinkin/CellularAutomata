package cellsociety.model;

/**
 * Represents a cell in the Darwin simulation that can contain a creature. Extends the basic Cell
 * class with creature-specific properties like species and orientation.
 *
 * @author Tatum McKinnis
 */
public class CreatureCell extends Cell {

  private Species species;
  private Species originalSpecies;
  private int orientation; // 0=north, 90=east, 180=south, 270=west
  private int programCounter;
  private int infectionStepsRemaining;

  /**
   * Creates a new creature cell with the specified state, species, and orientation.
   *
   * @param state       The initial state of the cell
   * @param species     The species of the creature (null for empty cells)
   * @param orientation The orientation in degrees
   */
  public CreatureCell(StateInterface state, Species species, int orientation) {
    super(state);
    this.species = species;
    this.originalSpecies = species;
    this.orientation = orientation;
    this.programCounter = 0;
    this.infectionStepsRemaining = 0;
  }

  /**
   * Gets the species of this creature.
   *
   * @return The species or null if the cell is empty
   */
  public Species getSpecies() {
    return species;
  }

  /**
   * Sets the species of this creature.
   *
   * @param species The new species (can be null for empty cells)
   */
  public void setSpecies(Species species) {
    this.species = species;
  }

  /**
   * Gets the original species of this creature (before infection).
   *
   * @return The original species
   */
  public Species getOriginalSpecies() {
    return originalSpecies;
  }

  /**
   * Gets the orientation of this creature.
   *
   * @return The orientation in degrees
   */
  public int getOrientation() {
    return orientation;
  }

  /**
   * Sets the orientation of this creature.
   *
   * @param orientation The new orientation in degrees
   */
  public void setOrientation(int orientation) {
    this.orientation = ((orientation % 360) + 360) % 360;
  }

  /**
   * Gets the program counter for this creature.
   *
   * @return The program counter
   */
  public int getProgramCounter() {
    return programCounter;
  }

  /**
   * Sets the program counter for this creature.
   *
   * @param counter The new program counter
   */
  public void setProgramCounter(int counter) {
    this.programCounter = counter;
  }

  /**
   * Advances the program counter to the next instruction.
   */
  public void advanceProgramCounter() {
    if (species == null) {
      return;
    }

    programCounter++;

    // Wrap around if we've reached the end of the program
    if (programCounter >= species.getProgramLength()) {
      programCounter = 0;
    }
  }

  /**
   * Gets the number of steps remaining in the current infection.
   *
   * @return The infection steps remaining
   */
  public int getInfectionStepsRemaining() {
    return infectionStepsRemaining;
  }

  /**
   * Checks if this creature is currently infected.
   *
   * @return true if infected, false otherwise
   */
  public boolean isInfected() {
    return infectionStepsRemaining > 0;
  }

  /**
   * Infects this creature with a new species for a specified number of steps.
   *
   * @param newSpecies The infecting species
   * @param steps      The number of steps the infection lasts
   */
  public void setInfection(Species newSpecies, int steps) {
    if (!isInfected()) {
      originalSpecies = species;
    }
    species = newSpecies;
    infectionStepsRemaining = steps;
    programCounter = 0;
  }

  /**
   * Decreases the infection counter and reverts to original species if infection is over.
   */
  public void decreaseInfectionCounter() {
    if (infectionStepsRemaining > 0) {
      infectionStepsRemaining--;
      if (infectionStepsRemaining == 0) {
        species = originalSpecies;
      }
    }
  }

  /**
   * Turns the creature left by the specified degrees.
   *
   * @param degrees The degrees to turn left
   */
  public void turnLeft(int degrees) {
    setOrientation(orientation - degrees);
  }

  /**
   * Turns the creature right by the specified degrees.
   *
   * @param degrees The degrees to turn right
   */
  public void turnRight(int degrees) {
    setOrientation(orientation + degrees);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void applyNextState() {
    super.applyNextState();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void applyPrevState() {
    super.applyPrevState();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    if (getCurrentState().toString().equals("Empty")) {
      return "Empty";
    } else {
      return species != null ? species.getName() : "No species";
    }
  }
}
