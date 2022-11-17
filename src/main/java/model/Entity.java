/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import lombok.Getter;

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
public class Entity extends DataClass{

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
        List<Entity> ancestors = this.getAdjacentDataClasses().stream()
                .filter(DataClass::isEntity)
                .map(dataClass -> (Entity) dataClass)
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
                " isWeak = " + isWeak +
                " keys: " +
                Arrays.toString(keys.toArray());
    }
}
