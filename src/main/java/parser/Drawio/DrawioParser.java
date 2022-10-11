package parser.Drawio;

import enums.Cardinality;
import enums.Coverage;
import enums.Disjointness;
import exception.CorruptedXmlException;
import lombok.extern.java.Log;
import model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author Adam Skarda
 */
//TODO javadoc, logging
@Log
public class DrawioParser implements parser.Parser {
    /**
     *
     */
    private final Diagram diagram = new Diagram();
    /**
     *
     */
    private final List<Element> edges = new LinkedList<>();
    /**
     *
     */
    private final List<Element> vertices = new LinkedList<>();
    /**
     *
     */
    private final List<Element> descriptions = new LinkedList<>();

    /**
     * Extracts given document elements into corresponding Diagram components.
     * Goes through document elements separating them into edges and vertices then parses
     * such elements as Connections and DataClasses.
     *
     * @param doc document of edge and vertex elements
     * @return Diagram of parsed document elements
     * @throws CorruptedXmlException if any element parsing errors occur
     * @see Diagram
     * @see Element
     */
    @Override
    public Diagram parse(Document doc) throws CorruptedXmlException {
        log.log(Level.FINE, "Parsing with drawio parser");
        try {
            NodeList cells = doc.getElementsByTagName(XMLTags.CELL.getValue());
            categorizeElements(cells);
            addVerticesToDiagram();
            addEdgesToDiagram();
            addCompositesToDiagram();
        }
        catch(RuntimeException e){
            throw new CorruptedXmlException(e);
        }

        return diagram;
    }

    /**
     *
     * @param cells
     */
    private void categorizeElements(NodeList cells){
        for (int i = 0; i < cells.getLength(); i++) {
            Node cell = cells.item(i);
            if (Node.ELEMENT_NODE == cell.getNodeType()) {
                Element element = (Element) cell;
                if (element.hasAttribute(XMLTags.PARENT_ATTRIBUTE.getValue())) {
                    if (element.hasAttribute(XMLTags.EDGE_ATTRIBUTE.getValue())) {
                        edges.add(element);
                    } else if (element.hasAttribute(XMLTags.VERTEX_ATTRIBUTE.getValue())) {
                        if (element.getAttribute(XMLTags.PARENT_ATTRIBUTE.getValue()).strip().equals("1")) {
                            vertices.add(element);
                        } else {
                            descriptions.add(element);
                        }
                    }
                }
            }
        }
    }


    /**
     * Adds DataClasses parsed from given Elements to the given Diagram object.
     * Goes through the given list and creates appropriate DataClasses by parsing the element information.
     * Corresponding DataClasses are by style attribute matching.
     *
     * @see Diagram
     * @see Element
     */
    private void addVerticesToDiagram(){
        for(Element e : vertices){
            String name = e.getAttribute(XMLTags.NAME_ATTRIBUTE.getValue()).strip();
            String id = e.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()).strip();
            String styleValue = e.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).strip();

            if(styleValue.matches(Tokens.ATTRIBUTE.getValue())){
                diagram.addVertex(new Attribute(name, id, false));
            }
            else if(styleValue.matches(Tokens.RELATIONSHIP.getValue())){
                diagram.addVertex(new Relationship(name, id));
            }
            else{
                if(styleValue.matches(Tokens.WEAK_ENTITY.getValue())){
                    diagram.addVertex(new Entity(name, id));
                }
                else{
                    diagram.addVertex(new Entity(name, id));
                }
            }
        }
    }

    /**
     *
     * @see Element
     */
    private void addEdgesToDiagram(){
        for(Element edge : edges){
            try{
                String styleValue = edge.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).strip();
                if(styleValue.matches(Tokens.CONNECTION.getValue())){
                    addConnection(edge);
                }
                else{
                    addGeneralization(edge);
                }
            }
            catch(RuntimeException e){
                log.log(Level.WARNING, String.format("Exception while parsing edge %s", e));
            }
        }
    }

    /**
     *
     * @param edge
     */
    private void addGeneralization(Element edge){
        String id = edge.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()).strip();
        DataClass source = diagram.findVertexById(
                edge.getAttribute(XMLTags.TARGET_ATTRIBUTE.getValue()).strip()).orElse(null);
        DataClass target = diagram.findVertexById(
                edge.getAttribute(XMLTags.SOURCE_ATTRIBUTE.getValue()).strip()).orElse(null);

        List<String> edgeDescriptions = descriptions.stream()
                .filter((desc)-> desc.getAttribute(XMLTags.PARENT_ATTRIBUTE.getValue()).equals(id))
                .map((i)-> i.getAttribute(XMLTags.NAME_ATTRIBUTE.getValue()))
                .collect(Collectors.toList());

        Coverage coverage = edgeDescriptions.stream()
                .map(String::strip)
                .map(Coverage::decideCoverage)
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);

        Disjointness disjointness = edgeDescriptions.stream()
                .map(String::strip)
                .map(Disjointness::decideDisjointness)
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);

        diagram.addEdge(Generalization.GeneralizationBuilder().id(id)
                .source(source)
                .target(target)
                .covering(coverage)
                .disjointness(disjointness)
                .build());
    }

    /**
     *
     * @param edge
     */
    private void addConnection(Element edge){
        String id = edge.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()).strip();
        DataClass source = diagram.findVertexById(edge.getAttribute(XMLTags.TARGET_ATTRIBUTE.getValue()).strip())
                .orElse(null);
        DataClass target = diagram.findVertexById(edge.getAttribute(XMLTags.SOURCE_ATTRIBUTE.getValue()).strip())
                .orElse(null);

        List<String> edgeDescriptions = descriptions.stream()
                .filter((desc)-> desc.getAttribute(XMLTags.PARENT_ATTRIBUTE.getValue()).equals(id))
                .map((i)-> i.getAttribute(XMLTags.NAME_ATTRIBUTE.getValue()))
                .collect(Collectors.toList());

        Cardinality cardinality = edgeDescriptions.stream().map(Cardinality::decideCardinality)
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);

        diagram.addEdge( Connection.builder().id(id)
                        .source(source)
                        .target(target)
                        .cardinality(cardinality)
                        .build());
    }

    /**
     *
     */
    private void addCompositesToDiagram(){
        //TODO Find a way to model composites in Drawio
    }

}
