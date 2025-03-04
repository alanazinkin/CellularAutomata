package cellsociety.controller;

import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.StateInterface;
import cellsociety.view.SimulationSelector;
import cellsociety.view.SimulationView;
import cellsociety.view.SplashScreen;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

/**
 * The SimulationUI class handles the graphical user interface for the simulation.
 * It manages the splash screen, main simulation window, and user interactions.
 * @author angelapredolac
 */
public class SimulationUI {

    private final ResourceBundle config;
    private Stage stage;
    private SimulationView simulationView;
    private ResourceBundle resources;
    private FileRetriever fileRetriever;
    private SimulationSelector simulationSelector;
    private String myLanguage;
    private String myThemeColor;
    private SimulationController controller;
    private GridPane gridPane;

    /**
     * Constructs a SimulationUI instance with the specified configuration.
     *
     * @param config the resource bundle containing configuration settings.
     */
    public SimulationUI(ResourceBundle config) {
        this.config = config;
        this.gridPane = new GridPane();
    }

    /**
     * Initializes the main simulation window.
     *
     * @param stage the primary stage of the application.
     * @param controller the simulation controller.
     * @throws Exception if an error occurs during initialization.
     */
    public void initialize(Stage stage, SimulationController controller) throws Exception {
        this.stage = stage;
        this.controller = controller;
        fileRetriever = new FileRetriever();
        setupSimulation(stage, myLanguage, myThemeColor, controller);
    }

  /**
   * Updates the simulation view with the given color mapping.
   *
   * @param colorMap the mapping of states to colors.
   */
  public void updateView(Map<StateInterface, String> colorMap, Map<StateInterface, Double> stateCountsMap)
      throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
    simulationView.updateGrid(colorMap);
    simulationView.updateIterationCounter();
    simulationView.updateCellCharts(stateCountsMap, colorMap);
  }

    /**
     * Initializes and displays the splash screen.
     *
     * @param stage the primary stage of the application.
     * @param controller the simulation controller.
     * @throws Exception if an error occurs during initialization.
     */
    public void initializeSplashScreen(Stage stage, SimulationController controller)
            throws Exception {
        this.stage = stage;
        SplashScreen screen = new SplashScreen();
        Stage splashStage = screen.showSplashScreen(
                new Stage(),
                config.getString("splash.title"),
                Integer.parseInt(config.getString("window.width")),
                Integer.parseInt(config.getString("window.height"))
        );
        setupSplashScreenControls(screen, splashStage, stage, controller);
    }

    /**
     * Configures the controls on the splash screen.
     *
     * @param screen the splash screen instance.
     * @param splashStage the splash screen stage.
     * @param mainStage the main application stage.
     * @param controller the simulation controller.
     * @throws Exception if an error occurs while setting up controls.
     */
    private void setupSplashScreenControls(SplashScreen screen, Stage splashStage,
                                           Stage mainStage, SimulationController controller)
            throws Exception {
        ComboBox<String> languageSelector = screen.makeComboBox(
                config.getString("language.selector"),
                Arrays.asList(config.getString("available.languages").split(","))
        );
        languageSelector.setValue(config.getString("default.language"));
        ComboBox<String> themeSelector = screen.makeComboBox(
                config.getString("theme.selector"),
                Arrays.asList(config.getString("available.themes").split(","))
        );
        themeSelector.setValue(config.getString("default.theme"));
        List<ComboBox<String>> simulationComboBoxes = screen.makeSimulationComboBoxes(controller);
        Button enterButton = screen.makeEnterButton();
        enterButton.setOnAction(e -> handleSplashScreenEnter(
                languageSelector, themeSelector, splashStage, mainStage, controller,
                simulationComboBoxes.getFirst(),
                simulationComboBoxes.get(1)));
    }

    /**
     * Handles the user input when entering the simulation from the splash screen.
     *
     * @param languageSelector the language selection combo box.
     * @param themeSelector the theme selection combo box.
     * @param splashStage the splash screen stage.
     * @param mainStage the main application stage.
     * @param controller the simulation controller.
     * @param simulationTypeSelector the simulation type selection combo box.
     * @param configFileComboBox the configuration file selection combo box.
     */
    private void handleSplashScreenEnter(ComboBox<String> languageSelector,
                                         ComboBox<String> themeSelector,
                                         Stage splashStage, Stage mainStage,
                                         SimulationController controller,
                                         ComboBox<String> simulationTypeSelector,
                                         ComboBox<String> configFileComboBox) {
        try {
            String selectedLanguage = Optional.ofNullable(languageSelector.getValue())
                    .orElseThrow(() -> new IllegalStateException("Language not selected"));
            String selectedTheme = Optional.ofNullable(themeSelector.getValue())
                    .orElseThrow(() -> new IllegalStateException("Theme not selected"));
            String selectedSimType = Optional.ofNullable(simulationTypeSelector.getValue())
                    .orElseThrow(() -> new IllegalStateException("Simulation Type not selected"));
            String selectedConfigFile = Optional.ofNullable(configFileComboBox.getValue())
                    .orElseThrow(() -> new IllegalStateException("Language not selected"));
            myLanguage = selectedLanguage;
            myThemeColor = selectedTheme;
            resources = ResourceBundle.getBundle(
                    SimulationController.DEFAULT_RESOURCE_PACKAGE + selectedLanguage);
            simulationSelector = new SimulationSelector(resources, controller);
            simulationSelector.respondToFileSelection(simulationTypeSelector, configFileComboBox, stage, resources);
            controller.selectSimulation(selectedSimType, selectedConfigFile, null, mainStage, controller);
            splashStage.close();
        } catch (Exception ex) {
            handleError("SetupError", ex);
        }
    }

    /**
     * Sets up the simulation view.
     *
     * @param stage the application stage.
     * @param language the selected language.
     * @param themeColor the selected theme color.
     * @param controller the simulation controller.
     */
    private void setupSimulation(Stage stage, String language, String themeColor,
                                 SimulationController controller) {
        try {
            SimulationConfig config = controller.getSimulationConfig();
            simulationView = new SimulationView(config, controller, resources);
            Simulation simulation = controller.getSimulation();
            Grid grid = controller.getGrid();
            Map<StateInterface, String> colorMap = simulation.getColorMap();
            simulationView.initView(stage, simulation, simulationView,
                    colorMap, grid, language, themeColor);
        } catch (Exception e) {
            handleError("SetupError", e);
        }
    }

    /**
     * Updates the simulation view with the given color mapping.
     *
     * @param colorMap the mapping of states to colors.
     */
    public void updateView(Map<StateInterface, String> colorMap)
            throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        simulationView.updateGrid(colorMap);
        simulationView.updateIterationCounter();
    }

    /**
     * Handles errors by displaying an alert with the error message.
     *
     * @param key the error message key.
     * @param e the exception that occurred.
     */
    public void handleError(String key, Exception e) {
        Platform.runLater(() -> {
            String title = getResourceString("Error");
            String message = String.format("%s: %s",
                    getResourceString(key),
                    e.getMessage());
            displayAlert(title, message);
        });
    }

    /**
     * Displays an alert message.
     *
     * @param title the title of the alert.
     * @param content the content of the alert message.
     */
    public static void displayAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().setId("myAlert");
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Retrieves a localized string from the resource bundle.
     *
     * @param key the key for the string.
     * @return the localized string.
     */
    private String getResourceString(String key) {
        return Optional.ofNullable(resources)
                .map(r -> r.getString(key))
                .orElse(key);
    }

    /**
     * Returns the resource bundle used by the simulation.
     *
     * @return the resource bundle.
     */
    public ResourceBundle getResources() {
        return resources;
    }

    /**
     * Sets whether grid outlines are visible.
     *
     * @param visible true to show grid outlines, false to hide them
     */
    public void setGridOutlineVisible(boolean visible) {
        gridPane.setGridLinesVisible(visible);
    }

    /**
     * Sets the color theme for the UI.
     *
     * @param theme the name of the theme to set
     */
    public void setColorTheme(String theme) {
        String cssFile;
        if (theme.equalsIgnoreCase("DARK")) {
            cssFile = "Dark.css";
        } else {
            cssFile = "Light.css";
        }

        String resourcePath = "/CSS/" + cssFile; // Match your directory name
        URL cssResource = getClass().getResource(resourcePath);

        if (cssResource == null) {
            System.err.println("ERROR: CSS file not found at: " + resourcePath);
            return;
        }

        if (stage != null && stage.getScene() != null) {
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(cssResource.toExternalForm());
        } else {
            System.out.println("Stage or scene not initialized. Theme will be applied later.");
            myThemeColor = theme;
        }
    }

    /**
     * Creates a menu for style operations.
     *
     * @return a Menu containing style-related operations
     */
    private Menu createStyleMenu() {
        Menu styleMenu = new Menu(resources.getString("StyleMenu"));

        MenuItem loadStyleItem = new MenuItem(resources.getString("LoadStyle"));
        loadStyleItem.setOnAction(e -> controller.chooseAndLoadStyle());

        MenuItem saveStyleItem = new MenuItem(resources.getString("SaveStyle"));
        saveStyleItem.setOnAction(e -> controller.saveStyle());

        styleMenu.getItems().addAll(loadStyleItem, saveStyleItem);

        Menu predefinedStylesMenu = new Menu(resources.getString("PredefinedStyles"));
        styleMenu.getItems().add(predefinedStylesMenu);

        addPredefinedStyleMenuItems(predefinedStylesMenu);

        return styleMenu;
    }

    /**
     * Adds predefined style menu items.
     *
     * @param menu the menu to add items to
     */
    private void addPredefinedStyleMenuItems(Menu menu) {
        File stylesDir = new File("CSS");
        if (stylesDir.exists() && stylesDir.isDirectory()) {
            File[] styleFiles = stylesDir.listFiles((dir, name) -> name.endsWith(".xml"));
            if (styleFiles != null) {
                for (File styleFile : styleFiles) {
                    MenuItem styleItem = new MenuItem(styleFile.getName().replace(".xml", ""));
                    styleItem.setOnAction(e -> controller.loadStyle(styleFile.getAbsolutePath()));
                    menu.getItems().add(styleItem);
                }
            }
        }
    }
}
