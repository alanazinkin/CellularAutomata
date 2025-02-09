package cellsociety.Model;
import cellsociety.Controller.SimulationConfig;
import java.util.Map;
import javafx.scene.paint.Color;

/**
 * Represents an abstract simulation that operates on a {@link Grid}.
 * <p>
 * This class provides a framework for simulations by defining methods to apply simulation-specific rules
 * and update the grid states. Subclasses must implement the {@link #applyRules()} method to define
 * the specific rules of the simulation.
 * </p>
 */
public abstract class Simulation {

  /**
   * The grid on which the simulation operates.
   */
  private Grid grid;

  /**
   * A mapping of simulation states to their corresponding display colors.
   * <p>
   * This map is used to translate each cell's integer value upon initialization into a state interface value.
   * The map is initialized by the {@link #initializeColorMap()} method during construction and can be
   * retrieved using the {@link #getColorMap()} method.
   * </p>
   */
  private Map<StateInterface, Color> colorMap;


  /**
   * A mapping of integer values to their corresponding state interface values.
   * <p>
   * This map is used to translate each cell's state into a visual color when rendering the simulation.
   * The map is initialized by the {@link #initializeStateMap()} method during construction and can be
   * retrieved using the {@link #getStateMap()} method.
   * </p>
   */
  private Map<Integer, StateInterface> stateMap;

  /**
   * Constructs a new {@code Simulation} instance with the specified grid and simulation configuration.
   * <p>
   * This constructor initializes the simulation by setting up the grid, mapping states to colors, and
   * applying the initial configuration. The grid must not be {@code null}; otherwise, an exception is thrown.
   * </p>
   *
   * @param simulationConfig the {@code SimulationConfig} object holding all the simulation information,
   *                         including initial states and parameters.
   * @param grid             the {@code Grid} object representing the simulation space.
   * @throws IllegalArgumentException if the provided grid is {@code null}.
   */
  public Simulation(SimulationConfig simulationConfig, Grid grid) {
    if (grid == null) {
      throw new IllegalArgumentException("Grid cannot be null");
    }
    this.grid = grid;
    this.colorMap = initializeColorMap();
    this.stateMap = initializeStateMap();
    initializeGrid(simulationConfig);
  }

  /**
   * method is used to retrieve grid instance variable for subclasses
   * @return Grid instance variable
   */
  protected Grid getGrid() {
    return grid;
  }

  /**
   * Initializes the mapping of simulation states to their corresponding display colors.
   * <p>
   * Subclasses must implement this method to provide a complete mapping for the specific simulation.
   * </p>
   *
   * @return a {@code Map} where keys are simulation states and values are their display colors
   */
  protected abstract Map<StateInterface, Color> initializeColorMap();

  /**
   * Initializes the mapping of integer states to their corresponding simulation interface values.
   * @return a {@code Map} where keys are integer states and values are corresponding simulation states.
   */
  protected abstract Map<Integer, StateInterface> initializeStateMap();

  /**
   * Applies the specific rules of the simulation.
   * <p>
   * This method must be implemented by subclasses to define how the grid's state is updated
   * according to the rules of the simulation.
   * </p>
   */
  public abstract void applyRules();

  /**
   * Performs a single step of the simulation by applying the rules and updating the grid.
   * <p>
   * The process involves:
   * <ol>
   *   <li>Calling {@link #applyRules()} to calculate the new states.</li>
   *   <li>Invoking {@link Grid#applyNextStates()} to update the grid with the calculated states.</li>
   * </ol>
   * </p>
   */
  public void step() {
    System.out.println("hello");
    applyRules();
    grid.applyNextStates();
  }
  /**
   * Initializes the grid's cell states based on the provided simulation configuration.
   * <p>
   * This method iterates over each cell in the grid and sets its state according to the corresponding
   * value in the simulation configuration's initial states array. The integer value from the configuration
   * is mapped to a {@code StateInterface} using the simulation's state map. Only cells whose current state
   * equals the grid's default state are updated, allowing previously modified cells to remain unchanged.
   * </p>
   * <p>
   * The method will throw a {@code NullPointerException} in the following cases:
   * <ul>
   *   <li>If the initial states array from the configuration is empty.</li>
   *   <li>If any cell in the grid is {@code null}.</li>
   *   <li>If the state map is {@code null}.</li>
   * </ul>
   * </p>
   *
   * @param simulationConfig the simulation configuration containing the initial states for the grid
   * @throws NullPointerException if the initial states array is empty, a grid cell is null, or the state map is null
   */
  private void initializeGrid(SimulationConfig simulationConfig) {
    int cellCount = 0;
    if (simulationConfig.getInitialStates().length == 0) {
      throw new NullPointerException("Initial states array is empty");
    }
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        if (grid.getCell(r, c) == null) {
          throw new NullPointerException("Cell at (" + r + "," + c + ") is null");
        }
        if (stateMap == null) {
          throw new NullPointerException("State map is null");
        }
        StateInterface newState = stateMap.get(simulationConfig.getInitialStates()[cellCount]);
        if (grid.getCell(r, c).getState().equals(grid.getDefaultState())) {
          grid.getCell(r, c).setState(newState);
        }
        cellCount++;
      }
    }
  }

  /**
   * Returns the mapping of simulation states to their corresponding display colors.
   * <p>
   * This color map is used to visually render the simulation grid by translating each cell's state into a color.
   * </p>
   * @return a {@code Map} where the keys are simulation states and the values are the colors associated with them
   */
  public Map<StateInterface, Color> getColorMap() {
    return colorMap;
  }

  /**
   * Returns the mapping of integer values to their corresponding state interface values
   * <p>
   *   This state map is used to interpret numerical values as state interface values
   * </p>
   * @return a {@code Map} where the keys are integer values and teh values are the state interface values
   */
  public Map<Integer, StateInterface> getStateMap() {return stateMap;}
}


