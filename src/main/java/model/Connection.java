/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import enums.Cardinality;
import lombok.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class Connection {
    private final DataClass source;
    private final DataClass target;
    private final String id;
    private Cardinality cardinality;
    private final List<String> description = new LinkedList<>();

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

    @Builder
    public Connection(String id, DataClass source, DataClass target, Cardinality cardinality) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.cardinality = cardinality;
    }

    @Override
    public String toString() {
        return "Connection{\n" +
                "   source = " + source.toString() +
                "   target = " + target.toString() +
                (description.isEmpty() ?
                        "":
                        "   description: "+ Arrays.toString(description.toArray())) +
                (this.cardinality != null ?
                        "   cardinality: " + cardinality :
                        "") +
                "}\n";
    }
}
