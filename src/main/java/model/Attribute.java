/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

public class Attribute extends DataClass{
    private final Boolean isKey;
    private final Boolean isMultivalued;

    public Attribute(String name, String id, Boolean isWeak, Boolean isKey, Boolean isMultivalued) {

        super(name, id, isWeak);
        this.isKey = isKey;
        this.isMultivalued = isMultivalued;
    }

    public Boolean getKey() {
        return isKey;
    }

    public Boolean getMultivalued() {
        return isMultivalued;
    }

    @Override
    public String toString() {
        return "Attribute{" +
                "isKey=" + isKey +
                ", isMultivalued=" + isMultivalued +
                " " + super.toString().substring(1);
    }
}
