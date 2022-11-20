/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import output.stringifier.DiagramVisitor;

/**
 * The class Relationship is a direct mapping of attributes from
 * Entity-Relationship diagram.
 * @author Adam Skarda
 */
public class Relationship extends DataClass{

    public Relationship(String name, String id) {
        super(name, id);
    }

    public boolean isRecursive(){
        long all = super.getConnections().stream()
                .map(connection -> connection.getOtherParticipant(this))
                .filter(DataClass::isEntity)
                .count();

        long distinct = super.getConnections().stream()
                .map(connection -> connection.getOtherParticipant(this))
                .filter(DataClass::isEntity)
                .distinct()
                .count();

        return distinct < all;
    }

    @Override
    public String toString() {
        return "Relationship" +
                super.toString();
    }

    @Override
    public String accept(DiagramVisitor visitor) {
        return visitor.visit(this);
    }
}
