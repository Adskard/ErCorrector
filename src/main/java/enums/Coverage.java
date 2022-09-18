package enums;

import java.util.regex.Pattern;

public enum Coverage {
    COMPLETE("complete"),
    PARTIAL("partial");

    private final String value;
    Coverage(String value){
        this.value = value;
    }

    public static Coverage decideCovering(String covering){
        Pattern completePattern = Pattern.compile(COMPLETE.getValue(), Pattern.CASE_INSENSITIVE);
        Pattern partialPattern = Pattern.compile(PARTIAL.getValue(), Pattern.CASE_INSENSITIVE);
        if(completePattern.matcher(covering).matches()){
            return COMPLETE;
        }
        else if(partialPattern.matcher(covering).matches()){
            return PARTIAL;
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
