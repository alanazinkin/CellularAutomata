package cellsociety.Controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FileRetriever {
  private static final List<String> SIMULATION_TYPES = List.of(
      "Game of Life",
      "Spreading of Fire",
      "Percolation",
      "Wa-Tor World",
      "Schelling State"
  );
  /**
   * creates list of different simulation types
   * @return list of all the different simulation types
   */
  public List<String> getSimulationTypes() {
    return SIMULATION_TYPES;
  }

  /**
   * retrieve all the files associated with a specific simulation type or throw file not found exception if no files exist
   * @param simulationType type of simulation as a string (Ex: Percolation, GameofLife)
   * @return collection of files associated with the type
   * @throws FileNotFoundException if there are no files associated with the simulation type
   */
  public Collection<String> getFileNames(String simulationType) throws FileNotFoundException {
    String simulationFilePath = getSimulationTypeFilePath(simulationType);
    List<File> simulationFiles = getFilesInFolder(simulationFilePath);
    List<String> fileNames = new ArrayList<>();
    for (File file : simulationFiles) {
      fileNames.add(file.getName()); // Use just the name, not full path
    }
    return fileNames;
  }

  public String getSimulationTypeFilePath(String simulationType) throws FileNotFoundException {
    String basePath = "data";
    String folderExtension;
    switch (simulationType) {
      case "Game of Life": folderExtension = "/GameOfLife"; break;
      case "Spreading of Fire": folderExtension = "/SpreadingFire"; break;
      case "Percolation": folderExtension = "/Percolation"; break;
      case "Schelling State": folderExtension = "/SchellingState"; break;
      case "Wa-Tor World": folderExtension = "/WaTorWorld"; break;
      // potential point of abuse! Handle edge case:
      default: throw new FileNotFoundException("No files found for " + simulationType);
    }
    return basePath + folderExtension;
  }

  private List<File> getFilesInFolder(String folderPath) throws FileNotFoundException {
    File folder = new File(folderPath);
    if (!folder.exists() || !folder.isDirectory()) {
      System.err.println("Folder path: " + folderPath + " does not exist!");
      throw new FileNotFoundException(folderPath);
    }
    File[] files = folder.listFiles();
    if (files == null) { // Prevents NullPointerException
      return new ArrayList<>();
    }
    Arrays.sort(files); // Sort only if not null
    return new ArrayList<>(List.of(files));
  }

}
