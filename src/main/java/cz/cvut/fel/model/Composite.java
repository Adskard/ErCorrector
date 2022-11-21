package cz.cvut.fel.model;

import cz.cvut.fel.enums.Cardinality;
import cz.cvut.fel.output.stringifier.DiagramVisitor;
import lombok.Getter;

import java.util.*;

/**
 * The class composite is a composition of Connections to members of
 * a composite key and the associated entity.
 */
@Getter
public class Composite implements Key, DiagramComponent {
    /**
     * Connections and members of composite key.
     */
    private final HashMap<Connection, DataClass> compositeMembers = new HashMap<>();
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
     * Adds a Connection as a member of this composite
     * @param member Connection of composite
     */
    public void addCompositeMember(Connection connection, DataClass member){
        compositeMembers.put(connection, member);
    }

    public boolean isRelationshipBased(){
        return compositeMembers.values().stream().anyMatch(DataClass::isRelationship);
    }

    public Optional<Connection> getRelationshipConnection(){
        return compositeMembers.keySet().stream()
                .filter(Connection::isRelationshipConnection)
                .findAny();
    }
    public boolean isWeakIdentifier(){
        boolean idAttribute = compositeMembers.keySet().stream()
                .filter(Connection::isAttributeConnection)
                .anyMatch(connection -> !connection.hasCardinality() ||
                        connection.getCardinality().equals(Cardinality.ONE_TO_MANY) ||
                        connection.getCardinality().equals(Cardinality.ONE));

        if(!idAttribute){
            return false;
        }

        return compositeMembers.keySet().stream()
                .filter(Connection::isRelationshipConnection)
                .anyMatch(Connection::isIdentifyingConnection);
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
