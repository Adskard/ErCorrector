package parser;

import exception.CorruptedXmlException;
import model.Diagram;
import org.w3c.dom.Document;

/**
 *
 */
public interface Parser {
    /**
     *
     * @param document
     * @return
     * @throws CorruptedXmlException
     */
    Diagram parse(Document document) throws CorruptedXmlException;
}
