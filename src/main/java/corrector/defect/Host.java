package corrector.defect;

import output.stringifier.DefectVisitor;

public interface Host {
    String accept(DefectVisitor defectVisitor);
}
