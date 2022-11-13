package corrector.defect;

import enums.DefectType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Getter
@Setter
@Log
public class Defect {
    private final DefectType type;
    private final Boolean present;
    private final String additionalInfo;
    private final float points;

    @Builder
    public Defect(DefectType type, Boolean present, float points, String additionalInfo) {
        this.type = type;
        this.present = present;
        this.additionalInfo = additionalInfo;
        this.points = points;
    }
}
