package parser;

import lombok.extern.java.Log;
import model.Diagram;
import org.w3c.dom.Document;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import parser.Drawio.DrawioParser;
import parser.Erdia.ErdiaParser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * @author Adam Skarda
 */
@Log
public class XMLValidator {
    private final File file;

    public XMLValidator(File file) throws IOException{
        this.file = file;
        if(!file.isFile() || !file.canRead()){
            throw new IOException("Invalid file");
        }
        log.log(Level.FINEST, "XMLValidator for file " + file.getName() + " was successfully created");
    }


    /**
     * Extracts diagram from validator file with an appropriate parser.
     * @return Diagram based on parsed information from XMLValidator file
     * @throws SAXException if any parse errors occur during DOM document parsing
     * @throws IOException if any IO errors occur during DOM document parsing or given XML format is not supported
     * @throws ParserConfigurationException if document could not be parsed with given configuration
     * @see DrawioParser
     * @see ErdiaParser
     */
    public Diagram extractDiagram() throws SAXException, IOException, ParserConfigurationException {
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

        return parser.parse(doc);
    }
}
