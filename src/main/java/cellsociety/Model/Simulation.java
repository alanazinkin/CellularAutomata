package cellsociety.Model;
/**
 * Represents an abstract simulation that operates on a {@link Grid}.
 * This class provides a framework for simulations by defining methods
 * to apply simulation-specific rules and update the grid states.
 * Subclasses must implement the {@link #applyRules()} method to define
 * the specific rules of the simulation.
 */
public abstract class Simulation {

  /**
   * The grid on which the simulation operates.
   */
  protected Grid grid;

  /**
   * Constructs a new {@code Simulation} instance with the specified grid.
   *
   * @param grid the {@code Grid} object representing the simulation space
   */
  public Simulation(Grid grid) {
    this.grid = grid;
  }

  /**
   * Applies the specific rules of the simulation.
   * This method must be implemented by subclasses to define
   * how the grid's state is updated according to the rules of the simulation.
   */
  public abstract void applyRules();

  /**
   * Performs a single step of the simulation by applying the rules and
   * updating the grid to reflect the next state.
   *
   * The process involves:
   * <ol>
   *   <li>Calling {@link #applyRules()} to calculate the new states.</li>
   *   <li>Invoking {@link Grid#applyNextStates()} to update the grid with the calculated states.</li>
   * </ol>
   */
  public void step() {
    applyRules();
    grid.applyNextStates();
  }
}

