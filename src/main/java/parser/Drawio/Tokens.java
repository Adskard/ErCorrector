package parser.Drawio;

public enum Tokens {
    //attribute primary
    ATTRIBUTE(".*ellipse.*"),

    //attribute secondary
    KEY_ATTRIBUTE(".*fontStyle=4.*"),

    //shape=doubleEllipse
    MULTIVALUED_ATTRIBUTE(".*shape=doubleEllipse.*"),

    WEAK_ATTRIBUTE(".*span\\sstyle=\"border-bottom:\\s[0-9]px dotted\".*"),

    //relationship primary
    RELATIONSHIP(".*rhombus.*"),

    //relationship secondary
    WEAK_RELATIONSHIP(".*double=[1-9].*"),

    //entity primary
    ENTITY(""),

    WEAK_ENTITY(".*double=[1-9].*"),

    CONNECTION(".*endArrow=none.*"),
    GENERALIZATION(""),
    CARDINALITY("parent");

    private final String value;
    Tokens(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
