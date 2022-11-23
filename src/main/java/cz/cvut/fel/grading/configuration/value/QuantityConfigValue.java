package cz.cvut.fel.grading.configuration.value;

import lombok.Getter;

/**
 * Class specifying configuration values for QuantityDefects.
 */
@Getter
public class QuantityConfigValue extends  ConfigValue{
    private final int min;
    private final int max;

    /**
     * Basic constructor
     * @param points Number of points awarded on success
     * @param min minimum number of cases described by associated defect present in diagram
     * @param max maximum number of cases described by associated defect present in diagram
     */
    public QuantityConfigValue(float points, int min, int max) {
        super(points);
        this.min = min;
        this.max = max;
    }
}
