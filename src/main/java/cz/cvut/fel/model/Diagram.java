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
    private final List<Vertex> vertices = new LinkedList<>();

    /**
     * Diagram edges between vertices
     */
    private final List<Edge> edges = new LinkedList<>();

    /**
     * Diagram composite identifiers of entities
     */
    private final List<Composite> composites = new LinkedList<>();

    public Diagram() {
    }

    /**
     * Organizes diagram edges for standardization purposes.
     * Organizing a connection means switching target and source of connection where appropriate.
     * Every entity is a target of attribute Edge and target of relationship Edge.
     * Relationship is target of attribute Edge and source of relationship Edge.
     * Attribute is always a source of an Edge.
     */
    public void organizeEdges(){
        edges.forEach(Edge::organize);
    }

    /**
     * Gets entities present in diagram
     * @return list of all present entities
     */
    public List<Entity> getEntities(){
        return vertices.stream()
                .filter(Vertex::isEntity)
                .map(vertex -> (Entity) vertex)
                .collect(Collectors.toList());
    }

    /**
     * Gets attributes present in diagram
     * @return list of all present attributes
     */
    public List<Attribute> getAttributes(){
        return vertices.stream()
                .filter(Vertex::isAttribute)
                .map(vertex -> (Attribute) vertex)
                .collect(Collectors.toList());
    }

    /**
     * Gets relationships present in diagram
     * @return list of all present relationships
     */
    public List<Relationship> getRelationships(){
        return vertices.stream()
                .filter(Vertex::isRelationship)
                .map(vertex -> (Relationship) vertex)
                .collect(Collectors.toList());
    }

    /**
     * For marking weak entities.
     * Goes through every entity and calls their weak entity method
     */
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
    public List<Vertex> getMissingVerticesFromMainComponent(){
        List<Vertex> visited = new LinkedList<>();
        Stack<Vertex> toBeVisited = new Stack<>();
        Vertex first = vertices.get(0);
        toBeVisited.push(first);

        //DFS
        while(!toBeVisited.isEmpty()){
            Vertex vertex = toBeVisited.pop();
            visited.add(vertex);

            for(Vertex adjacent : vertex.getAdjacentVertices()){
                if(!visited.contains(adjacent) && !toBeVisited.contains(adjacent)){
                    toBeVisited.push(adjacent);
                }
            }
        }

        return vertices.stream()
                .filter(vertex -> !visited.contains(vertex))
                .collect(Collectors.toList());
    }

    /**
     * For connecting keys to entities.
     * Goes through all entities and gives them associated keys
     */
    public void addKeysToEntities(){

        List<Entity> entities = vertices.stream()
                .filter(Vertex::isEntity)
                .map((vert)->(Entity) vert)
                .collect(Collectors.toList());

        for(Entity entity : entities){
            //add simple keys
            entity.getEdges().forEach((edge)->{
                if(edge.isAttributeConnection()){
                    Attribute attribute = (Attribute)  (edge.getTarget().equals(entity) ?
                            edge.getSource(): edge.getTarget());
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

    /**
     * Finds a vertex by its id
     * @param id unique identifier
     * @return optional of vertex with given id
     */
    public Optional<Vertex> findVertexById(String id){
        return vertices.stream().filter((vert) -> vert.getId().equals(id)).findAny();
    }

    /**
     * Finds an edge by its id
     * @param id unique identifier
     * @return optional of edge with given id
     */
    public Optional<Edge> findEdgeById(String id){
        return edges.stream().filter((edge) -> edge.getId().equals(id)).findAny();
    }

    /**
     * Finds a composite by its id
     * @param id unique identifier
     * @return optional of composite with given id
     */
    public Optional<Composite> findCompositeById(String id){
        return composites.stream().filter((edge) -> edge.getId().equals(id)).findAny();
    }

    /**
     * Adds a composite key into the diagram
     * @param composite Composite key with unique id
     */
    public void addComposite(Composite composite){
        composites.add(composite);
    }

    /**
     * Adds a vertex into the diagram
     * @param vert Vertex with unique id
     */
    public void addVertex(Vertex vert){
        vertices.add(vert);
    }

    /**
     * Adds an edge into the diagram
     * @param edge edge with unique id
     */
    public void addEdge(Edge edge){
        edges.add(edge);
    }

    /**
     * Vertex getter
     * @return Copied list of vertices
     */
    public List<Vertex> getVertices() {
        return new LinkedList<>(vertices);
    }

    /**
     * Edge getter
     * @return Copied list of edges
     */
    public List<Edge> getEdges() {
        return new LinkedList<>(edges);
    }

    /**
     * Composite getter
     * @return Copied list of composites
     */
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
