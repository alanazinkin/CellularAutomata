package cellsociety;

import static javafx.application.Application.launch;

import cellsociety.Controller.SimulationConfig;
import cellsociety.Controller.XMLParser;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
  private static final String FILE_PATH = "data/GOL1.xml";

  /**
   * entry point of simulation
   * @param primaryStage the primary stage for this application, onto which
   * the application scene can be set.
   * Applications may create other stages, if needed, but they will not be
   * primary stages.
   * @throws Exception
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    XMLParser xmlParser = new XMLParser();
    SimulationConfig mySimulationConfig = xmlParser.parseXMLFile(FILE_PATH);
    mySimulationConfig.init(primaryStage, mySimulationConfig);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
