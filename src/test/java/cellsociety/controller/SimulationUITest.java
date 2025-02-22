package cellsociety.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javafx.scene.control.Alert;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

class SimulationUITest extends DukeApplicationTest {

  @Test
  void initialize() {
  }

  @Test
  void initializeSplashScreen() {
  }

  @Test
  void updateView() {
  }

  @Test
  void handleError() {
  }

  @Test
  void displayAlert_MethodCalled_AlertIsDisplayed() {
    Alert mockAlert = mock(Alert.class);
    // Stub `showAndWait()` so it does nothing (prevents blocking UI)
    doNothing().when(mockAlert).showAndWait();
    // Call the method under test, replacing real alert creation
    SimulationUI.displayAlert("Test Title", "Test Content");
    // Verify that `showAndWait()` was called (meaning an alert was displayed)
    verify(mockAlert, times(1)).showAndWait();
  }

  @Test
  void getResources() {
  }
}