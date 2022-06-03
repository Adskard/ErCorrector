package enums;

public enum Cardinality {
    ONE("\\s1\\s"),
    MANY("\\s[m,n,M,N]\\s"),
    ZERO_OR_ONE("\\s0\\.+1\\s"),
    ZERO_OR_MANY("\\s0\\.+[m,n,M,N]\\s"),
    ONE_OR_MANY("\\s1\\.+[m,n,M,N]\\s");

    private final String value;
    private Cardinality(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
