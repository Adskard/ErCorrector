package enums;

import exception.CardinalityNotRecognizedException;

public enum Cardinality {
    ONE("\\s1\\.+1\\s","1..1"),
    ZERO_TO_ONE("\\s0\\.+1\\s", "0..1"),
    ZERO_TO_MANY("\\s0\\.+[M,N,m,n]\\s", "0..N"),
    ONE_TO_MANY("\\s1\\.+[M,N,m,n]\\s", "1..N");

    private final String regex;
    private final String value;

    Cardinality(String regex, String value){
        this.value = value;
        this.regex = regex;
    }

    public String getValue() {
        return regex;
    }

    public static Cardinality decideCardinality(String min, String max){
        if(min.strip().equals("0") && max.strip().equals("1")){
            return ZERO_TO_ONE;
        }
        if(min.strip().equals("0") && max.strip().equals("N")){
            return ZERO_TO_MANY;
        }
        if(min.strip().equals("1") && max.strip().equals("1")){
            return ONE;
        }
        if(min.strip().equals("1") && max.strip().equals("N")){
            return ONE_TO_MANY;
        }
        else{
            return null;
        }
    }

    public static Cardinality decideCardinality(String description) throws CardinalityNotRecognizedException{
        if(description.matches(ZERO_TO_ONE.getValue())){
            return ZERO_TO_ONE;
        }
        if(description.matches(ZERO_TO_MANY.getValue())){
            return ZERO_TO_MANY;
        }
        if(description.matches(ONE.getValue())){
            return ONE;
        }
        if(description.matches(ONE_TO_MANY.getValue())){
            return ONE_TO_MANY;
        }
        throw new CardinalityNotRecognizedException(description + " does not match a recognized cardinality!");
    }

    @Override
    public String toString() {
        return value;
    }
}
