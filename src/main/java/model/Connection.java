/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

public class Connection {
    private final DataClass source;
    private final DataClass target;
    private final String id;

    public Connection(String id, DataClass source, DataClass target) {
        this.id = id;
        this.source = source;
        this.target = target;
    }

    public String getId(){
        return id;
    }

    public DataClass getSource() {
        return source;
    }

    public DataClass getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "Connection{" +
                ",\nsource=" + source +
                ",target=" + target +
                "}\n";
    }
}
