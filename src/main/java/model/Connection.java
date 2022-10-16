/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import enums.Cardinality;
import lombok.*;

import java.util.*;

/**
 * The Connection class is mapping of an edge from Entity-Relationship
 * diagram. Used to store information about DataClass relations such as
 * Cardinality and descriptions.
 * @author Adam Skarda
 */
@Getter
@Setter
public class Connection {
    /**
     * Source DataClass
     */
    private DataClass source;

    /**
     * Target DataClass
     */
    private DataClass target;

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

    public boolean isTarget(DataClass dataClass){
        return target.equals(dataClass);
    }

    public boolean isSource(DataClass dataClass){
        return source.equals(dataClass);
    }

    public DataClass getOtherParticipant(DataClass participant){
        return source.equals(participant) ? target : source;
    }

    public boolean connectsTo(DataClass dataClass){
        return target.equals(dataClass) || source.equals(dataClass);
    }

    public boolean hasDescription(){
        return !description.isEmpty();
    }

    public boolean hasCardinality(){
        return !cardinality.equals(Cardinality.NO_CARDINALITY)
                && !cardinality.equals(Cardinality.NOT_RECOGNIZED);
    }

    public boolean isFullyConnected(){
        return Objects.nonNull(target) && Objects.nonNull(source);
    }

    public Connection(String id, DataClass source, DataClass target, List<String> description) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.description.addAll(description);
    }

    public Connection(String id, DataClass source, DataClass target){
        this.id = id;
        this.source = source;
        this.target = target;
    }

    public void addDescription(String description){
        this.description.add(description);
    }

    public void addAllDescriptions(Collection<String> descriptions){
        this.description.addAll(descriptions);
    }

    public List<String> getDescription(){
        return new LinkedList<>(description);
    }

    @Builder
    public Connection(String id, DataClass source, DataClass target, Cardinality cardinality) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.cardinality = cardinality;
    }

    /**
     * Switches target and source of connection where appropriate.
     * Every entity is a target of attribute connection and target of relationship connection.
     * Relationship is target of attribute connection and source of relationship connection.
     * Attribute is always a source of a connection.
     */
    public void organize(){
        if(target.isAttribute()){
            DataClass h = source;
            source = target;
            target = h;
        }
        else if(source.isEntity() && target.isRelationship()){
            DataClass h = source;
            source = target;
            target = h;
        }
    }

    public boolean isAttributeConnection(){
        return source instanceof Attribute || target instanceof Attribute;
    }

    public boolean isRelationshipConnection(){
        return (source instanceof Relationship && target instanceof Entity) ||
                (source instanceof Entity && target instanceof Relationship);
    }

    public boolean isGeneralization(){
        return this instanceof Generalization;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Connection){
            Connection comparedObject = (Connection) o;
            return comparedObject.getId().equals(id);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Connection{\n" +
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
}
