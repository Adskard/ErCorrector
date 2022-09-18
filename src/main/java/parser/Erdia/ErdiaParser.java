package parser.Erdia;

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
import parser.Parser;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class ErdiaParser implements Parser {
    public ErdiaParser() {
    }

    @Override
    public Diagram parse(Document document) throws CorruptedXmlException {
        log.log(Level.FINE, "Parsing with ERdia parser");
        Diagram diagram = new Diagram();

        NodeList cells = document.getElementsByTagName(XMLTags.CELL.getValue());
        List<Element> edges = new LinkedList<>();
        List<Element> vertices = new LinkedList<>();

        try{
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
            addVerticesToDiagram(vertices, diagram);
            addEdgesToDiagram(edges, vertices, diagram);
            fixWeakEntities(edges, vertices, diagram);
            addCompositeIdentifiers(edges, vertices, diagram);
        }
        catch (RuntimeException e){
            e.printStackTrace();
            throw new CorruptedXmlException(e);
        }

        System.out.println(diagram);
        return diagram;
    }

    private Diagram addEdgesToDiagram(List<Element> edges, List<Element> vertices, Diagram diagram){
        for(Element edge : edges){

            String id = edge.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()).strip();
            String sourceId = edge.getAttribute(XMLTags.SOURCE_ATTRIBUTE.getValue()).strip();
            String targetId = edge.getAttribute(XMLTags.TARGET_ATTRIBUTE.getValue()).strip();

            Optional<DataClass> source = diagram.findVertexById(sourceId);
            Optional<DataClass> target = diagram.findVertexById(targetId);

            if(source.isPresent() && target.isPresent()) {
                if (edge.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).contains(Tokens.ATTRIBUTE_CONNECTOR.getValue())) {
                    Element originalAttribute = (Element) vertices.stream()
                            .filter((vert) -> vert.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()).equals(source.get().getId()))
                            .findAny().orElse(null).getFirstChild();

                    diagram.addEdge(Connection.builder().id(id)
                            .source(source.get())
                            .target(target.get())
                            .cardinality(Cardinality.decideCardinality(
                                    originalAttribute.getAttribute(XMLTags.CARDINALITY_MIN_ATTRIBUTE.getValue()),
                                    originalAttribute.getAttribute(XMLTags.CARDINALITY_MAX_ATTRIBUTE.getValue())))
                            .build());

                } else if (edge.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).contains(Tokens.RELATIONSHIP_CONNECTOR.getValue())) {
                    Element innerElement = (Element) edge.getFirstChild();

                    diagram.addEdge(Connection.builder().id(id)
                            .source(source.get())
                            .target(target.get())
                            .cardinality(Cardinality.decideCardinality(
                                    innerElement.getAttribute(XMLTags.CARDINALITY_MIN_ATTRIBUTE.getValue()),
                                    innerElement.getAttribute(XMLTags.CARDINALITY_MAX_ATTRIBUTE.getValue())))
                            .build());

                }
            }
            else if(edge.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).contains(Tokens.GENERALIZATION.getValue())){
                Element associatedHierarchy = vertices.stream()
                        .filter((vert)-> vert.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()).equals(sourceId))
                        .findAny().get();

                Element hierarchyInfo = (Element) associatedHierarchy.getFirstChild();

                //Finds a target entity for the generalization connection
                //through the edge connecting associated hierarchy and the target entity
                Optional<DataClass> generalizationSource = diagram.findVertexById(
                            edges.stream()
                            .filter((specialization)-> specialization.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue())
                                    .contains(Tokens.SPECIALIZATION.getValue()) &&
                                    specialization.getAttribute(XMLTags.SOURCE_ATTRIBUTE.getValue())
                                    .contains(associatedHierarchy.getAttribute(XMLTags.ID_ATTRIBUTE.getValue())))
                            .findAny().get()
                            .getAttribute(XMLTags.TARGET_ATTRIBUTE.getValue())
                        .strip());

                diagram.addEdge(Generalization.GeneralizationBuilder().id(id)
                        .source(generalizationSource.get())
                        .target(target.get())
                        .covering(Coverage
                                .decideCovering(hierarchyInfo.getAttribute(XMLTags.COVERAGE_ATTRIBUTE.getValue())))
                        .disjointness(Disjointness
                                .decideDisjointness(hierarchyInfo
                                        .getAttribute(XMLTags.DISJOINTNESS_ATTRIBUTE.getValue())))
                        .build());
            }
        }
        return diagram;
    }

    private Diagram addVerticesToDiagram(List<Element> vertices, Diagram diagram){
        for(Element vertex : vertices){
            Element innerElement = (Element) vertex.getFirstChild();
            if(vertex.hasAttribute(XMLTags.STYLE_ATTRIBUTE.getValue())){
                if(vertex.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).contains(Tokens.ENTITY.getValue())){
                    diagram.addVertex(new Entity(innerElement.getAttribute(XMLTags.NAME_ATTRIBUTE.getValue()),
                            vertex.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()), true));

                }
                else if(vertex.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).contains(Tokens.RELATIONSHIP.getValue())){
                    diagram.addVertex(new Relationship(innerElement.getAttribute(XMLTags.NAME_ATTRIBUTE.getValue()),
                            vertex.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()), false));

                }
                else if(vertex.getAttribute(XMLTags.STYLE_ATTRIBUTE.getValue()).contains(Tokens.ATTRIBUTE.getValue())){
                    diagram.addVertex(new Attribute(innerElement.getAttribute(XMLTags.NAME_ATTRIBUTE.getValue()),
                            vertex.getAttribute(XMLTags.ID_ATTRIBUTE.getValue()),
                            false,
                            innerElement.hasAttribute(XMLTags.KEY_ATTRIBUTE.getValue()),
                            false,
                            false
                            ));

                }
            }
        }
        return diagram;
    }

    private Diagram fixWeakEntities(List<Element> edges, List<Element> vertices, Diagram diagram){
        return diagram;
    }

    private Diagram addCompositeIdentifiers(List<Element> edges, List<Element> vertices, Diagram diagram){
        return diagram;
    }
}
