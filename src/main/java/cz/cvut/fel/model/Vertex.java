/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.model;

import lombok.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The Vertex class is a common abstraction of entities, attributes
 * and relationship in Entity-Relationship diagram. It serves as an aggregation
 * of their basic properties.
 * @author Adam Skarda
 */
@Getter
@Setter
@RequiredArgsConstructor
public abstract class Vertex implements DiagramComponent{

    /**
     * Human readable Vertex name
     */
    private final String name;

    /**
     * Unique identifier in diagram
     */
    private final String id;

    /**
     * Edges leading to and from this Vertex
     */
    private final List<Edge> edges = new LinkedList<>();

    /**
     * For quick access to connected Vertices
     * @return Vertex connected to this Vertex
     */
    public List<Vertex> getAdjacentVertices(){
        return edges.stream()
                .map((edge) ->
                    this.equals(edge.getTarget()) ? edge.getSource() : edge.getTarget())
                .collect(Collectors.toList());
    }

    /**
     * For finding out if this is Entity
     * @return true if this is instance of Entity class
     */
    public boolean isEntity(){
        return this instanceof Entity;
    }

    /**
     * For finding out if this is Relationship
     * @return true if this is instance of Relationship class
     */
    public boolean isRelationship(){
        return this instanceof Relationship;
    }

    /**
     * For finding out if this is Attribute
     * @return true if this is instance of Attribute class
     */
    public boolean isAttribute(){
        return this instanceof Attribute;
    }

    /**
     * adds a connected edge
     * @param edge edge that has a source or target equal to this entity
     */
    public void addEdge(Edge edge){
        edges.add(edge);
    }

    /**
     * Gets connected Edges
     * @return list of connected Edges
     */
    public List<Edge> getEdges(){
        return new LinkedList<>(edges);
    }

    /**
     * For quick access to attributes of this vertex
     * @return list of connected attributes
     */
    public List<Attribute> getAttributes(){
        return edges.stream()
                .map((edge)->{
                    if(!edge.isFullyConnected()){
                        return null;
                    }
                    if(edge.isSource(this)){
                        return edge.getTarget().isAttribute() ? (Attribute)edge.getTarget() : null;
                    }
                    else{
                        return edge.getSource().isAttribute() ? (Attribute)edge.getSource() : null;
                    }
                })
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Vertex){
            Vertex comparedObject = (Vertex) o;
            return comparedObject.getId().equals(id);
        }
        return false;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", id=" + id +
                "}";
    }
}
