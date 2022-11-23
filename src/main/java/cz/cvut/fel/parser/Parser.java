package cz.cvut.fel.parser;

import cz.cvut.fel.exception.CorruptedXmlException;
import cz.cvut.fel.model.Diagram;
import org.w3c.dom.Document;

/**
 * Interface defining a Diagram parser.
 * @author Adam Skarda
 */
public interface Parser {


    /**
     * Populates diagram with Vertices and links them with Edges.
     *
     * @param document Document to be parsed
     * @return Populated and connected Diagram
     * @throws CorruptedXmlException if Document cannot be parsed
     * @see Diagram
     */
    Diagram parse(Document document) throws CorruptedXmlException;
}
