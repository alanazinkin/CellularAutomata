package cellsociety.controller;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;

/**
 * Parser for reading and validating configuration files in XML format.
 * This class handles parsing of simulation parameters, grid dimensions, and initial states from an XML
 * file and provides validation of the configuration.
 *
 * @author angelapredolac
 */
public class XMLParser extends BaseConfigParser{

  private static final String DEFAULT_PROPERTIES_PATH = "cellsociety.controller.simulation";

  private final XMLStructureValidator structureValidator;
  private final XMLInitialStateParser initialStateParser;
  private final XMLParameterParser parameterParser;
  private final XMLCellShapeParser cellShapeParser;
  private final FileValidator fileValidator;
  private final XMLTilingParser tilingParser;

  /**
   * Constructs an XMLParser with a default XMLFileValidator and properties path.
   */
  public XMLParser() {
    super(new XMLFileValidator(), DEFAULT_PROPERTIES_PATH);
    this.structureValidator = new XMLStructureValidator();
    this.initialStateParser = new XMLInitialStateParser();
    this.parameterParser = new XMLParameterParser();
    this.cellShapeParser = new XMLCellShapeParser();
    this.fileValidator = new FileValidator();
    this.tilingParser = new XMLTilingParser();
  }

  /**
   * Parses an XML configuration file and converts it into a SimulationConfig object.
   *
   * @param filePath The path to the XML file containing simulation configuration.
   * @return A SimulationConfig object populated with parsed values.
   * @throws ConfigurationException If there are issues with XML parsing or validation.
   */
  @Override
  protected SimulationConfig parseConfig(String filePath) throws ConfigurationException {
    try {
      Document document = loadXMLDocument(filePath);
      return parseXMLDocument(document);
    } catch (ParserConfigurationException e) {
      throw new ConfigurationException("XML parser configuration error: " + e.getMessage());
    } catch (SAXException e) {
      throw new ConfigurationException("Invalid XML format: " + e.getMessage());
    } catch (IOException e) {
      throw new ConfigurationException("Error reading file: " + e.getMessage());
    }
  }

  /**
   * Parses an XML file and returns a SimulationConfig object with configuration details.
   *
   * @param filePath The path to the XML configuration file.
   * @return A populated SimulationConfig object.
   * @throws ConfigurationException If an error occurs during parsing or validation.
   */
  public SimulationConfig parseXMLFile(String filePath) throws ConfigurationException {
    fileValidator.validateFile(filePath);
    try {
      Document document = loadXMLDocument(filePath);
      return parseXMLDocument(document);
    } catch (ParserConfigurationException e) {
      throw new ConfigurationException("XML parser configuration error: " + e.getMessage());
    } catch (SAXException e) {
      throw new ConfigurationException("Invalid XML format: " + e.getMessage());
    } catch (Exception e) {
      throw new ConfigurationException("Error reading file: " + e.getMessage());
    }
  }

  /**
   * Loads an XML document from a file path.
   *
   * @param filePath The path to the XML file.
   * @return The loaded Document object.
   * @throws ParserConfigurationException If there are parser configuration issues.
   * @throws SAXException If there are XML parsing issues.
   * @throws IOException If there are file reading issues.
   */
  private Document loadXMLDocument(String filePath)
          throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new File(filePath));
    document.getDocumentElement().normalize();
    return document;
  }

  /**
   * Parses the XML document to create a SimulationConfig object.
   *
   * @param document The XML document to parse.
   * @return A populated SimulationConfig object.
   * @throws ConfigurationException If validation or parsing errors occur.
   */
  private SimulationConfig parseXMLDocument(Document document) throws ConfigurationException {
    structureValidator.validateXMLStructure(document);
    structureValidator.validateRequiredFields(document);

    SimulationConfig config = new SimulationConfig();

    String simType = XMLDocumentUtil.getElementContent(document, "type");
    validateSimulationType(simType);
    config.setSimulationType(simType);

    config.setTitle(XMLDocumentUtil.getElementContent(document, "title"));
    config.setAuthor(XMLDocumentUtil.getElementContent(document, "author"));
    config.setDescription(XMLDocumentUtil.getElementContent(document, "description"));

    String widthStr = XMLDocumentUtil.getElementContent(document, "width");
    String heightStr = XMLDocumentUtil.getElementContent(document, "height");
    GridDimensionParser.setGridDimensions(widthStr, heightStr, config);

    initialStateParser.validateAndSetInitialStates(document, config);

    config.setParameters(parameterParser.parseParametersWithValidation(document));
    config.setCellShapeValues(cellShapeParser.parseCellShapesWithValidation(document));
    config.setTiling(tilingParser.parseTilingWithValidation(document));
    return config;
  }
}