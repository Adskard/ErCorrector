package cz.cvut.fel.parser;

import lombok.extern.java.Log;
import cz.cvut.fel.model.Diagram;
import org.w3c.dom.Document;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import cz.cvut.fel.parser.Drawio.DrawioParser;
import cz.cvut.fel.parser.Erdia.ErdiaParser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

/**
 * @author Adam Skarda
 */
@Log
public class XMLValidator {
    private final InputStream file;

    public XMLValidator(InputStream file) throws IOException{
        this.file = file;
        log.log(Level.FINEST, "XMLValidator was successfully created");
    }


    /**
     * Extracts diagram from validator file with an appropriate cz.cvut.fel.corrector.parser.
     * @return Diagram based on parsed information from XMLValidator file
     * @throws SAXException if any parse errors occur during DOM document parsing
     * @throws IOException if any IO errors occur during DOM document parsing or given XML format is not supported
     * @throws ParserConfigurationException if document could not be parsed with given configuration
     * @see DrawioParser
     * @see ErdiaParser
     */
    public Diagram extractDiagram() throws SAXException, IOException, ParserConfigurationException {
        log.log(Level.INFO, "Parsing diagram");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        Node root = doc.getFirstChild();
        root.normalize();

        Parser parser;

        //select parser
        if(doc.getDocumentElement().hasAttribute("host") &&
                doc.getDocumentElement().getAttribute("host").equals("app.diagrams.net")){
            parser = new DrawioParser();
        }
        else if(doc.getDocumentElement().getTagName().equals("mxGraphModel")){
            parser = new ErdiaParser();
        }
        else throw new IOException("Unsupported XML format");


        file.close();
        return parser.parse(doc);
    }
}
