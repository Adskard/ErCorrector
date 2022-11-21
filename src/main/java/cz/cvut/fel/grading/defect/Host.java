package cz.cvut.fel.grading.defect;

import cz.cvut.fel.output.stringifier.DefectVisitor;

public interface Host {
    String accept(DefectVisitor defectVisitor);
}
