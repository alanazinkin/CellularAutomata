package cellsociety.controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class XMLParserTest {
    private XMLParser xmlParser;
    private FileValidator mockFileValidator;

    @BeforeEach
    void setUp() {
        xmlParser = spy(new XMLParser());
        mockFileValidator = mock(FileValidator.class);

        try {
            java.lang.reflect.Field fileValidatorField = XMLParser.class.getDeclaredField("fileValidator");
            fileValidatorField.setAccessible(true);
            fileValidatorField.set(xmlParser, mockFileValidator);
        } catch (Exception e) {
            fail("Could not set up mock file validator: " + e.getMessage());
        }
    }

    @Test
    void testParseXMLFile_ValidFile(@TempDir Path tempDir) throws Exception {
        File validXmlFile = createValidXmlFile(tempDir);

        doNothing().when(mockFileValidator).validateFile(validXmlFile.getAbsolutePath());

        SimulationConfig config = xmlParser.parseXMLFile(validXmlFile.getAbsolutePath());

        assertNotNull(config);
        assertEquals("Game of Life", config.getType());
        assertEquals("Conway's Game of Life", config.getTitle());
        assertEquals("John Conway", config.getAuthor());
        assertNotNull(config.getParameters());
        assertNotNull(config.getCellShapeMap());
    }

    @Test
    void testParseXMLFile_FileValidationFails() throws ConfigurationException {
        doThrow(new ConfigurationException("Invalid file"))
                .when(mockFileValidator).validateFile(anyString());

        assertThrows(ConfigurationException.class, () -> {
            xmlParser.parseXMLFile("nonexistent.xml");
        });
    }

    @Test
    void testParseXMLFile_InvalidXMLFormat(@TempDir Path tempDir) throws IOException, ConfigurationException {
        File invalidXmlFile = createInvalidXmlFile(tempDir);

        doNothing().when(mockFileValidator).validateFile(invalidXmlFile.getAbsolutePath());

        assertThrows(ConfigurationException.class, () -> {
            xmlParser.parseXMLFile(invalidXmlFile.getAbsolutePath());
        });
    }

    @Test
    void testParseXMLFile_MissingRequiredFields(@TempDir Path tempDir) throws IOException, ConfigurationException {
        File incompleteXmlFile = createIncompleteXmlFile(tempDir);

        doNothing().when(mockFileValidator).validateFile(incompleteXmlFile.getAbsolutePath());

        assertThrows(ConfigurationException.class, () -> {
            xmlParser.parseXMLFile(incompleteXmlFile.getAbsolutePath());
        });
    }

    @Test
    void testParseXMLFile_NonExistentFile() {
        assertThrows(ConfigurationException.class, () -> {
            xmlParser.parseXMLFile("/path/to/nonexistent/file.xml");
        });
    }

    private File createValidXmlFile(Path tempDir) throws IOException {
        String validXmlContent = """
        <?xml version="1.0" encoding="UTF-8"?>
        <simulation>
            <type>Game of Life</type>
            <title>Conway's Game of Life</title>
            <author>Angela Predolac</author>
            <description>Classic cellular automaton simulation</description>
            <width>50</width>
            <height>50</height>
            <initial-states>
                <state>alive</state>
                <state>dead</state>
            </initial-states>
            <parameters>
                <param name="survival-min">2</param>
                <param name="survival-max">3</param>
            </parameters>
        </simulation>
        """;

        File xmlFile = tempDir.resolve("valid_simulation.xml").toFile();
        Files.writeString(xmlFile.toPath(), validXmlContent);
        return xmlFile;
    }

    private File createInvalidXmlFile(Path tempDir) throws IOException {
        String invalidXmlContent = """
        <?xml version="1.0" encoding="UTF-8"?>
        <simulation>
            <unclosed-tag>
        """;

        File xmlFile = tempDir.resolve("invalid_simulation.xml").toFile();
        Files.writeString(xmlFile.toPath(), invalidXmlContent);
        return xmlFile;
    }

    private File createIncompleteXmlFile(Path tempDir) throws IOException {
        String incompleteXmlContent = """
        <?xml version="1.0" encoding="UTF-8"?>
        <simulation>
            <type>Incomplete Simulation</type>
        </simulation>
        """;

        File xmlFile = tempDir.resolve("incomplete_simulation.xml").toFile();
        Files.writeString(xmlFile.toPath(), incompleteXmlContent);
        return xmlFile;
    }
}