package cz.cvut.fel.grading.configuration.value;

import cz.cvut.fel.enums.Cardinality;
import lombok.Getter;

import java.util.List;

/**
 * Class specifying configuration values for CardinalityUsageDefects.
 */
@Getter
public class CardinalityUsageConfigValue extends ConfigValue{
    private final List<Cardinality> expected;

    /**
     * Basic constructor
     * @param points Number of points awarded on success
     * @param expected Expected Cardinalities in diagram
     */
    public CardinalityUsageConfigValue(float points, List<Cardinality> expected) {
        super(points);
        this.expected = expected;
    }
}
