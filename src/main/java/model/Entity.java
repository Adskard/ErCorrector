/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

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
    private Boolean isWeak;

    /**
     * Keys associated with this Entity
     */
    private final List<Key> keys = new LinkedList<>();

    public Entity(String name, String id, Boolean isWeak) {
        super(name, id);
        this.isWeak = isWeak;
    }

    public void addKey(Key key){
        keys.add(key);
    }

    public List<Key> getKeys(){
        return new LinkedList<>(keys);
    }


    @Override
    public String toString() {
        return "Entity" +
                super.toString();
    }
}
