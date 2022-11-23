package cz.cvut.fel.grading.defect;

import cz.cvut.fel.enums.DefectType;
import cz.cvut.fel.output.stringifier.DefectVisitor;
import lombok.Builder;
import lombok.extern.java.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Class UsageDefect is a Defect that describes expected and
 * actual uses of some Object in an Entity relationship diagram.
 * Where expected is an enumeration of Objects of type T that we expect
 * to find in Er diagram and actual is an enumeration of T Objects that
 * are present in the diagram.
 *
 * @param <T> Object type aggregated int expected and actual usages. Depends on DefectType.
 * @author Adam Skarda
 */
@Log
public class UsageDefect<T> extends Defect{
    private final List<T> expected;
    private final List<T> actual;

    /**
     * Builder constructor
     *
     * @param type Type of modeling or task error that results in this defect
     * @param present true if the defect was found (There is an error in Er diagram) false otherwise
     * @param points number of points awarded for this defect (if it is not present)
     * @param additionalInfo more information about the defect e.g. how it was found, where, ...
     * @param expected list of expected uses of a given type
     * @param actual actual uses found during Er diagram grading
     */
    @Builder(builderMethodName = "usageBuilder")
    public UsageDefect(DefectType type, Boolean present, float points, String additionalInfo,
                       List<T> expected, List<T> actual) {
        super(type, present, points, additionalInfo);
        this.expected = expected;
        this.actual = actual;
    }

    @Override
    public String accept(DefectVisitor defectVisitor) {
        return defectVisitor.visit(this);
    }

    public List<T> getExpected(){
        return new LinkedList<>(expected);
    }
    public List<T> getActual(){
        return new LinkedList<>(actual);
    }
}
