package parser.Erdia;

import enums.Cardinality;
import enums.Coverage;
import enums.Disjointness;
import exception.CorruptedXmlException;
import exception.ParserException;
import lombok.extern.java.Log;
import model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import parser.Parser;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;
/**
 * The ErdiaParser class is a parser for XML files of Entity-Relationship diagram
 * exported from online modeling tool erdia.stejspet.cz. Its output is populated
 * and connected Diagram.
 * @author Adam Skarda
 */
@Log
public class ErdiaParser implements Parser {
    /**
     * Resulting diagram to be populated, connected and transformed
     */
    private final Diagram diagram = new Diagram();

    /**
     * XML elements identified by xml attribute "edge"
     */
    private final List<Element> edges = new LinkedList<>();

    /**
     * XML elements identified by xml attribute "vertex"
     */
    private final List<Element> vertices = new LinkedList<>();

    /**
     * Extracts given document elements into corresponding Diagram components.
     * Goes through document elements separating them into edges and vertices then parses
     * such elements as Connections and DataClasses.
     *
     * @param document document of edge and vertex elements
     * @return Diagram of parsed document elements
     * @throws CorruptedXmlException if any element parsing errors occur
     * @see Diagram
     * @see Element
     */
    @Override
    public Diagram parse(Document document) throws CorruptedXmlException {
        log.log(Level.FINE, "Parsing with Erdia parser");
        try{
            NodeList cells = document.getElementsByTagName(XMLTags.CELL.getValue());
            categorizeElements(cells);
            addVerticesToDiagram();
            addEdgesToDiagram();
            addCompositeIdentifiersToDiagram();
        }
        catch (RuntimeException e){
            throw new CorruptedXmlException(e);
        }
        return diagram;
    }


    /**
     * Sorts XML nodes into corresponding vertex and edge categories
     * @param cells NodeList with Element nodes
     * @see Element
     */
    public void categorizeElements(NodeList cells){
        for(int i = 0; i< cells.getLength(); i++){
            Node cell = cells.item(i);
            if(Node.ELEMENT_NODE == cell.getNodeType()){
                Element element = (Element) cell;
                if(element.hasAttribute(XMLTags.EDGE_ATTRIBUTE.getValue())){
                    edges.add(element);
                }
                else if(element.hasAttribute(XMLTags.VERTEX_ATTRIBUTE.getValue())){
                    vertices.add(element);
                }
            }
        }
    }

    /**
     * Adds Connections to this diagram which link Diagram DataClasses.
     * Goes through given xml elements with edge attribute finds their source and target
     * and creates a connection based on the style attribute and additional information
     * from vertex elements.
     *
     * @see DataClass
     * @see Diagram
     * @see Connection
     */
    private void addEdgesToDiagram(){
        for(Element edge : edges){
            try{
                String sourceId = edge.getAttribute(XMLTags.SOURCE_ATTRIBUTE.getValue()).strip();
                String targetId = edge.getAttribute(XMLTags.TARGET_ATTRIBUTE.getValue()).strip();
                DataClass source = diagram.findVertexById(sourceId).orElse(null);
                DataClass target = diagram.findVertexById(targetId).orElse(null);

                if(Objects.nonNull(source) && Objects.nonNull(target)) {
                    if (edge.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).contains(Tokens.ATTRIBUTE_CONNECTOR.getValue())) {
                        addAttributeConnection(edge, source, target);

                    } else if (edge.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).contains(Tokens.RELATIONSHIP_CONNECTOR.getValue())) {
                        addRelationshipConnection(edge, source, target);
                    }
                }
                else if(edge.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).contains(Tokens.SPECIALIZATION.getValue())){
                    addGeneralization(edge, sourceId, target);
                }
            }
            catch (RuntimeException e){
                log.log(Level.WARNING, "Exception while parsing edge ", e);
            }
        }
        diagram.organizeConnections();
    }

    /**
     * Adds a hierarchical generalization connection to the diagram
     * @param edge Edge element connecting source XML hierarchy tag and target child entity
     * @param sourceId XML tag id of connecting hierarchy
     * @param target Child Entity from diagram to be connected to its parent through hierarchy
     */
    private void addGeneralization(Element edge, String sourceId, DataClass target){
        String id = edge.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()).strip();

        Element sourceHierarchy = vertices.stream()
                .filter((vert)-> vert.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()).equals(sourceId))
                .findAny()
                .orElseThrow(() ->
                        new ParserException("Could not find source hierarchy id=" + sourceId +
                                " for Generalization id=" + id));

        Element hierarchyInfo = (Element) sourceHierarchy.getFirstChild();

        //Finds a target entity for the generalization connection
        //through the edge connecting associated hierarchy and the target entity
        DataClass generalizationTarget = diagram.findVertexById(
                        edges.stream()
                                .filter((generalization)-> generalization.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue())
                                        .contains(Tokens.GENERALIZATION.getValue()) &&
                                        generalization.getAttribute(XMLTags.SOURCE_ATTRIBUTE.getValue())
                                                .contains(sourceId))
                                .findAny()
                                .orElseThrow(()-> new ParserException("Could not find Specialization "
                                        + " edge  with  source hierarchy id=" + sourceId
                                ))
                                .getAttribute(XMLTags.TARGET_ATTRIBUTE.getValue())
                                .strip())
                .orElseThrow(()->
                        new ParserException("Could not find source entity forming a hierarchy with id=" + sourceId));

        Connection generalization = Generalization.GeneralizationBuilder().id(id)
                .source(target)
                .target(generalizationTarget)
                .covering(Coverage
                        .decideCoverage(hierarchyInfo.getAttribute(XMLTags.COVERAGE_ATTRIBUTE.getValue())))
                .disjointness(Disjointness
                        .decideDisjointness(hierarchyInfo
                                .getAttribute(XMLTags.DISJOINTNESS_ATTRIBUTE.getValue())))
                .build();

        diagram.addEdge(generalization);
        target.addConnection(generalization);
        generalizationTarget.addConnection(generalization);
        log.log(Level.FINER, String.format("Added connection: %s", generalization));
    }

    /**
     * Adds Connection joining relationship and an Entity to the diagram
     * @param edge Edge element connecting source and target DataClasses
     * @param source Diagram vertex either Entity or Relationship
     * @param target Diagram vertex either Entity or Relationship
     */
    private void addRelationshipConnection(Element edge, DataClass source, DataClass target){
        String id = edge.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()).strip();
        Element innerElement = (Element) edge.getFirstChild();
        Connection relationshipConnection = Connection.builder().id(id)
                .source(source)
                .target(target)
                .cardinality(Cardinality.decideCardinality(
                        innerElement.getAttribute(XMLTags.CARDINALITY_MIN_ATTRIBUTE.getValue()),
                        innerElement.getAttribute(XMLTags.CARDINALITY_MAX_ATTRIBUTE.getValue())))
                .build();
        relationshipConnection.addDescription(innerElement.getAttribute(XMLTags.NAME_ATTRIBUTE.getValue()));

        diagram.addEdge(relationshipConnection);
        source.addConnection(relationshipConnection);
        target.addConnection(relationshipConnection);

        log.log(Level.FINER, String.format("Added connection: %s", relationshipConnection));
    }

    /**
     * Adds Attribute connection to the diagram
     * @param edge Edge element connecting source and target
     * @param source Diagram vertex
     * @param target Diagram vertex
     */
    private void addAttributeConnection(Element edge, DataClass source, DataClass target){
        String id = edge.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()).strip();

        Element originalAttribute = (Element) vertices.stream()
                .filter((vert) -> vert.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()).equals(source.getId()))
                .findAny()
                .orElseThrow(() ->
                        new ParserException("Could not find source attribute id=" + source.getId() +
                                " for AttributeConnector id=" + id))
                .getFirstChild();

        Connection attributeConnection = Connection.builder().id(id)
                .source(source)
                .target(target)
                .cardinality(Cardinality.decideCardinality(
                        originalAttribute.getAttribute(XMLTags.CARDINALITY_MIN_ATTRIBUTE.getValue()),
                        originalAttribute.getAttribute(XMLTags.CARDINALITY_MAX_ATTRIBUTE.getValue())))
                .build();

        diagram.addEdge(attributeConnection);
        source.addConnection(attributeConnection);
        target.addConnection(attributeConnection);

        log.log(Level.FINER, String.format("Added connection: %s", attributeConnection));
    }

    /**
     * Populates this Diagram with vertices (DataClasses) parsed from given elements.
     * Parses xml elements with vertex attribute by style attribute to DataClasses - Attribute, Entity, Relationship.
     * Conserves id, name and key values of parsed elements. Does not infer multivalued attributes and
     * weak entities, composite identifier members.
     *
     * @see DataClass
     * @see Diagram
     */
    private void addVerticesToDiagram(){
        for(Element vertex : vertices){
            Element innerElement = (Element) vertex.getFirstChild();
            if(vertex.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).contains(Tokens.ENTITY.getValue())){

                Entity entity = new Entity(innerElement.getAttribute(XMLTags.NAME_ATTRIBUTE.getValue()),
                        vertex.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()), false);
                diagram.addVertex(entity);
                log.log(Level.FINER, String.format("Added entity: %s", entity));
            }
            else if(vertex.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).contains(Tokens.RELATIONSHIP.getValue())){

                Relationship relationship = new Relationship(innerElement.getAttribute(XMLTags.NAME_ATTRIBUTE.getValue()),
                        vertex.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()), false);
                diagram.addVertex(relationship);
                log.log(Level.FINER, String.format("Added relationship: %s", relationship));

            }
            else if(vertex.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).contains(Tokens.ATTRIBUTE.getValue())){

                Attribute attribute = new Attribute(innerElement.getAttribute(XMLTags.NAME_ATTRIBUTE.getValue()),
                        vertex.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()),
                        innerElement.getAttribute(XMLTags.KEY_ATTRIBUTE.getValue()).equals("1"));
                diagram.addVertex(attribute);
                log.log(Level.FINER, String.format("Added attribute: %s", attribute));
            }
        }
    }

    /**
     * Adds composite identifiers to the Diagram.
     * Edges must be first organized before adding Composites.
     *
     * @see Diagram
     * @see Composite
     */
    private void addCompositeIdentifiersToDiagram(){
        for(Element vertex : vertices){
            try{
                if(vertex.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).contains(Tokens.COMPOSITE_ID.getValue())){
                    String vertexId = vertex.getAttribute(XMLTags.ID_ATTRIBUTE.getValue());

                    List<Connection> compositeMemberEdges = edges.stream()
                            .filter((edge)->edge.getAttribute(XMLTags.SOURCE_ATTRIBUTE.getValue()).contains(vertexId))
                            .map((edge)->diagram.findEdgeById(
                                            edge.getAttribute(XMLTags.TARGET_ATTRIBUTE.getValue()))
                                    .orElse(null))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    if(compositeMemberEdges.isEmpty()){
                        continue;
                    }

                    //is always entity in organized diagram
                    Entity associatedEntity = (Entity) compositeMemberEdges.get(0).getTarget();

                    Composite composite = new Composite(associatedEntity, vertexId);
                    composite.addAllCompositeMembers(compositeMemberEdges);
                    diagram.addComposite(composite);
                    log.log(Level.FINER, String.format("Added composite: %s", composite));
                }
            }
            catch (RuntimeException e){
                log.log(Level.WARNING, "Exception while parsing edge ", e);
            }
        }
    }
}
