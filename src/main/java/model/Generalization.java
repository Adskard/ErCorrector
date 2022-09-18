/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import enums.Cardinality;
import enums.Coverage;
import enums.Disjointness;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class Generalization extends Connection{

    private Coverage coverage;
    private  Disjointness disjointness;

    @Builder(builderMethodName = "GeneralizationBuilder")
    public Generalization(String id, DataClass source, DataClass target,
                          Coverage covering, Disjointness disjointness) {
        super(id, source, target);
        this.coverage = covering;
        this.disjointness = disjointness;
    }

    public Generalization(String id, DataClass source, DataClass target, List<String> description) {
        super(id, source, target, description);
    }


    @Override
    public String toString() {

        return "Generalization" +
                super.toString().substring(0, super.toString().length()-2) +
                "   Coverage = " + coverage +
                "   Disjointness = " + disjointness +
                "}\n";
    }
}
