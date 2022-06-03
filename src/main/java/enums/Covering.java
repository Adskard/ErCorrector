package enums;

public enum Covering {
    COMPLETE(".*complete.*"),
    PARTIAL(".*partial.*");

    private final String value;
    private Covering(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
