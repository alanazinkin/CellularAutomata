package cellsociety.controller;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

class XMLDocumentUtil {
    /**
     * Retrieves the text content of the first occurrence of the specified tag from the XML document.
     *
     * @param doc The XML document to search.
     * @param tagName The name of the tag to retrieve content from.
     * @return The text content of the tag, or {@code null} if the tag does not exist.
     */
    public static String getElementContent(Document doc, String tagName) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return null;
    }
}
