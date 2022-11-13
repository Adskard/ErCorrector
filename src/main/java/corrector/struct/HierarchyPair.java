package corrector.struct;

import enums.Coverage;
import enums.Disjointness;
import lombok.Getter;

@Getter
public class HierarchyPair {
    private final Coverage coverage;
    private final Disjointness disjointness;

    public HierarchyPair(Coverage coverage, Disjointness disjointness) {
        this.coverage = coverage;
        this.disjointness = disjointness;
    }
}
