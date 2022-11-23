package cz.cvut.fel.grading.defect;

import cz.cvut.fel.enums.DefectType;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import cz.cvut.fel.output.stringifier.DefectVisitor;

/**
 * Class QuantityDefect is a Defect that describes expected and
 * actual number of occurrences of Er diagram parts/structures.
 * Expected quantity is described using inclusive interval min, max.
 * A specific quantity can be then described as min, max where min=max.
 * e.g. to expect exactly 7 of something in diagram min = 7 and max = 7.
 *
 * @author Adam Skarda
 */
@Getter
@Log
public class QuantityDefect extends Defect{
    private final int min;
    private final int max;
    private final int actual;

    /**
     * Builder constructor
     *
     * @param type Type of modeling or task error that results in this defect
     * @param present true if the defect was found (There is an error in Er diagram) false otherwise
     * @param points number of points awarded for this defect (if it is not present)
     * @param additionalInfo more information about the defect e.g. how it was found, where, ...
     * @param min minimum expected number of occurrences
     * @param max maximum expected number of occurrences
     * @param actual actual number of occurrences
     */
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
