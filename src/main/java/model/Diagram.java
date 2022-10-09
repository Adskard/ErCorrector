package model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The Diagram class is an aggregator of all Entity-Relationship components.
 * Its methods serve to manipulate with these components.
 * @author Adam Skarda
 */
public class Diagram {
    /**
     * Diagram Entities, Relationships, Attributes
     */
    private final List<DataClass> vertices = new LinkedList<>();

    /**
     * Diagram Connections of associated vertices
     */
    private final List<Connection> edges = new LinkedList<>();

    /**
     * Diagram composite identifiers of entities
     */
    private final List<Composite> composites = new LinkedList<>();

    public Diagram() {
    }

    /**
     * Organizes diagram connections for standardization purposes.
     * Organizing a connection means switching target and source of connection where appropriate.
     * Every entity is a target of attribute connection and target of relationship connection.
     * Relationship is target of attribute connection and source of relationship connection.
     * Attribute is always a source of a connection.
     */
    public void organizeConnections(){
        edges.forEach((edge) -> edge.organize());
    }



    public boolean isEntityIdentifiedBySimpleKey(Entity entity){
        List<Connection> connections = getDataClassConnections(entity);
        return connections.stream().anyMatch((connection)->{
            if(connection.getSource() instanceof Attribute){
                return ((Attribute) connection.getSource()).getIsKey();
            }
            else if(connection.getTarget() instanceof Attribute){
                return ((Attribute) connection.getTarget()).getIsKey();
            }
            else {
                return false;
            }
        });
    }

    public List<Attribute> getDataClassAttributes(DataClass dataClass){
        return getDataClassConnections(dataClass).stream()
                .map((connection)->{
                    if(!connection.isFullyConnected()){
                        return null;
                    }
                    if(isConnectionSource(dataClass, connection)){
                        return connection.getTarget() instanceof Attribute? (Attribute)connection.getTarget() : null;
                    }
                    else{
                        return connection.getSource() instanceof Attribute ? (Attribute)connection.getSource() : null;
                    }
                        })
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public boolean isConnectionSource(DataClass dataClass, Connection connection){
        return connection.getSource().getId().equals(dataClass.getId());
    }

    public List<Connection> getDataClassConnections(DataClass dataClass){
        return edges.stream()
                .filter((edge)->edge.getTarget().getId().equals(dataClass.getId())
                || edge.getSource().getId().equals(dataClass.getId()))
                .collect(Collectors.toList());
    }

    public Optional<DataClass> findVertexById(String id){
        return vertices.stream().filter((vert) -> vert.getId().equals(id)).findAny();
    }

    public Optional<Connection> findEdgeById(String id){
        return edges.stream().filter((edge) -> edge.getId().equals(id)).findAny();
    }

    public Optional<Composite> findCompositeById(String id){
        return composites.stream().filter((edge) -> edge.getId().equals(id)).findAny();
    }

    public void addComposite(Composite composite){
        composites.add(composite);
    }

    public void addVertex(DataClass vert){
        vertices.add(vert);
    }

    public void addEdge(Connection edge){
        edges.add(edge);
    }

    public List<DataClass> getVertices() {
        return new LinkedList<>(vertices);
    }

    public List<Connection> getEdges() {
        return new LinkedList<>(edges);
    }

    public List<Composite> getComposites() {
        return new LinkedList<>(composites);
    }

    @Override
    public String toString() {
        return "Diagram{\n=======================\n"+
                "VERTICES\n"+
                "=========================\n"+
                Arrays.toString(vertices.toArray()) +
                "\n=======================\n"+
                "EDGES\n"+
                "========================\n"+
                Arrays.toString(edges.toArray()) +
                "========================\n"+
                "Composite keys\n"+
                composites.toString() +
                "========================\n"+
                '}';
    }
}
