package parser;

import exception.CorruptedXmlException;
import model.Diagram;
import org.w3c.dom.Document;

/**
 * @author Adam Skarda
 */
public interface Parser {


    /**
     * Populates diagram with DataClasses and links them with Connections.
     *
     * @param document Document to be parsed
     * @return Populated and connected Diagram
     * @throws CorruptedXmlException if Document cannot be parsed
     * @see Diagram
     */
    Diagram parse(Document document) throws CorruptedXmlException;
}
