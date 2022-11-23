package cz.cvut.fel.grading.configuration.value;


import lombok.Getter;

/**
 * Class specifying configuration values for NaryRelationshipDefect.
 */
@Getter
public class NaryRelationshipConfigValue extends QuantityConfigValue{
    private final int edges;

    /**
     * Basic constructor
     * @param points Number of points awarded on success
     * @param min minimum number of NaryRelationships present in diagram
     * @param max maximum number of NaryRelationships present in diagram
     * @param edges Number of connection of NaryRelationship (3 edges = Ternary relationship)
     */
    public NaryRelationshipConfigValue(float points, int min, int max, int edges) {
        super(points, min, max);
        this.edges = edges;
    }
}
