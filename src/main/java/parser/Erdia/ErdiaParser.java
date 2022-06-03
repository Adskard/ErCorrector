package parser.Erdia;

import model.Diagram;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import parser.Parser;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ErdiaParser implements Parser {
    private static final Logger logger = Logger.getLogger(ErdiaParser.class.getName());
    public ErdiaParser() {
    }

    @Override
    public Diagram parse(Document document) {
        logger.log(Level.FINE, "Parsing with ERdia parser");
        Diagram diagram = new Diagram();

        NodeList cells = document.getElementsByTagName(XMLTags.XML_TAG.getValue());
        List<Element> edges = new LinkedList<>();
        List<Element> descriptions = new LinkedList<>();
        List<Element> vertexes = new LinkedList<>();

        //Go through DOM elements and extract information
        for(int i = 0; i< cells.getLength(); i++){
            Node cell = cells.item(i);
            if(Node.ELEMENT_NODE == cell.getNodeType()){
                Element element = (Element) cell;
                if(element.hasAttribute(XMLTags.PARENT_ATTRIBUTE.getValue())){
                    if(element.hasAttribute(XMLTags.EDGE_ATTRIBUTE.getValue())){
                        edges.add(element);
                    }
                    else if(element.hasAttribute(XMLTags.VERTEX_ATTRIBUTE.getValue())){
                        if(element.getAttribute(XMLTags.PARENT_ATTRIBUTE.getValue()).strip().equals("1")){
                            vertexes.add(element);
                        }
                        else{
                            descriptions.add(element);
                        }
                    }
                }
            }
        }
        return diagram;
    }
}
