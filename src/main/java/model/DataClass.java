/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

public abstract class DataClass {
    private final String name;
    private final String id;
    private final Boolean isWeak;

    protected DataClass(String name, String id, Boolean isWeak) {
        this.name = name;
        this.isWeak = isWeak;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getWeak() {
        return isWeak;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", isWeak=" + isWeak +
                "}\n";
    }
}
