package cellsociety.view;
import static org.junit.jupiter.api.Assertions.*;

import cellsociety.controller.SimulationController;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import util.DukeApplicationTest;

class SplashScreenTest extends DukeApplicationTest {

  private Stage testStage;
  private Pane layout;
  private SplashScreen splashScreen;
  private SimulationController mockSimulationController;

  @Override
  public void start(Stage stage) {
    // Set up the test stage
    testStage = stage;
    splashScreen = new SplashScreen();
    mockSimulationController = Mockito.mock(SimulationController.class);

  }

  @Test
  public void showSplashScreen_BasicTest_StageDisplaysCorrectly() {
    // GIVEN: A new instance of the class containing showSplashScreen()
    // WHEN: Calling the method under test
    runAsJFXAction(() -> splashScreen.showSplashScreen(testStage, "Test Title", 800, 600));
    // THEN: Verify that the stage title is correctly set
    assertEquals("Test Title", testStage.getTitle());
    // Verify that the scene is set and has the correct dimensions
    Scene scene = testStage.getScene();
    assertNotNull(scene);
    assertEquals(800, scene.getWidth());
    assertEquals(600, scene.getHeight());
    // Verify that the VBox contains the expected Text node
    layout = (VBox) scene.getRoot();
    assertNotNull(layout);
    assertFalse(layout.getChildren().isEmpty());
    // Check if the Text node contains "Cell Society Simulator"
    boolean textFound = layout.getChildren().stream()
        .anyMatch(node -> node instanceof Text && ((Text) node).getText()
            .equals("Cell Society Simulator"));
    assertTrue(textFound, "Splash screen should contain 'Cell Society Simulator' text.");
  }

  @Test
  public void makeComboBox_BasicTest_layoutContainsComboBox() {
    // GIVEN: Show the splash screen inside interact()
    interact(() -> splashScreen.showSplashScreen(testStage, "Test Title", 800, 600));
    // WHEN: Create the ComboBox inside interact()
    AtomicReference<ComboBox<String>> myTestBoxRef = new AtomicReference<>();
    interact(() -> myTestBoxRef.set(
        splashScreen.makeComboBox("Test Prompt", List.of("option1", "option2"))
    ));
    // Retrieve the actual ComboBox
    ComboBox<String> myTestBox = myTestBoxRef.get();
    // Get the layout from splashScreen (Assuming it's a VBox)
    Pane layout = (Pane) testStage.getScene().getRoot();
    // THEN: Verify the ComboBox is in the layout
    assertNotNull(myTestBox, "ComboBox should not be null");
    assertTrue(layout.getChildren().contains(myTestBox), "ComboBox should be in the layout");
  }

  @Test
  public void makeEnterButton_CreateButton_layoutContainsButton() {
    // GIVEN: Show the splash screen inside interact()
    interact(() -> splashScreen.showSplashScreen(testStage, "Test Title", 800, 600));
    // WHEN: Create the ComboBox inside interact()
    AtomicReference<Button> myButton = new AtomicReference<>();
    interact(() -> myButton.set(
        splashScreen.makeEnterButton()
    ));
    // Retrieve the actual ComboBox
    Button button = myButton.get();
    // Get the layout from splashScreen (Assuming it's a VBox)
    Pane layout = (Pane) testStage.getScene().getRoot();
    // THEN: Verify the ComboBox is in the layout
    assertEquals("Enter", button.getText());
    assertNotNull(button, "Button should not be null");
    assertTrue(layout.getChildren().contains(button), "Button should be in the layout");
  }

@Test
void makeSimulationComboBoxes_LayoutContainsComboBoxes() throws Exception {
  // GIVEN: Show the splash screen inside interact()
  interact(() -> splashScreen.showSplashScreen(testStage, "Test Title", 800, 600));
  // Use AtomicReference to store the ComboBox list inside interact()
  AtomicReference<List<ComboBox<String>>> comboBoxesRef = new AtomicReference<>();

  // WHEN: Create the ComboBoxes inside interact()
  interact(() -> {
    try {
      comboBoxesRef.set(splashScreen.makeSimulationComboBoxes(mockSimulationController));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  });
  // Retrieve the actual list of ComboBoxes
  List<ComboBox<String>> comboBoxes = comboBoxesRef.get();
  // Get the layout from splashScreen (Assuming it's a VBox or Pane)
  Pane layout = (Pane) testStage.getScene().getRoot();
  // THEN: Verify the ComboBoxes are in the layout
  assertNotNull(comboBoxes, "ComboBox list should not be null");
  assertFalse(comboBoxes.isEmpty(), "ComboBox list should not be empty");
  // Ensure each combo box is added to the layout
  for (ComboBox<String> comboBox : comboBoxes) {
    assertTrue(layout.getChildren().contains(comboBox), "ComboBox should be in the layout");
  }
}

  @Test
  void makeSimulationComboBoxes() {
  }
}
