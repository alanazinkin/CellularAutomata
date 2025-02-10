package cellsociety.Controller;

import javafx.stage.Stage;

public class SimulationMaker {

  /**
   * method is responsible for creating an entire new simulation instance
   * @throws Exception
   */
  public void makeNewSimulation() throws Exception {
    SimulationController controller = new SimulationController();
    controller.init(new Stage(), controller);
  }
}
