package utils;

import corrector.AssignmentGrader;
import corrector.defect.Defect;
import corrector.defect.QuantityDefect;

import java.util.List;

public class CorrectorOutputFormatter {
    public static String stringifyGrading(AssignmentGrader grader){
        List<Defect> defects = grader.getDefects();
        Float points = grader.getPoints();
        StringBuilder builder = new StringBuilder();
        builder.append("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
        builder.append("%\t\tErrors present in diagram\t\t%\n");
        builder.append("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
        defects.stream()
                //.filter(Defect::getPresent)
                .forEach(defect -> builder.append(stringifyDefect(defect)));
        builder.append(String.format("Final points: %f", points));
        return builder.toString();
    }

    private static String stringifyDefect(Defect defect){
        StringBuilder builder = new StringBuilder();
        

        return builder.toString();
    }

    private static String stringifyQuantityDefect(QuantityDefect defect){
        StringBuilder builder = new StringBuilder();
        return builder.toString();
    }

    private static String stringifyUsageDefect(QuantityDefect defect){
        StringBuilder builder = new StringBuilder();
        return builder.toString();
    }

    private static String stringifyCountDefect(QuantityDefect defect){
        StringBuilder builder = new StringBuilder();
        return builder.toString();
    }

    private static String stringifyBasicDefect(QuantityDefect defect){
        StringBuilder builder = new StringBuilder();
        return builder.toString();
    }
}
