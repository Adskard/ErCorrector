package cz.cvut.fel.grading.defect;

import cz.cvut.fel.enums.DefectType;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import cz.cvut.fel.output.stringifier.DefectVisitor;

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

    @Override
    public String accept(DefectVisitor defectVisitor) {
        return defectVisitor.visit(this);
    }
}
