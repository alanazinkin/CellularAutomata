package cellsociety.controller;

public class XMLFileValidator extends BaseFileValidator {
    @Override
    protected boolean isValidFileExtension(String extension) {
        return extension.equalsIgnoreCase("xml");
    }

    @Override
    protected String getExpectedFileExtension() {
        return "XML";
    }
}
