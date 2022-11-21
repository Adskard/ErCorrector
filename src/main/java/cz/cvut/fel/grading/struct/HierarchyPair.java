package cz.cvut.fel.grading.struct;

import cz.cvut.fel.enums.Coverage;
import cz.cvut.fel.enums.Disjointness;
import lombok.Getter;

import java.util.Objects;

@Getter
public class HierarchyPair {
    private final Coverage coverage;
    private final Disjointness disjointness;

    public HierarchyPair(Coverage coverage, Disjointness disjointness) {
        this.coverage = coverage;
        this.disjointness = disjointness;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HierarchyPair that = (HierarchyPair) o;
        return (coverage == that.coverage && disjointness == that.disjointness);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coverage, disjointness);
    }

    @Override
    public String toString() {
        return "["
                + coverage +
                " + " + disjointness +
                ']';
    }
}
