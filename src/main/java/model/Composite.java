package model;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The class composite is a composition of Connections to members of
 * a composite key and the associated entity.
 */
@Getter
public class Composite implements Key {
    /**
     * Connections to composite members. Connections are stare instead of
     * the members to preserve information.
     */
    private final List<Connection> identifierConnections = new LinkedList<>();
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

    public void addAllCompositeMembers(Collection<Connection> members){
        identifierConnections.addAll(members);
    }

    public void addCompositeMember(Connection member){
        identifierConnections.add(member);
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Composite){
            Composite comparedObject = (Composite) o;
            if(comparedObject.getId().equals(id)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString(){
        return "Entity: " + this.entity.toString()
                + " Members: "
                + Arrays.toString(identifierConnections.toArray()) +
                "\n";
    }
}
