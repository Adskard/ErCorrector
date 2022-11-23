package cz.cvut.fel.parser.Drawio;


/**
 *
 * Values of XML attributes used for parsing
 * @author Adam Skarda
 */
public enum Tokens {
    /**
     * Primary identification regex for Attributes, found inside style xml attribute.
     */
    ATTRIBUTE(".*ellipse.*"),
    /**
     *
     */
    KEY_ATTRIBUTE(".*fontStyle=4.*"),
    /**
     *
     */
    MULTIVALUED_ATTRIBUTE(".*shape=doubleEllipse.*"),
    WEAK_ATTRIBUTE(".*span\\sstyle=\"border-bottom:\\s[0-9]px dotted\".*"),

    //relationship primary
    RELATIONSHIP(".*rhombus.*"),
    WEAK_RELATIONSHIP(".*double=[1-9].*"),

    WEAK_ENTITY(".*double=[1-9].*"),

    EDGE(".*endArrow=none.*"),
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
