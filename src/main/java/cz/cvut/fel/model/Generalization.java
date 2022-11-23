/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.model;

import cz.cvut.fel.enums.Coverage;
import cz.cvut.fel.enums.Disjointness;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


/**
 * Connection denoting a generalization. Where target is the ancestor
 * and source is the child.
 * @author Adam Skarda
 */
@Getter
@Setter
public class Generalization extends Edge {

    /**
     * Generalization Coverage either complete, partial, or not recognized
     */
    private Coverage coverage;

    /**
     * Generalization Disjointness either exclusive, overlapping, or not recognized
     */
    private Disjointness disjointness;

    @Builder(builderMethodName = "GeneralizationBuilder")
    public Generalization(String id, Vertex source, Vertex target,
                          Coverage covering, Disjointness disjointness) {
        super(id, source, target);
        this.coverage = covering;
        this.disjointness = disjointness;
    }

    /**
     * Do not change generalization source and target
     */
    @Override
    public void organize(){
    }

    @Override
    public String toString() {

        return "Generalization" +
                super.toString().substring(0, super.toString().length()-2) +
                "   Coverage = " + coverage +
                "   Disjointness = " + disjointness +
                "\n}\n";
    }
}
