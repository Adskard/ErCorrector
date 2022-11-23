package cz.cvut.fel.model;

import cz.cvut.fel.enums.Cardinality;
import lombok.*;
import cz.cvut.fel.output.stringifier.DiagramVisitor;

import java.util.*;

/**
 * The Connection class is mapping of an edge from Entity-Relationship
 * diagram. Used to store information about Vertex relations such as
 * Cardinality and descriptions.
 * @author Adam Skarda
 */
@Getter
@Setter
public class Edge implements DiagramComponent{
    /**
     * Source Vertex
     */
    private Vertex source;

    /**
     * Target Vertex
     */
    private Vertex target;

    /**
     * Connection identifier, must be unique in diagram
     */
    private final String id;

    /**
     * Denotes the cardinality of the connection. Can be
     * 1..1, 0..1, 0..N, 1..N
     */
    private Cardinality cardinality;

    /**
     * Aggregation of string Connection descriptors
     */
    private final List<String> description = new LinkedList<>();

    /**
     * Used for finding out if a vertex is the edge target.
     * @param vertex vertex in question
     * @return true if edge target is equal to vertex else false
     */
    public boolean isTarget(Vertex vertex){
        return target.equals(vertex);
    }

    /**
     * Used for finding out if a vertex is the edge source.
     * @param vertex vertex in question
     * @return true if edge source is equal to vertex else false
     */
    public boolean isSource(Vertex vertex){
        return source.equals(vertex);
    }

    /**
     * Used for finding out the vertex at the other end of this Edge.
     * @param participant vertex that is either target or source of this Edge
     * @return The other edge participant either target or source
     */
    public Vertex getOtherParticipant(Vertex participant){
        return source.equals(participant) ? target : source;
    }

    /**
     * For finding out if edge has a description
     * @return true if edge has description
     */
    public boolean hasDescription(){
        return !description.isEmpty();
    }

    /**
     * For finding out if edge has a Cardinality
     * @return true if edge has a Cardinality and that cardinality is recognized
     */
    public boolean hasCardinality(){
        return !cardinality.equals(Cardinality.NO_CARDINALITY)
                && !cardinality.equals(Cardinality.NOT_RECOGNIZED);
    }

    /**
     * For finding out if edge can be used for identification.
     * @return true if connection identifies a weak entity
     */
    public boolean isIdentifyingEdge(){
        return cardinality.equals(Cardinality.ONE);
    }

    /**
     * For finding out if edge has both target and source
     * @return true if edge has both target and source are not null
     */
    public boolean isFullyConnected(){
        return Objects.nonNull(target) && Objects.nonNull(source);
    }

    public Edge(String id, Vertex source, Vertex target, List<String> description) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.description.addAll(description);
    }

    public Edge(String id, Vertex source, Vertex target){
        this.id = id;
        this.source = source;
        this.target = target;
    }

    /**
     * Adds String description to this edge
     * @param description string description of this edge
     */
    public void addDescription(String description){
        this.description.add(description);
    }

    /**
     * adds all string descriptions from collection to this edge
     * @param descriptions collection of descriptions
     */
    public void addAllDescriptions(Collection<String> descriptions){
        this.description.addAll(descriptions);
    }

    /**
     * returns all descriptions of this edge
     * @return Copy of this edge list of descriptions
     */
    public List<String> getDescription(){
        return new LinkedList<>(description);
    }

    @Builder
    public Edge(String id, Vertex source, Vertex target, Cardinality cardinality) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.cardinality = cardinality;
    }

    /**
     * Switches target and source of edge where appropriate.
     * Every entity is a target of attribute connection and target of relationship edge.
     * Relationship is target of attribute edge and source of relationship edge.
     * Attribute is always a source of an edge.
     */
    public void organize(){
        if(target.isAttribute()){
            Vertex h = source;
            source = target;
            target = h;
        }
        else if(source.isEntity() && target.isRelationship()){
            Vertex h = source;
            source = target;
            target = h;
        }
    }

    /**
     * For finding out if edge connects to an Attribute
     * @return true if source or target is an Attribute
     */
    public boolean isAttributeConnection(){
        return source.isAttribute() || target.isAttribute();
    }

    /**
     * For finding out if edge connects a Relationship and an Entity
     * @return true if source or target is an Attribute and the other is Entity
     */
    public boolean isRelationshipConnection(){
        return (source.isRelationship() && target.isEntity()) ||
                (source.isEntity() && target.isRelationship());
    }

    /**
     * For finding out if edge is a generalization
     * @return true if this is instance of Generalization
     */
    public boolean isGeneralization(){
        return this instanceof Generalization;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Edge){
            Edge comparedObject = (Edge) o;
            return comparedObject.getId().equals(id);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Edge{\n" +
                "   source = " + source +
                "   target = " + target +
                (description.isEmpty() ?
                        "":
                        "   description: "+ Arrays.toString(description.toArray())) +
                (this.cardinality != null ?
                        "   cardinality: " + cardinality :
                        "") +
                "}\n";
    }

    @Override
    public String accept(DiagramVisitor visitor) {
        return visitor.visit(this);
    }
}
