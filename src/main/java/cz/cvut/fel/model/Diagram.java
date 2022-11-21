package cz.cvut.fel.model;

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
        edges.forEach(Connection::organize);
    }

    public List<Entity> getEntities(){
        return vertices.stream()
                .filter(DataClass::isEntity)
                .map(dataClass -> (Entity) dataClass)
                .collect(Collectors.toList());
    }

    public List<Attribute> getAttributes(){
        return vertices.stream()
                .filter(DataClass::isAttribute)
                .map(dataClass -> (Attribute) dataClass)
                .collect(Collectors.toList());
    }

    public List<Relationship> getRelationships(){
        return vertices.stream()
                .filter(DataClass::isRelationship)
                .map(dataClass -> (Relationship) dataClass)
                .collect(Collectors.toList());
    }


    public void identifyWeakEntities(){
        List<Entity> entities = this.getEntities();

        for(Entity entity : entities){
            entity.isWeakEntity();
        }
    }


    /**
     * Gets vertices not connected by an edge to main diagram component.
     * If resulting list is empty, then the diagram has a single component.
     * Implementation using DFS.
     * Main component is one that contains diagram vertex with index 0.
     *
     * @return list of vertices not in main component
     */
    public List<DataClass> getMissingVerticesFromMainComponent(){
        List<DataClass> visited = new LinkedList<>();
        Stack<DataClass> toBeVisited = new Stack<>();
        DataClass first = vertices.get(0);
        toBeVisited.push(first);

        //DFS
        while(!toBeVisited.isEmpty()){
            DataClass vertex = toBeVisited.pop();
            visited.add(vertex);

            for(DataClass adjacent : vertex.getAdjacentDataClasses()){
                if(!visited.contains(adjacent) && !toBeVisited.contains(adjacent)){
                    toBeVisited.push(adjacent);
                }
            }
        }

        return vertices.stream()
                .filter(dataClass -> !visited.contains(dataClass))
                .collect(Collectors.toList());
    }

    public void addKeysToEntities(){

        List<Entity> entities = vertices.stream()
                .filter(DataClass::isEntity)
                .map((vert)->(Entity) vert)
                .collect(Collectors.toList());

        for(Entity entity : entities){
            //add simple keys
            entity.getConnections().forEach((connection)->{
                if(connection.isAttributeConnection()){
                    Attribute attribute = (Attribute)  (connection.getTarget().equals(entity) ?
                            connection.getSource(): connection.getTarget());
                    if(attribute.getIsKey()){
                        entity.addKey(attribute);
                    }
                }
            });

            //add composite keys
            composites.forEach((composite -> {
                if(composite.getEntity().equals(entity)){
                    entity.addKey(composite);
                }
            }));
        }

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
                composites +
                "========================\n"+
                '}';
    }
}
