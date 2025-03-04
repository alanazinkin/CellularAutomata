package cellsociety.view;

import cellsociety.view.gridview.GridView;
import static java.lang.Double.parseDouble;

import cellsociety.controller.SimulationController;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

/**
 * This class creates a settings display which had buttons for selecting the specific type of grid
 * neighborhood, edge type, and cell shape
 *
 * @author Alana Zinkin
 */
public class GridSettingsDisplay {

  private static final List<String> NEIGHBORHOOD_TYPES = List.of("MOORE", "VON_NEUMANN",
      "EXTENDED_MOORE_", "MULTIPLE");
  private static final List<String> EDGE_TYPES = List.of("BOUNDED", "TOROIDAL", "MIRROR", "INFINITE");
  private static final List<String> CELL_TILING_TYPES = List.of("Default", "Triangle", "Parallelogram", "Hexagon");
  private DialogPane myDialogPane;
  private Pane myContainer;
  private SimulationController myController;
  private UserController myUserController;
  private ResourceBundle myResources;
  private ControlPanel myControlPanel;
  private static final ResourceBundle myGridSettingsResources = ResourceBundle.getBundle(
      GridSettingsDisplay.class.getPackageName() + ".GridSettings");

  /**
   * constructor for making a new GridSettings instance
   *
   * @param resources            language resource bundle for displaying button/combobox text
   * @param simulationController simulation controller that manages the simulation
   */
  public GridSettingsDisplay(ResourceBundle resources, SimulationController simulationController, ControlPanel controlPanel) {
    myDialogPane = new DialogPane();
    myDialogPane.setMinWidth(parseDouble(myGridSettingsResources.getString("dialog.pane.width")));
    myDialogPane.setMinHeight(parseDouble(myGridSettingsResources.getString("dialog.pane.height")));
    myContainer = new VBox(parseDouble(myGridSettingsResources.getString("vbox.spacing")));
    myDialogPane.setContent(myContainer);
    myResources = resources;
    myUserController = new UserController(myResources, simulationController);
    myController = simulationController;
    myControlPanel = controlPanel;
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
    ComboBox<String> tilingSelector = myUserController.makeComboBox(
        myResources.getString("SelectTilingType"),
        CELL_TILING_TYPES);
    myContainer.getChildren().addAll(neighborhoodSelector, edgeSelector, tilingSelector);
    myDialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    Button okButton = (Button) myDialogPane.lookupButton(ButtonType.OK);
    okButton.setOnAction(event -> {
      String neighborhoodType = neighborhoodSelector.getValue();
      String edgeType = edgeSelector.getValue();
      String tilingType = tilingSelector.getValue();
      if (neighborhoodType != null) {
        myController.setNeighborhoodStrategy(neighborhoodType);
      }
      if (edgeType != null) {
        myController.setEdgeStrategy(edgeType);
      }
      if (tilingType != null) {
        try {
          GridView newGridView = myController.setGridTiling(tilingType, myController.getSimulation().getColorMap(), myController.getGrid());

          Button gridLinesButton = myControlPanel.getGridLinesToggleButton();
          myUserController.setGridLinesButtonAction(newGridView, gridLinesButton);

          Button gridFlipButton = myControlPanel.getGridFlipButton();
          myUserController.setGridFlipButtonAction(newGridView, gridFlipButton);
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException | IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    });

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
