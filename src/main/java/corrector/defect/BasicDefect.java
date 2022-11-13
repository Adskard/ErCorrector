package corrector.defect;

import enums.DefectType;
import lombok.Builder;

import java.util.List;

public class BasicDefect<T> extends Defect{
    private final List<T> incorrectObjects;

    @Builder(builderMethodName = "basicBuilder")
    public BasicDefect(DefectType type, Boolean present, float points, String additionalInfo,
                       List<T> incorrectObjects) {
        super(type, present, points, additionalInfo);
        this.incorrectObjects = incorrectObjects;
    }
}
