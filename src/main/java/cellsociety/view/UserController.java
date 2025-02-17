package cellsociety.view;

import static java.lang.Integer.parseInt;

import cellsociety.controller.FileRetriever;
import cellsociety.controller.SimulationController;
import cellsociety.controller.SimulationUI;
import cellsociety.view.gridview.GridView;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
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
import javafx.stage.Stage;

public class UserController {

  private boolean hasGridLines = true;

  private ResourceBundle myResources;
  private SimulationController myController;
  private Map<String, String> mySimulationResourceMap;
  private SimulationUI myUI;

  public UserController(ResourceBundle resources, SimulationController simulationController) {
    myResources = resources;
    myController = simulationController;
    mySimulationResourceMap = SimulationController.retrieveImmutableConfigResourceBundle();
    myUI = myController.getUI();
  }

  //TODO write better error message
  public void addElementToPane(Control element, Pane pane) throws Exception {
    if (pane != null) {
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
    slider.setPrefWidth(
        parseInt(mySimulationResourceMap.getOrDefault("window.width", "1000")) * .4);
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

  public ComboBox<String> makeThemeComboBox(SimulationView simulationView, Scene scene) {
    ComboBox<String> themeSelector = new ComboBox<>();
    themeSelector.setPromptText(myResources.getString("SelectTheme"));
    themeSelector.getItems().addAll("Dark", "Light");
    themeSelector.setOnAction(e -> {
      String selectedThemeColor = themeSelector.getValue();
      if (selectedThemeColor != null) {
        try {
          simulationView.setTheme(selectedThemeColor, scene);
        } catch (FileNotFoundException ex) {
          throw new RuntimeException(ex);
        }
      } else {
        myUI.displayAlert(myResources.getString("Error"),
            myResources.getString("NoThemeSelected"));
      }
    });
    return themeSelector;
  }

  public Button makeGridLinesToggleButton(String label, GridView gridView) {
    Button toggleButton = new Button(label);
    setGridLinesToggleButtonAction(gridView, toggleButton);
    return toggleButton;
  }

  private void setGridLinesToggleButtonAction(GridView gridView, Button toggleButton) {
    toggleButton.setOnAction(e -> {
      if (hasGridLines) {
        gridView.removeGridLines();
        hasGridLines = false;
      } else {
        gridView.addGridLines();
        hasGridLines = true;
      }
    });
  }
}
