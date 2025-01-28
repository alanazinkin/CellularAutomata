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
  private List<List<String>> parameters;
  /**
   * key indicating what each color means for a given simulation
   */
  private List<String> stateColors;

  public SimulationInfoPanel(String type, String title, String author, String description, List<List<String>> parameters, List<String> stateColors) {
    setType(type);
    setTitle(title);
    setAuthor(author);
    setDescription(description);
    setParameters(parameters);
    setStateColors(stateColors);
  }

  private void setType(String type) {
    this.type = type;
  }

  private void setTitle(String title) {
    this.title = title;
  }

  private void setAuthor(String author) {
    this.author = author;
  }

  private void setDescription(String description) {
    this.description = description;
  }

  private void setParameters(List<List<String>> parameters) {
    this.parameters = parameters;
  }

  private void setStateColors(List<String> stateColors) {
    this.stateColors = stateColors;
  }


}
