package enums;

public enum Disjointness {
    EXCLUSIVE(".*exclusive.*"),
    OVERLAPPING(".*overlapping.*");

    private final String value;
    private Disjointness(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
