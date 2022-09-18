/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


import lombok.*;

@Getter
@Setter
public class Attribute extends DataClass{
    private final Boolean isKey;
    private final Boolean isMultivalued;
    private final Boolean isCompositeKey;

    @Builder
    public Attribute(String name, String id, Boolean isWeak, Boolean isKey, Boolean isCompositeKey, Boolean isMultivalued) {
        super(name, id, isWeak);
        this.isKey = isKey;
        this.isMultivalued = isMultivalued;
        this.isCompositeKey = isCompositeKey;
    }
    @Override
    public String toString() {
        return "Attribute{ " +
                super.toString().substring(1,super.toString().length()-2) +
                ", isKey=" + isKey +
                ", isCompositeKey" + isCompositeKey +
                ", isMultivalued=" + isMultivalued
                + "}\n";
    }
}
