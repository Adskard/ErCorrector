package enums;

import java.util.regex.Pattern;

public enum Disjointness {
        EXCLUSIVE("exclusive"),
        OVERLAPPING("overlapping");

    private final String value;
    Disjointness(String value){
        this.value = value;
    }

    public static Disjointness decideDisjointness(String disjointness){
        Pattern exclusivePattern = Pattern.compile(EXCLUSIVE.getValue(), Pattern.CASE_INSENSITIVE);
        Pattern overlappingPattern = Pattern.compile(OVERLAPPING.getValue(), Pattern.CASE_INSENSITIVE);
        if(exclusivePattern.matcher(disjointness).matches()){
            return EXCLUSIVE;
        }
        else if(overlappingPattern.matcher(disjointness).matches()){
            return OVERLAPPING;
        }
        else return null;
    }

    public String getValue() {
        return value;
    }


    @Override
    public String toString() {
        return value;
    }
}
