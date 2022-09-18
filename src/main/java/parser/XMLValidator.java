package parser;

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

public class XMLValidator {
    private final File file;

    public XMLValidator(File file) throws IOException{
        this.file = file;
        if(!file.isFile() || !file.canRead()){
            throw new IOException("Invalid file");
        }
    }


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
