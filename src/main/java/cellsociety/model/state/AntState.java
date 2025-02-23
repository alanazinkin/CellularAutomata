package cellsociety.model.state;

import cellsociety.model.StateInterface;
import java.util.Objects;

/**
 * Represents the state of a cell in the ant foraging simulation. This includes pheromone levels,
 * ant population, and special cell types (nest, food, obstacle). Unlike enum-based states, this
 * class supports dynamic properties that change during simulation.
 */
public class AntState implements StateInterface {

  private final boolean isNest;
  private final boolean isFood;
  private final boolean isObstacle;
  private final double homePheromone;
  private final double foodPheromone;
  private final int antCount;

  /**
   * Constructs an AntState with specified properties.
   *
   * @param isNest        true if this cell is a nest
   * @param isFood        true if this cell is a food source
   * @param isObstacle    true if this cell is an obstacle
   * @param homePheromone concentration of home-directed pheromones
   * @param foodPheromone concentration of food-directed pheromones
   * @param antCount      number of ants currently occupying this cell
   */
  public AntState(boolean isNest, boolean isFood, boolean isObstacle, double homePheromone,
      double foodPheromone, int antCount) {
    this.isNest = isNest;
    this.isFood = isFood;
    this.isObstacle = isObstacle;
    this.homePheromone = homePheromone;
    this.foodPheromone = foodPheromone;
    this.antCount = antCount;
  }

  /**
   * Creates a new AntState with updated home pheromone levels.
   *
   * @param homePheromone new home pheromone value
   * @return new AntState instance
   */
  public AntState withHomePheromone(double homePheromone) {
    return new AntState(isNest, isFood, isObstacle, homePheromone, foodPheromone, antCount);
  }

  /**
   * Creates a new AntState with updated food pheromone levels.
   *
   * @param foodPheromone new food pheromone value
   * @return new AntState instance
   */
  public AntState withFoodPheromone(double foodPheromone) {
    return new AntState(isNest, isFood, isObstacle, homePheromone, foodPheromone, antCount);
  }

  /**
   * Creates a new AntState with updated ant count.
   *
   * @param antCount new ant count
   * @return new AntState instance
   */
  public AntState withAntCount(int antCount) {
    return new AntState(isNest, isFood, isObstacle, homePheromone, foodPheromone, antCount);
  }

  /**
   * Checks if this cell is a nest.
   *
   * @return true if the cell is a nest
   */
  public boolean isNest() {
    return isNest;
  }

  /**
   * Checks if this cell is a food source.
   *
   * @return true if the cell is a food source
   */
  public boolean isFood() {
    return isFood;
  }

  /**
   * Checks if this cell is an obstacle.
   *
   * @return true if the cell is an obstacle
   */
  public boolean isObstacle() {
    return isObstacle;
  }

  /**
   * Gets the home pheromone concentration.
   *
   * @return home pheromone level
   */
  public double getHomePheromone() {
    return homePheromone;
  }

  /**
   * Gets the food pheromone concentration.
   *
   * @return food pheromone level
   */
  public double getFoodPheromone() {
    return foodPheromone;
  }

  /**
   * Gets the number of ants in this cell.
   *
   * @return ant count
   */
  public int getAntCount() {
    return antCount;
  }

  /**
   * Returns a string representation of the cell's primary state.
   *
   * @return "Nest", "Food", "Obstacle", or "Empty"
   */
  @Override
  public String getStateValue() {
    if (isNest) {
      return "Nest";
    }
    if (isFood) {
      return "Food";
    }
    if (isObstacle) {
      return "Obstacle";
    }
    return "Empty";
  }

  @Override
  public int getNumericValue() {
    return 0;
  }

  /**
   * Compares this AntState with another object for equality. Two AntStates are considered equal
   * if they represent the same type of cell (nest, food, obstacle, or empty), regardless of their
   * pheromone levels or ant count. This allows for proper color mapping while maintaining dynamic state.
   *
   * @param o the object to compare with
   * @return true if the objects represent the same type of cell
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AntState other = (AntState) o;
    return isNest == other.isNest &&
        isFood == other.isFood &&
        isObstacle == other.isObstacle;
  }

  /**
   * Generates a hash code for this AntState. The hash code is based only on the cell type
   * flags (nest, food, obstacle) to maintain consistency with equals().
   *
   * @return hash code value for this object
   */
  @Override
  public int hashCode() {
    return Objects.hash(isNest, isFood, isObstacle);
  }
}

