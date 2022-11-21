package cz.cvut.fel.grading.defect;

import cz.cvut.fel.enums.DefectType;
import cz.cvut.fel.output.stringifier.DefectVisitor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Getter
@Setter
@Log
public class Defect implements Host{
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

    @Override
    public String accept(DefectVisitor defectVisitor) {
        return defectVisitor.visit(this);
    }

}
