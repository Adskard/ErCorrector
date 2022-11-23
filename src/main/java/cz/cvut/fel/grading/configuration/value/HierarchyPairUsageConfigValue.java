package cz.cvut.fel.grading.configuration.value;

import cz.cvut.fel.grading.struct.HierarchyPair;
import lombok.Getter;

import java.util.List;

/**
 * Class specifying configuration values for HierarchyPairUsageDefects.
 */
@Getter
public class HierarchyPairUsageConfigValue extends ConfigValue{
    private final List<HierarchyPair> expected;

    /**
     * Basic constructor
     * @param points Number of points awarded on success
     * @param expected Expected HierarchyPairs present in diagram
     * @see cz.cvut.fel.model.Diagram
     */
    public HierarchyPairUsageConfigValue(float points, List<HierarchyPair> expected) {
        super(points);
        this.expected = expected;
    }
}