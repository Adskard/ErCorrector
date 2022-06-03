/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import enums.Cardinality;

import java.util.LinkedList;
import java.util.List;

public class ValuedConnection extends Connection{

    private Cardinality cardinality;
    private final List<String> description = new LinkedList<>();


    public ValuedConnection(String id, DataClass source, DataClass target, List<String> description) {
        super(id, source, target);
        this.description.addAll(description);
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public List<String> getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "ValuedConnection{" +
                "cardinality=" + cardinality +
                ", descrpition=" + description +
                ",\nsource=" + super.getSource() +
                ",\ntarget= " + super.getTarget()
                + "}\n";
    }
}
