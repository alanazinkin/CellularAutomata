package cellsociety.controller;

import static cellsociety.controller.XMLDocumentUtil.getElementContent;

import java.util.Set;
import org.w3c.dom.Document;

public class XMLTilingParser {
  private static final Set<String> VALID_TILING = Set.of("Default", "Triangle");

  /**
   * parses the tiling of the configuration
   * @param document configuration file
   * @return String representing the tiling
   * @throws ConfigurationException if the tiling is not a valid type of tiling
   */
  public String parseTilingWithValidation(Document document)
      throws ConfigurationException {
    String tiling = getElementContent(document, "tiling");
    // set a default tiling
    if (tiling == null) {
      tiling = "Default";
    }
    if (!VALID_TILING.contains(tiling)) {
      throw new ConfigurationException(tiling + "is not a valid tiling.");
    }
    return tiling;
  }

}
