package cz.cvut.fel.output.stringifier;

import cz.cvut.fel.grading.defect.BasicDefect;
import cz.cvut.fel.grading.defect.Defect;
import cz.cvut.fel.grading.defect.QuantityDefect;
import cz.cvut.fel.grading.defect.UsageDefect;

/**
 * Class DefectStringifier is an implementation of Visitor pattern.
 * Its use is to create comprehensive string representation of various Defects.
 * @author Adam Skarda
 */
public class DefectStringifier implements DefectVisitor{
    /**
     * Creates a string representation of given BasicDefect
     * @param basicDefect defect we want to stringify
     * @return String representing given defect
     */
    @Override
    public String visit(BasicDefect<?> basicDefect) {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("%s\n", basicDefect.getType().getMessage()));
        
        builder.append(String.format("\tAwarded %f points out of %f\n",
                basicDefect.getPresent() ? 0.0f : basicDefect.getPoints(),
                basicDefect.getPoints()));

        if(!basicDefect.getIncorrectObjects().isEmpty()){
            builder.append(String.format("\tIncorrect usages: %s\n", basicDefect.getIncorrectObjects()));
        }


        if(!basicDefect.getAdditionalInfo().isEmpty()){
            builder.append(String.format("\tAdditional information: \n\t\t%s\n",
                    basicDefect.getAdditionalInfo()));
        }
        return builder.toString();
    }

    /**
     * Creates a string representation of given UsageDefect
     * @param usageDefect defect we want to stringify
     * @return String representing given defect
     */
    @Override
    public String visit(UsageDefect<?> usageDefect) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s\n", usageDefect.getType().getMessage()));

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

    /**
     * Creates a string representation of given QuantityDefect
     * @param quantityDefect defect we want to stringify
     * @return String representing given defect
     */
    @Override
    public String visit(QuantityDefect quantityDefect) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s\n", quantityDefect.getType().getMessage()));

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

    /**
     * Creates a string representation of given Defect
     * @param defect defect we want to stringify
     * @return String representing given defect
     */
    @Override
    public String visit(Defect defect) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s\n", defect.getType().getMessage()));
        builder.append(String.format("\tAwarded %f points out of %f\n",
                defect.getPresent() ? 0.0f : defect.getPoints(),
                defect.getPoints()));

        if(!defect.getAdditionalInfo().isEmpty()){
            builder.append(String.format("\tAdditional information: \n\t\t%s\n",defect.getAdditionalInfo()));
        }
        return builder.toString();
    }

}
