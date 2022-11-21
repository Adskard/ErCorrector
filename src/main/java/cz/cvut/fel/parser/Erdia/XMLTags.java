package cz.cvut.fel.parser.Erdia;


/**
 * XML tags and attribute names used for parsing
 * @author Adam Skarda
 */
public enum XMLTags {
    CELL("mxCell"),
    ENTITY("ErEntity"),
    ATTRIBUTE("ErAttribute"),
    RELATIONSHIP("ErRelationship"),
    ATTRIBUTE_CONNECTOR("ErAttributeConnector"),
    RELATIONSHIP_CONNECTOR("ErRelationshipConnector"),
    HIERARCHY("ErHierarchy"),
    GENERALIZATION_CONNECTOR("ErGeneralizationConnector"),
    SPECIALIZATION_CONNECTOR("ErSpecializationConnector"),
    COMPOSITE_ID("compositeIdentifier"),
    KEY_ATTRIBUTE("simpleIdentifier"),
    COMPOSITE_MEMBER("ErCompositeIdentifierMember"),
    NAME_ATTRIBUTE("name"),
    STYLE_ATTRIBUTE("style"),
    ID_ATTRIBUTE("id"),
    CARDINALITY_MIN_ATTRIBUTE("cardinalityMin"),
    CARDINALITY_MAX_ATTRIBUTE("cardinalityMax"),
    COVERAGE_ATTRIBUTE("coverage"),
    DISJOINTNESS_ATTRIBUTE("disjointness"),
    SOURCE_ATTRIBUTE("source"),
    TARGET_ATTRIBUTE("target"),
    EDGE_ATTRIBUTE("edge"),
    VERTEX_ATTRIBUTE("vertex");

    private final String value;
    XMLTags(String value){
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
