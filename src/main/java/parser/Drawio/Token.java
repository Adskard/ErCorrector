package parser.Drawio;

public enum Token {
    //attribute primary
    ATTRIBUTE(".*ellipse.*"),

    //attribute secondary
    //fontStyle=3 is underscoring text - may not vary
    KEY_ATTRIBUTE(".*fontStyle=4.*"),

    //shape=doubleEllipse - may not vary
    MULTIVALUED_ATTRIBUTE(".*shape=doubleEllipse.*"),

    //EXCEPTION - found in value
    WEAK_ATTRIBUTE(".*span\\sstyle=\"border-bottom:\\s[0-9]px dotted\".*"),

    //relationship primary
    RELATIONSHIP(".*rhombus.*"),

    //relationship secondary
    //double=1 - number may vary
    WEAK_RELATIONSHIP(".*double=[1-9].*"),

    //entity primary
    ENTITY(""),

    //entity secondary - number may vary
    WEAK_ENTITY(".*double=[1-9].*"),

    CONNECTION(".*endArrow=none.*"),
    GENERALIZATION(""),
    CARDINALITY("parent");

    private final String value;
    private Token(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
