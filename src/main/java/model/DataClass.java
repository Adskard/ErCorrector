/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public abstract class DataClass {
    private final String name;
    private final String id;
    private Boolean isWeak;

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", isWeak=" + isWeak +
                "}\n";
    }
}
