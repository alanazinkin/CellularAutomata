package cellsociety.Controller;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class FileRetrieverTest {
  private static final List<String> SIM_TYPES = List.of("sim1", "sim2", "sim3");
  FileRetriever fileRetriever = new FileRetriever();


  /**
   * test to ensure that getSimulationTypes() throws a FileNotFoundException
   */
  @Test
  void getSimulationTypes_WhenNoFilesExist_ExceptionThrown() {
    String fakeSimulationType = "hello";
    assertThrows(FileNotFoundException.class, () -> fileRetriever.getFileNames(fakeSimulationType));
  }

  /**
   * tests that it retrieves the correct files when the simulation type does exist and there are files
   * @throws FileNotFoundException if there are no files associated with the realSimType
   */
  @Test
  void getSimulationTypes_WhenSimulationIsReal_AssertEquals() throws FileNotFoundException {
    String realSimType = "Game of Life";
    assertEquals(List.of("GOL1.xml", "GOL2.xml", "GOL3.xml", "GOL4.xml", "GOL5.xml"), fileRetriever.getFileNames(realSimType));
  }

  /**
   * empty file list is returned when there is a folder but no files in the folder
   * @throws FileNotFoundException
   */
  @Test
  void getSimulationTypes_WhenFolderExistsButNoFiles_NewList() throws FileNotFoundException {
    String realSimType = "Percolation";
    assertEquals(new ArrayList<>(), fileRetriever.getFileNames(realSimType));
  }


}