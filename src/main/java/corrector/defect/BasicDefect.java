package corrector.defect;

import enums.DefectType;
import lombok.Builder;
import lombok.Getter;
import output.stringifier.DefectVisitor;

import java.util.List;

@Getter
public class BasicDefect<T> extends Defect{
    private final List<T> incorrectObjects;

    @Builder(builderMethodName = "basicBuilder")
    public BasicDefect(DefectType type, Boolean present, float points, String additionalInfo,
                       List<T> incorrectObjects) {
        super(type, present, points, additionalInfo);
        this.incorrectObjects = incorrectObjects;
    }

    @Override
    public String accept(DefectVisitor defectVisitor) {
        return defectVisitor.visit(this);
    }
}
