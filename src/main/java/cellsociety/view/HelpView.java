package cellsociety.view;

import static java.lang.Integer.parseInt;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.io.File;

/**
 * Help View displays an HTML help view for each simulation
 */
public class HelpView {

  private static final File helpFile = new File("src/main/resources/cellsociety/view/help.html");
  private static final ResourceBundle helpResources = ResourceBundle.getBundle(
      HelpView.class.getPackage().getName() + ".help");

  /**
   * opens up a help documentation window providing information about the program to assist users
   */
  public void showHelpWindow() {
    Stage helpStage = new Stage();
    helpStage.setTitle("Simulation Help");

    WebView webView = new WebView();
    webView.getEngine().load(helpFile.toURI().toString());

    Scene scene = new Scene(webView, parseInt(helpResources.getString("help.window.width")),
        parseInt(helpResources.getString("help.window.height")));
    helpStage.setScene(scene);
    helpStage.show();
  }
}

