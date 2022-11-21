package cz.cvut.fel.grading.configuration.value;

import cz.cvut.fel.enums.Cardinality;
import lombok.Getter;

import java.util.List;

@Getter
public class CardinalityUsageConfigValue extends ConfigValue{
    private final List<Cardinality> expected;

    public CardinalityUsageConfigValue(float points, List<Cardinality> expected) {
        super(points);
        this.expected = expected;
    }
}
