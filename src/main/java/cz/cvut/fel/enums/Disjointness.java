package cz.cvut.fel.enums;

import lombok.Getter;

/**
 * Diagram hierarchy disjointness information
 * @author Adam Skarda
 */
@Getter
public enum Disjointness {
        NOT_RECOGNIZED("Not recognized"),
        EXCLUSIVE("exclusive"),
        OVERLAPPING("overlapping");

    private final String value;
    Disjointness(String value){
        this.value = value;
    }

    /**
     * Decides disjointness from a string value
     * @param disjointness string disjointness information
     * @return Appropriate disjointness, or NOT_RECOGNIZED if the disjointness
     * could not be accurately decided
     */
    public static Disjointness decideDisjointness(String disjointness){
        if(disjointness.toLowerCase().contains(EXCLUSIVE.getValue())){
            return EXCLUSIVE;
        }
        else if(disjointness.toLowerCase().contains(OVERLAPPING.getValue())){
            return OVERLAPPING;
        }
        else return NOT_RECOGNIZED;
    }

    @Override
    public String toString() {
        return value;
    }
}
