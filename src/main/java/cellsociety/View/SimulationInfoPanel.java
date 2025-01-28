package cellsociety.View;

import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This class creates a display window that informs the user of relevant simulation information
 * such as its type, name, author, description, state colors, and relevant parameters
 */
public class SimulationInfoPanel {

  private static int SCREEN_WIDTH = 800;
  private static int SCREEN_HEIGHT = 600;
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
   * description of the simulation type
   */
  private String myDescription;
  /**
   * inputs to the simulation (ex: probCatch in Spreading Fire)
   */
  private List<List<String>> myParameters;
  /**
   * key indicating what each color means for a given simulation
   */
  private List<String> myStateColors;

  /**
   * constructor for creating a simulation information display
   * @param type of simulation
   * @param title is the full name of the simulation
   * @param author of the XML file
   * @param description of the simulation type
   * @param parameters to the simulation
   * @param stateColors is a key that indicates the meaning of the color of each cell
   */
  public SimulationInfoPanel(String type, String title, String author, String description, List<List<String>> parameters, List<String> stateColors) {
    setType(type);
    setTitle(title);
    setAuthor(author);
    setDescription(description);
    setParameters(parameters);
    setStateColors(stateColors);
  }

  private void setType(String type) {
    myType = type;
  }

  private void setTitle(String title) {
    myTitle = title;
  }

  private void setAuthor(String author) {
    myAuthor = author;
  }

  private void setDescription(String description) {
    myDescription = description;
  }

  private void setParameters(List<List<String>> parameters) {
    myParameters = parameters;
  }

  private void setStateColors(List<String> stateColors) {
    myStateColors = stateColors;
  }

  private Scene createDisplayBox(Stage primaryStage, String title) {
    // create display box to hold relevant information
    primaryStage.setTitle(title);
    BorderPane root = new BorderPane();
    VBox vbox = new VBox();
    vbox.setAlignment(Pos.CENTER);
    root.setCenter(vbox);
    // add relevant text to scene
    addSimulationInformationToScene(root);
    // create and set the scene
    Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
    primaryStage.setScene(scene);
    primaryStage.show();
    return scene;
  }

  private void addSimulationInformationToScene(BorderPane root) {
    addTextToScene(root, "Type: " + myType);
    addTextToScene(root, "Title: " + myTitle);
    addTextToScene(root, "Author: " + myAuthor);
    addTextToScene(root, "Description: " + myDescription);
    addTextToScene(root, "Parameters:");
    for (List<String> params : myParameters) {
      addTextToScene(root, params.getFirst() + params.get(1));
    }
    addTextToScene(root, "StateColors: " + myStateColors);
  }

  private Text createText(String text) {
    return new Text(text);
  }

  private void addTextToScene(BorderPane root, String text) {
    root.getChildren().add(createText(text));
  }


}
