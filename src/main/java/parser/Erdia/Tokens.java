package parser.Erdia;

public enum Tokens {
    ENTITY("entity"),
    ATTRIBUTE_CONNECTOR("attributeConnector"),
    COMPOSITE_ID("compositeIdentifier"),
    RELATIONSHIP_CONNECTOR("relationshipConnector"),
    RELATIONSHIP("relationship"),
    HIERARCHY("hierarchy"),
    GENERALIZATION("generalization"),
    SPECIALIZATION("specialization"),
    ATTRIBUTE("attribute");

    private final String value;
    Tokens(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
