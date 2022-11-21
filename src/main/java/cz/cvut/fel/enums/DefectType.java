package cz.cvut.fel.enums;

import lombok.Getter;


/**
 * Types of defects found during diagram correction
 */
@Getter
public enum DefectType {
    MULTIVALUED_ATTRIBUTE_COUNT("Multivalued attribute count",
            "attribute_multival", ConfigValueType.QUANTITY, "1, 100, 1.00"),

    STRUCTURED_ATTRIBUTE_COUNT("Structured attribute count",
            "attribute_structured", ConfigValueType.QUANTITY, "1, 100, 1.00"),

    ORDINARY_ATTRIBUTE_COUNT("Ordinary attribute count",
            "attribute_ordinary", ConfigValueType.QUANTITY, "1, 100, 1.00"),

    ENTITY_COUNT("Entity count", "entity_count",
            ConfigValueType.QUANTITY, "1, 100, 1.00"),

    RELATIONSHIP_COUNT("Relationship count", "relationship_count",
            ConfigValueType.QUANTITY, "1, 100, 1.00"),

    RECURSIVE_RELATIONSHIP_COUNT("Recursive relationship count",
            "recursion", ConfigValueType.QUANTITY, "1, 100, 1.00"),

    N_ARY_RELATIONSHIP_COUNT("N-ary relationship count",
            "n_ary_relationship", ConfigValueType.NARY_RELATIONSHIP, "1, 100, 3, 1.00"),

    WEAK_ENTITY_COUNT("Weak entity count","weak_entity_count",
            ConfigValueType.QUANTITY, "1, 100, 1.00"),

    MULTIPLE_ID_COUNT("Multiple identifiers on one entity count","several_identifiers",
            ConfigValueType.QUANTITY, "1, 100, 1.00"),

    COMPOSITE_ID_COUNT("Composite identifier count","composite_id",
            ConfigValueType.QUANTITY, "1, 100, 1.00"),

    HIERARCHY_COUNT("Hierarchy count","hierarchy_count",
            ConfigValueType.QUANTITY, "1, 100, 1.00"),



    CARDINALITY_TYPE_USAGE("Cardinality type usage",
            "cardinality_types", ConfigValueType.USAGE, "all, 2.00"),

    CARDINALITY_PAIR_USAGE("Cardinality pair usage",
            "cardinality_pairs", ConfigValueType.USAGE, "all, 2.00"),

    MULTIVALUED_ATTRIBUTE_CARDINALITY_USAGE("Usage of cardinality types on multivalued attributes",
            "attribute_multival_cardinality", ConfigValueType.USAGE, "all, 2.00"),

    HIERARCHY_USAGE("Hierarchy usages","hierarchy_usage",
            ConfigValueType.USAGE, "all, 2.00"),



    HIERARCHY_ANNOTATED("Hierarchy annotated","hierarchy_annotation",
            ConfigValueType.BASIC, "0.5"),

    MULTIVALUED_ATTRIBUTE_ILLEGAL_CARDINALITY("Illegal multivalued attributes",
            "illegal_multival_attributes", ConfigValueType.BASIC, "0.5"),

    NAMED_VERTICES("Named entities, attributes, relationships","named_vertices",
            ConfigValueType.BASIC, "0.5"),

    NO_DUPLICATE_NAMES("No duplicate names","no_duplicate_names",
            ConfigValueType.BASIC, "0.5"),

    NO_DUPLICATE_ATTRIBUTES("No duplicate attributes on vertex","no_duplicate_attributes",
            ConfigValueType.BASIC, "0.5"),

    EVERY_ENTITY_IDENTIFIED("Every entity identified","entity_identified",
            ConfigValueType.BASIC, "0.5"),

    WEAK_ENTITY_IDENTIFIED("Weak entity identification","weak_entity_identified",
            ConfigValueType.BASIC, "0.5"),

    CARDINALITIES_PRESENT("Presence of cardinalities on relationship connections",
            "presence_of_cardinalities", ConfigValueType.BASIC, "0.5"),

    ONE_COMPONENT("Single diagram component","component", ConfigValueType.BASIC, "0.5");

    /**
     * Message representing the defect
     */
    private final String message;

    /**
     * Key value string for configuration specification
     */
    private final String configKey;

    /**
     * Type of configuration value structure
     */
    private final ConfigValueType classification;

    /**
     * Default configuration value
     */
    private final String defaultValue;

    DefectType(String message, String configKey, ConfigValueType classification, String defaultValue){
        this.message = message;
        this.configKey = configKey;
        this.classification = classification;
        this.defaultValue = defaultValue;
    }
}
