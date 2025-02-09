package cellsociety.View;

import cellsociety.Controller.SimulationConfig;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SplashScreen {
  private Pane layout;
  public Stage showSplashScreen(Stage stage, SimulationConfig simulationConfig, String title, double width, double height) {
    stage.setTitle(title);
    Text titleText = new Text(simulationConfig.getType());
    layout = new VBox();
    layout.getChildren().add(titleText);

    Scene scene = new Scene(layout, width, height);
    stage.setScene(scene);
    stage.show();
    return stage;
  }

  public ComboBox<String> makeLanguageComboBox() {
    // make language selector combo Box
    ComboBox<String> languageSelector = new ComboBox<>();
    languageSelector.setPromptText("Select Language");
    languageSelector.getItems().addAll("English", "Spanish", "Italian");
    layout.getChildren().add(languageSelector);
    return languageSelector;
  }

  public Button makeEnterButton() {
    Button enterButton = new Button("Enter");
    layout.getChildren().add(enterButton);
    return enterButton;
  }


}
