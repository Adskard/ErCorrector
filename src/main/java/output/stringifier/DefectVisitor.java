package output.stringifier;

import corrector.defect.BasicDefect;
import corrector.defect.Defect;
import corrector.defect.QuantityDefect;
import corrector.defect.UsageDefect;

public interface DefectVisitor {
    String visit(BasicDefect basicDefect);
    String visit(UsageDefect usageDefect);
    String visit(QuantityDefect quantityDefect);
    String visit(Defect defect);
}
