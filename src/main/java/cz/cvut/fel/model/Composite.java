package cz.cvut.fel.model;

import cz.cvut.fel.enums.Cardinality;
import cz.cvut.fel.output.stringifier.DiagramVisitor;
import lombok.Getter;

import java.util.*;

/**
 * The class composite is a representation of composite key
 * and stores members and the associated entity.
 */
@Getter
public class Composite implements Key, DiagramComponent {
    /**
     * Edges and members of composite key.
     */
    private final HashMap<Edge, Vertex> compositeMembers = new HashMap<>();
    /**
     * Entity identified by this composite identifier.
     */
    private final Entity entity;
    /**
     * Unique identification of this Composite.
     */
    private final String id;

    public Composite(Entity entity, String id){
        this.id = id;
        this.entity = entity;
    }

    /**
     * Adds an Edge as a member of this composite
     * @param member Edge of composite
     */
    public void addCompositeMember(Edge edge, Vertex member){
        compositeMembers.put(edge, member);
    }

    /**
     * For finding out if composite key has a relationship as its part
     * @return true if relationship is member of this composite key else false
     */
    public boolean isRelationshipBased(){
        return compositeMembers.values().stream().anyMatch(Vertex::isRelationship);
    }

    /**
     * For finding out if composite correctly identifies a weak entity
     * @return true if composite ids a weak entity, else false
     */
    public boolean isWeakIdentifier(){
        boolean idAttribute = compositeMembers.keySet().stream()
                .filter(Edge::isAttributeConnection)
                .anyMatch(edge -> !edge.hasCardinality() ||
                        edge.getCardinality().equals(Cardinality.ONE_TO_MANY) ||
                        edge.getCardinality().equals(Cardinality.ONE));

        if(!idAttribute){
            return false;
        }

        return compositeMembers.keySet().stream()
                .filter(Edge::isRelationshipConnection)
                .anyMatch(Edge::isIdentifyingEdge);
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Composite){
            Composite comparedObject = (Composite) o;
            return comparedObject.getId().equals(id);
        }
        return false;
    }

    @Override
    public String toString(){
        return String.format("Composite Key id=%s ", id)
                + "\n";
    }

    @Override
    public String accept(DiagramVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean isSimple() {
        return false;
    }
}
