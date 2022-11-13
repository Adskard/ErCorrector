package corrector.configuration.value;

import lombok.Getter;

import java.util.List;

@Getter
public class UsageConfigValue<T> extends ConfigValue{
    private final List<T> expected;

    public UsageConfigValue(float points, List<T> expected) {
        super(points);
        this.expected = expected;
    }
}
