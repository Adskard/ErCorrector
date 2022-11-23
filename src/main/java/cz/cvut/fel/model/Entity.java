/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.model;

import lombok.Getter;
import cz.cvut.fel.output.stringifier.DiagramVisitor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The class Entity is a direct mapping of an entity from Entity-Relationship
 * diagram.
 * @author Adam Skarda
 */
@Getter
public class Entity extends Vertex {

    /**
     * True if this Entity is weak, meaning it does not its own key
     */
    private Boolean isWeak = true;

    /**
     * Keys associated with this Entity
     */
    private final List<Key> keys = new LinkedList<>();

    public Entity(String name, String id) {
        super(name, id);
    }

    public void addKey(Key key){
        keys.add(key);
    }

    public List<Key> getKeys(){
        return new LinkedList<>(keys);
    }

    /**
     * For finding out ancestors in hierarchy of this Entity.
     * By going through generalization connection that source from
     * this Entity and have target that is also an Entity.
     * @return List of ancestor entities
     */
    public List<Entity> getAncestors(){
        return super.getEdges().stream()
                .filter(Edge::isGeneralization)
                .map(Edge::getTarget)
                .map(vertex -> (Entity) vertex)
                .filter(vertex -> !equals(vertex))
                .collect(Collectors.toList());
    }

    /**
     * Finds out if this entity is identified by its key or ancestor key.
     * @return true if entity is identified
     */
    public boolean hasIdentifier(){
        if(!keys.isEmpty()){
            return true;
        }

        return getAncestors().stream().anyMatch(Entity::hasIdentifier);
    }

    /**
     * Finds out if this entity is weak.
     * Weak entities do not have attributes as keys
     * and have relationship based composite key.
     *
     * @return true if this is weak entity
     */
    public boolean isWeakEntity(){

        boolean hasRelationshipComposite = false;

        //is not identified by a key
        for(Key key : this.getKeys()){

            //simple key
            if(key.isSimple()){
                this.isWeak = false;
                return false;
            }

            //composite key
            else{
                Composite composite = (Composite) key;
                if(composite.isRelationshipBased()){
                    hasRelationshipComposite = true;
                }
                else{
                    this.isWeak = false;
                    return false;
                }
            }
        }

        //ancestor keys
        List<Entity> ancestors = this.getAdjacentVertices().stream()
                .filter(Vertex::isEntity)
                .map(vertex -> (Entity) vertex)
                .collect(Collectors.toList());

        for(Entity ancestor : ancestors){
            if(!ancestor.isWeakEntity()){
                this.isWeak = false;
                return false;
            }
        }

        this.isWeak = hasRelationshipComposite;
        return hasRelationshipComposite;
    }


    @Override
    public String toString() {
        return "Entity"  +
                super.toString().substring(0, super.toString().length()-1) +
                ", isWeak = " + isWeak +
                ", keys: " +
                Arrays.toString(keys.toArray());
    }

    @Override
    public String accept(DiagramVisitor visitor) {
        return visitor.visit(this);
    }
}
