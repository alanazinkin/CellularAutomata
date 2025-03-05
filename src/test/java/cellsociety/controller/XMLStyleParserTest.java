package cellsociety.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;



class XMLStyleParserTest {
    private XMLStyleParser parser;
    private File validXMLFile;
    private File invalidXMLFile;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws IOException {
        parser = new XMLStyleParser();

        validXMLFile = tempDir.resolve("bacteria_default.xml").toFile();
        String validXMLContent = """
            <style>
                <id>BacteriaSimulation</id>
                <cell-states>
                    <state name=\"Healthy\" color=\"green\"/>
                    <state name=\"Infected\" color=\"red\"/>
                </cell-states>
                <grid>
                    <edge-policy>finite</edge-policy>
                    <cell-shape>square</cell-shape>
                </grid>
                <display>
                    <grid-outline>true</grid-outline>
                    <color-theme>dark</color-theme>
                    <animation-speed>1.5</animation-speed>
                </display>
            </style>
        """;
        Files.writeString(validXMLFile.toPath(), validXMLContent);

        invalidXMLFile = tempDir.resolve("invalid.xml").toFile();
        Files.writeString(invalidXMLFile.toPath(), "<style><id>Invalid");
    }

    @Test
    void testParseStyleFile_validXML() {
        assertDoesNotThrow(() -> {
            SimulationStyle style = parser.parseStyleFile(validXMLFile.getAbsolutePath());
            assertNotNull(style);
            assertEquals("dark", style.getColorTheme().toString().toLowerCase());
        });
    }

    @Test
    void testParseStyleFile_invalidXML() {
        Exception exception = assertThrows(ConfigurationException.class, () -> {
            parser.parseStyleFile(invalidXMLFile.getAbsolutePath());
        });
        assertTrue(exception.getMessage().contains("Invalid XML format"));
    }

    @Test
    void testValidateStyleXMLStructure_valid() throws Exception {
        Document document = loadDocument(validXMLFile);
        assertDoesNotThrow(() -> parser.validateStyleXMLStructure(document));
    }

    @Test
    void testValidateStyleXMLStructure_invalid() throws Exception {
        String invalidContent = """
            <invalidRoot>
                <id>InvalidSimulation</id>
            </invalidRoot>
        """;
        File invalidFile = Files.createTempFile("invalid_structure", ".xml").toFile();
        Files.writeString(invalidFile.toPath(), invalidContent);
        Document document = loadDocument(invalidFile);

        ConfigurationException exception = assertThrows(ConfigurationException.class, () -> {
            parser.validateStyleXMLStructure(document);
        });
        assertTrue(exception.getMessage().contains("Root element must be <style>"));
    }

    private Document loadDocument(File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(file);
    }
}
