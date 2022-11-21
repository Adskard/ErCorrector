package cz.cvut.fel.enums;

import lombok.Getter;

/**
 * Diagram hierarchy coverage information
 * @author Adam Skarda
 */
@Getter
public enum Coverage {
    NOT_RECOGNIZED("Not recognized"),
    COMPLETE("complete"),
    PARTIAL("partial");

    private final String value;
    Coverage(String value){
        this.value = value;
    }

    /**
     * Decides coverage from a string value
     * @param covering string containing coverage information
     * @return Appropriate coverage, or NOT_RECOGNIZED if the coverage
     * could not be accurately decided
     */
    public static Coverage decideCoverage(String covering){
        if(covering.toLowerCase().contains(COMPLETE.getValue())){
            return COMPLETE;
        }
        else if(covering.toLowerCase().contains(PARTIAL.getValue())){
            return PARTIAL;
        }
        else return NOT_RECOGNIZED;
    }

    @Override
    public String toString() {
        return value;
    }
}
