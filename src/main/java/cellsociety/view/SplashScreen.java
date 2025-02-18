package cellsociety.view;

import cellsociety.controller.FileRetriever;
import cellsociety.controller.SimulationController;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SplashScreen {

  private static final ResourceBundle CONFIG = ResourceBundle.getBundle(
      SimulationController.class.getPackageName() + ".Simulation");
  private Pane layout;

  /**
   * displays the initial splash screen that allows the user to select a language, theme and
   * specific simulation
   *
   * @param stage  new stage that holds the splash screen
   * @param title  title of the splash screen
   * @param width  window width
   * @param height window height
   * @return the Stage parameter - initialized
   */
  public Stage showSplashScreen(Stage stage, String title, double width, double height) {
    stage.setTitle(title);
    Text titleText = new Text("Cell Society Simulator");
    layout = new VBox();
    layout.getChildren().add(titleText);

    Scene scene = new Scene(layout, width, height);
    stage.setScene(scene);
    stage.show();
    return stage;
  }

  /**
   * creates a new ComboBox
   *
   * @param prompt  initial prompt in the ComboBox
   * @param options list of options within the ComboBox
   * @return a new ComboBox
   */
  public ComboBox<String> makeComboBox(String prompt, List<String> options) {
    ComboBox<String> selector = new ComboBox<>();
    selector.setPromptText(prompt);
    selector.getItems().addAll(options);
    layout.getChildren().add(selector);
    return selector;
  }

  /**
   * creates an enter button
   *
   * @return a new enter Button object
   */
  public Button makeEnterButton() {
    Button enterButton = new Button("Enter");
    layout.getChildren().add(enterButton);
    return enterButton;
  }

  /**
   * creates simulation selector ComboBoxes
   *
   * @param simulationController SimulationController object responsible for managing simulation
   * @return a list of two new comboboxes for selecting a simulation
   * @throws Exception if file is non-existent
   */
  public List<ComboBox<String>> makeSimulationComboBoxes(SimulationController simulationController)
      throws Exception {
    SimulationSelector simSelector = new SimulationSelector(null, simulationController);
    FileRetriever fileRetriever = new FileRetriever();
    List<ComboBox<String>> simulationComboBoxes = simSelector.makeSimSelectorComboBoxes(
        "Select Simulation Type",
        "Select Config File",
        fileRetriever.getSimulationTypes()
    );
    for (ComboBox<String> simulationComboBox : simulationComboBoxes) {
      layout.getChildren().add(simulationComboBox);
    }
    return simulationComboBoxes;
  }
}
