package cellsociety.view;

import static java.lang.Double.parseDouble;

import cellsociety.controller.SimulationController;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

public class GridSettingsDisplay {

  private static final List<String> NEIGHBORHOOD_TYPES = List.of("Moore", "Von Neumann",
      "Extended Moore");
  private static final List<String> EDGE_TYPES = List.of("Toroidal", "Mirror", "Infinite");
  private static final List<String> CELL_SHAPE_TYPES = List.of("Rectangle", "Triangle", "Circle",
      "Pentagon", "Hexagon");
  private DialogPane myDialogPane;
  private Pane myContainer;
  private UserController myUserController;
  private ResourceBundle myResources;
  private static final ResourceBundle myGridSettingsResources = ResourceBundle.getBundle(
      GridSettingsDisplay.class.getPackageName() + ".GridSettings");

  /**
   * constructor for making a new GridSettings instance
   *
   * @param resources            language resource bundle for displaying button/combobox text
   * @param simulationController simulation controller that manages the simulation
   */
  public GridSettingsDisplay(ResourceBundle resources, SimulationController simulationController) {
    myDialogPane = new DialogPane();
    myDialogPane.setMinWidth(parseDouble(myGridSettingsResources.getString("dialog.pane.width")));
    myDialogPane.setMinHeight(parseDouble(myGridSettingsResources.getString("dialog.pane.height")));
    myContainer = new VBox(parseDouble(myGridSettingsResources.getString("vbox.spacing")));
    myDialogPane.setContent(myContainer);
    myResources = resources;
    myUserController = new UserController(myResources, simulationController);
  }

  /**
   * initializes the grid settings display by adding relevant buttons and selectors
   */
  public void makeGridSettingsDisplay() {
    ComboBox<String> neighborhoodSelector = myUserController.makeComboBox(
        myResources.getString("SelectNeighborhoodType"),
        NEIGHBORHOOD_TYPES);
    ComboBox<String> edgeSelector = myUserController.makeComboBox(
        myResources.getString("SelectEdgeType"),
        EDGE_TYPES);
    ComboBox<String> cellShapeSelector = myUserController.makeComboBox(
        myResources.getString("SelectCellShape"),
        CELL_SHAPE_TYPES);
    myContainer.getChildren().addAll(neighborhoodSelector, edgeSelector, cellShapeSelector);
    myDialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
  }

  /**
   * opens the grid settings dialog pane when called
   *
   * @throws NullPointerException if the dialog pane was never initialized
   */
  public void openWindow() throws NullPointerException {
    if (myDialogPane != null) {
      Dialog<Void> dialog = new Dialog<>();
      dialog.setDialogPane(myDialogPane);
      dialog.initModality(Modality.APPLICATION_MODAL);
      dialog.setTitle(myResources.getString("GridSettings"));
      dialog.showAndWait();
    } else {
      throw new NullPointerException(myResources.getString("DialogPaneNull"));
    }

  }
}
