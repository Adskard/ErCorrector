package corrector.defect;

import enums.DefectType;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;

@Getter
@Log
public class QuantityDefect extends Defect{
    private final int min;
    private final int max;
    private final int actual;

    @Builder(builderMethodName = "quantityBuilder")
    public QuantityDefect(DefectType type, Boolean present, float points, String additionalInfo,
                          int min, int max, int actual) {
        super(type, present, points, additionalInfo);
        this.min = min;
        this.max = max;
        this.actual = actual;
    }
}
