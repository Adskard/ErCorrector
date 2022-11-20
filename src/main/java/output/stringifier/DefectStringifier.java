package output.stringifier;

import corrector.defect.BasicDefect;
import corrector.defect.Defect;
import corrector.defect.QuantityDefect;
import corrector.defect.UsageDefect;

public class DefectStringifier implements DefectVisitor{
    @Override
    public String visit(BasicDefect basicDefect) {
        StringBuilder builder = new StringBuilder();

        builder.append(basicDefect.getType().getMessage() + "\n");
        
        builder.append(String.format("\tAwarded %f points out of %f\n",
                basicDefect.getPresent() ? 0.0f : basicDefect.getPoints(),
                basicDefect.getPoints()));

        builder.append(String.format("\tIncorrect usages: %s\n", basicDefect.getIncorrectObjects()));

        if(!basicDefect.getAdditionalInfo().isEmpty()){
            builder.append(String.format("\tAdditional information: \n\t\t%s\n",
                    basicDefect.getAdditionalInfo()));
        }
        return builder.toString();
    }

    @Override
    public String visit(UsageDefect usageDefect) {
        StringBuilder builder = new StringBuilder();
        builder.append(usageDefect.getType().getMessage() + "\n");

        builder.append(String.format("\tAwarded %f points out of %f\n",
                usageDefect.getPresent() ? 0.0f : usageDefect.getPoints(),
                usageDefect.getPoints()));

        builder.append(String.format("\tExpected usages: %s\n",
                usageDefect.getExpected()));

        builder.append(String.format("\tActual usages: %s\n",
                usageDefect.getActual()));

        if(!usageDefect.getAdditionalInfo().isEmpty()){
            builder.append(String.format("\tAdditional information: \n\t\t%s\n",
                    usageDefect.getAdditionalInfo()));
        }
        return builder.toString();
    }

    @Override
    public String visit(QuantityDefect quantityDefect) {
        StringBuilder builder = new StringBuilder();
        builder.append(quantityDefect.getType().getMessage() + "\n");

        builder.append(String.format("\tAwarded %f points out of %f\n",
                quantityDefect.getPresent() ? 0.0f : quantityDefect.getPoints(),
                quantityDefect.getPoints()));

        builder.append(String.format("\tRequired: <%s, %s> \n\tActual: %s\n",
                quantityDefect.getMin(),
                quantityDefect.getMax(),
                quantityDefect.getActual()));

        if(!quantityDefect.getAdditionalInfo().isEmpty()){
            builder.append(String.format("\tAdditional information: \n\t\t%s\n",
                    quantityDefect.getAdditionalInfo()));
        }
        return builder.toString();
    }

    @Override
    public String visit(Defect defect) {
        StringBuilder builder = new StringBuilder();
        builder.append(defect.getType().getMessage() + "\n");
        builder.append(String.format("\tAwarded %f points out of %f\n",
                defect.getPresent() ? 0.0f : defect.getPoints(),
                defect.getPoints()));

        if(!defect.getAdditionalInfo().isEmpty()){
            builder.append(String.format("\tAdditional information: \n\t\t%s\n",defect.getAdditionalInfo()));
        }
        return builder.toString();
    }
}
