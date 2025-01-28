package cellsociety.View;

import java.util.List;

/**
 * This class creates a display window that informs the user of relevant simulation information
 * such as its type, name, author, description, state colors, and relevant parameters
 */
public class SimulationInfoPanel {

  /**
   * type of simulation (ex: Fire, GameofLife, etc.)
   */
  private String type;
  /**
   * full name of the simulation (ex: Spreading Fire, Game of Life, etc.)
   */
  private String title;
  /**
   * creator of the XML simulation file
   */
  private String author;
  /**
   * description of the simulation type
   */
  private String description;
  /**
   * inputs to the simulation (ex: probCatch in Spreading Fire)
   */
  private String parameters;
  /**
   * key indicating what each color means for a given simulation
   */
  private List<String> stateColors;


}
