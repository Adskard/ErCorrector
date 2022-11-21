package cz.cvut.fel.grading.defect;

import cz.cvut.fel.enums.DefectType;
import lombok.Builder;
import lombok.Getter;
import cz.cvut.fel.output.stringifier.DefectVisitor;

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
