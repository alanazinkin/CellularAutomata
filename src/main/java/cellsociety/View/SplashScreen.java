package cellsociety.View;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SplashScreen {
  public Scene showSplashScreen(Stage stage, String title, double width, double height) {
    stage.setTitle(title);
    StackPane layout = new StackPane();

    Scene scene = new Scene(layout, width, height);
    stage.setScene(scene);
    stage.show();
    return scene;
  }

  public void handleSplashScreenEvent(Scene scene, Stage newStage) {
    scene.setOnKeyPressed(event -> {
      closeSplashScreen(newStage);
    });
    scene.setOnMouseClicked(event -> {
      closeSplashScreen(newStage);
    });
    newStage.setOnCloseRequest((WindowEvent event) -> {
      closeSplashScreen(newStage);
    });
  }

  public void closeSplashScreen(Stage stage) {
    stage.close();
  }

}
