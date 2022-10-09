/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 * The class Relationship is a direct mapping of attributes from
 * Entity-Relationship diagram.
 * @author Adam Skarda
 */
public class Relationship extends DataClass{

    public Relationship(String name, String id, Boolean isWeak) {
        super(name, id);
    }

    @Override
    public String toString() {
        return "Relationship" +
                super.toString();
    }
}
