package cz.cvut.fel.enums;

import lombok.Getter;


/**
 * Types of defects found during diagram correction
 */
@Getter
public enum DefectType {

    /**
     * Task specific quantity defect. Its presence signifies that the number of multivalued attributes
     * in a Diagram is out of bounds specified by configuration or default value. It is a Task error.
     * Its absence signifies that such a task error was not committed in a diagram and the
     * number of multivalued attributes is within expected (given) bounds.
     */
    MULTIVALUED_ATTRIBUTE_COUNT("Multivalued attribute count",
            "attribute_multival", ConfigValueType.QUANTITY, "1, 100, 1.00"),

    /**
     * Task specific quantity defect. Its presence signifies that the number of structured attributes
     * in a Diagram is out of bounds specified by configuration or default value. It is a Task error.
     * Its absence signifies that such a task error was not committed in a diagram and the
     * number of structured attributes is within expected (given) bounds.
     */
    STRUCTURED_ATTRIBUTE_COUNT("Structured attribute count",
            "attribute_structured", ConfigValueType.QUANTITY, "1, 100, 1.00"),

    /**
     * Task specific quantity defect. Its presence signifies that the number of attributes
     * in a Diagram is out of bounds specified by configuration or default value. It is a Task error.
     * Its absence signifies that such a task error was not committed in a diagram and the
     * number of attributes is within expected (given) bounds.
     */
    ORDINARY_ATTRIBUTE_COUNT("Ordinary attribute count",
            "attribute_ordinary", ConfigValueType.QUANTITY, "1, 100, 1.00"),

    /**
     * Task specific quantity defect. Its presence signifies that the number of entities
     * in a Diagram is out of bounds specified by configuration or default value. It is a Task error.
     * Its absence signifies that such a task error was not committed in a diagram and the
     * number of entities is within expected (given) bounds.
     */
    ENTITY_COUNT("Entity count", "entity_count",
            ConfigValueType.QUANTITY, "1, 100, 1.00"),

    /**
     * Task specific quantity defect. Its presence signifies that the number of relationships
     * in a Diagram is out of bounds specified by configuration or default value. It is a Task error.
     * Its absence signifies that such a task error was not committed in a diagram and the
     * number of relationships is within expected (given) bounds.
     */
    RELATIONSHIP_COUNT("Relationship count", "relationship_count",
            ConfigValueType.QUANTITY, "1, 100, 1.00"),

    /**
     * Task specific quantity defect. Its presence signifies that the number of recursive relationships
     * in a Diagram is out of bounds specified by configuration or default value. It is a Task error.
     * Its absence signifies that such a task error was not committed in a diagram and the
     * number of recursive relationships is within expected (given) bounds.
     */
    RECURSIVE_RELATIONSHIP_COUNT("Recursive relationship count",
            "recursion", ConfigValueType.QUANTITY, "1, 100, 1.00"),

    /**
     * Task specific quantity defect. Its presence signifies that the number of n-ary relationships
     * that have at least n connections, is out of bounds specified
     * by configuration or default value. It is a Task error.
     * Its absence signifies that such a task error was not committed in a diagram and the
     * number of n-ary relationships is within expected (given) bounds.
     * N-ary relationships is the number of entity edges leading to a relationship.
     * E.g. relationship connecting three entities is a ternary (3 - ary) and any higher order relationship
     * is also acceptable.
     */
    N_ARY_RELATIONSHIP_COUNT("N-ary relationship count",
            "n_ary_relationship", ConfigValueType.NARY_RELATIONSHIP, "1, 100, 3, 1.00"),

    /**
     * Task specific quantity defect. Its presence signifies that the number of weak entities
     * in a Diagram is out of bounds specified by configuration or default value. It is a Task error.
     * Its absence signifies that such a task error was not committed in a diagram and the
     * number of weak entities is within expected (given) bounds.
     */
    WEAK_ENTITY_COUNT("Weak entity count","weak_entity_count",
            ConfigValueType.QUANTITY, "1, 100, 1.00"),

    /**
     * Task specific quantity defect. Its presence signifies that the number of entities with multiple identifiers
     * in a Diagram is out of bounds specified by configuration or default value. It is a Task error.
     * Its absence signifies that such a task error was not committed in a diagram and the
     * number of entities with multiple identifiers is within expected (given) bounds.
     */
    MULTIPLE_ID_COUNT("Multiple identifiers on one entity count","several_identifiers",
            ConfigValueType.QUANTITY, "1, 100, 1.00"),

    /**
     * Task specific quantity defect. Its presence signifies that the number of composite identifiers
     * in a Diagram is out of bounds specified by configuration or default value. It is a Task error.
     * Its absence signifies that such a task error was not committed in a diagram and the
     * number of composite identifiers is within expected (given) bounds.
     */
    COMPOSITE_ID_COUNT("Composite identifier count","composite_id",
            ConfigValueType.QUANTITY, "1, 100, 1.00"),

    /**
     * Task specific quantity defect. Its presence signifies that the number of ancestor entities
     * in a Diagram is out of bounds specified by configuration or default value. It is a Task error.
     * Its absence signifies that such a task error was not committed in a diagram and the
     * number of ancestor entities is within expected (given) bounds.
     */
    HIERARCHY_COUNT("Hierarchy count","hierarchy_count",
            ConfigValueType.QUANTITY, "1, 100, 1.00"),


    /**
     * Task specific usage defect. Its presence signifies that all expected Cardinalities
     * on relationship edges were not used. Expected cardinalities are set either by
     * default or by configuration. Its absence signifies that all expected Cardinalities were
     * used at least once.
     */
    CARDINALITY_TYPE_USAGE("Cardinality type usage",
            "cardinality_types", ConfigValueType.USAGE, "all, 2.00"),

    /**
     * Task specific usage defect. Its presence signifies that all expected Cardinality pairs
     * on relationship edges were not used. Expected cardinality pairs are set either by
     * default or by configuration. Its absence signifies that all expected Cardinality pairs were
     * used at least once.
     * A Cardinality pair are Cardinalities on connection Entity-Relationship-Entity.
     */
    CARDINALITY_PAIR_USAGE("Cardinality pair usage",
            "cardinality_pairs", ConfigValueType.USAGE, "all, 2.00"),

    /**
     * Task specific usage defect. Its presence signifies that all expected Cardinalities
     * on multivalued attribute edges were not used. Expected cardinalities are set either by
     * default or by configuration. Its absence signifies that all expected Cardinalities were
     * used at least once.
     */
    MULTIVALUED_ATTRIBUTE_CARDINALITY_USAGE("Usage of cardinality types on multivalued attributes",
            "attribute_multival_cardinality", ConfigValueType.USAGE, "all, 2.00"),

    /**
     * Task specific usage defect. Its presence signifies that all expected Hierarchies
     * on Generalization connections were not used. Expected hierarchies are set either by
     * default or by configuration. Its absence signifies that all expected Cardinalities were
     * used at least once.
     * A Hierarchy is specified by a HierarchyPair which is a Coverage and Disjointness pair.
     */
    HIERARCHY_USAGE("Hierarchy usages","hierarchy_usage",
            ConfigValueType.USAGE, "all, 2.00"),

    /**
     * Basic Er modeling defect, can be in any Entity relationship diagram independently of given task.
     * Its presence signifies that not every Generalization edge has valid Coverage and Disjointness.
     */
    HIERARCHY_ANNOTATED("Hierarchy annotated","hierarchy_annotation",
            ConfigValueType.BASIC, "0.5"),

    /**
     * Basic Er modeling defect, can be in any Entity relationship diagram independently of given task.
     * Its presence signifies that an illegal cardinality 1..1 was used on a multivalued attribute.
     */
    MULTIVALUED_ATTRIBUTE_ILLEGAL_CARDINALITY("Illegal multivalued attributes",
            "illegal_multival_attributes", ConfigValueType.BASIC, "0.5"),

    /**
     * Basic Er modeling defect, can be in any Entity relationship diagram independently of given task.
     * Its presence signifies not every vertex (Entity, Relationship, Attribute) was named.
     */
    NAMED_VERTICES("Named entities, attributes, relationships","named_vertices",
            ConfigValueType.BASIC, "0.5"),

    /**
     * Basic Er modeling defect, can be in any Entity relationship diagram independently of given task.
     * Its presence signifies that there exist duplicate Entity or Relationship names in the Diagram.
     */
    NO_DUPLICATE_NAMES("No duplicate names","no_duplicate_names",
            ConfigValueType.BASIC, "0.5"),

    /**
     * Basic Er modeling defect, can be in any Entity relationship diagram independently of given task.
     * Its presence signifies there are attributes with duplicate names on single Entity or Relationship.
     */
    NO_DUPLICATE_ATTRIBUTES("No duplicate attributes on vertex","no_duplicate_attributes",
            ConfigValueType.BASIC, "0.5"),

    /**
     * Basic Er modeling defect, can be in any Entity relationship diagram independently of given task.
     * Its presence signifies not every non-weak Entity was identified by a key either simple or composite.
     */
    EVERY_ENTITY_IDENTIFIED("Every entity identified","entity_identified",
            ConfigValueType.BASIC, "0.5"),

    /**
     * Basic Er modeling defect, can be in any Entity relationship diagram independently of given task.
     * Its presence signifies that not every weak Entity was properly identified by its composite key.
     * A composite key identifies a weak entity when its member is a relationship with edge Cardinality
     * 1..1 and at least one member attribute.
     */
    WEAK_ENTITY_IDENTIFIED("Weak entity identification","weak_entity_identified",
            ConfigValueType.BASIC, "0.5"),

    /**
     * Basic Er modeling defect, can be in any Entity relationship diagram independently of given task.
     * Its presence signifies that not every Entity-Relationship edge has a specified Cardinality.
     */
    CARDINALITIES_PRESENT("Presence of cardinalities on relationship edges",
            "presence_of_cardinalities", ConfigValueType.BASIC, "0.5"),

    /**
     * Basic Er modeling defect, can be in any Entity relationship diagram independently of given task.
     * Its presence signifies that the Diagram is not in one cohesive component.
     */
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
