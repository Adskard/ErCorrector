package cz.cvut.fel.output.stringifier;

import cz.cvut.fel.grading.defect.BasicDefect;
import cz.cvut.fel.grading.defect.Defect;
import cz.cvut.fel.grading.defect.QuantityDefect;
import cz.cvut.fel.grading.defect.UsageDefect;

/**
 * Visitor interface for Defect classes
 * @author Adam Skarda
 */
public interface DefectVisitor {
    String visit(BasicDefect<?> basicDefect);
    String visit(UsageDefect<?> usageDefect);
    String visit(QuantityDefect quantityDefect);
    String visit(Defect defect);
}
