/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.model;

import cz.cvut.fel.output.stringifier.DiagramVisitor;

/**
 * The class Relationship is a direct mapping of attributes from
 * Entity-Relationship diagram.
 * @author Adam Skarda
 */
public class Relationship extends Vertex {

    public Relationship(String name, String id) {
        super(name, id);
    }

    /**
     * For finding out if this relationship is recursive.
     * Recursive relationship has two relationship edges to the same entity.
     * @return true if this is recursive relationship
     */
    public boolean isRecursive(){
        long all = super.getEdges().stream()
                .map(edge -> edge.getOtherParticipant(this))
                .filter(Vertex::isEntity)
                .count();

        long distinct = super.getEdges().stream()
                .map(edge -> edge.getOtherParticipant(this))
                .filter(Vertex::isEntity)
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
