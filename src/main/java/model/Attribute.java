/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


import lombok.*;
import output.stringifier.DiagramVisitor;

/**
 * The class Attribute is a direct mapping of attributes from
 * Entity-Relationship diagram. Attribute can be used as a Key.
 * @author Adam Skarda
 */
@Getter
@Setter
public class Attribute extends DataClass implements Key, DiagramComponent{
    /**
     * True if this Attribute is a key, false otherwise.
     */
    private final Boolean isKey;

    public boolean isStructured(){
        return !super.getDataClassAttributes().isEmpty();
    }

    @Builder
    public Attribute(String name, String id, Boolean isKey) {
        super(name, id);
        this.isKey = isKey;
    }

    @Override
    public String toString() {
        return "Attribute{" +
                super.toString().substring(1,super.toString().length()-2) +
                ", isKey=" + isKey +
                "}";
    }

    @Override
    public String accept(DiagramVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean isSimple() {
        return true;
    }
}
