package cz.cvut.fel.grading.configuration.value;

import lombok.Getter;

@Getter
public class QuantityConfigValue extends  ConfigValue{
    private final int min;
    private final int max;

    public QuantityConfigValue(float points, int min, int max) {
        super(points);
        this.min = min;
        this.max = max;
    }
}
