package utils;

import corrector.AssignmentGrader;
import corrector.Defect;

import java.util.List;

public class CorrectorOutputFormatter {
    public static String stringifyGrading(AssignmentGrader grader){
        List<Defect> defects = grader.getDefects();
        Float points = grader.getPoints();
        StringBuilder builder = new StringBuilder();
        builder.append("Errors present in diagram:\n");
        defects.stream()
                .filter(Defect::getPresent)
                .forEach(defect -> builder.append(String.format("\tDefect: %s - %s\n",
                defect.getType().getMessage(), defect.getAdditionalInfo())));
        builder.append(String.format("Final points: %f", points));
        return builder.toString();
    }
}
