package parser.Drawio;

import exception.CorruptedXmlException;
import lombok.extern.java.Log;
import model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 */
@Log
public class DrawioParser implements parser.Parser {

    /**
     *
     * @param doc
     * @return
     * @throws CorruptedXmlException
     */
    @Override
    public Diagram parse(Document doc) throws CorruptedXmlException {
        log.log(Level.FINE, "Parsing with drawio parser");
        Diagram diagram = new Diagram();

        NodeList cells = doc.getElementsByTagName(XMLTags.CELL.getValue());
        List<Element> edges = new LinkedList<>();
        List<Element> descriptions = new LinkedList<>();
        List<Element> vertices = new LinkedList<>();

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
                            vertices.add(element);
                        }
                        else{
                            descriptions.add(element);
                        }
                    }
                }
            }
        }

        addVerticesToDiagram(vertices, diagram);
        addEdgesToDiagram(edges, descriptions, diagram);

        System.out.println(diagram);
        return diagram;
    }

    /**
     *
     * @param vertices
     * @param diagram
     */
    private void addVerticesToDiagram(List<Element> vertices, Diagram diagram){
        for(Element e : vertices){
            String name = e.getAttribute(XMLTags.NAME_ATTRIBUTE.getValue()).strip();
            String id = e.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()).strip();
            String styleValue = e.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).strip();
            if(styleValue.matches(Tokens.ATTRIBUTE.getValue())){
                if(styleValue.matches(Tokens.MULTIVALUED_ATTRIBUTE.getValue())){
                    diagram.addVertex(new Attribute(name, id, false, false, false, true));
                }
                else if(styleValue.matches(Tokens.KEY_ATTRIBUTE.getValue())){
                    diagram.addVertex(new Attribute(name, id, false, true, false, false));
                }
                else if(name.matches(Tokens.WEAK_ATTRIBUTE.getValue())){
                    Pattern pattern = Pattern.compile(">.*<");
                    Matcher matcher = pattern.matcher(name);
                    matcher.find();
                    name = matcher.group();
                    diagram.addVertex(new Attribute(name.substring(1, name.length()-1),
                            id, true, false, false, false));
                }
                else{
                    diagram.addVertex(new Attribute(name, id, false, false, false, false));
                }
            }
            else if(styleValue.matches(Tokens.RELATIONSHIP.getValue())){
                if(styleValue.matches(Tokens.WEAK_RELATIONSHIP.getValue())){
                    diagram.addVertex(new Relationship(name, id, true));
                }
                else{
                    diagram.addVertex(new Relationship(name, id, false));
                }
            }
            else{
                if(styleValue.matches(Tokens.WEAK_ENTITY.getValue())){
                    diagram.addVertex(new Entity(name, id,true));
                }
                else{
                    diagram.addVertex(new Entity(name, id,false));
                }
            }
        }
    }

    private void addEdgesToDiagram(List<Element> edges, List<Element> descriptions, Diagram diagram){
        for(Element e : edges){
            String id = e.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()).strip();
            Optional<DataClass> source = diagram.findVertexById(e.getAttribute(XMLTags.TARGET_ATTRIBUTE.getValue()).strip());
            Optional<DataClass> target = diagram.findVertexById(e.getAttribute(XMLTags.SOURCE_ATTRIBUTE.getValue()).strip());
            String styleValue = e.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).strip();
            List<String> edgeDescriptions = descriptions.stream()
                    .filter((desc)-> desc.getAttribute(XMLTags.PARENT_ATTRIBUTE.getValue()).equals(id))
                    .map((i)-> i.getAttribute(XMLTags.NAME_ATTRIBUTE.getValue()))
                    .collect(Collectors.toList());
            if(styleValue.matches(Tokens.CONNECTION.getValue())){
                if(edgeDescriptions.isEmpty()){
                    diagram.addEdge(new Connection(id, source.orElse(null),
                            target.orElse(null), edgeDescriptions));
                }
                else{
                    diagram.addEdge(new Connection(id, source.orElse(null),
                            target.orElse(null), edgeDescriptions));
                }
            }
            else{
                diagram.addEdge(new Generalization(id, source.orElse(null),
                        target.orElse(null), edgeDescriptions));
            }
        }
    }

}
