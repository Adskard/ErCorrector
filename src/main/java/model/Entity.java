/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

public class Entity extends DataClass{

    public Entity(String name, String id, Boolean isWeak) {
        super(name, id, isWeak);
    }

    @Override
    public String toString() {
        return "Entity" +
                super.toString();
    }
}