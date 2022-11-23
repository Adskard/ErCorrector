package cz.cvut.fel.grading.configuration.value;

import cz.cvut.fel.grading.struct.CardinalityPair;
import lombok.Getter;

import java.util.List;

/**
 * Class specifying configuration values for CardinalityPairUsageDefects.
 */
@Getter
public class CardinalityPairUsageConfigValue extends ConfigValue{
    private final List<CardinalityPair> expected;

    /**
     * Basic constructor
     * @param points Points awarded on defect absence
     * @param expected Expected CardinalityPairs in Diagram
     * @see CardinalityPair
     * @see cz.cvut.fel.model.Diagram
     */
    public CardinalityPairUsageConfigValue(float points, List<CardinalityPair> expected) {
        super(points);
        this.expected = expected;
    }
}
