package cz.cvut.fel.grading.configuration.value;

import lombok.Getter;

/**
 * Class specifying configuration values for Defects.
 */
@Getter
public class ConfigValue {
    private final float points;

    /**
     * Basic constructor
     * @param points Number of points awarded on success
     */
    public ConfigValue(float points) {
        this.points = points;
    }
}
