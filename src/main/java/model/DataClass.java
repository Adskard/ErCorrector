/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import lombok.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The DataClass class is a common abstraction of entities, attributes
 * and relationship in Entity-Relationship diagram. It serves as an aggregation
 * of their basic properties.
 * @author Adam Skarda
 */
@Getter
@Setter
@RequiredArgsConstructor
public abstract class DataClass {
    /**
     * Human readable DataClass name
     */
    private final String name;

    /**
     * Unique identifier in diagram
     */
    private final String id;

    /**
     * Connections leading to and from this DataClass
     */
    private final List<Connection> connections = new LinkedList<>();

    /**
     * For quick access to connected DataClasses
     * @return DataClasses connected to this DataClass
     */
    public List<DataClass> getAdjacentDataClasses(){
        return connections.stream()
                .map((connection) ->
                    this.equals(connection.getTarget()) ? connection.getSource() : connection.getTarget())
                .collect(Collectors.toList());
    }

    public boolean isEntity(){
        return this instanceof Entity;
    }

    public boolean isRelationship(){
        return this instanceof Relationship;
    }

    public boolean isAttribute(){
        return this instanceof Attribute;
    }

    public void addConnection(Connection connection){
        connections.add(connection);
    }

    public List<Connection> getConnections(){
        return new LinkedList<>(connections);
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof DataClass){
            DataClass comparedObject = (DataClass) o;
            return comparedObject.getId().equals(id);
        }
        return false;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", id=" + id +
                "}\n";
    }
}
