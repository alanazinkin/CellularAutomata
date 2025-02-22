package cellsociety.view;

import static java.lang.Integer.parseInt;

import cellsociety.controller.SimulationController;
import cellsociety.controller.SimulationUI;
import cellsociety.view.gridview.GridView;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;

public class UserController {


  private ResourceBundle myResources;
  private SimulationController myController;
  private Map<String, String> mySimulationResourceMap;

  /**
   * constructor to make a new UserController instance
   *
   * @param resources            ResourceBundle used to display the text in the user-selected
   *                             language
   * @param simulationController the simulation controller that manages the simulation
   */
  public UserController(ResourceBundle resources, SimulationController simulationController) {
    myResources = resources;
    myController = simulationController;
    mySimulationResourceMap = SimulationController.retrieveImmutableConfigResourceBundle();
  }

  /**
   * adds an element to a specified pane
   *
   * @param element a Control object (button, slider, combobox etc.)
   * @param pane    a Pane object (gridPane, VBox, HBox etc.)
   * @throws Exception throws a null pointer exception if the pane is null or uninitialized
   */
  public void addElementToPane(Control element, Pane pane) throws NullPointerException {
    if (pane != null && element != null) {
      pane.getChildren().add(element);
    } else {
      throw new NullPointerException("Pane is null");
    }
  }

  /**
   * create and initialize a new button and add it to the Control Bar
   *
   * @param label   of the button that is displayed to user
   * @param handler is the action that occurs upon clicking button
   */
  public Button makeButton(String label, EventHandler<ActionEvent> handler) {
    Button button = new Button(label);
    button.setOnAction(handler);
    return button;
  }

  /**
   * make a new slider with Text label centered above it
   */
  public Slider makeSpeedSlider() {
    Slider slider = new Slider(0.1, 5, 1);
    slider.setSnapToTicks(true);
    slider.setShowTickLabels(true);
    slider.setShowTickMarks(true);
    slider.setMajorTickUnit(1.0);
    slider.setMinorTickCount(9);
    slider.setBlockIncrement(0.1);
    slider.setMaxWidth(parseInt(mySimulationResourceMap.getOrDefault("window.width", "1000")) * .5);
    makeSliderAdjustToSpeed(slider);
    return slider;
  }

  private void makeSliderAdjustToSpeed(Slider slider) {
    slider.valueProperty().addListener((observable, oldValue, newValue) -> {
      myController.setSimulationSpeed(newValue.doubleValue());
    });
  }

  /**
   * initializes and sets the action to set the theme of the simulation
   *
   * @return a new ComboBox<String> with the theme options
   */
  public ComboBox<String> makeThemeComboBox(SimulationView simulationView, Scene scene) {
    ComboBox<String> themeSelector = new ComboBox<>();
    themeSelector.setPromptText(myResources.getString("SelectTheme"));
    themeSelector.getItems().addAll("Dark", "Light");
    selectTheme(simulationView, scene, themeSelector);
    return themeSelector;
  }

  /**
   * sets the action of the ComboBox to select the theme chosen by the user
   *
   * @param simulationView the SimulatonView object holding all the simulation information
   * @param scene          the Scene of the simulation
   * @param themeSelector  comboBox with the theme options
   */
  public void selectTheme(SimulationView simulationView, Scene scene,
      ComboBox<String> themeSelector) {
    themeSelector.setOnAction(e -> {
      String selectedThemeColor = themeSelector.getValue();
      if (selectedThemeColor != null) {
        try {
          simulationView.setTheme(selectedThemeColor, scene);
        } catch (FileNotFoundException ex) {
          SimulationUI.displayAlert(myResources.getString("Error"),
              myResources.getString("InvalidSimType"));
        }
      } else {
        SimulationUI.displayAlert(myResources.getString("Error"),
            myResources.getString("NoThemeSelected"));
      }
    });
  }

  /**
   * creates toggle button that switches on/off the grid lines
   *
   * @param label    label of the button
   * @param gridView GridView object
   * @return a new GridLines toggle button
   */
  public Button makeGridLinesToggleButton(String label, GridView gridView) {
    Button toggleButton = new Button(label);
    gridView.setGridLinesToggleButtonAction(gridView, toggleButton);
    return toggleButton;
  }

  /**
   * creates toggle button that flips the grid horizontally
   *
   * @param label button label
   * @param gridView gridview that is flipped horizontally
   * @return newly created flip grid button
   */
  public Button makeFlipGridButton(String label, GridView gridView) {
    Button flipGrid = new Button(label);
    flipGrid.setOnAction(e -> {gridView.renderGridFlippedVertically();});
    return flipGrid;
  }


}
