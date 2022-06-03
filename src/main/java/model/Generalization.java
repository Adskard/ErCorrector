/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import enums.Covering;
import enums.Disjointness;

import java.util.LinkedList;
import java.util.List;

public class Generalization extends Connection{

    private  Covering covering;
    private  Disjointness disjointness;
    private final List<String> description = new LinkedList<>();
    public List<String> getDescription() {
        return description;
    }

    public Generalization(String id, DataClass source, DataClass target,List<String> description) {
        super(id, source, target);
        this.description.addAll(description);
    }

    public Covering getCovering() {
        return covering;
    }

    public Disjointness getDisjointness() {
        return disjointness;
    }

    @Override
    public String toString() {
        return "Generalization{" +
                "covering=" + covering +
                ", disjointness=" + disjointness +
                ",\nsource=" + super.getSource() +
                ",target=" + super.getTarget() +
                "}\n";
    }
}
