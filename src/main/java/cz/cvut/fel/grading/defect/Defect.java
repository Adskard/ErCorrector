package cz.cvut.fel.grading.defect;

import cz.cvut.fel.enums.DefectType;
import cz.cvut.fel.output.stringifier.DefectVisitor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

/**
 * Class Defect represents and describes modeling or task errors
 * that can be made during Entity relationship modeling.
 * And are present in the resulting Diagram.
 *
 * @author Adam Skarda
 */
@Getter
@Setter
@Log
public class Defect implements Host{
    private final DefectType type;
    private final Boolean present;
    private final String additionalInfo;
    private final float points;

    /**
     * Builder constructor
     *
     * @param type Type of modeling or task error that results in this defect
     * @param present true if the defect was found (There is an error in Er diagram) false otherwise
     * @param points number of points awarded for this defect (if it is not present)
     * @param additionalInfo more information about the defect e.g. how it was found, where, ...
     */
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
