package enums;

import lombok.Getter;

/**
 *
 */
@Getter
public enum DefectType {
    MULTIVALUED_ATTRIBUTE("Multivalued attributes","attribute_multival"),
    STRUCTURED_ATTRIBUTE("Structured attributes","attribute_structured"),
    ORDINARY_ATTRIBUTE("Ordinary attributes","attribute_ordinary"),

    CARDINALITY_TYPE_USAGE("Cardinality type usages","cardinality_types"),
    RECURSIVE_RELATIONSHIP("Recursive relationship usages","recursion"),
    N_ARY_RELATIONSHIP("N-ary relationship usages","n_ary_relationship"),

    WEAK_ENTITY("Weak entity usages","weak_entity"),
    MULTIPLE_IDS("Multiple identifiers","several_identifiers"),
    HIERARCHY("Hierarchy usages","hierarchy"),
    COMPOSITE_ID("Composite identification usages","composite_id"),

    ENTITY_COUNT("Entity count", "entity_count"),
    RELATIONSHIP_COUNT("Relationship count", "relationship_count"),
    /**
     * Basic defect
     */
    NAMED_VERTICES("Named entities, attributes, relationships","named_vertices"),
    NO_DUPLICATE_NAMES("Duplicate names","no_duplicate_names"),
    NO_DUPLICATE_ATTRIBUTES("Duplicate attributes","no_duplicate_attributes"),
    EVERY_ENTITY_IDENTIFIED("Entity identification","entity_identified"),
    WEAK_ENTITY_IDENTIFIED("Weak entity identification","weak_entity_identified"),
    CARDINALITIES_PRESENT("Presence of cardinalities","presence_of_cardinalities"),
    ONE_COMPONENT("Diagram component","component");
    /**
     *
     */
    private final String message;
    /**
     *
     */
    private final String configKey;
    DefectType(String message, String configKey){
        this.message = message;
        this.configKey = configKey;
    }
}
