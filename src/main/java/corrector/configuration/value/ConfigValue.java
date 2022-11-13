package corrector.configuration.value;

import lombok.Getter;

@Getter
public class ConfigValue {
    private final float points;

    public ConfigValue(float points) {
        this.points = points;
    }
}
