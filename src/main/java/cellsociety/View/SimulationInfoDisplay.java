package cellsociety.View;

import cellsociety.Model.StateColor;
import cellsociety.Model.StateInterface;
import java.util.List;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * This class creates a display window that informs the user of relevant simulation information
 * such as its type, name, author, description, state colors, and relevant parameters
 */
public class  SimulationInfoDisplay {

  private static int SCREEN_WIDTH = 800;
  private static int SCREEN_HEIGHT = 600;
  private static int TEXT_SIZE = 25;
  /**
   * type of simulation (ex: Fire, GameofLife, etc.)
   */
  private String myType;
  /**
   * full name of the simulation (ex: Spreading Fire, Game of Life, etc.)
   */
  private String myTitle;
  /**
   * creator of the XML simulation file
   */
  private String myAuthor;
  /**
   * explanation of the simulation type
   */
  private String myDescription;
  /**
   * inputs to the simulation (ex: probCatch in Spreading Fire)
   */
  private Map<String, String> myParameters;
  /**
   * key indicating what each color means for a given simulation
   */
  private Map<StateInterface, Color> myStateColors;

  /**
   * constructor for creating a simulation information display
   * @param type of simulation
   * @param title is the full name of the simulation
   * @param author of the XML file
   * @param description of the simulation type
   * @param parameters to the simulation
   * @param stateColors is a key that indicates the meaning of the color of each cell
   */
  public SimulationInfoDisplay(String type, String title, String author, String description, Map<String, String> parameters, Map<StateInterface, Color> stateColors) {
    setType(type);
    setTitle(title);
    setAuthor(author);
    setDescription(description);
    setParameters(parameters);
    setStateColors(stateColors);
  }

  /**
   * set the type instance variable
   * @param type type of simulation (ex: Fire, GameofLife, etc.)
   */
  private void setType(String type) {
    myType = type;
  }

  /**
   * set title instance variable
   * @param title full name of the simulation (ex: Spreading Fire, Game of Life, etc.)
   */
  private void setTitle(String title) {
    myTitle = title;
  }

  /**
   * set author instance variable
   * @param author creator of the XML file
   */
  private void setAuthor(String author) {
    myAuthor = author;
  }

  /**
   * set description instance variable
   * @param description explanation of the simulation type
   */
  private void setDescription(String description) {
    myDescription = description;
  }

  /**
   * set the parameters instance variable
   * @param parameters inputs to the simulation (ex: probCatch in Spreading Fire)
   */
  private void setParameters(Map<String, String> parameters) {
    myParameters = parameters;
  }

  /**
   * set the stateColors instance variable
   * @param stateColors a key that indicates the meaning of the color of each cell
   */
  private void setStateColors(Map<StateInterface, Color> stateColors) {
    myStateColors = stateColors;
  }

  /**
   * create a new display window to present relevant simulation information to user such as type of simulation, title,
   * description, author, parameters, and state color mappings
   * @param stage JavaFX stage for holding all the elements
   * @param title title of the border pane (what's displayed at the top of the window)
   * @return a new Scene the size of SCREEN_WIDTH and SCREEN_HEIGHT static constants with a BorderPane
   * root as the root element
   */
  public void createDisplayBox(Stage stage, String title) {
    // create display box to hold relevant information
    stage.setTitle(title);
    BorderPane root = new BorderPane();
    VBox vbox = new VBox();
    vbox.setAlignment(Pos.CENTER);
    root.setCenter(vbox);
    // add relevant text to scene
    addSimulationInformationToScene(vbox);
    // create and set the scene
    Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * Adds all the relevant simulation information to the new scene
   * @param vbox a vertical box pane that holds all the text elements
   */
  private void addSimulationInformationToScene(VBox vbox) {
    addTextToScene(vbox, "Type: " + myType);
    addTextToScene(vbox, "Title: " + myTitle);
    addTextToScene(vbox, "Author: " + myAuthor);
    addTextToScene(vbox, "Description: " + myDescription);
    addTextToScene(vbox, "Parameters: ");
    // Case when there are no parameters to the method
    if (myParameters.isEmpty()) {
      addTextToScene(vbox, "None");
    }
    for (String param : myParameters.keySet()) {
      addTextToScene(vbox, param + ": " + myParameters.get(param));
    }
    addTextToScene(vbox, "StateColors: ");
    StateColor standardColors = new StateColor();
    for (StateInterface state : myStateColors.keySet()) {
      addTextToScene(vbox, state.getStateValue() + ": " + standardColors.getColor(myStateColors.get(state)));
    }
  }

  /**
   * method for creating new text and setting the font, weight, and size
   * @param text the text string to display
   * @return the new Text object
   */
  private Text createText(String text) {
    Text infoText = new Text(text);
    infoText.setTextAlignment(TextAlignment.CENTER);
    infoText.setFont(Font.font("Verdana", FontWeight.BOLD, TEXT_SIZE));
    infoText.setWrappingWidth(SCREEN_WIDTH * .75);
    return infoText;
  }

  /**
   * adds a text element to a vertical box object
   * @param vbox the vertical box pane
   * @param text the text string to add to the pane
   */
  private void addTextToScene(VBox vbox, String text) {
    vbox.getChildren().add(createText(text));
  }

}