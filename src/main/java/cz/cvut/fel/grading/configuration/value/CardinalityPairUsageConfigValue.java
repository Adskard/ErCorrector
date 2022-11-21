package cz.cvut.fel.grading.configuration.value;

import cz.cvut.fel.grading.struct.CardinalityPair;
import lombok.Getter;

import java.util.List;

@Getter
public class CardinalityPairUsageConfigValue extends ConfigValue{
    private final List<CardinalityPair> expected;
    //TODO fix generics
    public CardinalityPairUsageConfigValue(float points, List<CardinalityPair> expected) {
        super(points);
        this.expected = expected;
    }
}
