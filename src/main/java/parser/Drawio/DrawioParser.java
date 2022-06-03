package parser.Drawio;

import enums.Cardinality;
import model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DrawioParser implements parser.Parser {
    private static final Logger logger = Logger.getLogger(DrawioParser.class.getName());

    @Override
    public Diagram parse(Document doc){
        logger.log(Level.FINE, "Parsing with drawio parser");
        Diagram diagram = new Diagram();

        NodeList cells = doc.getElementsByTagName(XMLTags.XML_TAG.getValue());
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

        //Separate vertices
        for(Element e : vertexes){
            String name = e.getAttribute(XMLTags.NAME_ATTRIBUTE.getValue()).strip();
            String id = e.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()).strip();
            String styleValue = e.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).strip();
            if(styleValue.matches(Token.ATTRIBUTE.getValue())){
                if(styleValue.matches(Token.MULTIVALUED_ATTRIBUTE.getValue())){
                    diagram.addVertex(new Attribute(name, id, false, false, true));
                }
                else if(styleValue.matches(Token.KEY_ATTRIBUTE.getValue())){
                    diagram.addVertex(new Attribute(name, id, false, true, false));
                }
                //EXCEPTION
                else if(name.matches(Token.WEAK_ATTRIBUTE.getValue())){
                    Pattern pattern = Pattern.compile(">.*<");
                    Matcher matcher = pattern.matcher(name);
                    matcher.find();
                    name = matcher.group();
                    diagram.addVertex(new Attribute(name.substring(1, name.length()-1), id, true, false, false));
                }
                else{
                    diagram.addVertex(new Attribute(name, id, false, false, false));
                }
            }
            else if(styleValue.matches(Token.RELATIONSHIP.getValue())){
                if(styleValue.matches(Token.WEAK_RELATIONSHIP.getValue())){
                    diagram.addVertex(new Relationship(name, id, true));
                }
                else{
                    diagram.addVertex(new Relationship(name, id, false));
                }
            }
            //its entity
            else{
                if(styleValue.matches(Token.WEAK_ENTITY.getValue())){
                    diagram.addVertex(new Entity(name, id,true));
                }
                else{
                    diagram.addVertex(new Entity(name, id,false));
                }
            }
        }

        //Connecting edges
        for(Element e : edges){
            String id = e.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()).strip();
            Optional<DataClass> source = diagram.findVertexById(e.getAttribute(XMLTags.TARGET_ATTRIBUTE.getValue()).strip());
            Optional<DataClass> target = diagram.findVertexById(e.getAttribute(XMLTags.SOURCE_ATTRIBUTE.getValue()).strip());
            String styleValue = e.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).strip();
            List<String> edgeDescriptions = descriptions.stream()
                            .filter((desc)-> desc.getAttribute(XMLTags.PARENT_ATTRIBUTE.getValue()).equals(id))
                            .map((i)-> i.getAttribute(XMLTags.NAME_ATTRIBUTE.getValue()))
                            .collect(Collectors.toList());
            if(styleValue.matches(Token.CONNECTION.getValue())){
                if(edgeDescriptions.isEmpty()){
                    diagram.addEdges(new Connection(id, source.isPresent()? source.get() : null,
                            target.isPresent()? target.get() : null));
                }
                else{
                    diagram.addEdges(new ValuedConnection(id, source.isPresent()? source.get() : null,
                            target.isPresent()? target.get() : null, edgeDescriptions));
                }
            }
            else{
                diagram.addEdges(new Generalization(id, source.isPresent()? source.get() : null,
                        target.isPresent()? target.get() : null, edgeDescriptions));
            }
        }

        System.out.println(diagram.toString());

        return diagram;
    }

}
