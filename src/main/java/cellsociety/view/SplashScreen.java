package cellsociety.view;

import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SplashScreen {

  private Pane layout;

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

  public ComboBox<String> makeComboBox(String prompt, List<String> options) {
    // make language selector combo Box
    ComboBox<String> selector = new ComboBox<>();
    selector.setPromptText(prompt);
    selector.getItems().addAll(options);
    layout.getChildren().add(selector);
    return selector;
  }

  public Button makeEnterButton() {
    Button enterButton = new Button("Enter");
    layout.getChildren().add(enterButton);
    return enterButton;
  }


}
