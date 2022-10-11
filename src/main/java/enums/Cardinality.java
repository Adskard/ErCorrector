package enums;

import lombok.Getter;
/**
 * @author Adam Skarda
 * Connection cardinality
 */
@Getter
public enum Cardinality {
    NOT_RECOGNIZED("", "Not recognized"),
    NO_CARDINALITY("", ""),
    ONE("\\s*1\\.+1\\s*","1..1"),
    ZERO_TO_ONE("\\s*0\\.+1\\s*", "0..1"),
    ZERO_TO_MANY("\\s*0\\.+[M,N,m,n]\\s*", "0..N"),
    ONE_TO_MANY("\\s*1\\.+[M,N,m,n]\\s*", "1..N");

    private final String regex;
    private final String value;

    Cardinality(String regex, String value){
        this.value = value;
        this.regex = regex;
    }
    /**
     * Parses cardinality based on minimal and maximal number of members.
     * Uses standard ER notation.
     *
     * @param min Minimum number of members
     * @param max Maximum number of members
     * @return Suitable cardinality, null if such cardinality does not exist
     */
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
        if(min.strip().equals("") && max.strip().equals("")){
            return NO_CARDINALITY;
        }
        else{
            return NOT_RECOGNIZED;
        }
    }

    /**
     * Parses cardinality based on regular expression matching
     *
     * @param description cardinality string in proper form (0..N, 1..N, 0..1, 1..1)
     * @return Suitable cardinality, null if such cardinality does not exist
     */

    public static Cardinality decideCardinality(String description){
        if(description.matches(ZERO_TO_ONE.getRegex())){
            return ZERO_TO_ONE;
        }
        if(description.matches(ZERO_TO_MANY.getRegex())){
            return ZERO_TO_MANY;
        }
        if(description.matches(ONE.getRegex())){
            return ONE;
        }
        if(description.matches(ONE_TO_MANY.getRegex())){
            return ONE_TO_MANY;
        }
        if(description.strip().equals("")){
            return NO_CARDINALITY;
        }
        return NOT_RECOGNIZED;
    }

    @Override
    public String toString() {
        return value;
    }
}
