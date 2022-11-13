package corrector.defect;

import enums.DefectType;
import lombok.Builder;
import lombok.extern.java.Log;

import java.util.LinkedList;
import java.util.List;

@Log
public class UsageDefect<T> extends Defect{
    private final List<T> expected;
    private final List<T> actual;

    @Builder(builderMethodName = "usageBuilder")
    public UsageDefect(DefectType type, Boolean present, float points, String additionalInfo,
                       List<T> expected, List<T> actual) {
        super(type, present, points, additionalInfo);
        this.expected = expected;
        this.actual = actual;
    }

    public List<T> getExpected(){
        return new LinkedList<>(expected);
    }
    public List<T> getActual(){
        return new LinkedList<>(actual);
    }
}
