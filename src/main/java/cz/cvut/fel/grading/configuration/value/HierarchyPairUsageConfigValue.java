package cz.cvut.fel.grading.configuration.value;

import cz.cvut.fel.grading.struct.HierarchyPair;
import lombok.Getter;

import java.util.List;

@Getter
public class HierarchyPairUsageConfigValue extends ConfigValue{
    private final List<HierarchyPair> expected;

    public HierarchyPairUsageConfigValue(float points, List<HierarchyPair> expected) {
        super(points);
        this.expected = expected;
    }
}