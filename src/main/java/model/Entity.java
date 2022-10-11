/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The class Entity is a direct mapping of an entity from Entity-Relationship
 * diagram.
 * @author Adam Skarda
 */
@Getter
@Setter
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


    @Override
    public String toString() {
        return "Entity"  +
                super.toString().substring(0, super.toString().length()-1) +
                " isWeak = " + isWeak +
                " keys: " +
                Arrays.toString(keys.toArray());
    }
}
