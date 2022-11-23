package cz.cvut.fel.grading.defect;

import cz.cvut.fel.enums.DefectType;
import lombok.Builder;
import lombok.Getter;
import cz.cvut.fel.output.stringifier.DefectVisitor;

import java.util.List;

/**
 * Class BasicDefect is a Defect that describes
 * a fundamental Er diagram modeling error.
 * These errors can be present in any Er diagrams independently
 * of the modeling task.
 *
 * @param <T> type of objects that are part of this Defect
 * @author Adam Skarda
 */
@Getter
public class BasicDefect<T> extends Defect{
    private final List<T> incorrectObjects;

    /**
     * Builder constructor
     *
     * @param type Type of modeling or task error that results in this defect
     * @param present true if the defect was found (There is an error in Er diagram) false otherwise
     * @param points number of points awarded for this defect (if it is not present)
     * @param additionalInfo more information about the defect e.g. how it was found, where, ...
     * @param incorrectObjects objects that are associated with the Er modeling error
     */
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
